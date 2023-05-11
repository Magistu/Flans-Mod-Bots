package noppes.npcs.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.IPos;
import net.minecraft.entity.EntityLiving;

public interface IEntityLiving<T extends EntityLiving> extends IEntityLivingBase<T>
{
    boolean isNavigating();
    
    void clearNavigation();
    
    void navigateTo(double p0, double p1, double p2, double p3);
    
    void jump();
    
    T getMCEntity();
    
    IPos getNavigationPath();
}
