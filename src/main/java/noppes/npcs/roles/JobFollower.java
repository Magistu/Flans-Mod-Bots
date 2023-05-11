package noppes.npcs.roles;

import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.CustomNpcs;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.data.role.IJobFollower;

public class JobFollower extends JobInterface implements IJobFollower
{
    public EntityNPCInterface following;
    private int ticks;
    private int range;
    public String name;
    
    public JobFollower(EntityNPCInterface npc) {
        super(npc);
        this.following = null;
        this.ticks = 40;
        this.range = 20;
        this.name = "";
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("FollowingEntityName", this.name);
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.name = compound.getString("FollowingEntityName");
    }
    
    @Override
    public boolean aiShouldExecute() {
        if (this.npc.isAttacking()) {
            return false;
        }
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.ticks = 10;
        this.following = null;
        List<EntityNPCInterface> list = (List<EntityNPCInterface>)this.npc.world.getEntitiesWithinAABB((Class)EntityNPCInterface.class, this.npc.getEntityBoundingBox().grow((double)this.getRange(), (double)this.getRange(), (double)this.getRange()));
        for (EntityNPCInterface entity : list) {
            if (entity != this.npc) {
                if (entity.isKilled()) {
                    continue;
                }
                if (entity.display.getName().equalsIgnoreCase(this.name)) {
                    this.following = entity;
                    break;
                }
                continue;
            }
        }
        return false;
    }
    
    private int getRange() {
        if (this.range > CustomNpcs.NpcNavRange) {
            return CustomNpcs.NpcNavRange;
        }
        return this.range;
    }
    
    @Override
    public boolean isFollowing() {
        return this.following != null;
    }
    
    @Override
    public void reset() {
    }
    
    @Override
    public void resetTask() {
        this.following = null;
    }
    
    public boolean hasOwner() {
        return !this.name.isEmpty();
    }
    
    @Override
    public String getFollowing() {
        return this.name;
    }
    
    @Override
    public void setFollowing(String name) {
        this.name = name;
    }
    
    @Override
    public ICustomNpc getFollowingNpc() {
        if (this.following == null) {
            return null;
        }
        return this.following.wrappedNPC;
    }
}
