package com.magistumod.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class AbstractMessage implements IMessage {
	NBTTagCompound tag = new NBTTagCompound();
		
	public void fromBytes(ByteBuf buf) {
		this.tag = ByteBufUtils.readTag(buf);
	}
		
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.tag);
	}
}