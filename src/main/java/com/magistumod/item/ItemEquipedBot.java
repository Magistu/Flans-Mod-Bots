package com.magistumod.item;

import com.magistumod.entity.EntitySoldier;
import com.magistumod.entity.EnumCoalitions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemEquipedBot extends Item
{
	public ItemEquipedBot()
	{
		this.maxStackSize = 1;
		this.setRegistryName("equiped_bot");
        this.setTranslationKey("equiped_bot");
	}
    
	@Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) 
    {
        if (world.isRemote) 
        {
            return EnumActionResult.SUCCESS;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (spawn(player, stack, world, pos) == null) 
        {
            return EnumActionResult.FAIL;
        }
        if (!player.capabilities.isCreativeMode) 
        {
            stack.splitStack(1);
        }
        return EnumActionResult.SUCCESS;
    }
    
    public static Entity spawn(EntityPlayer player, ItemStack stack, World world, BlockPos pos) 
    {
        if (world.isRemote) 
        {
            return null;
        }
        
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("Info", 10)) 
        {
            return null;
        }
        
        NBTTagCompound compound = stack.getTagCompound().getCompoundTag("Info");
        
        EntitySoldier entity = new EntitySoldier(world);
        entity.initEquipment(
        		new ItemStack(Item.getByNameOrId(compound.getString("Weapon"))), 
        		compound.getInteger("AmmoAmount"), 
				new ItemStack(Item.getByNameOrId(compound.getString("Helmet"))), 
				new ItemStack(Item.getByNameOrId(compound.getString("Chest"))), 
				new ItemStack(Item.getByNameOrId(compound.getString("Leggings"))), 
				new ItemStack(Item.getByNameOrId(compound.getString("Boots")))
				);
        entity.initAI(EnumCoalitions.getCoalition(compound.getString("Coalition")), compound.getInteger("TargetPriority"));
        entity.setPosition(pos.getX() + 0.5, (double)(pos.getY() + 1 + 0.2f), pos.getZ() + 0.5);
        
        if (!world.spawnEntity(entity)) 
        {
            player.sendMessage((ITextComponent)new TextComponentTranslation("error.failedToSpawn", new Object[0]));
            return null;
        }
        return entity;
    }
}
