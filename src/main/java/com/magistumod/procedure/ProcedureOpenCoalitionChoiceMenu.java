package com.magistumod.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import com.magistumod.Elements;
import com.magistumod.Main;

import com.magistumod.common.storage.StorageHelper;
import com.magistumod.gui.GuiCoalitions;

@Elements.ModElement.Tag
public class ProcedureOpenCoalitionChoiceMenu extends Elements.ModElement 
{
	public ProcedureOpenCoalitionChoiceMenu(Elements instance) 
	{
		super(instance, 7);
	}

	public static void executeProcedure(java.util.HashMap<String, Object> dependencies) 
	{
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if (entity instanceof EntityPlayer && !StorageHelper.isPlayerInCoalition(entity.getUniqueID()))
			((EntityPlayer) entity).openGui(Main.instance, GuiCoalitions.GUIID, world, x, y, z);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) 
	{
		Entity entity = event.player;
		java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
		dependencies.put("x", (int) entity.posX);
		dependencies.put("y", (int) entity.posY);
		dependencies.put("z", (int) entity.posZ);
		dependencies.put("world", entity.world);
		dependencies.put("entity", entity);
		dependencies.put("event", event);
		this.executeProcedure(dependencies);
	}

	@SubscribeEvent
	public void onPlayerRespawned(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) 
	{
		Entity entity = event.player;
		java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
		dependencies.put("x", (int) entity.posX);
		dependencies.put("y", (int) entity.posY);
		dependencies.put("z", (int) entity.posZ);
		dependencies.put("world", entity.world);
		dependencies.put("entity", entity);
		dependencies.put("endconquered", event.isEndConquered());
		dependencies.put("event", event);
		this.executeProcedure(dependencies);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) 
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
}
