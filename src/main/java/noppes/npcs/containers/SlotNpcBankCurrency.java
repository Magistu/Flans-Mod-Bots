package noppes.npcs.containers;

import noppes.npcs.NoppesUtilServer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;

public class SlotNpcBankCurrency extends Slot
{
    public ItemStack item;
    
    public SlotNpcBankCurrency(ContainerNPCBankInterface containerplayer, IInventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
        this.item = ItemStack.EMPTY;
    }
    
    public int getSlotStackLimit() {
        return 64;
    }
    
    public boolean isItemValid(ItemStack itemstack) {
        return !NoppesUtilServer.IsItemStackNull(itemstack) && (this.item.getItem() == itemstack.getItem() && (!this.item.getHasSubtypes() || this.item.getItemDamage() == itemstack.getItemDamage()));
    }
}
