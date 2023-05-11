package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.Item;
import java.util.Random;
import noppes.npcs.CustomItems;
import net.minecraft.item.ItemStack;
import noppes.npcs.blocks.tiles.TileDoor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.material.Material;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.BlockDoor;

public abstract class BlockNpcDoorInterface extends BlockDoor implements ITileEntityProvider
{
    public BlockNpcDoorInterface() {
        super(Material.WOOD);
        this.hasTileEntity = true;
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }
    
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDoor();
    }
    
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(CustomItems.scriptedDoorTool, 1, this.damageDropped(state));
    }
    
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }
    
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue((IProperty)BlockNpcDoorInterface.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
            IBlockState iblockstate1 = worldIn.getBlockState(pos.up());
            if (iblockstate1.getBlock() == this) {
                state = state.withProperty((IProperty)BlockNpcDoorInterface.HINGE, iblockstate1.getValue((IProperty)BlockNpcDoorInterface.HINGE)).withProperty((IProperty)BlockNpcDoorInterface.POWERED, iblockstate1.getValue((IProperty)BlockNpcDoorInterface.POWERED));
            }
        }
        else {
            IBlockState iblockstate1 = worldIn.getBlockState(pos.down());
            if (iblockstate1.getBlock() == this) {
                state = state.withProperty((IProperty)BlockNpcDoorInterface.FACING, iblockstate1.getValue((IProperty)BlockNpcDoorInterface.FACING)).withProperty((IProperty)BlockNpcDoorInterface.OPEN, iblockstate1.getValue((IProperty)BlockNpcDoorInterface.OPEN));
            }
        }
        return state;
    }
    
    public Block setTranslationKey(String name) {
		super.setTranslationKey(name);
		return this.setRegistryName("customnpcs", name);
    }
}
