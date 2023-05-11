package noppes.npcs.items;

import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomNpcs;
import net.minecraft.util.EnumActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.CustomItems;
import noppes.npcs.util.IPermission;
import net.minecraft.item.Item;

public class ItemNpcScripter extends Item implements IPermission
{
    public ItemNpcScripter() {
        this.maxStackSize = 1;
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName("customnpcs", name);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!world.isRemote || hand != EnumHand.MAIN_HAND) {
            return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
        }
        CustomNpcs.proxy.openGui(0, 0, 0, EnumGuiType.ScriptPlayers, player);
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
    }
    
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.ScriptDataGet || e == EnumPacketServer.ScriptDataSave || e == EnumPacketServer.ScriptBlockDataSave || e == EnumPacketServer.ScriptDoorDataSave || e == EnumPacketServer.ScriptPlayerGet || e == EnumPacketServer.ScriptPlayerSave || e == EnumPacketServer.ScriptForgeGet || e == EnumPacketServer.ScriptForgeSave;
    }
}
