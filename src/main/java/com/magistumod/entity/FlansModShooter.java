package com.magistumod.entity;

import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.driveables.EnumDriveablePart;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.FireableGun;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.guns.ItemShootable;
import com.flansmod.common.guns.ShootableType;
import com.flansmod.common.network.PacketPlaySound;
import com.flansmod.common.vector.Vector3f;
import com.magistumod.ConfigHandler;
import com.magistumod.Main;
import com.magistumod.common.guns.FiredShot2;
import com.magistumod.common.guns.ShotHandler2;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.item.ItemSword;

public abstract class FlansModShooter extends EntityCreature implements IShootingMob
{
	public ItemStack[] ammoStacks;
	public int shootDelay = 10;
	public float minigunSpeed = 0.0F;
	public int loopedSoundDelay = 0;
	public boolean reloading = false;
	public boolean shouldPlayWarmupSound = true;
	private int soundDelay = 0;
	public int debugTimer;
	
	public void addEquipment(ItemStack stack)
	{
		if (!world.isRemote) 
		{
			
			Item item = stack.getItem();
			if (item instanceof ItemGun)
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
			if (item instanceof ItemArmor)
			{
				if (((ItemArmor)item).armorType == EntityEquipmentSlot.HEAD)
					setItemStackToSlot(EntityEquipmentSlot.HEAD, stack);
				if (((ItemArmor)item).armorType == EntityEquipmentSlot.CHEST)
					setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
				if (((ItemArmor)item).armorType == EntityEquipmentSlot.LEGS)
					setItemStackToSlot(EntityEquipmentSlot.LEGS, stack);
				if (((ItemArmor)item).armorType == EntityEquipmentSlot.FEET)
					setItemStackToSlot(EntityEquipmentSlot.FEET, stack);
			}
		}
	}
	
	public void initEquipment(ItemStack stack, int numAmmo)
    {
		ammoStacks = new ItemStack[0];
		
		if(!world.isRemote)
		{
			if (stack.getItem() instanceof ItemGun)
			{
				//Add gun
				GunType gun = ((ItemGun)stack.getItem()).GetType();
				NBTTagCompound tags = new NBTTagCompound();
				if(gun.ammo.size() > 0)
				{
					NBTTagList ammoTagsList = new NBTTagList();
					for(int i = 0; i < gun.numAmmoItemsInGun; i++)
					{
						NBTTagCompound ammoTag = new NBTTagCompound();
						ShootableType ammoType = gun.ammo.get(gun.ammo.size() - 1);
						ItemStack ammoStack = new ItemStack(ammoType.item);
						ammoStack.writeToNBT(ammoTag);
						ammoTagsList.appendTag(ammoTag);
					}
					tags.setTag("ammo", ammoTagsList);
				}
				stack.setTagCompound(tags);
			
				//Add ammo
				ammoStacks = new ItemStack[numAmmo];
				for(int i = 0; i < numAmmo; i++)
				{
					ShootableType type = gun.ammo.get(gun.ammo.size() - 1);
					ammoStacks[i] = new ItemStack(type.item);
				}
			}
		}

		for(int i = 0; i < inventoryArmorDropChances.length; ++i)
		{
			inventoryArmorDropChances[i] = 0.5F;
		}
		inventoryHandsDropChances[0] = 1F;

		experienceValue = 20;
    }
	
	public void initEquipment(ItemStack stack, int numAmmo, ItemStack helmet, ItemStack chest, ItemStack leggings, ItemStack boots)
	{
		initEquipment(stack, numAmmo);
		
		if(!world.isRemote)
		{
			//Add weapon
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
			
			//Add armour
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, helmet);
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, chest);
			this.setItemStackToSlot(EntityEquipmentSlot.LEGS, leggings);
			this.setItemStackToSlot(EntityEquipmentSlot.FEET, boots);
		}
	}
	
	public FlansModShooter(World worldIn)
    {
        super(worldIn);
        this.setSize(0.6F, 1.99F);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);

		if(world != null && world.isRemote)
		{
			setRenderDistanceWeight(200D);
		}
    }

    public FlansModShooter(World worldIn, ItemStack stack, int numAmmo, ItemStack helmet, ItemStack chest, ItemStack leggings, ItemStack boots)
    {
        super(worldIn);
        this.setSize(0.6F, 1.99F);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);

		if(world != null && world.isRemote)
		{
			setRenderDistanceWeight(200D);
		}
		
		initEquipment(stack, numAmmo, helmet, chest, leggings, boots);

		for(int i = 0; i < inventoryArmorDropChances.length; ++i)
		{
			inventoryArmorDropChances[i] = 0.5F;
		}
		inventoryHandsDropChances[0] = 1F;

		experienceValue = 20;
    }
    
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if(shootDelay > 0)
			shootDelay--;
		if(--debugTimer == 0)
			this.dismountRidingEntity();
	}
	
	//
    
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ConfigHandler.INFANTRY_FIRING_RANGE);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

    protected void entityInit()
    {
        super.entityInit();

    	debugTimer = 100;
    }
    
    

    /**
     * Get this Entity's EnumCoalitions
     */
    public EnumCoalitions getCoalition()
    {
        return EnumCoalitions.UNDEFINED;
    }
    
    public abstract void removeAvoidTask();
    
    public abstract void addAvoidTask();
    
    public abstract void removeFollowTask();
    
    public abstract void addFollowTask(EntityLiving follower, EntityPlayer commander, float followRadius, float stopDistance, double movespeed);

    public abstract void removeControlTask();
    
    public abstract void addControlTask(EntitySeat seat, int j, float r);
    
    /**
     * Attack the specified entity using a melee attack.
     */
    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
    	float f = 1.0f;
    	if (getHeldItemMainhand().getItem() instanceof ItemSword)
    	{
    		f = ((ItemSword)getHeldItemMainhand().getItem()).getAttackDamage();
    	}
        int i = 0;

        if (entityIn instanceof EntityLivingBase)
        {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entityIn).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag)
        {
            if (i > 0 && entityIn instanceof EntityLivingBase)
            {
                ((EntityLivingBase)entityIn).knockBack(this, (float)i * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0)
            {
                entityIn.setFire(j * 4);
            }

            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }
	
    /**
     * Attack the specified entity using a ranged attack.
     */
    @Override
	public int shootEntity(EntityLivingBase entity, float range)
	{
		ItemStack stack = getHeldItemMainhand();
		if(stack != null && stack.getItem() instanceof ItemGun)
		{
			ItemGun item = (ItemGun)stack.getItem();
			GunType type = item.GetType();
			boolean shouldShoot;
			switch(type.mode)
			{
				case MINIGUN:
					shouldShoot = minigunSpeed >= type.minigunStartSpeed && shootDelay <= 0;
					break;
				case BURST:
				case FULLAUTO:
				case SEMIAUTO:
					shouldShoot = shootDelay <= 0;
					break;
			}
			
			if(type.useLoopingSounds && loopedSoundDelay <= 0 && minigunSpeed > 0.1F && !reloading)
			{
				loopedSoundDelay = shouldPlayWarmupSound ? type.warmupSoundLength : type.loopedSoundLength;
				PacketPlaySound.sendSoundPacket(posX, posY, posZ, FlansMod.soundRange, dimension, shouldPlayWarmupSound ? type.warmupSound : type.loopedSound, false);
				shouldPlayWarmupSound = false;
			}
	
			//player.inventory.setInventorySlotContents(player.inventory.currentItem, tryToShoot(itemstack, type, world, player, false));
			int damage = 0;
			//Check all gun's slots for a valid bullet to shoot
			int bulletID = 0;
			ItemStack bulletStack = ItemStack.EMPTY.copy();
			for(; bulletID < type.numAmmoItemsInGun; bulletID++)
			{
				ItemStack checkingStack = item.getBulletItemStack(stack, bulletID);
				if(checkingStack != null && !checkingStack.isEmpty() && checkingStack.getItemDamage() < checkingStack.getMaxDamage())
				{
					bulletStack = checkingStack;
					break;
				}
			}
			
			//If no bullet stack was found, reload
			if(bulletStack == null || bulletStack.isEmpty())
			{
				if(reload(stack, type, world, this, false, false))
				{
					//Set player shoot delay to be the reload delay
					//Set both gun delays to avoid reloading two guns at once
					shootDelay = (int)type.getReloadTime(stack);
					
					reloading = true;
					
					//Play reload sound
					if(type.reloadSound != null)
						PacketPlaySound.sendSoundPacket(posX, posY, posZ, FlansMod.soundRange, dimension, type.reloadSound, true);
				}
			}
			//A bullet stack was found, so try shooting with it
			else if(bulletStack.getItem() instanceof ItemShootable)
			{
				//Shoot
				shoot(stack, type, world, bulletStack, this, false, entity);
				//Damage the bullet item
				damage = bulletStack.getItemDamage() + 1;
				bulletStack.setItemDamage(damage);
				
				//Update the stack in the gun
				item.setBulletItemStack(stack, bulletStack, bulletID);
				
				switch(type.mode)
				{
					case FULLAUTO: case MINIGUN:
						shootDelay = (int)type.shootDelay;
						break;
					case SEMIAUTO:
						shootDelay = (int)type.shootDelay;
						break;
					case BURST:
						shootDelay = (damage % 3 == 0 ? 3 * shootDelay : shootDelay);
						break;
				}
			}
		}
		return shootDelay;
	}
	
	/**
	 * Reload method. Called automatically when firing with an empty clip
	 */
	public boolean reload(ItemStack gunStack, GunType gunType, World world, Entity entity, boolean creative, boolean forceReload)
	{
		ItemGun item = ((ItemGun)gunType.item);
		//Deployable guns cannot be reloaded in the inventory
		if(gunType.deployable)
			return false;
		//If you cannot reload half way through a clip, reject the player for trying to do so
		if(forceReload && !gunType.canForceReload)
			return false;
		//For playing sounds afterwards
		boolean reloadedSomething = false;
		//Check each ammo slot, one at a time
		for(int i = 0; i < gunType.numAmmoItemsInGun; i++)
		{
			//Get the stack in the slot
			ItemStack bulletStack = item.getBulletItemStack(gunStack, i);
			
			//If there is no magazine, if the magazine is empty or if this is a forced reload
			if(bulletStack == null || bulletStack.isEmpty() || bulletStack.getItemDamage() == bulletStack.getMaxDamage() || forceReload)
			{
				//Iterate over all inventory slots and find the magazine / bullet item with the most bullets
				int bestSlot = -1;
				int bulletsInBestSlot = 0;
				for(int j = 0; j < ammoStacks.length; j++)
				{
					ItemStack searchingStack = ammoStacks[j];
					if(searchingStack != null && searchingStack.getItem() instanceof ItemShootable && gunType.isCorrectAmmo(((ItemShootable)(searchingStack.getItem())).type))
					{
						int bulletsInThisSlot = searchingStack.getMaxDamage() - searchingStack.getItemDamage();
						if(bulletsInThisSlot > bulletsInBestSlot)
						{
							bestSlot = j;
							bulletsInBestSlot = bulletsInThisSlot;
						}
					}
				}
				//If there was a valid non-empty magazine / bullet item somewhere in the inventory, load it
				if(bestSlot != -1)
				{
					ItemStack newBulletStack = ammoStacks[bestSlot];
					ShootableType newBulletType = ((ItemShootable)newBulletStack.getItem()).type;
					//Unload the old magazine (Drop an item if it is required and the player is not in creative mode)
					if(bulletStack != null && bulletStack.getItem() instanceof ItemShootable && ((ItemShootable)bulletStack.getItem()).type.dropItemOnReload != null && !creative)
						item.dropItem(world, this, ((ItemShootable)bulletStack.getItem()).type.dropItemOnReload);
					
					//Load the new magazine
					ItemStack stackToLoad = newBulletStack.copy();
					stackToLoad.setCount(1);
					item.setBulletItemStack(gunStack, stackToLoad, i);
					
					//Remove the magazine from the inventory
					if(!creative)
						newBulletStack.setCount(newBulletStack.getCount() - 1);
					if(newBulletStack.getCount() <= 0)
						newBulletStack = null;
					ammoStacks[bestSlot] = newBulletStack;

					//Tell the sound player that we reloaded something
					reloadedSomething = true;
				}
			}
		}
		return reloadedSomething;
	}
	
	/**
	 * Method for shooting to avoid repeated code
	 */
	private void shoot(ItemStack stack, GunType gunType, World world, ItemStack bulletStack, Entity entity, boolean left, EntityLivingBase target)
	{
		ShootableType bullet = ((ItemShootable)bulletStack.getItem()).type;
		// Play a sound if the previous sound has finished
		if(soundDelay <= 0 && gunType.shootSound != null)
		{
			AttachmentType barrel = gunType.getBarrel(stack);
			boolean silenced = barrel != null && barrel.silencer;
			PacketPlaySound.sendSoundPacket(posX, posY, posZ, FlansMod.soundRange * 4, dimension, gunType.shootSound, gunType.distortSound, silenced);
			soundDelay = gunType.shootSoundLength;
		}
		if(!world.isRemote)
		{
			if (target instanceof EntityPlayer)
			{
				
			}
			
			float inaccuracy = 1.5F;
			
			switch(gunType.mode)
			{
				case MINIGUN:
					inaccuracy = 2.0F;
				case FULLAUTO:
				case SEMIAUTO:
					inaccuracy = 0.08F;
				case BURST:
			}
			
			if (gunType.hasScopeOverlay)
			{
				inaccuracy = 0.005F;
			}
			
			double dX;
    		double dY;
    		double dZ;
			
			if(target.isRiding() && target.getRidingEntity() instanceof EntitySeat)
			{
				EntityDriveable targetDriveable = ((EntitySeat)target.getRidingEntity()).driveable;
				Vec3d driveableTargetPos = targetDriveable.axes.findLocalVectorGlobally(targetDriveable.getDriveableData().parts.get(EnumDriveablePart.core).box.getCentre()).toVec3().add(targetDriveable.getPositionVector());

				dX = driveableTargetPos.x - posX;
				dY = driveableTargetPos.y - (posY + getEyeHeight());
				dZ = driveableTargetPos.z - posZ;
			}
			else
			{
				dX = target.posX - posX;
				dY = target.posY + target.getEyeHeight() - (posY + getEyeHeight());
				dZ = target.posZ - posZ;
			}
			
    		double heightDifference = 0;
    		
    		if (bullet.explodeOnImpact) {
    			inaccuracy += 0.4F;
//				
//				double a = 0.02F * bullet.fallSpeed;
//				double v = gunType.getBulletSpeed(stack);
//				dX += dX * (target.motionX / v);
//				dY -= target.getEyeHeight() / 2;
//				dZ += dZ * (target.motionZ / v);
//				
//				heightDifference = (-a*dY + v*v - Math.sqrt(-a*a*dX*dX - a*a*dZ*dZ - 2*a*v*v*dY + v*v*v*v))/a;
//				
//				inaccuracy += 0.2F;
    		}

			// Spawn the bullet entities
			Vector3f origin = new Vector3f(posX, posY + getEyeHeight(), posZ);
			Vector3f direction = new Vector3f(dX, dY + heightDifference, dZ).normalise(null);
			Vector3f.add(direction, new Vector3f(rand.nextFloat() * direction.x * inaccuracy, rand.nextFloat() * direction.y * inaccuracy, rand.nextFloat() * direction.z * inaccuracy), direction);
			
			FireableGun fireableGun = new FireableGun(gunType, gunType.getDamage(stack), gunType.getSpread(stack), gunType.getBulletSpeed(stack), gunType.getSpreadPattern(stack));
			
			FiredShot2 shot = new FiredShot2(fireableGun, (BulletType)bullet, this, entity);
			
			ShotHandler2.fireGun(world, shot, gunType.numBullets*bullet.numBullets, origin, direction);
		}
	}
	
	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return true;
	}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
    }
    
    public void setSwingingArms(boolean swingingArms)
    {
        
    }
}