package com.magistumod.common.network;


import com.magistumod.common.network.messages.MessageInvite;
import com.magistumod.common.network.messages.MessageSaveData;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler 
{
	static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("coalitionsmod");
	
	public static void register(Side side) 
	{
		int id = 0;
		    
		INSTANCE.registerMessage(MessageSaveData.MessageHandler.class, MessageSaveData.class, ++id, side);
		INSTANCE.registerMessage(MessageInvite.MessageHandler.class, MessageInvite.class, ++id, side);
	}
}
