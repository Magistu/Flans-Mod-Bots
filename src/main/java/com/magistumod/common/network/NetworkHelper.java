package com.magistumod.common.network;

import java.util.UUID;

import com.magistumod.common.network.messages.AbstractMessage;
import com.magistumod.common.storage.StorageHelper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NetworkHelper 
{
	public static void sendToCoalition(EntityPlayerMP player, AbstractMessage message) 
	{
		String coalitionName = StorageHelper.getCoalition(player.getUniqueID());
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (coalitionName != null) 
		{
			for (UUID playerId : StorageHelper.getCoalitionPlayers(coalitionName)) 
			{
				if (!playerId.equals(player.getUniqueID())) 
				{
					EntityPlayerMP coalitionPlayer = server.getPlayerList().getPlayerByUUID(playerId);
					if (coalitionPlayer != null) 
					{
						sendToPlayer(coalitionPlayer, message);
					}
				} 
			} 
		}
	}
  
	public static void sendToPlayer(EntityPlayerMP player, AbstractMessage message) 
	{
		PacketHandler.INSTANCE.sendTo((IMessage)message, player);
	}
  
	public static void sendToAll(AbstractMessage message) 
	{
		PacketHandler.INSTANCE.sendToAll((IMessage)message);
	}
  
	public static void sendToServer(AbstractMessage message) 
	{
		PacketHandler.INSTANCE.sendToServer((IMessage)message);
	}
}