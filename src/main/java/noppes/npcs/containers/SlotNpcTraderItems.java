package noppes.npcs.containers;

import noppes.npcs.NoppesUtilServer;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

class SlotNpcTraderItems extends Slot
{
    public SlotNpcTraderItems(IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }
    
    public ItemStack onTake(EntityPlayer player, ItemStack itemstack) {
        if (NoppesUtilServer.IsItemStackNull(itemstack) || NoppesUtilServer.IsItemStackNull(this.getStack())) {
            return itemstack;
        }
        if (itemstack.getItem() != this.getStack().getItem()) {
            return itemstack;
        }
        itemstack.shrink(1);
        return itemstack;
    }
    
    public int getSlotStackLimit() {
        return 64;
    }
    
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }
}
