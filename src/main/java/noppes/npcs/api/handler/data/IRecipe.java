package noppes.npcs.api.handler.data;

import net.minecraft.item.ItemStack;

public interface IRecipe
{
    String getName();
    
    boolean isGlobal();
    
    void setIsGlobal(boolean p0);
    
    boolean getIgnoreNBT();
    
    void setIgnoreNBT(boolean p0);
    
    boolean getIgnoreDamage();
    
    void setIgnoreDamage(boolean p0);
    
    int getWidth();
    
    int getHeight();
    
    ItemStack getResult();
    
    ItemStack[] getRecipe();
    
    void saves(boolean p0);
    
    boolean saves();
    
    void save();
    
    void delete();
    
    int getId();
}
