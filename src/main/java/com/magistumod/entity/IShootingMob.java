package com.magistumod.entity;

import net.minecraft.entity.EntityLivingBase;

public interface IShootingMob 
{
	
    int shootEntity(EntityLivingBase target, float distanceFactor);

    void setSwingingArms(boolean swingingArms);
}
