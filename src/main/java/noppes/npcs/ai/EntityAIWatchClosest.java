package noppes.npcs.ai;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.constants.AiMutex;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIWatchClosest extends EntityAIBase
{
    private EntityNPCInterface npc;
    protected Entity closestEntity;
    private float maxDistance;
    private int lookTime;
    private float chance;
    private Class watchedClass;
    
    public EntityAIWatchClosest(EntityNPCInterface par1EntityLiving, Class par2Class, float par3) {
        this.npc = par1EntityLiving;
        this.watchedClass = par2Class;
        this.maxDistance = par3;
        this.chance = 0.002f;
        this.setMutexBits((int)AiMutex.LOOK);
    }
    
    public boolean shouldExecute() {
        if (this.npc.getRNG().nextFloat() >= this.chance || this.npc.isInteracting()) {
            return false;
        }
        if (this.npc.getAttackTarget() != null) {
            this.closestEntity = (Entity)this.npc.getAttackTarget();
        }
        if (this.watchedClass == EntityPlayer.class) {
            this.closestEntity = (Entity)this.npc.world.getClosestPlayerToEntity((Entity)this.npc, (double)this.maxDistance);
        }
        else {
            this.closestEntity = this.npc.world.findNearestEntityWithinAABB(this.watchedClass, this.npc.getEntityBoundingBox().grow((double)this.maxDistance, 3.0, (double)this.maxDistance), (Entity)this.npc);
            if (this.closestEntity != null) {
                return this.npc.canSee(this.closestEntity);
            }
        }
        return this.closestEntity != null;
    }
    
    public boolean shouldContinueExecuting() {
        return !this.npc.isInteracting() && !this.npc.isAttacking() && this.closestEntity.isEntityAlive() && this.npc.isEntityAlive() && this.npc.isInRange(this.closestEntity, this.maxDistance) && this.lookTime > 0;
    }
    
    public void startExecuting() {
        this.lookTime = 60 + this.npc.getRNG().nextInt(60);
    }
    
    public void resetTask() {
        this.closestEntity = null;
    }
    
    public void updateTask() {
        this.npc.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0f, (float)this.npc.getVerticalFaceSpeed());
        --this.lookTime;
    }
}
