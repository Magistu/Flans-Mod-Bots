package noppes.npcs.blocks;

import noppes.npcs.blocks.tiles.TileBlockAnvil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.IProperty;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomNpcs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;

public class BlockCarpentryBench extends BlockInterface
{
    public static PropertyInteger ROTATION;
    
    public BlockCarpentryBench() {
        super(Material.WOOD);
        this.setSoundType(SoundType.WOOD);
    }
    
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!par1World.isRemote) {
            player.openGui((Object)CustomNpcs.instance, EnumGuiType.PlayerAnvil.ordinal(), par1World, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public int getMetaFromState(IBlockState state) {
        return (int)state.getValue((IProperty)BlockCarpentryBench.ROTATION);
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty((IProperty)BlockCarpentryBench.ROTATION, (Comparable)(meta % 4));
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)BlockCarpentryBench.ROTATION });
    }
    
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        int var6 = MathHelper.floor(entity.rotationYaw / 90.0f + 0.5) & 0x3;
        world.setBlockState(pos, state.withProperty((IProperty)BlockCarpentryBench.ROTATION, (Comparable)var6), 2);
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileBlockAnvil();
    }
    
    static {
        ROTATION = PropertyInteger.create("rotation", 0, 3);
    }
}
