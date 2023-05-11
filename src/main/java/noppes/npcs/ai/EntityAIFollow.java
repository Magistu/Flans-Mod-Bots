package noppes.npcs.ai;

import net.minecraft.entity.Entity;
import noppes.npcs.constants.AiMutex;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIFollow extends EntityAIBase
{
    private EntityNPCInterface npc;
    private EntityLivingBase owner;
    public int updateTick;
    
    public EntityAIFollow(EntityNPCInterface npc) {
        this.updateTick = 0;
        this.npc = npc;
        this.setMutexBits(AiMutex.PASSIVE + AiMutex.LOOK);
    }
    
    public boolean shouldExecute() {
        return this.canExcute() && !this.npc.isInRange((Entity)this.owner, this.npc.followRange());
    }
    
    public boolean canExcute() {
        return this.npc.isEntityAlive() && this.npc.isFollower() && !this.npc.isAttacking() && (this.owner = this.npc.getOwner()) != null && this.npc.ais.animationType != 1;
    }
    
    public void startExecuting() {
        this.updateTick = 10;
    }
    
    public boolean shouldContinueExecuting() {
        return !this.npc.getNavigator().noPath() && !this.npc.isInRange((Entity)this.owner, 2.0) && this.canExcute();
    }
    
    public void resetTask() {
        this.owner = null;
        this.npc.getNavigator().clearPath();
    }
    
    public void updateTask() {
        ++this.updateTick;
        if (this.updateTick < 10) {
            return;
        }
        this.updateTick = 0;
        this.npc.getLookHelper().setLookPositionWithEntity((Entity)this.owner, 10.0f, (float)this.npc.getVerticalFaceSpeed());
        double distance = this.npc.getDistance((Entity)this.owner);
        double speed = 1.0 + distance / 150.0;
        if (speed > 3.0) {
            speed = 3.0;
        }
        if (this.owner.isSprinting()) {
            speed += 0.5;
        }
        if (this.npc.getNavigator().tryMoveToEntityLiving((Entity)this.owner, speed) || this.npc.isInRange((Entity)this.owner, 16.0)) {
            return;
        }
        this.npc.tpTo(this.owner);
    }
}
