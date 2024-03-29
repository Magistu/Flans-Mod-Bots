package com.flansmod.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.flansmod.client.ClientRenderHooks;
import com.flansmod.common.FlansMod;
import com.flansmod.common.types.InfoType;

public class PacketKillMessage extends PacketBase
{
	public InfoType killedBy;
	public int itemDamage;
	public String killerName;
	public String killedName;
	public boolean headshot;
	public float distance;
	
	public PacketKillMessage()
	{
		
	}
	
	public PacketKillMessage(boolean head, InfoType weapon, int itmDmg, String victim, String murderer, Float dist)
	{
		killedBy = weapon;
		itemDamage = itmDmg;
		killerName = murderer;
		killedName = victim;
		headshot = head;
		distance = dist;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{
		data.writeBoolean(headshot);
		writeUTF(data, killedBy.shortName);
		data.writeInt(itemDamage);
		writeUTF(data, killerName);
		writeUTF(data, killedName);
		data.writeFloat(distance);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{
		headshot = data.readBoolean();
		killedBy = InfoType.getType(readUTF(data));
		itemDamage = data.readInt();
		killerName = readUTF(data);
		killedName = readUTF(data);
		distance = data.readFloat();
	}
	
	@Override
	public void handleServerSide(EntityPlayerMP playerEntity)
	{
		//FlansMod.log.warn("Received kill message packet on the server. Skipping.");
		FlansMod.log.info("Player kill Killer: " + killerName + " Killed " + killedName + " using: " + killedBy.shortName + " Headshot: " + headshot);
		FlansMod.log.info("Distance " + distance);
	}
	
	@Override
	public void handleClientSide(EntityPlayer clientPlayer)
	{
		ClientRenderHooks.addKillMessage(headshot, killedBy, killerName, killedName);
	}
	
}
