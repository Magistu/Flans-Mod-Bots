package com.magistumod.entity.ai;

import com.magistumod.common.storage.StorageHelper;
import com.magistumod.entity.EnumCoalitions;
import com.magistumod.entity.FlansModShooter;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIHurtByEnemyTarget extends EntityAITarget {

    private final boolean entityCallsForHelp;
    /** Store the previous revengeTimer value */
    private int revengeTimerOld;
    private final Class<?>[] excludedReinforcementTypes;

    public EntityAIHurtByEnemyTarget(EntityCreature creatureIn, boolean entityCallsForHelpIn, Class<?>... excludedReinforcementTypes)
    {
        super(creatureIn, true);
        this.entityCallsForHelp = entityCallsForHelpIn;
        this.excludedReinforcementTypes = excludedReinforcementTypes;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        int i = this.taskOwner.getRevengeTimer();
        EntityLivingBase entitytargetbase = this.taskOwner.getRevengeTarget();
        return i != this.revengeTimerOld && entitytargetbase != null && this.isSuitableTarget(entitytargetbase, false);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        EntityLivingBase target = this.taskOwner.getRevengeTarget();
        EnumCoalitions targetCoalition = null;
        EnumCoalitions taskOwnerCoalition = ((FlansModShooter)this.taskOwner).getCoalition();
        
        if (target instanceof FlansModShooter) 
        {
			targetCoalition = ((FlansModShooter)target).getCoalition();
		}

        if (target instanceof EntityPlayer && StorageHelper.getCoalition(((EntityPlayer)target).getUniqueID()) != null) {
			
			targetCoalition = EnumCoalitions.getCoalition(StorageHelper.getCoalition(((EntityPlayer)target).getUniqueID()));
		}
       
        
        if (targetCoalition != taskOwnerCoalition) 
        {
        	this.taskOwner.setAttackTarget(this.taskOwner.getRevengeTarget());
        	
		    this.target = this.taskOwner.getAttackTarget();
		    this.revengeTimerOld = this.taskOwner.getRevengeTimer();
		    this.unseenMemoryTicks = 100;
		
		    if (this.entityCallsForHelp)
		    {
		        this.alertOthers();
		    }
		    
		    super.startExecuting();
        }
    }

    protected void alertOthers()
    {
        double d0 = this.getTargetDistance();

        for (EntityCreature entitycreature : this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), (new AxisAlignedBB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D)).grow(d0, 10.0D, d0)))
        {
            if (this.taskOwner != entitycreature && entitycreature.getAttackTarget() == null && (!(this.taskOwner instanceof EntityTameable) || ((EntityTameable)this.taskOwner).getOwner() == ((EntityTameable)entitycreature).getOwner()) && !entitycreature.isOnSameTeam(this.taskOwner.getRevengeTarget()))
            {
                boolean flag = false;

                for (Class<?> oclass : this.excludedReinforcementTypes)
                {
                    if (entitycreature.getClass() == oclass)
                    {
                        flag = true;
                        break;
                    }
                }

                if (!flag)
                {
                    this.setEntityAttackTarget(entitycreature, this.taskOwner.getRevengeTarget());
                }
            }
        }
    }
    
    protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn)
    {
        creatureIn.setAttackTarget(entityLivingBaseIn);
    }
}

