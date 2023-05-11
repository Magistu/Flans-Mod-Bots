package noppes.npcs.api.entity;

import noppes.npcs.api.item.IItemStack;
import net.minecraft.entity.projectile.EntityThrowable;

public interface IProjectile<T extends EntityThrowable> extends IThrowable<T>
{
    IItemStack getItem();
    
    void setItem(IItemStack p0);
    
    boolean getHasGravity();
    
    void setHasGravity(boolean p0);
    
    int getAccuracy();
    
    void setAccuracy(int p0);
    
    void setHeading(IEntity p0);
    
    void setHeading(double p0, double p1, double p2);
    
    void setHeading(float p0, float p1);
    
    void enableEvents();
}
