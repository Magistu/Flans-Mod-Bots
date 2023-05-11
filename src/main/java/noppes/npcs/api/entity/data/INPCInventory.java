package noppes.npcs.api.entity.data;

import noppes.npcs.api.item.IItemStack;

public interface INPCInventory
{
    IItemStack getRightHand();
    
    void setRightHand(IItemStack p0);
    
    IItemStack getLeftHand();
    
    void setLeftHand(IItemStack p0);
    
    IItemStack getProjectile();
    
    void setProjectile(IItemStack p0);
    
    IItemStack getArmor(int p0);
    
    void setArmor(int p0, IItemStack p1);
    
    void setDropItem(int p0, IItemStack p1, int p2);
    
    IItemStack getDropItem(int p0);
    
    int getExpMin();
    
    int getExpMax();
    
    int getExpRNG();
    
    void setExp(int p0, int p1);
    
    IItemStack[] getItemsRNG();
}
