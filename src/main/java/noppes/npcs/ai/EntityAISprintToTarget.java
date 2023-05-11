package noppes.npcs.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISprintToTarget extends EntityAIBase
{
    private EntityNPCInterface npc;
    
    public EntityAISprintToTarget(EntityNPCInterface par1EntityLiving) {
        this.npc = par1EntityLiving;
        this.setMutexBits((int)AiMutex.PASSIVE);
    }
    
    public boolean shouldExecute() {
        EntityLivingBase runTarget = this.npc.getAttackTarget();
        if (runTarget == null || this.npc.getNavigator().noPath()) {
            return false;
        }
        switch (this.npc.ais.onAttack) {
            case 0: {
                return !this.npc.isInRange((Entity)runTarget, 8.0) && this.npc.onGround;
            }
            case 2: {
                return this.npc.isInRange((Entity)runTarget, 7.0) && this.npc.onGround;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean shouldContinueExecuting() {
        return this.npc.isEntityAlive() && this.npc.onGround && this.npc.hurtTime <= 0 && this.npc.motionX != 0.0 && this.npc.motionZ != 0.0;
    }
    
    public void startExecuting() {
        this.npc.setSprinting(true);
    }
    
    public void resetTask() {
        this.npc.setSprinting(false);
    }
}
