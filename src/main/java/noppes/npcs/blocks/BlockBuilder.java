package noppes.npcs.blocks;

import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.blocks.tiles.TileBuilder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import net.minecraft.item.Item;
import noppes.npcs.CustomItems;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import noppes.npcs.util.IPermission;

public class BlockBuilder extends BlockInterface implements IPermission
{
    public static PropertyInteger ROTATION;
    
    public BlockBuilder() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
    }
    
    public int getMetaFromState(IBlockState state) {
        return (int)state.getValue((IProperty)BlockBuilder.ROTATION);
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty((IProperty)BlockBuilder.ROTATION, (Comparable)meta);
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)BlockBuilder.ROTATION });
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (par1World.isRemote) {
            return true;
        }
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == Item.getItemFromBlock(CustomItems.builder)) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.BuilderBlock, null, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        int var6 = MathHelper.floor(entity.rotationYaw / 90.0f + 0.5) & 0x3;
        world.setBlockState(pos, state.withProperty((IProperty)BlockBuilder.ROTATION, (Comparable)var6), 2);
        if (entity instanceof EntityPlayer && !world.isRemote) {
            NoppesUtilServer.sendOpenGui((EntityPlayer)entity, EnumGuiType.BuilderBlock, null, pos.getX(), pos.getY(), pos.getZ());
        }
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileBuilder();
    }
    
    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.SchematicsSet || e == EnumPacketServer.SchematicsTile || e == EnumPacketServer.SchematicsTileSave || e == EnumPacketServer.SchematicsBuild;
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (TileBuilder.DrawPos != null && TileBuilder.DrawPos.equals((Object)pos)) {
            TileBuilder.SetDrawPos(null);
        }
    }
    
    static {
        ROTATION = PropertyInteger.create("rotation", 0, 3);
    }
}
