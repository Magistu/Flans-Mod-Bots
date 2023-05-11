package com.magistumod.client;

import com.magistumod.IProxy;
import com.magistumod.common.CommonProxy;
import com.magistumod.entity.EntitySoldier;
import com.magistumod.render.RenderSoldier;

import net.minecraft.client.model.ModelBiped;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ClientProxy extends CommonProxy implements IProxy
{
	@Override
	public void init(FMLInitializationEvent event) {}

	@Override
	public void preInit(FMLPreInitializationEvent event) 
	{
		OBJLoader.INSTANCE.addDomain("magistumod");
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {}

	@Override
	public void serverLoad(FMLServerStartingEvent event) {}
	
	@Override
	public void registerRenderThings()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntitySoldier.class, manager -> new RenderSoldier(manager, new ModelBiped(), 0));
	}
}
