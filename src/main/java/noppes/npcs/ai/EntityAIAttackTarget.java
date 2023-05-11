package noppes.npcs.ai;

import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import noppes.npcs.constants.AiMutex;
import net.minecraft.pathfinding.Path;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.world.World;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAttackTarget extends EntityAIBase
{
    private World world;
    private EntityNPCInterface npc;
    private EntityLivingBase entityTarget;
    private int attackTick;
    private Path entityPathEntity;
    private int delayCounter;
    private boolean navOverride;
    
    public EntityAIAttackTarget(EntityNPCInterface par1EntityLiving) {
        this.navOverride = false;
        this.attackTick = 0;
        this.npc = par1EntityLiving;
        this.world = par1EntityLiving.world;
        this.setMutexBits(this.navOverride ? AiMutex.PATHING : (AiMutex.LOOK + AiMutex.PASSIVE));
    }
    
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.npc.getAttackTarget();
        if (entitylivingbase == null || !entitylivingbase.isEntityAlive()) {
            return false;
        }
        int melee = this.npc.stats.ranged.getMeleeRange();
        if (this.npc.inventory.getProjectile() != null && (melee <= 0 || !this.npc.isInRange((Entity)entitylivingbase, melee))) {
            return false;
        }
        this.entityTarget = entitylivingbase;
        this.entityPathEntity = this.npc.getNavigator().getPathToEntityLiving((Entity)entitylivingbase);
        return this.entityPathEntity != null;
    }
    
    public boolean shouldContinueExecuting() {
        this.entityTarget = this.npc.getAttackTarget();
        if (this.entityTarget == null) {
            this.entityTarget = this.npc.getRevengeTarget();
        }
        if (this.entityTarget == null || !this.entityTarget.isEntityAlive()) {
            return false;
        }
        if (!this.npc.isInRange((Entity)this.entityTarget, this.npc.stats.aggroRange)) {
            return false;
        }
        int melee = this.npc.stats.ranged.getMeleeRange();
        return (melee <= 0 || this.npc.isInRange((Entity)this.entityTarget, melee)) && this.npc.isWithinHomeDistanceFromPosition(new BlockPos((Entity)this.entityTarget));
    }
    
    public void startExecuting() {
        if (!this.navOverride) {
            this.npc.getNavigator().setPath(this.entityPathEntity, 1.3);
        }
        this.delayCounter = 0;
    }
    
    public void resetTask() {
        this.entityPathEntity = null;
        this.entityTarget = null;
        this.npc.getNavigator().clearPath();
    }
    
    public void updateTask() {
        this.npc.getLookHelper().setLookPositionWithEntity((Entity)this.entityTarget, 30.0f, 30.0f);
        if (!this.navOverride && --this.delayCounter <= 0) {
            this.delayCounter = 4 + this.npc.getRNG().nextInt(7);
            this.npc.getNavigator().tryMoveToEntityLiving((Entity)this.entityTarget, 1.2999999523162842);
        }
        this.attackTick = Math.max(this.attackTick - 1, 0);
        double y = this.entityTarget.posY;
        if (this.entityTarget.getEntityBoundingBox() != null) {
            y = this.entityTarget.getEntityBoundingBox().minY;
        }
        double distance = this.npc.getDistanceSq(this.entityTarget.posX, y, this.entityTarget.posZ);
        double range = this.npc.stats.melee.getRange() * this.npc.stats.melee.getRange() + this.entityTarget.width;
        double minRange = this.npc.width * 2.0f * this.npc.width * 2.0f + this.entityTarget.width;
        if (minRange > range) {
            range = minRange;
        }
        if (distance <= range && (this.npc.canSee((Entity)this.entityTarget) || distance < minRange) && this.attackTick <= 0) {
            this.attackTick = this.npc.stats.melee.getDelay();
            this.npc.swingArm(EnumHand.MAIN_HAND);
            this.npc.attackEntityAsMob((Entity)this.entityTarget);
        }
    }
    
    public void navOverride(boolean nav) {
        this.navOverride = nav;
        this.setMutexBits(this.navOverride ? AiMutex.PATHING : (AiMutex.LOOK + AiMutex.PASSIVE));
    }
}
