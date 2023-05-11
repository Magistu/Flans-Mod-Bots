package noppes.npcs.containers;

import noppes.npcs.api.NpcAPI;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import noppes.npcs.roles.RoleCompanion;
import net.minecraft.inventory.Slot;

class SlotCompanionWeapon extends Slot
{
    RoleCompanion role;
    
    public SlotCompanionWeapon(RoleCompanion role, IInventory iinventory, int id, int x, int y) {
        super(iinventory, id, x, y);
        this.role = role;
    }
    
    public int getSlotStackLimit() {
        return 1;
    }
    
    public boolean isItemValid(ItemStack itemstack) {
        return !NoppesUtilServer.IsItemStackNull(itemstack) && this.role.canWearSword(NpcAPI.Instance().getIItemStack(itemstack));
    }
}
