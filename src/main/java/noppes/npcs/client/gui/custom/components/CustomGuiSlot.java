package noppes.npcs.client.gui.custom.components;

import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.EventHooks;
import net.minecraft.entity.Entity;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.api.NpcAPI;
import net.minecraft.inventory.IInventory;
import noppes.npcs.api.gui.IItemSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class CustomGuiSlot extends Slot
{
    EntityPlayer player;
    IItemSlot slot;
    
    public CustomGuiSlot(IInventory inventoryIn, int index, IItemSlot slot, EntityPlayer player) {
        super(inventoryIn, index, slot.getPosX(), slot.getPosY());
        this.player = player;
        this.slot = slot;
    }
    
    public void onSlotChanged() {
        if (!this.player.world.isRemote && this.getStack() != this.slot.getStack().getMCItemStack()) {
            this.slot.setStack(NpcAPI.Instance().getIItemStack(this.getStack()));
            if (this.player.openContainer instanceof ContainerCustomGui) {
                EventHooks.onCustomGuiSlot((PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)this.player), ((ContainerCustomGui)this.player.openContainer).customGui, this.getSlotIndex());
            }
        }
        super.onSlotChanged();
    }
}
