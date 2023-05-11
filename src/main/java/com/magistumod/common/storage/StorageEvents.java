package com.magistumod.common.storage;

import com.magistumod.common.network.NetworkHelper;
import com.magistumod.common.network.messages.AbstractMessage;
import com.magistumod.common.network.messages.MessageSaveData;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@EventBusSubscriber(modid = "magistumod")
public class StorageEvents
{
	public static CoalitionDataManager data;
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void playerLogIn(PlayerEvent.PlayerLoggedInEvent event) 
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) 
		{
			NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());
			NetworkHelper.sendToPlayer((EntityPlayerMP)event.player, (AbstractMessage)new MessageSaveData());
		}
	}
}