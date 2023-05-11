package com.magistumod.entity.ai;

import com.flansmod.common.driveables.EntitySeat;
import com.magistumod.entity.IShootingMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityAIFirefight extends EntityAIBase
{
    /** The entity the AI instance has been applied to */
    private final EntityLiving entityHost;
    /** The entity (as a RangedAttackMob) the AI instance has been applied to. */
    private final IShootingMob rangedAttackEntityHost;
    private EntityLivingBase attackTarget;
    /**
     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    private int shootDelay;
    private int reactionTime;
    private final double entityMoveSpeed;
    private int seeTime;
    private final int attackIntervalMin;
    /** The maximum time the AI has to wait before peforming another ranged attack. */
    private final int maxRangedAttackTime;
    private final float attackRadius;
    private final float maxAttackDistance;

    public EntityAIFirefight(IShootingMob flansModShooter, double movespeed, int maxAttackTime, float maxAttackDistanceIn)
    {
        this(flansModShooter, movespeed, maxAttackTime, maxAttackTime, maxAttackDistanceIn);
    }

    public EntityAIFirefight(IShootingMob attacker, double movespeed, int p_i1650_4_, int maxAttackTime, float maxAttackDistanceIn)
    {
        this.shootDelay = -1;

        if (!(attacker instanceof EntityLivingBase))
        {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        else
        {
            this.rangedAttackEntityHost = attacker;
            this.entityHost = (EntityLiving)attacker;
            this.entityMoveSpeed = movespeed;
            this.attackIntervalMin = p_i1650_4_;
            this.maxRangedAttackTime = maxAttackTime;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
            this.setMutexBits(3);
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();

        if (entitylivingbase == null || (entityHost.isRiding() && entityHost.getRidingEntity() instanceof EntitySeat && (((EntitySeat)entityHost.getRidingEntity()).seatInfo.gunType != null || ((EntitySeat)entityHost.getRidingEntity()).seatInfo.id == 0)))
        {
            return false;
        }
        else
        {
            this.attackTarget = entitylivingbase;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.attackTarget = null;
        this.seeTime = 0;
        this.shootDelay = -1;
        this.reactionTime = -1;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
        boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
        
        if (this.reactionTime == -1)
        {
        	reactionTime = MathHelper.floor(Math.random() * (MathHelper.sqrt(d0) / this.attackRadius * (this.maxRangedAttackTime - this.attackIntervalMin) + 1) + (float)this.attackIntervalMin);
        }
        
        if (this.shootDelay == 0 && !flag && entityHost.onGround && canEntityBeSeenByJump(this.attackTarget) && Math.random() > 0.98) {
        	entityHost.motionY = 0.4D;
        }

        if (flag)
        {
            ++this.seeTime;
        }
        else if (seeTime > 0)
        {
            --this.seeTime;
        }
        
        if (attackTarget.isRiding() && attackTarget.getRidingEntity() instanceof EntitySeat && ((EntitySeat)attackTarget.getRidingEntity()).driveable != null && !entityHost.getEntitySenses().canSee(((EntitySeat)attackTarget.getRidingEntity()).driveable))
        {
        	entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }
        else if (d0 < maxAttackDistance && this.seeTime >= 20)
        {
            entityHost.getNavigator().clearPath();
        }
        else
        {
            entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);

        if (this.shootDelay > 0) {
        	this.shootDelay--;
        }
        
        if (this.shootDelay == 0 && this.seeTime >= reactionTime)
        {
            if (!flag)
            {
                return;
            }

            float f = MathHelper.sqrt(d0) / this.attackRadius;
            float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
            this.shootDelay = this.rangedAttackEntityHost.shootEntity(this.attackTarget, lvt_5_1_);
            this.reactionTime = -1;
        }
        else if (this.shootDelay < 0)
        {
            this.shootDelay = reactionTime;
        }
    }
    
    public boolean canEntityBeSeenByJump(Entity entityIn)
    {
        return entityHost.world.rayTraceBlocks(new Vec3d(entityHost.posX, entityHost.posY + (double)entityHost.getEyeHeight() + 1, entityHost.posZ), new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ), false, true, false) == null;
    }
}
