package noppes.npcs.api.entity;

import noppes.npcs.api.item.IItemStack;
import net.minecraft.entity.item.EntityItem;

public interface IEntityItem<T extends EntityItem> extends IEntity<T>
{
    String getOwner();
    
    void setOwner(String p0);
    
    int getPickupDelay();
    
    void setPickupDelay(int p0);
    
    long getAge();
    
    void setAge(long p0);
    
    int getLifeSpawn();
    
    void setLifeSpawn(int p0);
    
    IItemStack getItem();
    
    void setItem(IItemStack p0);
}
