package noppes.npcs.controllers.data;

import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.CustomNpcs;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.constants.EnumScriptType;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import noppes.npcs.controllers.ScriptContainer;
import java.util.List;
import noppes.npcs.controllers.IScriptHandler;

public class ForgeScriptData implements IScriptHandler
{
    private List<ScriptContainer> scripts;
    private String scriptLanguage;
    public long lastInited;
    public boolean hadInteract;
    private boolean enabled;
    
    public ForgeScriptData() {
        this.scripts = new ArrayList<ScriptContainer>();
        this.scriptLanguage = "ECMAScript";
        this.lastInited = -1L;
        this.hadInteract = true;
        this.enabled = false;
    }
    
    public void clear() {
        this.scripts = new ArrayList<ScriptContainer>();
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
    }
    
    public void runScript(String type, Event event) {
        if (!this.isEnabled()) {
            return;
        }
        CustomNpcs.Server.addScheduledTask(() -> {
            if (ScriptController.Instance.lastLoaded > this.lastInited) {
                this.lastInited = ScriptController.Instance.lastLoaded;
                if (!type.equals("init")) {
                    EventHooks.onForgeInit(this);
                }
            }
            Iterator<ScriptContainer> iterator = this.scripts.iterator();
            while (iterator.hasNext()) {
            	ScriptContainer script = iterator.next();
                script.run(type, event);
            }
        });
    }
    
    public boolean isEnabled() {
        return this.enabled && ScriptController.HasStart && this.scripts.size() > 0;
    }
    
    @Override
    public boolean isClient() {
        return false;
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
        return "ForgeScript";
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
