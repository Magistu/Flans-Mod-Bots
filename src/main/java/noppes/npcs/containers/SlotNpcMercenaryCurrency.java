package noppes.npcs.containers;

import java.util.Iterator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import noppes.npcs.roles.RoleFollower;
import net.minecraft.inventory.Slot;

class SlotNpcMercenaryCurrency extends Slot
{
    RoleFollower role;
    
    public SlotNpcMercenaryCurrency(RoleFollower role, IInventory inv, int i, int j, int k) {
        super(inv, i, j, k);
        this.role = role;
    }
    
    public int getSlotStackLimit() {
        return 64;
    }
    
    public boolean isItemValid(ItemStack itemstack) {
        Item item = itemstack.getItem();
        for (ItemStack is : this.role.inventory.items) {
            if (item == is.getItem()) {
                if (itemstack.getHasSubtypes() && itemstack.getItemDamage() != is.getItemDamage()) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
}
