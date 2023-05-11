package noppes.npcs.api.entity;

import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.item.IItemStack;
import net.minecraft.entity.EntityLivingBase;

public interface IEntityLivingBase<T extends EntityLivingBase> extends IEntity<T>
{
    float getHealth();
    
    void setHealth(float p0);
    
    float getMaxHealth();
    
    void setMaxHealth(float p0);
    
    boolean isAttacking();
    
    void setAttackTarget(IEntityLivingBase p0);
    
    IEntityLivingBase getAttackTarget();
    
    IEntityLivingBase getLastAttacked();
    
    int getLastAttackedTime();
    
    boolean canSeeEntity(IEntity p0);
    
    void swingMainhand();
    
    void swingOffhand();
    
    IItemStack getMainhandItem();
    
    void setMainhandItem(IItemStack p0);
    
    IItemStack getOffhandItem();
    
    void setOffhandItem(IItemStack p0);
    
    IItemStack getArmor(int p0);
    
    void setArmor(int p0, IItemStack p1);
    
    void addPotionEffect(int p0, int p1, int p2, boolean p3);
    
    void clearPotionEffects();
    
    int getPotionEffect(int p0);
    
    IMark addMark(int p0);
    
    void removeMark(IMark p0);
    
    IMark[] getMarks();
    
    boolean isChild();
    
    T getMCEntity();
    
    float getMoveForward();
    
    void setMoveForward(float p0);
    
    float getMoveStrafing();
    
    void setMoveStrafing(float p0);
    
    float getMoveVertical();
    
    void setMoveVertical(float p0);
}
