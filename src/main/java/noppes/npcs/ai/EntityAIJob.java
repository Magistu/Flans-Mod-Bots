package noppes.npcs.ai;

import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIJob extends EntityAIBase
{
    private EntityNPCInterface npc;
    
    public EntityAIJob(EntityNPCInterface npc) {
        this.npc = npc;
    }
    
    public boolean shouldExecute() {
        return !this.npc.isKilled() && this.npc.jobInterface != null && this.npc.jobInterface.aiShouldExecute();
    }
    
    public void startExecuting() {
        this.npc.jobInterface.aiStartExecuting();
    }
    
    public boolean shouldContinueExecuting() {
        return !this.npc.isKilled() && this.npc.jobInterface != null && this.npc.jobInterface.aiContinueExecute();
    }
    
    public void updateTask() {
        if (this.npc.jobInterface != null) {
            this.npc.jobInterface.aiUpdateTask();
        }
    }
    
    public void resetTask() {
        if (this.npc.jobInterface != null) {
            this.npc.jobInterface.resetTask();
        }
    }
    
    public int getMutexBits() {
        if (this.npc.jobInterface == null) {
            return super.getMutexBits();
        }
        return this.npc.jobInterface.getMutexBits();
    }
}
