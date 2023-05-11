package noppes.npcs.entity.data;

import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.EventHooks;
import noppes.npcs.entity.EntityNPCInterface;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.CustomNPCsException;
import java.util.HashMap;
import java.util.Map;
import noppes.npcs.api.ITimers;

public class DataTimers implements ITimers
{
    private Object parent;
    private Map<Integer, Timer> timers;
    
    public DataTimers(Object parent) {
        this.timers = new HashMap<Integer, Timer>();
        this.parent = parent;
    }
    
    @Override
    public void start(int id, int ticks, boolean repeat) {
        if (this.timers.containsKey(id)) {
            throw new CustomNPCsException("There is already a timer with id: " + id, new Object[0]);
        }
        this.timers.put(id, new Timer(id, ticks, repeat));
    }
    
    @Override
    public void forceStart(int id, int ticks, boolean repeat) {
        this.timers.put(id, new Timer(id, ticks, repeat));
    }
    
    @Override
    public boolean has(int id) {
        return this.timers.containsKey(id);
    }
    
    @Override
    public boolean stop(int id) {
        return this.timers.remove(id) != null;
    }
    
    @Override
    public void reset(int id) {
        Timer timer = this.timers.get(id);
        if (timer == null) {
            throw new CustomNPCsException("There is no timer with id: " + id, new Object[0]);
        }
        timer.ticks = 0;
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Timer timer : this.timers.values()) {
            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("ID", timer.id);
            c.setInteger("TimerTicks", timer.id);
            c.setBoolean("Repeat", timer.repeat);
            c.setInteger("Ticks", timer.ticks);
            list.appendTag((NBTBase)c);
        }
        compound.setTag("NpcsTimers", (NBTBase)list);
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        Map<Integer, Timer> timers = new HashMap<Integer, Timer>();
        NBTTagList list = compound.getTagList("NpcsTimers", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            Timer t = new Timer(c.getInteger("ID"), c.getInteger("TimerTicks"), c.getBoolean("Repeat"));
            t.ticks = c.getInteger("Ticks");
            timers.put(t.id, t);
        }
        this.timers = timers;
    }
    
    public void update() {
        for (Timer timer : new ArrayList<Timer>(this.timers.values())) {
            timer.update();
        }
    }
    
    @Override
    public void clear() {
        this.timers = new HashMap<Integer, Timer>();
    }
    
    class Timer
    {
        public int id;
        private boolean repeat;
        private int timerTicks;
        private int ticks;
        
        public Timer(int id, int ticks, boolean repeat) {
            this.ticks = 0;
            this.id = id;
            this.repeat = repeat;
            this.timerTicks = ticks;
            this.ticks = ticks;
        }
        
        public void update() {
            if (this.ticks-- > 0) {
                return;
            }
            if (this.repeat) {
                this.ticks = this.timerTicks;
            }
            else {
                DataTimers.this.stop(this.id);
            }
            Object ob = DataTimers.this.parent;
            if (ob instanceof EntityNPCInterface) {
                EventHooks.onNPCTimer((EntityNPCInterface)ob, this.id);
            }
            else if (ob instanceof PlayerData) {
                EventHooks.onPlayerTimer((PlayerData)ob, this.id);
            }
            else {
                EventHooks.onScriptBlockTimer((IScriptBlockHandler)ob, this.id);
            }
        }
    }
}
