package noppes.npcs.api.entity;

import noppes.npcs.api.IRayTrace;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.INbt;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.IPos;
import net.minecraft.entity.Entity;

public interface IEntity<T extends Entity>
{
    double getX();
    
    void setX(double p0);
    
    double getY();
    
    void setY(double p0);
    
    double getZ();
    
    void setZ(double p0);
    
    int getBlockX();
    
    int getBlockY();
    
    int getBlockZ();
    
    IPos getPos();
    
    void setPos(IPos p0);
    
    void setPosition(double p0, double p1, double p2);
    
    void setRotation(float p0);
    
    float getRotation();
    
    float getHeight();
    
    float getEyeHeight();
    
    float getWidth();
    
    void setPitch(float p0);
    
    float getPitch();
    
    IEntity getMount();
    
    void setMount(IEntity p0);
    
    IEntity[] getRiders();
    
    IEntity[] getAllRiders();
    
    void addRider(IEntity p0);
    
    void clearRiders();
    
    void knockback(int p0, float p1);
    
    boolean isSneaking();
    
    boolean isSprinting();
    
    IEntityItem dropItem(IItemStack p0);
    
    boolean inWater();
    
    boolean inFire();
    
    boolean inLava();
    
    IData getTempdata();
    
    IData getStoreddata();
    
    INbt getNbt();
    
    boolean isAlive();
    
    long getAge();
    
    void despawn();
    
    void spawn();
    
    void kill();
    
    boolean isBurning();
    
    void setBurning(int p0);
    
    void extinguish();
    
    IWorld getWorld();
    
    String getTypeName();
    
    int getType();
    
    boolean typeOf(int p0);
    
    T getMCEntity();
    
    String getUUID();
    
    String generateNewUUID();
    
    void storeAsClone(int p0, String p1);
    
    INbt getEntityNbt();
    
    void setEntityNbt(INbt p0);
    
    IRayTrace rayTraceBlock(double p0, boolean p1, boolean p2);
    
    IEntity[] rayTraceEntities(double p0, boolean p1, boolean p2);
    
    String[] getTags();
    
    void addTag(String p0);
    
    boolean hasTag(String p0);
    
    void removeTag(String p0);
    
    void playAnimation(int p0);
    
    void damage(float p0);
    
    double getMotionX();
    
    double getMotionY();
    
    double getMotionZ();
    
    void setMotionX(double p0);
    
    void setMotionY(double p0);
    
    void setMotionZ(double p0);
    
    String getName();
    
    void setName(String p0);
    
    boolean hasCustomName();
    
    String getEntityName();
}
