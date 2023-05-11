package com.magistumod.entity.ai;

import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityAIFollow extends EntityAIBase
{
	
	private EntityPlayer commander;
	private EntityLiving entityHost;
	private float followRadius;
	private float followRadiusSq = followRadius * followRadius;
	private float stopDistance;
	private float stopDistanceSq = stopDistance * stopDistance;
	private double entityMoveSpeed;
	private boolean shouldNavigate;
	private Random rand = new Random();
	private double randPosX = 0;
	private double randPosZ = 0;
	private Vec3d vec;
	private AxisAlignedBB area;
	private int formationUpdateDelay = 80;
	
	public EntityAIFollow(EntityLiving follower, EntityPlayer commander, float followRadius, float stopDistance, double movespeed)
	{
		this.commander = commander;
		this.entityHost = follower;
		this.followRadius = followRadius;
		this.entityMoveSpeed = movespeed;
		this.rand = new Random();
	}

	@Override
	public boolean shouldExecute() 
	{
		if (commander == null)
        {
            return false;
        }
		return true;
	}
	
	@Override
	public void updateTask()
	{
		double d0 = this.entityHost.getDistanceSq(this.commander.posX + randPosX, this.commander.posY, this.commander.posZ + randPosZ);
		
		if (formationUpdateDelay-- == 0)
		{
			area = this.entityHost.getEntityBoundingBox().grow(0.5);
			List<Entity> list = this.commander.world.<Entity>getEntitiesWithinAABB(Entity.class, area, (Predicate)null);
			if (list.size() > 1)
			{
				generateRandomPosInFormation();
			}
			
			formationUpdateDelay = 80;
		}
		
		
		if (d0 > followRadiusSq)
		{
			if (d0 > stopDistance)
			{
				if (!this.shouldNavigate)
				{
					generateRandomPos();
				}
				this.shouldNavigate = true;
			}
			else 
			{
				this.shouldNavigate = false;
			}
		}
		
		if (shouldNavigate)
		{
			this.entityHost.getNavigator().tryMoveToXYZ(this.commander.posX + randPosX, this.commander.posY, this.commander.posZ + randPosZ, this.entityMoveSpeed);
		}
		else
		{
			this.entityHost.getNavigator().clearPath();
		}
	}
	
	void generateRandomPosInFormation()
	{
		generateRandomPos();
		
		vec = new Vec3d(this.commander.posX + randPosX, this.commander.posY, this.commander.posZ + randPosZ);
		area = new AxisAlignedBB(new BlockPos(vec));
		area = area.grow(0.5);
		List<Entity> list = this.commander.world.<Entity>getEntitiesWithinAABB(Entity.class, area, (Predicate)null);
		
		if (!list.isEmpty())
		{
			generateRandomPosInFormation();
		}
	}
	
	void generateRandomPos()
	{
		rand = new Random();
		randPosX = Math.pow(-1, rand.nextInt(2))*rand.nextInt((int)(followRadius/2));
		randPosZ = Math.pow(-1, rand.nextInt(2))*rand.nextInt((int)(followRadius/2));
	}
}