package noppes.npcs.roles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.data.INPCRole;

public abstract class RoleInterface implements INPCRole
{
    public EntityNPCInterface npc;
    public HashMap<String, String> dataString;
    
    public RoleInterface(EntityNPCInterface npc) {
        this.dataString = new HashMap<String, String>();
        this.npc = npc;
    }
    
    public abstract NBTTagCompound writeToNBT(NBTTagCompound p0);
    
    public abstract void readFromNBT(NBTTagCompound p0);
    
    public abstract void interact(EntityPlayer p0);
    
    public void killed() {
    }
    
    public void delete() {
    }
    
    public boolean aiShouldExecute() {
        return false;
    }
    
    public boolean aiContinueExecute() {
        return false;
    }
    
    public void aiStartExecuting() {
    }
    
    public void aiUpdateTask() {
    }
    
    public boolean defendOwner() {
        return false;
    }
    
    public boolean isFollowing() {
        return false;
    }
    
    public void clientUpdate() {
    }
    
    @Override
    public int getType() {
        return this.npc.advanced.role;
    }
}
