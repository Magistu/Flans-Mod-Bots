package noppes.npcs.controllers.data;

import net.minecraft.entity.Entity;
import noppes.npcs.api.NpcAPI;
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
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;
import noppes.npcs.api.entity.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.ScriptContainer;
import java.util.List;
import noppes.npcs.controllers.IScriptHandler;

public class PlayerScriptData implements IScriptHandler
{
    private List<ScriptContainer> scripts;
    private String scriptLanguage;
    private EntityPlayer player;
    private IPlayer playerAPI;
    private long lastPlayerUpdate;
    public long lastInited;
    public boolean hadInteract;
    private boolean enabled;
    private static Map<Long, String> console;
    private static List<Integer> errored;
    
    public PlayerScriptData(EntityPlayer player) {
        this.scripts = new ArrayList<ScriptContainer>();
        this.scriptLanguage = "ECMAScript";
        this.lastPlayerUpdate = 0L;
        this.lastInited = -1L;
        this.hadInteract = true;
        this.enabled = false;
        this.player = player;
    }
    
    public void clear() {
        PlayerScriptData.console = new TreeMap<Long, String>();
        PlayerScriptData.errored = new ArrayList<Integer>();
        this.scripts = new ArrayList<ScriptContainer>();
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        this.scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
        PlayerScriptData.console = NBTTags.GetLongStringMap(compound.getTagList("ScriptConsole", 10));
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Scripts", (NBTBase)NBTTags.NBTScript(this.scripts));
        compound.setString("ScriptLanguage", this.scriptLanguage);
        compound.setBoolean("ScriptEnabled", this.enabled);
        compound.setTag("ScriptConsole", (NBTBase)NBTTags.NBTLongStringMap(PlayerScriptData.console));
        return compound;
    }
    
    @Override
    public void runScript(EnumScriptType type, Event event) {
        if (!this.isEnabled()) {
            return;
        }
        if (ScriptController.Instance.lastLoaded > this.lastInited || ScriptController.Instance.lastPlayerUpdate > this.lastPlayerUpdate) {
            this.lastInited = ScriptController.Instance.lastLoaded;
            PlayerScriptData.errored.clear();
            if (this.player != null) {
                this.scripts.clear();
                for (ScriptContainer script : ScriptController.Instance.playerScripts.scripts) {
                    ScriptContainer s = new ScriptContainer(this);
                    s.readFromNBT(script.writeToNBT(new NBTTagCompound()));
                    this.scripts.add(s);
                }
            }
            this.lastPlayerUpdate = ScriptController.Instance.lastPlayerUpdate;
            if (type != EnumScriptType.INIT) {
                EventHooks.onPlayerInit(this);
            }
        }
        for (int i = 0; i < this.scripts.size(); ++i) {
            ScriptContainer script = this.scripts.get(i);
            if (!PlayerScriptData.errored.contains(i)) {
                script.run(type, event);
                if (script.errored) {
                    PlayerScriptData.errored.add(i);
                }
                for (Map.Entry<Long, String> entry : script.console.entrySet()) {
                    if (!PlayerScriptData.console.containsKey(entry.getKey())) {
                        PlayerScriptData.console.put(entry.getKey(), " tab " + (i + 1) + ":\n" + entry.getValue());
                    }
                }
                script.console.clear();
            }
        }
    }
    
    public boolean isEnabled() {
        return ScriptController.Instance.playerScripts.enabled && ScriptController.HasStart && (this.player == null || !this.player.world.isRemote);
    }
    
    @Override
    public boolean isClient() {
        return !this.player.isServerWorld();
    }
    
    @Override
    public boolean getEnabled() {
        return ScriptController.Instance.playerScripts.enabled;
    }
    
    @Override
    public void setEnabled(boolean bo) {
        this.enabled = bo;
    }
    
    @Override
    public String getLanguage() {
        return ScriptController.Instance.playerScripts.scriptLanguage;
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
        if (this.player == null) {
            return "Global script";
        }
        BlockPos pos = this.player.getPosition();
        return MoreObjects.toStringHelper((Object)this.player).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
    }
    
    public IPlayer getPlayer() {
        if (this.playerAPI == null) {
            this.playerAPI = (IPlayer)NpcAPI.Instance().getIEntity((Entity)this.player);
        }
        return this.playerAPI;
    }
    
    @Override
    public Map<Long, String> getConsoleText() {
        return PlayerScriptData.console;
    }
    
    @Override
    public void clearConsole() {
        PlayerScriptData.console.clear();
    }
    
    static {
        PlayerScriptData.console = new TreeMap<Long, String>();
        PlayerScriptData.errored = new ArrayList<Integer>();
    }
}
