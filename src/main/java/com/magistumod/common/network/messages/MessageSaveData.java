package com.magistumod.common.network.messages;

import com.magistumod.client.ClientHelper;
import com.magistumod.common.storage.StorageHandler;
import com.magistumod.common.storage.StorageHelper;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSaveData extends AbstractMessage
{
	public static class MessageHandler implements IMessageHandler<MessageSaveData, IMessage>
	{
		public IMessage onMessage(MessageSaveData message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
				StorageHandler.readFromNBT(message.tag);
				String myCoalition = StorageHelper.getCoalition(ClientHelper.mc.player.getUniqueID());
			});
			return null;
		}
	}
}
