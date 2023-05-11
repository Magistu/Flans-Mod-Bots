package noppes.npcs.containers;

import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.inventory.Slot;
import noppes.npcs.client.gui.custom.components.CustomGuiSlot;
import noppes.npcs.api.gui.IItemSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import net.minecraft.inventory.Container;

public class ContainerCustomGui extends Container
{
    public CustomGuiWrapper customGui;
    public IInventory guiInventory;
    int slotCount;
    
    public ContainerCustomGui(IInventory inventory) {
        this.slotCount = 0;
        this.guiInventory = inventory;
    }
    
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
    
    public void setGui(CustomGuiWrapper gui, EntityPlayer player) {
        this.customGui = gui;
        for (IItemSlot slot : this.customGui.getSlots()) {
            int index = this.slotCount++;
            this.addSlotToContainer((Slot)new CustomGuiSlot(this.guiInventory, index, slot, player));
            this.guiInventory.setInventorySlotContents(index, slot.getStack().getMCItemStack());
        }
        if (this.customGui.getShowPlayerInv()) {
            this.addPlayerInventory(player, this.customGui.getPlayerInvX(), this.customGui.getPlayerInvY());
        }
    }
    
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (index < this.guiInventory.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack2, this.guiInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack2, 0, this.guiInventory.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack2.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
    
    void addPlayerInventory(EntityPlayer player, int x, int y) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot((IInventory)player.inventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
        for (int row = 0; row < 9; ++row) {
            this.addSlotToContainer(new Slot((IInventory)player.inventory, row, x + row * 18, y + 58));
        }
    }
}
