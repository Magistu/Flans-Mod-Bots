package com.magistumod;

import com.magistumod.entity.EntitySoldier;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntityRegistry
{
	
	static int id = 400;
	
	public static void mainRegistry()
	{
		modRegisterEntities();
	}
	
	public static void modRegisterEntities()
	{
		
		registerEntity(EntitySoldier.class, "Custom Soldier");
	}
	
	private static void registerEntity(Class<? extends Entity> entity, String name)
	{
		id++;
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID + ":" + entity.toString()), entity, name, id, Main.instance, 400, 1, true, 000000, 000000);
	}
}
