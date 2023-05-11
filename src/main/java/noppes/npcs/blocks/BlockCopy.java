package noppes.npcs.blocks;

import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.blocks.tiles.TileCopy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomItems;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import noppes.npcs.util.IPermission;

public class BlockCopy extends BlockInterface implements IPermission
{
    public BlockCopy() {
        super(Material.ROCK);
        this.setSoundType(SoundType.STONE);
    }
    
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (par1World.isRemote) {
            return true;
        }
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == CustomItems.wand) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.CopyBlock, null, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        if (entity instanceof EntityPlayer && !world.isRemote) {
            NoppesUtilServer.sendOpenGui((EntityPlayer)entity, EnumGuiType.CopyBlock, null, pos.getX(), pos.getY(), pos.getZ());
        }
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileCopy();
    }
    
    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.GetTileEntity || e == EnumPacketServer.SchematicStore || e == EnumPacketServer.SchematicsTile || e == EnumPacketServer.SchematicsTileSave || e == EnumPacketServer.SaveTileEntity;
    }
}
