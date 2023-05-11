package noppes.npcs.api.handler;

import net.minecraft.item.ItemStack;
import noppes.npcs.api.handler.data.IRecipe;
import java.util.List;

public interface IRecipeHandler
{
    List<IRecipe> getGlobalList();
    
    List<IRecipe> getCarpentryList();
    
    IRecipe addRecipe(String p0, boolean p1, ItemStack p2, Object... p3);
    
    IRecipe addRecipe(String p0, boolean p1, ItemStack p2, int p3, int p4, ItemStack... p5);
    
    IRecipe delete(int p0);
}
