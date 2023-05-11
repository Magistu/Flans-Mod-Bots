package com.magistumod.entity.ai;

import java.util.List;

import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.driveables.EntityVehicle;
import com.google.common.base.Predicate;
import com.magistumod.ConfigHandler;
import com.magistumod.Main;
import com.magistumod.common.ServerHelper;
import com.magistumod.entity.FlansModShooter;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIGetInDriveable extends EntityAIBase
{
    private final EntityLiving entityHost;
    private final double entityMoveSpeed;
	private float searchRadius;

    public EntityAIGetInDriveable(EntityLiving host, double movespeed, float searchDistanceIn)
    {
    	searchRadius = searchDistanceIn;
        entityHost = host;
        entityMoveSpeed = movespeed;
        setMutexBits(4);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if(entityHost.isRiding() || ((FlansModShooter)entityHost).debugTimer > 0)
        {
            return false;
        }
        
        return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {}

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
    	AxisAlignedBB area = entityHost.getEntityBoundingBox().grow(searchRadius);
    	List<EntityDriveable> list = entityHost.world.<EntityDriveable>getEntitiesWithinAABB(EntityDriveable.class, area, (Predicate)null);
		for(int i = 0; i < list.size(); ++i)
		{
			if(list.get(i) != null && list.get(i) instanceof EntityVehicle)
			{
    			for (int j = 0; j < list.get(i).getSeats().length; ++j)
    			{
    				if(list.get(i).getSeats()[j] == null)
    				{
    					continue;
    				}
    				
	    			if(list.get(i).getSeats()[j].getPassengers().isEmpty() && list.get(i).getSeats()[0] != null && !ServerHelper.canDamage(entityHost, list.get(i).getSeats()[0].getControllingPassenger(), false))
	    			{
	    				EntitySeat seat = list.get(i).getSeats()[j];
	    				if(entityHost.getDistanceSq(seat) < 16)
	    				{
		        			entityHost.getNavigator().clearPath();
			        		entityHost.startRiding(seat);
			        		((FlansModShooter)entityHost).addControlTask(seat, j, ConfigHandler.VEHICLE_FIRING_RANGE);
	    				}
		        		else
		        		{
		        			entityHost.getLookHelper().setLookPositionWithEntity(seat, 30.0F, 30.0F);
		        			entityHost.getNavigator().tryMoveToXYZ(seat.posX, seat.posY, seat.posZ, entityMoveSpeed);
		        		}
		        		
		        		break;
	    			}
    			}
			}
		}
    }
}

