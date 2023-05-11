package com.magistumod.entity.ai;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

public class EntityAIOpenAnyDoor extends EntityAIBase 
{
	private EntityCreature entity;
	private BlockPos position;
	private Block door;
	private IProperty property;
	private boolean hasStoppedDoorInteraction;
	private float entityX;
	private float entityZ;
	private int closeDoorTemporisation;
	
	public EntityAIOpenAnyDoor(EntityCreature entity) 
	{
		this.entity = entity;
	}
	
	public boolean shouldExecute() 
	{
		if (!this.entity.collidedHorizontally) 
		{
			return false;
		}
		
		Path pathentity = this.entity.getNavigator().getPath();
		
		if (pathentity != null && !pathentity.isFinished()) 
		{
			
			for (int i = 0; i < Math.min(pathentity.getCurrentPathIndex() + 2, pathentity.getCurrentPathLength()); i++) 
			{
				
				PathPoint pathpoint = pathentity.getPathPointFromIndex(i);
				this.position = new BlockPos(pathpoint.x, pathpoint.y + 1, pathpoint.z);
				
				if (this.entity.getDistanceSq(this.position.getX(), this.entity.posY, this.position.getZ()) <= 2.25D) 
				{
					
					this.door = getDoor(this.position);
					
					if (this.door != null)
					{
						return true;
					}
				} 
			} 
			
			this.position = (new BlockPos((Entity)this.entity)).up();
			this.door = getDoor(this.position);
			return (this.door != null);
		} 
		return false;
	}




	
	public boolean shouldContinueExecuting() 
	{
		return (this.closeDoorTemporisation > 0 && !this.hasStoppedDoorInteraction);
	}

	
	public void startExecuting() 
	{
		this.hasStoppedDoorInteraction = false;
		this.entityX = (float)((this.position.getX() + 0.5F) - this.entity.posX);
		this.entityZ = (float)((this.position.getZ() + 0.5F) - this.entity.posZ);
		this.closeDoorTemporisation = 20;
		setDoorState(this.door, this.position, true);
	}
	
	public void resetTask() 
	{
		setDoorState(this.door, this.position, false);
	}
	
	public void updateTask() 
	{
		this.closeDoorTemporisation--;
		float f = (float)((this.position.getX() + 0.5F) - this.entity.posX);
		float f1 = (float)((this.position.getZ() + 0.5F) - this.entity.posZ);
		float f2 = this.entityX * f + this.entityZ * f1;
		
		if (f2 < 0.0F) 
		{
			this.hasStoppedDoorInteraction = true;
		}
	}
	
	public Block getDoor(BlockPos pos) 
	{
		IBlockState state = this.entity.world.getBlockState(pos);
		Block block = state.getBlock();
		if (state.isFullBlock() || block == Blocks.IRON_DOOR)
			return null; 
		if (block instanceof BlockDoor)
			return block; 
		
		final Set<IProperty<?>> set = (Set<IProperty<?>>)state.getProperties().keySet();
        for (final IProperty prop : set) 
        {
            if (prop instanceof PropertyBool && prop.getName().equals("open")) 
            {
                this.property = prop;
                return block;
            }
        }
		
		return null;
	}
	
	public void setDoorState(Block block, BlockPos position, boolean open) 
	{
		if (block instanceof BlockDoor) 
		{
			((BlockDoor)block).toggleDoor(this.entity.world, position, open);
		}
		else 
		{
			
			IBlockState state = this.entity.world.getBlockState(position);
			if (state.getBlock() != block)
				return; 
			this.entity.world.setBlockState(position, state.withProperty(this.property, Boolean.valueOf(open)));
			this.entity.world.playEvent((EntityPlayer)null, open ? 1003 : 1006, position, 0);
		} 
	}
}