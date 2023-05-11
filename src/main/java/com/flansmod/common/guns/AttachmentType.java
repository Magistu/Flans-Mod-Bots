package com.flansmod.common.guns;

import java.util.ArrayList;
import java.util.List;

import com.flansmod.common.util.Parser;
import net.minecraft.client.model.ModelBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.flansmod.client.model.ModelAttachment;
import com.flansmod.common.FlansMod;
import com.flansmod.common.paintjob.PaintableType;
import com.flansmod.common.types.TypeFile;

public class AttachmentType extends PaintableType implements IScope
{
	public static ArrayList<AttachmentType> attachments = new ArrayList<>();
	
	/**
	 * The type of attachment. Each gun can have one barrel, one scope, one
	 * grip, one stock and some number of generics up to a limit set by the gun
	 */
	public EnumAttachmentType type = EnumAttachmentType.generic;
	
	/**
	 * This variable controls whether or not bullet sounds should be muffled
	 */
	public boolean silencer = false;
	/**
	 * If true, then this attachment will act like a flashlight
	 */
	public boolean flashlight = false;
	/**
	 * Flashlight range. How far away it lights things up
	 */
	public float flashlightRange = 10F;
	/**
	 * Flashlight strength between 0 and 15
	 */
	public int flashlightStrength = 12;
	/** If true, disable the muzzle flash model */
	public boolean disableMuzzleFlash = false;
	
	// Gun behaviour modifiers
	/**
	 * These stack between attachments and apply themselves to the gun's default
	 * spread
	 */
	public float spreadMultiplier = 1F;
	/**
	 * Likewise these stack and affect recoil
	 */
	public float recoilMultiplier = 1F;
	/** The return to center force LOWER = BETTER */
	public float recoilControlMultiplier = 1F;
	public float recoilControlMultiplierSneaking = 1F;
	public float recoilControlMultiplierSprinting = 1F;
	/** Another stacking variable for damage */
	public float damageMultiplier = 1F;
	/**
	 * Melee damage modifier
	 */
	public float meleeDamageMultiplier = 1F;
	/**
	 * Bullet speed modifier
	 */
	public float bulletSpeedMultiplier = 1F;
	
	public float shootDelayMultiplier = 1f;
	/**
	 * This modifies the reload time, which is then rounded down to the nearest
	 * tick
	 */
	public float reloadTimeMultiplier = 1F;
	
	/** Movement speed modifier */
	public float moveSpeedMultiplier = 1F;
	/** If set to anything other than null, then this attachment will override the weapon's default firing mode */
	
	/**
	 * If set to anything other than null, then this attachment will override
	 * the weapon's default firing mode
	 */
	public EnumFireMode modeOverride = null;
	
	public EnumSpreadPattern spreadPattern = null;
	
	//Underbarrel functions
	/** This variable controls whether the underbarrel is enabled */
	public boolean secondaryFire = false;
	/** The list of bullet types that can be used in the secondary mode */
	public List<String> secondaryAmmo = new ArrayList<String>();
	/** The delay between shots in ticks (1/20ths of seconds) */
	public float secondaryDamage = 1;
	/** The delay between shots in ticks (1/20ths of seconds) */
	public float secondarySpread = 1;
	/** The speed of bullets upon leaving this gun */
	public float secondarySpeed = 5.0F;
	/** The time (in ticks) it takes to reload this gun */
	public int secondaryReloadTime = 1;
	/** The delay between shots in ticks (1/20ths of seconds) */
	public int secondaryShootDelay = 1;
	/** The sound played upon shooting */
	public String secondaryShootSound;
	/** The sound to play upon reloading */
	public String secondaryReloadSound;
	/** The firing mode of the gun. One of semi-auto, full-auto, minigun or burst */
	public EnumFireMode secondaryFireMode = EnumFireMode.SEMIAUTO;
	/** The sound to play if toggling between primary and underbarrel */
	public String toggleSound;
	/** The number of bullet entities created by each shot */
	public int secondaryNumBullets = 1;
	/** The number of bullet stacks in the magazine */
	public int numSecAmmoItems = 1;	
	
	// Scope variables (These variables only come into play for scope
	// attachments)
	/**
	 * The zoomLevel of this scope
	 */
	public float zoomLevel = 1F;
	/**
	 * The FOV zoom level of this scope
	 */
	public float FOVZoomLevel = 1F;
	/**
	 * The overlay to render when using this scope
	 */
	public String zoomOverlay;
	/**
	 * Whether to overlay a texture or not
	 */
	public boolean hasScopeOverlay = false;
	/** If true, then this scope will active night vision potion effect*/
	public boolean hasNightVision = false;
	
	@SideOnly(Side.CLIENT)
	/** Model. Only applicable when the attachment is added to 3D guns */
	public ModelAttachment model;
	/** For making detailed models and scaling down mainly */
	@SideOnly(Side.CLIENT)
	public float modelScale = 1F;
	
	// Some more mundane variables
	/**
	 * The max stack size in the inventory
	 */
	public int maxStackSize = 1;
	/** Default spread of the underbarrel. Do not modify. */
	public float secondaryDefaultSpread = 0F;
	
	public AttachmentType(TypeFile file)
	{
		super(file);
		attachments.add(this);
	}
	
	@Override
	protected void read(String[] split, TypeFile file)
	{
		super.read(split, file);
		try
		{
			if(split[0].equals("AttachmentType"))
				type = EnumAttachmentType.get(split[1]);
			else if(FMLCommonHandler.instance().getSide().isClient()
					&& (split[0].equals("Model")))
				model = FlansMod.proxy.loadModel(split[1], shortName,
						ModelAttachment.class, fileName, packName);
			else if(split[0].equals("ModelScale"))
				modelScale = Parser.parseFloat(split[1]);
			else if(split[0].equals("Texture"))
				texture = split[1];
			
			else if(split[0].equals("Silencer"))
				silencer = Boolean.parseBoolean(split[1].toLowerCase());
			else if(split[0].equals("DisableMuzzleFlash") || split[0].equals("DisableFlash"))
				disableMuzzleFlash = Boolean.parseBoolean(split[1].toLowerCase());
				
				// Flashlight settings
			else if(split[0].equals("Flashlight"))
				flashlight = Boolean.parseBoolean(split[1].toLowerCase());
			else if(split[0].equals("FlashlightRange"))
				flashlightRange = Parser.parseFloat(split[1]);
			else if(split[0].equals("FlashlightStrength"))
				flashlightStrength = Parser.parseInt(split[1]);
				// Mode override
			else if(split[0].equals("ModeOverride"))
				modeOverride = EnumFireMode.getFireMode(split[1]);
			
			//Secondary Stuff
			else if(split[0].equals("SecondaryMode"))
				secondaryFire = Boolean.parseBoolean(split[1].toLowerCase());
			else if(split[0].equals("SecondaryAmmo"))
				secondaryAmmo.add(split[1]);
			else if(split[0].equals("SecondaryDamage"))
				secondaryDamage = Parser.parseFloat(split[1]);
			else if(split[0].equals("SecondarySpread") || split[0].equals("SecondaryAccuracy"))
				secondarySpread = secondaryDefaultSpread = Parser.parseFloat(split[1]);
			else if(split[0].equals("SecondaryBulletSpeed"))
				secondarySpeed = Parser.parseFloat(split[1]);
			else if(split[0].equals("SecondaryShootDelay"))
				secondaryShootDelay = Parser.parseInt(split[1]);
			else if(split[0].equals("SecondaryReloadTime"))
				secondaryReloadTime = Parser.parseInt(split[1]);
			else if(split[0].equals("SecondaryShootDelay"))
				secondaryShootDelay = Parser.parseInt(split[1]);
			else if(split[0].equals("SecondaryNumBullets"))
				secondaryNumBullets = Parser.parseInt(split[1]);
			else if(split[0].equals("LoadSecondaryIntoGun"))
				numSecAmmoItems = Parser.parseInt(split[1]);
			else if(split[0].equals("SecondaryFireMode"))
				secondaryFireMode = EnumFireMode.getFireMode(split[1]);
			else if(split[0].equals("SecondaryShootSound"))
			{
				secondaryShootSound = split[1];
				FlansMod.proxy.loadSound(contentPack, "guns", split[1]);
			}
			else if(split[0].equals("SecondaryReloadSound"))
			{
				secondaryReloadSound = split[1];
				FlansMod.proxy.loadSound(contentPack, "guns", split[1]);
			}
			else if(split[0].equals("ModeSwitchSound"))
			{
				toggleSound = split[1];
				FlansMod.proxy.loadSound(contentPack, "guns", split[1]);
			}
			
				// Multipliers
			else if(split[0].equals("MeleeDamageMultiplier"))
				meleeDamageMultiplier = Parser.parseFloat(split[1]);
			else if(split[0].equals("DamageMultiplier"))
				damageMultiplier = Parser.parseFloat(split[1]);
			else if(split[0].equals("SpreadMultiplier"))
				spreadMultiplier = Parser.parseFloat(split[1]);
			else if(split[0].equals("RecoilMultiplier"))
				recoilMultiplier = Parser.parseFloat(split[1]);
			else if(split[0].equals("RecoilControlMultiplier"))
				recoilControlMultiplier = Parser.parseFloat(split[1]);
			else if(split[0].equals("RecoilControlMultiplierSneaking"))
				recoilControlMultiplierSneaking = Parser.parseFloat(split[1]);
			else if(split[0].equals("RecoilControlMultiplierSprinting"))
				recoilControlMultiplierSprinting = Float.parseFloat(split[1]);
			else if(split[0].equals("BulletSpeedMultiplier"))
				bulletSpeedMultiplier = Float.parseFloat(split[1]);
			else if(split[0].equals("ShootDelayMultiplier"))
				shootDelayMultiplier = Float.parseFloat(split[1]);
			else if(split[0].equals("ReloadTimeMultiplier"))
				reloadTimeMultiplier = Float.parseFloat(split[1]);
			else if(split[0].equals("MovementSpeedMultiplier"))
				moveSpeedMultiplier = Float.parseFloat(split[1]);
			
			if(split[0].equals("SpreadPattern"))
				spreadPattern = EnumSpreadPattern.get(split[1]);
			
				// Scope Variables
			else if(split[0].equals("ZoomLevel"))
				zoomLevel = Float.parseFloat(split[1]);
			else if(split[0].equals("FOVZoomLevel"))
				FOVZoomLevel = Float.parseFloat(split[1]);
			else if(split[0].equals("ZoomOverlay"))
			{
				hasScopeOverlay = true;
				if(split[1].equals("None"))
					hasScopeOverlay = false;
				else
					zoomOverlay = split[1];
			}
			else if(split[0].equals("HasNightVision"))
				hasNightVision = Boolean.parseBoolean(split[1].toLowerCase());
		}
		catch(Exception e)
		{
			FlansMod.log.error("Reading attachment file" + file.name + " failed from content pack " + file.contentPack);
			if (split != null)
			{
				FlansMod.log.error("Errored reading line: " + String.join(" ", split));
			}
			FlansMod.log.throwing(e);
		}
	}
	
	/**
	 * To be overriden by subtypes for model reloading
	 */
	public void reloadModel()
	{
		model = FlansMod.proxy.loadModel(modelString, shortName,
				ModelAttachment.class);
	}
	
	public static AttachmentType getFromNBT(NBTTagCompound tags)
	{
		ItemStack stack = new ItemStack(tags);
		if(stack != null && stack.getItem() instanceof ItemAttachment)
			return ((ItemAttachment)stack.getItem()).type;
		return null;
	}
	
	@Override
	public float getZoomFactor()
	{
		return zoomLevel;
	}
	
	@Override
	public boolean hasZoomOverlay()
	{
		return hasScopeOverlay;
	}
	
	@Override
	public String getZoomOverlay()
	{
		return zoomOverlay;
	}
	
	@Override
	public float getFOVFactor()
	{
		return FOVZoomLevel;
	}
	
	public static AttachmentType getAttachment(String s)
	{
		for(AttachmentType attachment : attachments)
		{
			if(attachment.shortName.equals(s))
				return attachment;
		}
		return null;
	}
	
	@Override
	public void preRead(TypeFile file)
	{
		super.preRead(file);
	}
	
	@Override
	public void postRead(TypeFile file)
	{
		super.postRead(file);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBase GetModel()
	{
		return model;
	}
	
	@Override
	public float GetRecommendedScale()
	{
		return 100.0f;
	}
}
