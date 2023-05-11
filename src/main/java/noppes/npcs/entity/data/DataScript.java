package noppes.npcs.entity.data;

import java.util.TreeMap;
import java.util.Map;
import net.minecraft.util.math.BlockPos;
import com.google.common.base.MoreObjects;
import java.util.Iterator;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.ScriptController;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.constants.EnumScriptType;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.controllers.ScriptContainer;
import java.util.List;
import noppes.npcs.controllers.IScriptHandler;

public class DataScript implements IScriptHandler
{
    private List<ScriptContainer> scripts;
    private String scriptLanguage;
    private EntityNPCInterface npc;
    private boolean enabled;
    public long lastInited;
    
    public DataScript(EntityNPCInterface npc) {
        this.scripts = new ArrayList<ScriptContainer>();
        this.scriptLanguage = "ECMAScript";
        this.enabled = false;
        this.lastInited = -1L;
        this.npc = npc;
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        this.scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Scripts", (NBTBase)NBTTags.NBTScript(this.scripts));
        compound.setString("ScriptLanguage", this.scriptLanguage);
        compound.setBoolean("ScriptEnabled", this.enabled);
        return compound;
    }
    
    @Override
    public void runScript(EnumScriptType type, Event event) {
        if (!this.isEnabled()) {
            return;
        }
        if (ScriptController.Instance.lastLoaded > this.lastInited) {
            this.lastInited = ScriptController.Instance.lastLoaded;
            if (type != EnumScriptType.INIT) {
                EventHooks.onNPCInit(this.npc);
            }
        }
        for (ScriptContainer script : this.scripts) {
            script.run(type, event);
        }
    }
    
    public boolean isEnabled() {
        return this.enabled && ScriptController.HasStart && !this.npc.world.isRemote;
    }
    
    @Override
    public boolean isClient() {
        return this.npc.isRemote();
    }
    
    @Override
    public boolean getEnabled() {
        return this.enabled;
    }
    
    @Override
    public void setEnabled(boolean bo) {
        this.enabled = bo;
    }
    
    @Override
    public String getLanguage() {
        return this.scriptLanguage;
    }
    
    @Override
    public void setLanguage(String lang) {
        this.scriptLanguage = lang;
    }
    
    @Override
    public List<ScriptContainer> getScripts() {
        return this.scripts;
    }
    
    @Override
    public String noticeString() {
        BlockPos pos = this.npc.getPosition();
        return MoreObjects.toStringHelper((Object)this.npc).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
    }
    
    @Override
    public Map<Long, String> getConsoleText() {
        Map<Long, String> map = new TreeMap<Long, String>();
        int tab = 0;
        for (ScriptContainer script : this.getScripts()) {
            ++tab;
            for (Map.Entry<Long, String> entry : script.console.entrySet()) {
                map.put(entry.getKey(), " tab " + tab + ":\n" + entry.getValue());
            }
        }
        return map;
    }
    
    @Override
    public void clearConsole() {
        for (ScriptContainer script : this.getScripts()) {
            script.console.clear();
        }
    }
}
