package noppes.npcs.containers;

import java.util.Iterator;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCustom extends ContainerNpcInterface
{
    private InventoryNPC inventory;
    public int columns;
    public int rows;
    
    public ContainerCustom(EntityPlayer player, int columns, int rows) {
        super(player);
        this.columns = columns;
        this.rows = rows;
        this.inventory = new InventoryNPC("currency", columns * rows, this);
        for (int j = 0; j < rows; ++j) {
            for (int k = 0; k < columns; ++k) {
                this.addSlotToContainer((Slot)new SlotApi((IInventory)this.inventory, k + j * 9, 179 + k * 24, 138));
            }
        }
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot((IInventory)player.inventory, k + j * 9 + 9, 28 + k * 18, 175 + j * 18));
            }
        }
        for (int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot((IInventory)player.inventory, j, 28 + j * 18, 230));
        }
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(par2);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (par2 < 4) {
                if (!this.mergeItemStack(itemstack2, 4, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack2, 0, 4, false)) {
                return null;
            }
            if (itemstack2.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
    
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.world.isRemote) {
            PlayerMailData data = PlayerData.get(player).mailData;
            for (PlayerMail playerMail : data.playermail) {}
        }
    }
}
