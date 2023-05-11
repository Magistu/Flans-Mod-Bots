package noppes.npcs.blocks;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.EnumBlockRenderType;
import noppes.npcs.blocks.tiles.TileWaypoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomNpcsPermissions;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.CustomItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import noppes.npcs.util.IPermission;

public class BlockWaypoint extends BlockInterface implements IPermission
{
    public BlockWaypoint() {
        super(Material.IRON);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (par1World.isRemote) {
            return false;
        }
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == CustomItems.wand && CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.EDIT_BLOCKS)) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.Waypoint, null, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }
    
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        if (entity instanceof EntityPlayer && !world.isRemote) {
            NoppesUtilServer.sendOpenGui((EntityPlayer)entity, EnumGuiType.Waypoint, null, pos.getX(), pos.getY(), pos.getZ());
        }
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileWaypoint();
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.SaveTileEntity;
    }
}
