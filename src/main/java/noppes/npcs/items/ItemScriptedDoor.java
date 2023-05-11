package noppes.npcs.items;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
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
import net.minecraft.block.Block;
import noppes.npcs.util.IPermission;
import net.minecraft.item.ItemDoor;

public class ItemScriptedDoor extends ItemDoor implements IPermission
{
    public ItemScriptedDoor(Block block) {
        super(block);
        this.maxStackSize = 1;
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        EnumActionResult res = super.onItemUse(playerIn, worldIn, pos, hand, side, hitX, hitY, hitZ);
        if (res == EnumActionResult.SUCCESS && !worldIn.isRemote) {
            BlockPos newPos = pos.up();
            NoppesUtilServer.sendOpenGui(playerIn, EnumGuiType.ScriptDoor, null, newPos.getX(), newPos.getY(), newPos.getZ());
            return EnumActionResult.SUCCESS;
        }
        return res;
    }
    
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase playerIn) {
        return stack;
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName("customnpcs", name);
    }
    
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.ScriptDoorDataSave;
    }
}
