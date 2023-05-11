package com.magistumod.entity;

import java.util.Iterator;

import javax.annotation.Nullable;

import com.flansmod.api.IControllable;
import com.flansmod.common.driveables.EntitySeat;
import com.google.common.base.Predicate;
import com.magistumod.common.storage.StorageHelper;
import com.magistumod.entity.ai.EntityAIControl;
import com.magistumod.entity.ai.EntityAIFirefight;
import com.magistumod.entity.ai.EntityAIFollow;
import com.magistumod.entity.ai.EntityAIGetInDriveable;
import com.magistumod.entity.ai.EntityAIHurtByEnemyTarget;
import com.magistumod.entity.ai.EntityAIMoveAwayFromEntity;
import com.magistumod.entity.ai.EntityAINearestAttackableTargetFromAnyHeight;
import com.magistumod.entity.ai.EntityAIOpenAnyDoor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySoldier extends FlansModShooter
{
	
	public static Predicate<Entity> INSIDER_SOLDIER_NON_VEHICLE;
    public static Predicate<Entity> INSIDER_PLAYER_NON_VEHICLE;
	public static Predicate<Entity> INSIDER_SOLDIER_VEHICLE;
    public static Predicate<Entity> INSIDER_PLAYER_VEHICLE;
    
    private EnumCoalitions coalition;
    private int targetPriority;
    
    
    public EnumCoalitions getCoalition()
    {
        return coalition;
    }
    
    public EntitySoldier(World world) 
	{
		super(world);
	}
    
    //AI
	EntityAIMoveAwayFromEntity moveAwayFromEntity;
	EntityAIFollow follow;
	private EntityAIControl control;
	
	public void initAI(EnumCoalitions coalition, int targetPriority)
    {
		this.coalition = coalition;
		this.targetPriority = targetPriority;
		EnumCoalitions enemyCoalition;
		
		if (coalition == EnumCoalitions.AXIS) 
		{
			enemyCoalition = EnumCoalitions.ALLIES;
		}
		else
		{
			enemyCoalition = EnumCoalitions.AXIS;
		}
		
		INSIDER_SOLDIER_NON_VEHICLE = new Predicate<Entity>()
	    {
	        public boolean apply(@Nullable Entity p_apply_1_)
	        {
	            return p_apply_1_ instanceof FlansModShooter && (!p_apply_1_.isRiding() || !(p_apply_1_.getRidingEntity() instanceof IControllable)) && ((FlansModShooter)p_apply_1_).getCoalition() == enemyCoalition && ((EntityLivingBase)p_apply_1_).attackable();
	        }
	    };
	    
	    INSIDER_PLAYER_NON_VEHICLE = new Predicate<Entity>()
	    {
	        public boolean apply(@Nullable Entity p_apply_1_)
	        {
	            return p_apply_1_ instanceof EntityPlayer && (!p_apply_1_.isRiding() || !(p_apply_1_.getRidingEntity() instanceof IControllable)) &&  StorageHelper.getCoalition(p_apply_1_.getUniqueID()) != null && EnumCoalitions.getCoalition(StorageHelper.getCoalition(p_apply_1_.getUniqueID())).equals(enemyCoalition) && ((EntityLivingBase)p_apply_1_).attackable();
	        }
	    };
	    
		INSIDER_SOLDIER_VEHICLE = new Predicate<Entity>()
	    {
	        public boolean apply(@Nullable Entity p_apply_1_)
	        {
	            return p_apply_1_ instanceof FlansModShooter && p_apply_1_.isRiding() && p_apply_1_.getRidingEntity() instanceof IControllable && ((FlansModShooter)p_apply_1_).getCoalition() == enemyCoalition && ((EntityLivingBase)p_apply_1_).attackable();
	        }
	    };
	    
	    INSIDER_PLAYER_VEHICLE = new Predicate<Entity>()
	    {
	        public boolean apply(@Nullable Entity p_apply_1_)
	        {
	            return p_apply_1_ instanceof EntityPlayer && p_apply_1_.isRiding() && p_apply_1_.getRidingEntity() instanceof IControllable &&  StorageHelper.getCoalition(p_apply_1_.getUniqueID()) != null && EnumCoalitions.getCoalition(StorageHelper.getCoalition(p_apply_1_.getUniqueID())).equals(enemyCoalition) && ((EntityLivingBase)p_apply_1_).attackable();
	        }
	    };
		
		moveAwayFromEntity = new EntityAIMoveAwayFromEntity(this, Entity.class, 1.0F, 1.0D, 1.0D);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(1, new EntityAIOpenAnyDoor(this));
    	this.tasks.addTask(3, new EntityAIGetInDriveable(this, 1.5D, 60.0F));
//		this.tasks.addTask(4, moveAwayFromEntity);
//    	this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
    	this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 9.0F));
    	this.tasks.addTask(7, new EntityAILookIdle(this));
    	
    	this.targetTasks.addTask(1, new EntityAIHurtByEnemyTarget(this, false, new Class[0]));
    	switch(targetPriority)
    	{
    	case -1:
    		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.3D, true));
	    	this.targetTasks.addTask(2, new EntityAINearestAttackableTargetFromAnyHeight(this, FlansModShooter.class, 0, false, false, INSIDER_SOLDIER_NON_VEHICLE));
	    	this.targetTasks.addTask(2, new EntityAINearestAttackableTargetFromAnyHeight(this, EntityPlayer.class, 0, false, false, INSIDER_PLAYER_NON_VEHICLE));
	    	this.tasks.addTask(3, new EntityAIMoveAwayFromEntity(this, FlansModShooter.class, INSIDER_SOLDIER_VEHICLE, 80.0F, 1.5D, 1.5D));
	    	this.tasks.addTask(3, new EntityAIMoveAwayFromEntity(this, EntityPlayer.class, INSIDER_PLAYER_VEHICLE, 80.0F, 1.5D, 1.5D));
	    	this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, false, true, new Predicate<EntityLiving>()
	        {
	            public boolean apply(@Nullable EntityLiving p_apply_1_)
	            {
	                return p_apply_1_ != null && IMob.VISIBLE_MOB_SELECTOR.apply(p_apply_1_) && !(p_apply_1_ instanceof EntityCreeper);
	            }
	        }));
    	case 0:
    		this.tasks.addTask(2, new EntityAIFirefight(this, 1.0D, 6, 12, 80.0F));
	    	this.targetTasks.addTask(2, new EntityAINearestAttackableTargetFromAnyHeight(this, FlansModShooter.class, 0, false, false, INSIDER_SOLDIER_NON_VEHICLE));
	    	this.targetTasks.addTask(2, new EntityAINearestAttackableTargetFromAnyHeight(this, EntityPlayer.class, 0, false, false, INSIDER_PLAYER_NON_VEHICLE));
	    	this.targetTasks.addTask(3, new EntityAINearestAttackableTargetFromAnyHeight(this, FlansModShooter.class, 0, false, false, INSIDER_SOLDIER_VEHICLE));
	    	this.targetTasks.addTask(3, new EntityAINearestAttackableTargetFromAnyHeight(this, EntityPlayer.class, 0, false, false, INSIDER_PLAYER_VEHICLE));
	    	this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, false, true, new Predicate<EntityLiving>()
	        {
	            public boolean apply(@Nullable EntityLiving p_apply_1_)
	            {
	                return p_apply_1_ != null && IMob.VISIBLE_MOB_SELECTOR.apply(p_apply_1_) && !(p_apply_1_ instanceof EntityCreeper);
	            }
	        }));
    	case 1:
    		this.tasks.addTask(2, new EntityAIFirefight(this, 1.0D, 6, 12, 80.0F));
	    	this.targetTasks.addTask(2, new EntityAINearestAttackableTargetFromAnyHeight(this, FlansModShooter.class, 0, false, false, INSIDER_SOLDIER_VEHICLE));
	    	this.targetTasks.addTask(2, new EntityAINearestAttackableTargetFromAnyHeight(this, EntityPlayer.class, 0, false, false, INSIDER_SOLDIER_VEHICLE));
	    	this.targetTasks.addTask(3, new EntityAINearestAttackableTargetFromAnyHeight(this, FlansModShooter.class, 0, false, false, INSIDER_SOLDIER_NON_VEHICLE));
	    	this.targetTasks.addTask(3, new EntityAINearestAttackableTargetFromAnyHeight(this, EntityPlayer.class, 0, false, false, INSIDER_PLAYER_NON_VEHICLE));
	    	this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, false, true, new Predicate<EntityLiving>()
	        {
	            public boolean apply(@Nullable EntityLiving p_apply_1_)
	            {
	                return p_apply_1_ != null && IMob.VISIBLE_MOB_SELECTOR.apply(p_apply_1_) && !(p_apply_1_ instanceof EntityCreeper);
	            }
	        }));
    	}
    }

	@Override
	public void removeAvoidTask() 
	{
		tasks.removeTask(moveAwayFromEntity);
		
	}

	@Override
	public void addAvoidTask() 
	{
		tasks.addTask(4, moveAwayFromEntity);
		
	}

	@Override
	public void removeFollowTask() 
	{
		tasks.removeTask(follow);
	}

	@Override
	public void addFollowTask(EntityLiving follower, EntityPlayer commander, float followRadius, float stopDistance,
			double movespeed) {
		
        Iterator<EntityAITasks.EntityAITaskEntry> iterator = tasks.taskEntries.iterator();

        while (iterator.hasNext())
        {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
            EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;

            if (entityaibase == follow)
            {
                return;
            }
        }
		
		follow = new EntityAIFollow(follower, commander, followRadius, stopDistance, movespeed);
		this.tasks.addTask(3, follow);
	}
	
	public void addControlTask(EntitySeat seat, int j, float r)
	{
		if (control != null)
		{
			this.removeControlTask();
		}
		control = new EntityAIControl(this, seat, j, r);
		this.tasks.addTask(2, control);
	}
	
	public void removeControlTask()
	{
		this.tasks.removeTask(control);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		
		compound.setString("Coalition", coalition.name());
		compound.setInteger("TargetPriority", targetPriority);
		compound.setInteger("AmmoAmount", ammoStacks.length);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        
        this.initAI(EnumCoalitions.getCoalition(compound.getString("Coalition")), compound.getInteger("TargetPriority"));
        this.initEquipment(getHeldItemMainhand(), compound.getInteger("AmmoAmount"));
    }
}
