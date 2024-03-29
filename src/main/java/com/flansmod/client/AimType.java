package com.flansmod.client;

import net.minecraft.util.text.translation.I18n;

public enum AimType {

	HOLD,
	TOGGLE;

	public String getName()
	{
		return I18n.translateToLocal(String.format("aimtype.%s.name", name().toLowerCase()));
	}

	public static AimType fromString(String input)
	{
		for(AimType aimType : values())
		{
			if(aimType.name().equalsIgnoreCase(input))
			{
				return aimType;
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}

}
