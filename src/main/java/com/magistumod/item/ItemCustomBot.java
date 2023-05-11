package com.magistumod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;

import com.magistumod.procedure.ProcedureCustomBotOpenMenu;
import com.magistumod.Elements;

@Elements.ModElement.Tag
public class ItemCustomBot extends Elements.ModElement {
	@GameRegistry.ObjectHolder("magistumod:custom_bot")
	public static final Item block = null;
	public ItemCustomBot(Elements instance) {
		super(instance, 4);
	}

	@Override
	public void initElements() 
	{
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("magistumod:custom_bot", "inventory"));
	}
	
	public static class ItemCustom extends Item 
	{
		public ItemCustom() 
		{
			setMaxDamage(0);
			maxStackSize = 64;
			setRegistryName("custom_bot");
			setTranslationKey("custom_bot");
			setCreativeTab(CreativeTabs.MISC);
		}
		
		public void clickedEntity(World world, EntityPlayer player, Entity clicked)
		{
			clicked.setDead();
		}

		@Override
		public int getItemEnchantability() 
		{
			return 0;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) 
		{
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) 
		{
			return 1F;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) 
		{
			ActionResult<ItemStack> ar = super.onItemRightClick(world, entity, hand);
			ItemStack itemstack = ar.getResult();
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			{
				java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("world", world);
				ProcedureCustomBotOpenMenu.executeProcedure($_dependencies);
			}
			return ar;
		}

		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer entity, World world, BlockPos pos, EnumFacing direction, float hitX, float hitY,
				float hitZ, EnumHand hand) 
		{
			EnumActionResult retval = super.onItemUseFirst(entity, world, pos, direction, hitX, hitY, hitZ, hand);
			ItemStack itemstack = entity.getHeldItem(hand);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			{
				java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("world", world);
				ProcedureCustomBotOpenMenu.executeProcedure($_dependencies);
			}
			return retval;
		}
	}
}
