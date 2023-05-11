package noppes.npcs.blocks;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.EnumBlockRenderType;
import noppes.npcs.blocks.tiles.TileRedstoneBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.IProperty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.CustomItems;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import noppes.npcs.util.IPermission;

public class BlockNpcRedstone extends BlockInterface implements IPermission
{
    public static PropertyBool ACTIVE;
    
    public BlockNpcRedstone() {
        super(Material.ROCK);
    }
    
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (par1World.isRemote) {
            return false;
        }
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == CustomItems.wand && CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.EDIT_BLOCKS)) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.RedstoneBlock, null, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }
    
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state) {
        par1World.notifyNeighborsOfStateChange(pos, (Block)this, false);
        par1World.notifyNeighborsOfStateChange(pos.down(), (Block)this, false);
        par1World.notifyNeighborsOfStateChange(pos.up(), (Block)this, false);
        par1World.notifyNeighborsOfStateChange(pos.west(), (Block)this, false);
        par1World.notifyNeighborsOfStateChange(pos.east(), (Block)this, false);
        par1World.notifyNeighborsOfStateChange(pos.south(), (Block)this, false);
        par1World.notifyNeighborsOfStateChange(pos.north(), (Block)this, false);
    }
    
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityliving, ItemStack item) {
        if (entityliving instanceof EntityPlayer && !world.isRemote) {
            NoppesUtilServer.sendOpenGui((EntityPlayer)entityliving, EnumGuiType.RedstoneBlock, null, pos.getX(), pos.getY(), pos.getZ());
        }
    }
    
    public void onPlayerDestroy(World par1World, BlockPos pos, IBlockState state) {
        this.onBlockAdded(par1World, pos, state);
    }
    
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return this.isActivated(state);
    }
    
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return this.isActivated(state);
    }
    
    public boolean canProvidePower(IBlockState state) {
        return true;
    }
    
    public int getMetaFromState(IBlockState state) {
        return ((boolean)state.getValue((IProperty)BlockNpcRedstone.ACTIVE)) ? 1 : 0;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty((IProperty)BlockNpcRedstone.ACTIVE, (Comparable)false);
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)BlockNpcRedstone.ACTIVE });
    }
    
    public int isActivated(IBlockState state) {
        return state.getValue(BlockNpcRedstone.ACTIVE) ? 15 : 0;
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileRedstoneBlock();
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.SaveTileEntity;
    }
    
    static {
        ACTIVE = PropertyBool.create("active");
    }
}
