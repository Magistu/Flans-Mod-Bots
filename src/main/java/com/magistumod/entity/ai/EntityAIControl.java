package com.magistumod.entity.ai;

import com.flansmod.common.FlansMod;
import com.flansmod.common.RotatedAxes;
import com.flansmod.common.driveables.CollisionBox;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.driveables.EnumDriveablePart;
import com.flansmod.common.driveables.EnumWeaponType;
import com.flansmod.common.driveables.PilotGun;
import com.flansmod.common.driveables.ShootPoint;
import com.flansmod.common.driveables.VehicleType;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.FireableGun;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemBullet;
import com.flansmod.common.network.PacketPlaySound;
import com.flansmod.common.network.PacketSeatUpdates;
import com.flansmod.common.network.PacketVehicleControl;
import com.flansmod.common.vector.Vector3f;
import com.magistumod.common.guns.FiredShot2;
import com.magistumod.common.guns.ShotHandler2;
import com.magistumod.common.network.PacketVehicleControl2;
import com.magistumod.ConfigHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAIControl extends EntityAIBase
{
    /** The entity the AI instance has been applied to */
    private final EntityLiving entityHost;
    private EntityLivingBase attackTarget;
    /**
     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    private int shootDelay;
    private final float attackRadius;
    private final float maxAttackDistance;
	private EntityDriveable driving;
	private AxisAlignedBB area;
	private EntitySeat seat;
	private boolean readyForUpdates = false;
	private VehicleType type;
	private ShootPoint shootPoint;
	private EnumWeaponType weaponType;
	private BulletType shellType;
	private ItemStack itemShell;
	private Vec3d turretOrigin;
	private Vec3d gunOrigin;
	private double dX;
	private double dY;
	private double dZ;
	private float a;
	private float v;
	private float barrelLength = -1;
	private RotatedAxes looking;
	private int controlTicks = 5;
	private int globalSignDeltaX;
	private double targetMotionX;
	private double targetMotionZ;
	private double prevTargetX;
	private double prevTargetZ;
	private CollisionBox driveableTargetBox;
	private EntityDriveable targetDriveable;
	private Vector3f shootPointLooking;
	private float heightDifference;
	private int seatIndex;
	private int soundDelay = 0;

	
    public EntityAIControl(EntityLiving pilot, EntitySeat seat, int seatIndex, float maxAttackDistanceIn)
    {
        shootDelay = -1;
        
        entityHost = pilot;
        driving = seat.driveable;
        this.seat = seat;
        this.seatIndex = seatIndex;
        attackRadius = maxAttackDistanceIn;
        maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        
        setMutexBits(4);
        
        entityHost.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(maxAttackDistanceIn);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();
        
        if(!entityHost.isRiding())
        {
        	if (entityHost.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue() != ConfigHandler.INFANTRY_FIRING_RANGE)
        	{
        		entityHost.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ConfigHandler.INFANTRY_FIRING_RANGE);
        	}
//			((FlansModShooter)entityHost).removeControlTask();
        	return false;
        }

        if(entitylivingbase == null)
        {
            return false;
        }
        
        attackTarget = entitylivingbase;
        return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.shouldExecute();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        attackTarget = null;
        prevTargetX = 0;
        prevTargetZ = 0;
        barrelLength = -1;
        shootDelay = -1;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        double d0 = entityHost.getDistanceSq(attackTarget.posX, attackTarget.getEntityBoundingBox().minY, attackTarget.posZ);
        boolean flag = entityHost.getEntitySenses().canSee(attackTarget);
        
        entityHost.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
        
        if (seat == null || driving == null)
        {
        	seat = (EntitySeat)entityHost.getRidingEntity();
        	driving = seat.driveable;
        }
        
        if (driving == null)
        {
        	entityHost.dismountRidingEntity();
        	return;
        }
        
        if (!flag)
        {
            return;
        }
        
        if (seatIndex == 0 && driving.getDriveableType().shootPoints(false).size() > 0 && ((EntityVehicle)driving).isPartIntact(EnumDriveablePart.turret))
        {
	        if (--controlTicks == 0)
	        {
	        	controlTicks = 5;
	        	type = ((EntityVehicle)driving).getVehicleType();
	        	shootPoint = type.shootPoints(false).get(0);
	            shootPointLooking = new Vector3f(driving.axes.findLocalVectorGlobally(seat.looking.getXAxis()).toVec3().normalize());
	            
	            if (barrelLength < 0)
	            {
	            	barrelLength = getBarrelLength();
	            }
	            
	            gunOrigin = Vector3f.sub(driving.getOrigin(shootPoint), new Vector3f(shootPointLooking.x * barrelLength, shootPointLooking.y * barrelLength, shootPointLooking.z * barrelLength), null).toVec3().add(driving.getPositionVector());
	            turretOrigin = driving.axes.findLocalVectorGlobally(new Vector3f(type.turretOrigin)).toVec3().add(driving.posX, driving.posY, driving.posZ);
	            weaponType = type.weaponType(false);
	            
	            boolean hasMissileAmmo = false;
	            
	            for(int i = driving.driveableData.getMissileInventoryStart();
	    			i <  driving.driveableData.getMissileInventoryStart() + type.numMissileSlots; i++)
				{
					ItemStack shell =  driving.driveableData.getStackInSlot(i);
					if(shell != null && shell.getItem() instanceof ItemBullet && type.isValidAmmo(
						((ItemBullet)shell.getItem()).type, weaponType))
					{
						hasMissileAmmo = true;
						shellType = ((ItemBullet)shell.getItem()).type;
						break;
					}
				}
	            
	            if (!hasMissileAmmo)
	            {
	            	for(int i = driving.driveableData.getMissileInventoryStart();
	    	    		i <  driving.driveableData.getMissileInventoryStart() + type.numMissileSlots; i++)
	    			{
	            		driving.driveableData.setInventorySlotContents(i, new ItemStack(type.ammo.get(0).item));
	    			}
	            	
	            	for(int i = driving.driveableData.getMissileInventoryStart();
	            			i <  driving.driveableData.getMissileInventoryStart() + type.numMissileSlots; i++)
	    			{
	    				ItemStack shell =  driving.driveableData.getStackInSlot(i);
	    				if(shell != null && shell.getItem() instanceof ItemBullet && type.isValidAmmo(
	    					((ItemBullet)shell.getItem()).type, weaponType))
	    				{
	    					hasMissileAmmo = true;
	    					shellType = ((ItemBullet)shell.getItem()).type;
	    					break;
	    				}
	    			}
	            }
	            
	            double dTurretX;
				double dTurretZ;
				
				boolean targetIsRiding = false;
				if(attackTarget.isRiding() && attackTarget.getRidingEntity() instanceof EntitySeat)
				{
					targetDriveable = ((EntitySeat)attackTarget.getRidingEntity()).driveable;
					if (targetDriveable != null)
					{
						driveableTargetBox = targetDriveable.getDriveableData().parts.get(EnumDriveablePart.core).box;
						Vec3d driveableTargetPos = targetDriveable.axes.findLocalVectorGlobally(driveableTargetBox.getCentre()).toVec3().add(targetDriveable.getPositionVector());
		
						dTurretX = driveableTargetPos.x - turretOrigin.x;
						dTurretZ = driveableTargetPos.z - turretOrigin.z;
						
						dX = driveableTargetPos.x - gunOrigin.x;
						dY = driveableTargetPos.y - gunOrigin.y;
						dZ = driveableTargetPos.z - gunOrigin.z;
						
						targetIsRiding = true;
					}
					else
					{
						dTurretX = attackTarget.posX - turretOrigin.x;
						dTurretZ = attackTarget.posZ - turretOrigin.z;
						
						dX = attackTarget.posX - gunOrigin.x;
						dY = attackTarget.posY + attackTarget.getEyeHeight() - gunOrigin.y;
						dZ = attackTarget.posZ - gunOrigin.z;
					}
				}
				else
				{
					dTurretX = attackTarget.posX - turretOrigin.x;
					dTurretZ = attackTarget.posZ - turretOrigin.z;
					
					dX = attackTarget.posX - gunOrigin.x;
					dY = attackTarget.posY + attackTarget.getEyeHeight() - gunOrigin.y;
					dZ = attackTarget.posZ - gunOrigin.z;
				}
				if (prevTargetX == 0.0D && prevTargetZ == 0.0D)
				{
					prevTargetX = attackTarget.posX;
			    	prevTargetZ = attackTarget.posZ;
				}
				else
				{
					targetMotionX = 2 * (attackTarget.posX - prevTargetX) / 5;
					if (attackTarget.posX < driving.posX)
						targetMotionX *= -1;
					targetMotionZ = 2 * (attackTarget.posZ - prevTargetZ) / 5;
					if (attackTarget.posZ < driving.posZ)
						targetMotionZ *= -1;
				}
				v = (float)driving.getSpeed() + 3.0F;
				dTurretX += dTurretX * (targetMotionX / v);
				dTurretZ += dTurretZ * (targetMotionZ / v);
				dX += dX * (targetMotionX / v);
				dZ += dZ * (targetMotionZ / v);
				
				if(dX*dX + dY*dY + dZ*dZ >= 40000F)
					return;
				
				heightDifference = 0;
				if (shellType != null)
				{
					a = 0.02F * shellType.fallSpeed;
					float multiplyByDrag = -(float)(300F * Math.log(1 - Math.sqrt(dX*dX + dY*dY + dZ*dZ) / 300F) / Math.sqrt(dX*dX + dY*dY + dZ*dZ));
					heightDifference = (float)NewtonFindRoot(0.0F, 200, 0.1F) * multiplyByDrag;
				}
				
				float pitch = -(float)Math.atan2((heightDifference + dY), Math.sqrt(dX*dX + dZ*dZ)) * 180F / 3.14159F;
				
				looking = new RotatedAxes((float)Math.atan2(dTurretZ, dTurretX) * 180F / 3.14159F, pitch, 0F);
				
				looking = looking.rotateGlobalYaw(-driving.axes.getYaw()).rotateGlobalPitch(-driving.axes.getPitch()).rotateGlobalRoll(-driving.axes.getRoll());
				
	            seat.prevPlayerLooking = looking.clone();
	            seat.playerLooking = looking.clone();
	            
	        	float targetX = (float)(Math.atan2(dZ, dX) * 180F / 3.14159F);
	        	float rotatingToYaw = looking.getYaw();
	        	float rotatingToPitch = looking.getPitch();
	        	float lookingYaw = seat.looking.getYaw();
	        	float lookingPitch = seat.looking.getPitch();
	        	d0 = dX * dX + dY * dY + dZ * dZ;
	        	
	        	float yawToMove = (targetX - driving.axes.getYaw());
	        	while(yawToMove > 180F)
				{
					yawToMove -= 360F;
				}
				while(yawToMove <= -180F)
				{
					yawToMove += 360F;
				}
				
				float yawToMoveTurret = (rotatingToYaw - lookingYaw);
				while(yawToMoveTurret > 180F)
				{
					yawToMoveTurret -= 360F;
				}
				while(yawToMoveTurret <= -180F)
				{
					yawToMoveTurret += 360F;
				}
				
				float pitchToMoveTurret = (rotatingToPitch - lookingPitch);
				while(pitchToMoveTurret > 180F)
				{
					pitchToMoveTurret -= 360F;
				}
				while(pitchToMoveTurret <= -180F)
				{
					pitchToMoveTurret += 360F;
				}
				
				boolean shouldMoveToTarget = false;
				if(!targetIsRiding)
				{
					if(Math.abs(yawToMoveTurret) < 1F && pitchToMoveTurret < (1F + Math.atan2(attackTarget.getEyeHeight() * 180F / 3.14159F, d0)) && pitchToMoveTurret > -1F)
						driving.shoot(false);
				}
				else
				{
					if (Math.abs(yawToMoveTurret) < 3F && pitchToMoveTurret < (1F + Math.atan2(attackTarget.getEyeHeight() * 180F / 3.14159F, d0)) && pitchToMoveTurret > -1F)
						driving.shoot(false);
					
					if (((EntitySeat)attackTarget.getRidingEntity()).driveable != null && !entityHost.getEntitySenses().canSee(((EntitySeat)attackTarget.getRidingEntity()).driveable))
					{
						shouldMoveToTarget = true;
					}
				}
	        	
	        	boolean shouldGo = false;
	        	int reverse = 1;
	        	
	        	if (driving.driveableData.fuelInTank <= driving.driveableData.engine.fuelConsumption * driving.throttle)
	        	{
	        		driving.driveableData.fuelInTank = type.fuelTankSize;
	        	}
	        	
	        	if((d0 > 10000D || shouldMoveToTarget) && canVehicleMove((EntityVehicle)driving))
	        	{
	        		if (Math.abs(yawToMove) < 45F)
	        		{
	        			shouldGo = true;
	        			((EntityVehicle)driving).throttle += 0.05F;
	    				if(((EntityVehicle)driving).throttle > 1F)
	    					((EntityVehicle)driving).throttle = 1F;
	        		}
	        		rotateVehicleTo(targetX, true);
	        	}
	        	else if(d0 < 400D && canVehicleMove((EntityVehicle)driving))
	        	{
	        		if (Math.abs(yawToMove) < 90F)
	        		{
	        			rotateVehicleTo(targetX, false);
	        		}
	        		else
	        		{
	        			if((yawToMove += 180F) > 180F)
	        			{
	        				yawToMove -= 360F;
	        			}
	        			rotateVehicleTo(targetX + 180F, false);
	        			reverse = -1;
	        		}
	        		if (Math.abs(yawToMove) < 45F)
	        		{
	        			shouldGo = true;
	        			((EntityVehicle)driving).throttle -= reverse*0.05F;
	    				if(((EntityVehicle)driving).throttle > 1F)
	    					((EntityVehicle)driving).throttle = 1F;
	        		}
	        	}
	        	else if(rotatingToPitch < seat.seatInfo.minPitch || rotatingToPitch > seat.seatInfo.maxPitch && canVehicleMove((EntityVehicle)driving))
				{
		        	shouldGo = true;
		        	((EntityVehicle)driving).throttle -= 0.05F;
					if(((EntityVehicle)driving).throttle > 1F)
						((EntityVehicle)driving).throttle = 1F;
					rotateVehicleTo(targetX, false);
				}
				else
				{
					if(Math.abs(yawToMoveTurret) > 10F || (rotatingToPitch < seat.seatInfo.minYaw || rotatingToPitch > seat.seatInfo.maxYaw))
					{
						rotateVehicleTo(looking.getYaw() + driving.axes.getYaw(), true);
					}
					else
					{
						((EntityVehicle)driving).wheelsYaw = 0F;
					}
				}
	        	
		        if (!shouldGo)
		        {
		        	((EntityVehicle)driving).throttle = 0F;
		        }
		        
		        prevTargetX = attackTarget.posX;
		    	prevTargetZ = attackTarget.posZ;
	    	}
	        
	        updateVehicleControl();
	        updateSeatRotation();
	        updatePassenger();
			
	        if(driving.shootDelaySecondary == 0)
	        {
	        	type = ((EntityVehicle)driving).getVehicleType();
	        	driving.shootDelaySecondary = type.shootDelaySecondary;
	    		if (type.shootPoints(true).size() > 0)
	    		{
	            	shootPoint = type.shootPoints(true).get(0);
	            	shootPointLooking = driving.axes.findLocalVectorGlobally(seat.looking.getXAxis());
	            	gunOrigin = driving.axes.findLocalVectorGlobally(shootPoint.rootPos.position).toVec3().add(driving.posX, driving.posY, driving.posZ);
	            	
		        	PilotGun pilotGun = (PilotGun)shootPoint.rootPos;
					GunType gunType = pilotGun.type;
					BulletType bulletType = (BulletType)gunType.ammo.get(0);
					FireableGun fireableGun = new FireableGun(
							gunType,
							gunType.damage,
							gunType.bulletSpread,
							gunType.bulletSpeed,
							gunType.spreadPattern);
					
					FiredShot2 shot = new FiredShot2(fireableGun, bulletType, driving, entityHost);
					Vector3f direction = new Vector3f();
					Vector3f shootPointDirection = new Vector3f();
					
					if (attackTarget.isRiding() && attackTarget.getRidingEntity() instanceof EntitySeat)
					{
						targetDriveable = ((EntitySeat)attackTarget.getRidingEntity()).driveable;
						if (targetDriveable.isPartIntact(EnumDriveablePart.turret))
						{
							direction = (new Vector3f(targetDriveable.getSeat(0).posX - gunOrigin.x, targetDriveable.getSeat(0).posY - gunOrigin.y, targetDriveable.getSeat(0).posZ - gunOrigin.z)).normalise(direction);
						}
						else
						{
							driveableTargetBox = targetDriveable.getDriveableData().parts.get(EnumDriveablePart.core).box;
							direction = new Vector3f(targetDriveable.axes.findLocalVectorGlobally(driveableTargetBox.getCentre()).toVec3().add(targetDriveable.getPositionVector())).normalise(direction);
						}
					}
					else
					{
						direction = (new Vector3f(attackTarget.posX - gunOrigin.x, attackTarget.posY + attackTarget.getEyeHeight() - gunOrigin.y, attackTarget.posZ - gunOrigin.z)).normalise(direction);
					}
					
					shootPointDirection = shootPointLooking.normalise(shootPointDirection);
					
					if((shootPointDirection.x - direction.x) * (shootPointDirection.x - direction.x) + (shootPointDirection.z - direction.z) * (shootPointDirection.z - direction.z) < 0.09F)
					{
						ShotHandler2.fireGun(entityHost.getEntityWorld(), shot, bulletType.numBullets, new Vector3f(gunOrigin), direction);
						
						if(type.shootSound(true) != null)
						{
							PacketPlaySound.sendSoundPacket(gunOrigin.x,
									gunOrigin.y,
									gunOrigin.z,
									FlansMod.soundRange * 4,
									entityHost.getEntityWorld().provider.getDimension(),
									type.shootSound(true),
									false);
						}
					}
	        	}
	        }
        }
        
        if(seat.seatInfo.gunType != null)
        {
        	updateSeatRotation();
        	
        	if(--shootDelay == 0)
	        {
	        	type = ((EntityVehicle)driving).getVehicleType();
	        	
            	shootPointLooking = driving.axes.findLocalVectorGlobally(seat.looking.getXAxis());
            	gunOrigin = driving.axes.findLocalVectorGlobally(seat.seatInfo.gunOrigin).toVec3().add(driving.posX, driving.posY, driving.posZ);

				GunType gunType = seat.seatInfo.gunType;
				
				shootDelay = (int)gunType.shootDelay;
				
				BulletType bulletType = (BulletType)gunType.ammo.get(0);
				FireableGun fireableGun = new FireableGun(gunType,
						gunType.damage,
						gunType.bulletSpread,
						gunType.bulletSpeed,
						gunType.spreadPattern);
				
				FiredShot2 shot = new FiredShot2(fireableGun, bulletType, driving, entityHost);
				Vector3f direction = new Vector3f();
				Vector3f shootPointDirection = new Vector3f();

				if (attackTarget.isRiding() && attackTarget.getRidingEntity() instanceof EntitySeat)
				{
					targetDriveable = ((EntitySeat)attackTarget.getRidingEntity()).driveable;
					if (targetDriveable != null)
					{
						if (targetDriveable.isPartIntact(EnumDriveablePart.turret))
						{
							direction = (new Vector3f(targetDriveable.getSeat(0).posX - gunOrigin.x, targetDriveable.getSeat(0).posY - gunOrigin.y, targetDriveable.getSeat(0).posZ - gunOrigin.z)).normalise(direction);
						}
						else
						{
							driveableTargetBox = targetDriveable.getDriveableData().parts.get(EnumDriveablePart.core).box;
							direction = new Vector3f(targetDriveable.axes.findLocalVectorGlobally(driveableTargetBox.getCentre()).toVec3().add(targetDriveable.getPositionVector())).normalise(direction);
						}
					}
				}
				else
				{
					direction = (new Vector3f(attackTarget.posX - gunOrigin.x, attackTarget.posY + attackTarget.getEyeHeight() - gunOrigin.y, attackTarget.posZ - gunOrigin.z)).normalise(direction);
				}
				
				shootPointDirection = shootPointLooking.normalise(shootPointDirection);
				
				looking = new RotatedAxes((float)Math.atan2(direction.z, direction.x) * 180F / 3.14159F, (float)Math.atan2((heightDifference + dY), Math.sqrt(dX*dX + dZ*dZ)) * 180F / 3.14159F, 0F);
				
				looking = looking.rotateGlobalYaw(-driving.axes.getYaw()).rotateGlobalPitch(-driving.axes.getPitch()).rotateGlobalRoll(-driving.axes.getRoll());
				
	            seat.prevPlayerLooking = looking.clone();
	            seat.playerLooking = looking.clone();
				
				if((shootPointDirection.x - direction.x) * (shootPointDirection.x - direction.x) + (shootPointDirection.z - direction.z) * (shootPointDirection.z - direction.z) < 0.01F)
				{
					ShotHandler2.fireGun(entityHost.getEntityWorld(), shot, bulletType.numBullets, new Vector3f(gunOrigin), direction);
					
					int dimension;
					if (entityHost.getEntityWorld() != null)
			        {
			            dimension = entityHost.getEntityWorld().provider.getDimension();
			            
			            if(soundDelay  <= 0 && gunType.shootSound != null)
						{
							soundDelay = gunType.shootSoundLength;
							PacketPlaySound.sendSoundPacket(gunOrigin.x, gunOrigin.y, gunOrigin.z, FlansMod.soundRange * 4, dimension, gunType.shootSound, gunType.distortSound);
							
						}
			        }
				}
	        }
        	else if(shootDelay < 0)
        	{
        		shootDelay = (int)seat.
        				seatInfo.
        				gunType.shootDelay;
        	}
        }
    }
    
    

	private boolean canVehicleMove(EntityVehicle vehicle) {
    	 return (vehicle.isPartIntact(EnumDriveablePart.leftTrack) && vehicle.isPartIntact(EnumDriveablePart.rightTrack)) || 
    			(vehicle.isPartIntact(EnumDriveablePart.frontWheel) && vehicle.isPartIntact(EnumDriveablePart.backWheel)) ||
    	   		(vehicle.isPartIntact(EnumDriveablePart.backRightWheel) && vehicle.isPartIntact(EnumDriveablePart.frontRightWheel) &&  vehicle.isPartIntact(EnumDriveablePart.backLeftWheel) && vehicle.isPartIntact(EnumDriveablePart.frontLeftWheel));
	}

	private void updateSeatRotation()
	{
		// Move the seat accordingly
		// Consider new Yaw and Yaw limiters
		
		float targetX = seat.playerLooking.getYaw();
		
		float yawToMove = (targetX - seat.looking.getYaw());
		while (yawToMove > 180F)
		{
			yawToMove -= 360F;
		}
		while (yawToMove <= -180F)
		{
			yawToMove += 360F;
		}
		
		float signDeltaX = 0;
		if (yawToMove > (seat.seatInfo.aimingSpeed.x / 2) && !seat.seatInfo.legacyAiming)
		{
			signDeltaX = 1;
		}
		else if (yawToMove < -(seat.seatInfo.aimingSpeed.x / 2) && !seat.seatInfo.legacyAiming)
		{
			signDeltaX = -1;
		}
		else
		{
			signDeltaX = 0;
		}
		globalSignDeltaX  = (int)signDeltaX;
		
		
		// Calculate new yaw and consider yaw limiters
		float newYaw = 0f;
		
		if (seat.seatInfo.legacyAiming || (signDeltaX == 0))
		{
			newYaw = seat.playerLooking.getYaw();
		}
		else
		{
			newYaw = seat.looking.getYaw() + signDeltaX * seat.seatInfo.aimingSpeed.x;
		}
		// Since the yaw limiters go from -360 to 360, we need to find a pair of yaw values and check them both
		float otherNewYaw = newYaw - 360F;
		if (newYaw < 0)
			otherNewYaw = newYaw + 360F;
		if ((!(newYaw >= seat.seatInfo.minYaw) || !(newYaw <= seat.seatInfo.maxYaw)) &&
				(!(otherNewYaw >= seat.seatInfo.minYaw) || !(otherNewYaw <= seat.seatInfo.maxYaw)))
		{
			float newYawDistFromRange =
					Math.min(Math.abs(newYaw - seat.seatInfo.minYaw), Math.abs(newYaw - seat.seatInfo.maxYaw));
			float otherNewYawDistFromRange =
					Math.min(Math.abs(otherNewYaw - seat.seatInfo.minYaw), Math.abs(otherNewYaw - seat.seatInfo.maxYaw));
			// If the newYaw is closer to the range than the otherNewYaw, move newYaw into the range
			if (newYawDistFromRange <= otherNewYawDistFromRange)
			{
				if(newYaw > seat.seatInfo.maxYaw)
					newYaw = seat.seatInfo.maxYaw;
				if(newYaw < seat.seatInfo.minYaw)
					newYaw = seat.seatInfo.minYaw;
			}
			// Else, the otherNewYaw is closer, so move it in
			else
			{
				if (otherNewYaw > seat.seatInfo.maxYaw)
					otherNewYaw = seat.seatInfo.maxYaw;
				if (otherNewYaw < seat.seatInfo.minYaw)
					otherNewYaw = seat.seatInfo.minYaw;
				// Then match up the newYaw with the otherNewYaw
				if (newYaw < 0)
					newYaw = otherNewYaw - 360F;
				else newYaw = otherNewYaw + 360F;
			}
		}
		
		
		// Calculate the new pitch and consider pitch limiters
		float targetY = seat.playerLooking.getPitch();
		
		float pitchToMove = (targetY - seat.looking.getPitch());
		while(pitchToMove > 180F)
		{
			pitchToMove -= 360F;
		}
		while(pitchToMove <= -180F)
		{
			pitchToMove += 360F;
		}
		
		float signDeltaY = 0;
		if(pitchToMove > (seat.seatInfo.aimingSpeed.y / 2) && !seat.seatInfo.legacyAiming)
		{
			signDeltaY = 1;
		}
		else if(pitchToMove < -(seat.seatInfo.aimingSpeed.y / 2) && !seat.seatInfo.legacyAiming)
		{
			signDeltaY = -1;
		}
		else
		{
			signDeltaY = 0;
		}
		
		float newPitch = 0f;
		
		
		// Pitches the gun at the last possible moment in order to reach target pitch at the same time as target yaw.
		float minYawToMove = 0f;
		
		float currentYawToMove = 0f;
		
		if(seat.seatInfo.latePitch)
		{
			minYawToMove = ((float)Math
					.sqrt((pitchToMove / seat.seatInfo.aimingSpeed.y) * (pitchToMove / seat.seatInfo.aimingSpeed.y))) *
					seat.seatInfo.aimingSpeed.x;
		}
		else
		{
			minYawToMove = 360f;
		}
		
		currentYawToMove = (float)Math.sqrt((yawToMove) * (yawToMove));
		
		if(seat.seatInfo.legacyAiming || (signDeltaY == 0))
		{
			newPitch = seat.playerLooking.getPitch();
		}
		else if(!seat.seatInfo.yawBeforePitch && currentYawToMove < minYawToMove)
		{
			newPitch = seat.looking.getPitch() + signDeltaY * seat.seatInfo.aimingSpeed.y;
		}
		else if(seat.seatInfo.yawBeforePitch && signDeltaX == 0)
		{
			newPitch = seat.looking.getPitch() + signDeltaY * seat.seatInfo.aimingSpeed.y;
		}
		else if(seat.seatInfo.yawBeforePitch)
		{
			newPitch = seat.looking.getPitch(); 
		}
		else
		{
			newPitch = seat.looking.getPitch();
		}
		
		if(newPitch > -seat.seatInfo.minPitch)
			newPitch = -seat.seatInfo.minPitch;
		if(newPitch < -seat.seatInfo.maxPitch)
			newPitch = -seat.seatInfo.maxPitch;
		
		
		if(seat.looking.getYaw() != newYaw || seat.looking.getPitch() != newPitch)
		{
			// Now set the new angles
			seat.prevLooking = seat.looking.clone();
			seat.looking.setAngles(newYaw, newPitch, 0F);
			FlansMod.getPacketHandler().sendToServer(new PacketSeatUpdates(seat));
			
			EntityDriveable driveable = null;
			for(Object obj : entityHost.world.loadedEntityList)
			{
				if(obj instanceof EntityDriveable && ((Entity)obj).getEntityId() == seat.driveable.getEntityId())
				{
					driveable = (EntityDriveable)obj;
					break;
				}
			}
			
			driveable.getSeat(seat.seatInfo.id).prevLooking = driveable.getSeat(seat.seatInfo.id).looking.clone();
			driveable.getSeat(seat.seatInfo.id).looking.setAngles(seat.looking.getYaw(), seat.looking.getPitch(), 0F);
			//If on the server, update all surrounding players with these new angles
			FlansMod.getPacketHandler().sendToAllAround(new PacketSeatUpdates(seat), driveable.posX, driveable.posY, driveable.posZ, FlansMod.driveableUpdateRange, driveable.dimension);
		}
		
		seat.playYawSound = signDeltaX != 0 && seat.seatInfo.traverseSounds;
		
		if(signDeltaY != 0 && !seat.seatInfo.yawBeforePitch && currentYawToMove < minYawToMove)
		{
			seat.playPitchSound = true;
		}
		else seat.playPitchSound = signDeltaY != 0 && seat.seatInfo.yawBeforePitch && signDeltaX == 0;
	}
    
	
    private void updateVehicleControl()
    {
    	
    	if (driving.serverPositionTransitionTicker > 0)
    	{
	    	double x = driving.posX + (driving.serverPosX - driving.posX) / driving.serverPositionTransitionTicker;
			double y = driving.posY + (driving.serverPosY - driving.posY) / driving.serverPositionTransitionTicker;
			double z = driving.posZ + (driving.serverPosZ - driving.posZ) / driving.serverPositionTransitionTicker;
			double dYaw = MathHelper.wrapDegrees(driving.serverYaw - driving.axes.getYaw());
			double dPitch = MathHelper.wrapDegrees(driving.serverPitch - driving.axes.getPitch());
			double dRoll = MathHelper.wrapDegrees(driving.serverRoll - driving.axes.getRoll());
			driving.rotationYaw = (float)(driving.axes.getYaw() + dYaw / driving.serverPositionTransitionTicker);
			driving.rotationPitch = (float)(driving.axes.getPitch() + dPitch / driving.serverPositionTransitionTicker);
			float rotationRoll = (float)(driving.axes.getRoll() + dRoll / driving.serverPositionTransitionTicker);
			--driving.serverPositionTransitionTicker;
			driving.setPosition(x, y, z);
			driving.setRotation(driving.rotationYaw, driving.rotationPitch, rotationRoll);
    	}
		
    	if(driving.serverPosX != driving.posX || driving.serverPosY != driving.posY || driving.serverPosZ != driving.posZ || driving.serverYaw != driving.axes.getYaw())
		{
    		driving.onUpdate();
    		
    		driving.serverPosX = driving.posX;
    		driving.serverPosY = driving.posY;
    		driving.serverPosZ = driving.posZ;
    		driving.serverYaw = driving.axes.getYaw();
    		
    		FlansMod.getPacketHandler().sendToServer(new PacketVehicleControl(driving));
    		
    		driving.setPositionRotationAndMotion(driving.posX, driving.posY, driving.posZ, driving.axes.getYaw(), driving.axes.getPitch(), driving.axes.getRoll(), driving.motionX, driving.motionY, driving.motionZ, driving.angularVelocity.x, driving.angularVelocity.y, driving.angularVelocity.z, driving.throttle, ((EntityVehicle)driving).wheelsYaw);
    		driving.driveableData.fuelInTank = driving.driveableData.fuelInTank;
    		EntityVehicle vehicle = (EntityVehicle)driving;
    		
    		if(!entityHost.world.isRemote) //!isClientSide
    		{
    			FlansMod.getPacketHandler().sendToAllAround(new PacketVehicleControl(vehicle),
    					driving.posX,
    					driving.posY,
    					driving.posZ,
    					FlansMod.driveableUpdateRange,
    					vehicle.dimension);
    		}
		}
    }
    
    
    private void updatePassenger() 
    {
    	seat.updatePassenger(entityHost);
	}
    
    private double findHeightDifference(double dH)
    {
        return 2*Math.sqrt(dH*dH + 2*dH*dY + dX*dX + dY*dY + dZ*dZ)*a*barrelLength - a*dH*dH - 2*dH*a*dY + 2*dH*v*v - a*barrelLength*barrelLength - a*dX*dX - a*dY*dY - a*dZ*dZ;
    }

    private double NewtonFindRoot(double x0, int n, double eps)
    {
        double x = x0, df, h = 0.1;
        for (int i = 0; i < n && Math.abs(findHeightDifference(x)) > eps; ++i)
        {
            df = (findHeightDifference(x + h) - findHeightDifference(x)) / h;
            x = x - findHeightDifference(x) / df;
        }
        return x;
    }
    
    private float getBarrelLength()
    {
    	RotatedAxes t = seat.looking;
    	seat.looking = new RotatedAxes(0F, 0F, 0F);
    	Vector3f firstPoint = driving.getOrigin(shootPoint);
    	seat.looking = new RotatedAxes(0F, 10F, 0F);
    	Vector3f secondPoint = driving.getOrigin(shootPoint);
    	Vector3f dVec = Vector3f.sub(secondPoint, firstPoint, null);
    	seat.looking = t;
    	
    	return (float)((dVec.length() / Math.sin(5F * 3.14159F / 180F) - 4F) / 2F);
    }
    
    private int rotateVehicleTo(float targetX, boolean synchronously)
    {
    	float yawToMove = (targetX - driving.axes.getYaw());
		while(yawToMove > 180F)
		{
			yawToMove -= 360F;
		}
		while(yawToMove <= -180F)
		{
			yawToMove += 360F;
		}
		
		int signDeltaX = 0;
		if(!synchronously)
		{
			if(yawToMove > Math.abs(5 + ((EntityVehicle)driving).wheelsYaw * 2))
			{
				signDeltaX = 1;
			}
			else if(yawToMove < -Math.abs(5 + ((EntityVehicle)driving).wheelsYaw * 2))
			{
				signDeltaX = -1;
			}
			else
			{
				((EntityVehicle)driving).wheelsYaw = 0;
				signDeltaX = 0;
			}
		}
		else
		{
			signDeltaX = globalSignDeltaX;
		}
		
		float speedModifier = 3F;
		
		//TODO Anti-aircraft guns should rotates faster 
		
    	((EntityVehicle)driving).wheelsYaw += speedModifier*signDeltaX;
    	
    	return signDeltaX;
    }
}