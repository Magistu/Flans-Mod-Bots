package com.magistumod.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.network.PacketDriveableControl;

public class PacketVehicleControl2 extends PacketDriveableControl
{
	public boolean doors;
	
	public PacketVehicleControl2()
	{
	}
	
	public PacketVehicleControl2(EntityDriveable driveable)
	{
		super(driveable);
		EntityVehicle vehicle = (EntityVehicle)driveable;
		doors = vehicle.varDoor;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{
		super.encodeInto(ctx, data);
		data.writeBoolean(doors);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{
		super.decodeInto(ctx, data);
		doors = data.readBoolean();
		
		data.release();
	}
	
	@Override
	protected void updateDriveable(EntityDriveable driveable, boolean clientSide)
	{
		super.updateDriveable(driveable, clientSide);
		EntityVehicle vehicle = (EntityVehicle)driveable;
		vehicle.varDoor = doors;
		System.out.println("Point 1");
		
		if(!clientSide)
		{
			System.out.println("Point 2");
			FlansMod.getPacketHandler().sendToAllAround(new PacketVehicleControl2(vehicle),
					posX,
					posY,
					posZ,
					FlansMod.driveableUpdateRange,
					vehicle.dimension);
		}
	}
}
