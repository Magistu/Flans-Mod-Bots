package noppes.npcs.items;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.CustomItems;
import noppes.npcs.util.IPermission;
import net.minecraft.item.Item;

public class ItemNpcCloner extends Item implements IPermission
{
    public ItemNpcCloner() {
        this.maxStackSize = 1;
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.MobSpawner, null, pos.getX(), pos.getY(), pos.getZ());
        }
        return EnumActionResult.SUCCESS;
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName("customnpcs", name);
    }
    
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.CloneList || e == EnumPacketServer.SpawnMob || e == EnumPacketServer.MobSpawner || e == EnumPacketServer.ClonePreSave || e == EnumPacketServer.CloneRemove || e == EnumPacketServer.CloneSave;
    }
}
