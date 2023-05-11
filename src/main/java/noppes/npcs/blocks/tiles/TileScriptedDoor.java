package noppes.npcs.blocks.tiles;

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
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.wrapper.BlockScriptedDoorWrapper;
import java.util.ArrayList;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.controllers.ScriptContainer;
import java.util.List;
import noppes.npcs.controllers.IScriptBlockHandler;
import net.minecraft.util.ITickable;

public class TileScriptedDoor extends TileDoor implements ITickable, IScriptBlockHandler
{
    public List<ScriptContainer> scripts;
    public boolean shouldRefreshData;
    public String scriptLanguage;
    public boolean enabled;
    private IBlock blockDummy;
    public DataTimers timers;
    public long lastInited;
    private short ticksExisted;
    public int newPower;
    public int prevPower;
    public float blockHardness;
    public float blockResistance;
    
    public TileScriptedDoor() {
        this.scripts = new ArrayList<ScriptContainer>();
        this.shouldRefreshData = false;
        this.scriptLanguage = "ECMAScript";
        this.enabled = false;
        this.blockDummy = null;
        this.timers = new DataTimers(this);
        this.lastInited = -1L;
        this.ticksExisted = 0;
        this.newPower = 0;
        this.prevPower = 0;
        this.blockHardness = 5.0f;
        this.blockResistance = 10.0f;
    }
    
    public IBlock getBlock() {
        if (this.blockDummy == null) {
            this.blockDummy = new BlockScriptedDoorWrapper(this.getWorld(), this.getBlockType(), this.getPos());
        }
        return this.blockDummy;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.setNBT(compound);
        this.timers.readFromNBT(compound);
    }
    
    public void setNBT(NBTTagCompound compound) {
        this.scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
        this.prevPower = compound.getInteger("BlockPrevPower");
        if (compound.hasKey("BlockHardness")) {
            this.blockHardness = compound.getFloat("BlockHardness");
            this.blockResistance = compound.getFloat("BlockResistance");
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        this.getNBT(compound);
        this.timers.writeToNBT(compound);
        return super.writeToNBT(compound);
    }
    
    public NBTTagCompound getNBT(NBTTagCompound compound) {
        compound.setTag("Scripts", (NBTBase)NBTTags.NBTScript(this.scripts));
        compound.setString("ScriptLanguage", this.scriptLanguage);
        compound.setBoolean("ScriptEnabled", this.enabled);
        compound.setInteger("BlockPrevPower", this.prevPower);
        compound.setFloat("BlockHardness", this.blockHardness);
        compound.setFloat("BlockResistance", this.blockResistance);
        return compound;
    }
    
    public void runScript(EnumScriptType type, Event event) {
        if (!this.isEnabled()) {
            return;
        }
        if (ScriptController.Instance.lastLoaded > this.lastInited) {
            this.lastInited = ScriptController.Instance.lastLoaded;
            if (type != EnumScriptType.INIT) {
                EventHooks.onScriptBlockInit(this);
            }
        }
        for (ScriptContainer script : this.scripts) {
            script.run(type, event);
        }
    }
    
    private boolean isEnabled() {
        return this.enabled && ScriptController.HasStart && !this.world.isRemote;
    }
    
    @Override
    public void update() {
        super.update();
        ++this.ticksExisted;
        if (this.prevPower != this.newPower) {
            EventHooks.onScriptBlockRedstonePower(this, this.prevPower, this.newPower);
            this.prevPower = this.newPower;
        }
        this.timers.update();
        if (this.ticksExisted >= 10) {
            EventHooks.onScriptBlockUpdate(this);
            this.ticksExisted = 0;
        }
    }
    
    public boolean isClient() {
        return this.getWorld().isRemote;
    }
    
    public boolean getEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(boolean bo) {
        this.enabled = bo;
    }
    
    public String getLanguage() {
        return this.scriptLanguage;
    }
    
    public void setLanguage(String lang) {
        this.scriptLanguage = lang;
    }
    
    public List<ScriptContainer> getScripts() {
        return this.scripts;
    }
    
    public String noticeString() {
        BlockPos pos = this.getPos();
        return MoreObjects.toStringHelper((Object)this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
    }
    
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
    
    public void clearConsole() {
        for (ScriptContainer script : this.getScripts()) {
            script.console.clear();
        }
    }
}
