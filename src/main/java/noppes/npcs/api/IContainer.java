package noppes.npcs.api;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import noppes.npcs.api.item.IItemStack;

public interface IContainer
{
    int getSize();
    
    IItemStack getSlot(int p0);
    
    void setSlot(int p0, IItemStack p1);
    
    IInventory getMCInventory();
    
    Container getMCContainer();
    
    int count(IItemStack p0, boolean p1, boolean p2);
    
    IItemStack[] getItems();
}
