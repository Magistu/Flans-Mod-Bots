package com.magistumod.common.guns;

import com.flansmod.common.types.InfoType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class EntityDamageSourceFlan2 extends EntityDamageSourceIndirect {

	private InfoType weapon;
	private boolean headshot;
	/**
	 * @param s        Name of the damage source (Usually the shortName of the gun)
	 * @param entity   The Entity causing the damage (e.g. Grenade). Can be the same as 'player'
	 * @param wep      The InfoType of weapon used
	 */
	public EntityDamageSourceFlan2(String s, Entity entity, Entity source, InfoType wep)
	{
		this(s, entity, source, wep, false);
	}

	/**
	 * @param s        Name of the damage source (Usually the shortName of the gun)
	 * @param entity   The Entity causing the damage (e.g. Grenade). Can be the same as 'player'
	 * @param wep      The InfoType of weapon used
	 * @param headshot True if this was a headshot, false if not
	 */
	public EntityDamageSourceFlan2(String s, Entity entity, Entity source, InfoType wep, boolean headshot)
	{
		super(s, entity, source);
		this.weapon = wep;
		this.headshot = headshot;
	}

	@Override
	public ITextComponent getDeathMessage(EntityLivingBase living)
	{
		if(!(living instanceof EntityPlayer) || this.getTrueSource() == null)
		{
			return super.getDeathMessage(living);
		}

		return new TextComponentString("#flansmod");
	}

	/**
	 * @return The weapon (InfoType) used to cause this damage
	 */
	public InfoType getWeapon()
	{
		return weapon;
	}

	/**
	 * @return True if this is a headshot, false if not
	 */
	public boolean isHeadshot()
	{
		return headshot;
	}
}
