package noppes.npcs.api.wrapper.gui;

import net.minecraft.item.ItemStack;
import noppes.npcs.api.NpcAPI;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.Slot;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.gui.IItemSlot;

public class CustomGuiItemSlotWrapper extends CustomGuiComponentWrapper implements IItemSlot
{
    IItemStack stack;
    
    public CustomGuiItemSlotWrapper() {
        this.stack = ItemStackWrapper.AIR;
    }
    
    public CustomGuiItemSlotWrapper(int x, int y, IItemStack stack) {
        this.stack = ItemStackWrapper.AIR;
        this.setPos(x, y);
        this.setStack(stack);
    }
    
    @Override
    public boolean hasStack() {
        return this.stack != null && !this.stack.isEmpty();
    }
    
    @Override
    public IItemStack getStack() {
        return this.stack;
    }
    
    @Override
    public IItemSlot setStack(IItemStack itemStack) {
        if (itemStack == null) {
            this.stack = ItemStackWrapper.AIR;
        }
        else {
            this.stack = itemStack;
        }
        return this;
    }
    
    @Override
    public Slot getMCSlot() {
        return null;
    }
    
    @Override
    public int getType() {
        return 5;
    }
    
    @Override
    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        super.toNBT(nbt);
        nbt.setTag("stack", (NBTBase)this.stack.getMCItemStack().serializeNBT());
        return nbt;
    }
    
    @Override
    public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
        super.fromNBT(nbt);
        this.setStack(NpcAPI.Instance().getIItemStack(new ItemStack(nbt.getCompoundTag("stack"))));
        return this;
    }
}
