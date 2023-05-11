package com.magistumod.common.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class MessageInvite extends AbstractMessage
{
	public MessageInvite() {}
	
	public MessageInvite(String coalitionName) 
	{
		this.tag.setString("coalitionName", coalitionName);
	}
	
	@SideOnly(Side.CLIENT)
	private static void displayToast(MessageInvite message) {}
	
	public static class MessageHandler implements IMessageHandler<MessageInvite, IMessage> 
	{
		public IMessage onMessage(MessageInvite message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> MessageInvite.displayToast(message));
			return null;
		}
	}
}
