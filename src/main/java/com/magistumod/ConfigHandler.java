package com.magistumod;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler
{
	public static Configuration config;
	
	//Constants
	public static float VEHICLE_FIRING_RANGE = 148.0F;
	public static float INFANTRY_FIRING_RANGE = 128.0F;
	
	public static void init(File configDirectory)
	{
		File configFile = new File(configDirectory, Reference.MODID.toLowerCase() + ".cfg");

		if (config == null)
		{
			config = new Configuration(configFile, "2.0");
		}
		
		loadConfig();
	}
	
	

	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent ev) 
	{
		if (ev.getModID().equalsIgnoreCase(Reference.MODID))
		{
			loadConfig();
		}
	}
	
	
	private static void loadConfig() 
	{
		VEHICLE_FIRING_RANGE = config.getFloat("vehicleFiringRange", "range", 148.0F, 0.0F, 500.0F, "This is an boolean. Default value is true.");
		INFANTRY_FIRING_RANGE = config.getFloat("infantryFiringRange", "range", 128.0F, 0.0F, 500.0F, "This is an boolean. Default value is true.");

		if (config.hasChanged())
		{
			config.save();
		}
	}
}
