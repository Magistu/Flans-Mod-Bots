package noppes.npcs.containers;

import noppes.npcs.NoppesUtilServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;

public class SlotNpcCrafting extends SlotCrafting
{
    private InventoryCrafting craftMatrix;
    
    public SlotNpcCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventory, int slotIndex, int x, int y) {
        super(player, craftingInventory, inventory, slotIndex, x, y);
        this.craftMatrix = craftingInventory;
    }
    
    public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, itemStack, (IInventory)this.craftMatrix);
        this.onCrafting(itemStack);
        for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
            ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);
            if (!NoppesUtilServer.IsItemStackNull(itemstack1)) {
                this.craftMatrix.decrStackSize(i, 1);
                if (itemstack1.getItem().hasContainerItem(itemstack1)) {
                    ItemStack itemstack2 = itemstack1.getItem().getContainerItem(itemstack1);
                    if (NoppesUtilServer.IsItemStackNull(itemstack2) || !itemstack2.isItemStackDamageable() || itemstack2.getItemDamage() <= itemstack2.getMaxDamage()) {
                        if (!player.inventory.addItemStackToInventory(itemstack2)) {
                            if (NoppesUtilServer.IsItemStackNull(this.craftMatrix.getStackInSlot(i))) {
                                this.craftMatrix.setInventorySlotContents(i, itemstack2);
                            }
                            else {
                                player.dropItem(itemstack2, false);
                            }
                        }
                    }
                }
            }
        }
        return itemStack;
    }
}
