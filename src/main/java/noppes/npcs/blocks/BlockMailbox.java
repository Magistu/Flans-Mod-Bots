package noppes.npcs.blocks;

import java.util.List;
import noppes.npcs.blocks.tiles.TileMailbox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.IProperty;
import java.util.ArrayList;
import net.minecraft.world.IBlockAccess;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;

public class BlockMailbox extends BlockInterface
{
    public static PropertyInteger ROTATION;
    public static PropertyInteger TYPE;
    
    public BlockMailbox() {
        super(Material.IRON);
        this.setSoundType(SoundType.METAL);
    }
    
    public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
        par3List.add(new ItemStack(this, 1, 0));
        par3List.add(new ItemStack(this, 1, 1));
        par3List.add(new ItemStack(this, 1, 2));
    }
    
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!par1World.isRemote) {
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.GUI, EnumGuiType.PlayerMailbox, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        int damage = (int)state.getValue((IProperty)BlockMailbox.TYPE);
        ret.add(new ItemStack((Block)this, 1, damage));
        return ret;
    }
    
    public int damageDropped(IBlockState state) {
        return (int)state.getValue((IProperty)BlockMailbox.TYPE);
    }
    
    public int getMetaFromState(IBlockState state) {
        return (int)state.getValue((IProperty)BlockMailbox.ROTATION) | (int)state.getValue((IProperty)BlockMailbox.TYPE) << 2;
    }
    
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty((IProperty)BlockMailbox.TYPE, (Comparable)((meta >> 2) % 3)).withProperty((IProperty)BlockMailbox.ROTATION, (Comparable)((meta | 0x4) % 4));
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)BlockMailbox.TYPE, (IProperty)BlockMailbox.ROTATION });
    }
    
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        int l = MathHelper.floor(entity.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        world.setBlockState(pos, state.withProperty((IProperty)BlockMailbox.TYPE, (Comparable)stack.getItemDamage()).withProperty((IProperty)BlockMailbox.ROTATION, (Comparable)(l % 4)), 2);
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileMailbox();
    }
    
    static {
        ROTATION = PropertyInteger.create("rotation", 0, 3);
        TYPE = PropertyInteger.create("type", 0, 2);
    }
}
