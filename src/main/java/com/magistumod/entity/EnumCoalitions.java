package com.magistumod.entity;

public enum EnumCoalitions {
	UNDEFINED, AXIS, ALLIES;

	public static EnumCoalitions getCoalition(String s) {
		if(s.equals("AXIS"))
			return AXIS;
		if(s.equals("ALLIES"))
			return ALLIES;
		return UNDEFINED;
	}
}
