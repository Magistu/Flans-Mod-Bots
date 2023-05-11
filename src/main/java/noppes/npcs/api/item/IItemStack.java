package noppes.npcs.api.item;

import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.INbt;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.entity.IEntityLiving;

public interface IItemStack
{
    int getStackSize();
    
    void setStackSize(int p0);
    
    int getMaxStackSize();
    
    int getItemDamage();
    
    void setItemDamage(int p0);
    
    int getMaxItemDamage();
    
    double getAttackDamage();
    
    void damageItem(int p0, IEntityLiving p1);
    
    void addEnchantment(String p0, int p1);
    
    boolean isEnchanted();
    
    boolean hasEnchant(String p0);
    
    boolean removeEnchant(String p0);
    
    @Deprecated
    boolean isBlock();
    
    boolean isWearable();
    
    boolean hasCustomName();
    
    void setCustomName(String p0);
    
    String getDisplayName();
    
    String getItemName();
    
    String getName();
    
    @Deprecated
    boolean isBook();
    
    IItemStack copy();
    
    ItemStack getMCItemStack();
    
    INbt getNbt();
    
    boolean hasNbt();
    
    void removeNbt();
    
    INbt getItemNbt();
    
    boolean isEmpty();
    
    int getType();
    
    String[] getLore();
    
    void setLore(String[] p0);
    
    @Deprecated
    void setAttribute(String p0, double p1);
    
    void setAttribute(String p0, double p1, int p2);
    
    double getAttribute(String p0);
    
    boolean hasAttribute(String p0);
    
    IData getTempdata();
    
    IData getStoreddata();
    
    int getFoodLevel();
    
    boolean compare(IItemStack p0, boolean p1);
}
