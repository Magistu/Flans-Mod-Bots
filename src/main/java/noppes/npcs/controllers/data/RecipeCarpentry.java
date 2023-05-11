package noppes.npcs.controllers.data;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import noppes.npcs.controllers.RecipeController;
import java.util.Iterator;
import net.minecraftforge.common.ForgeHooks;
import noppes.npcs.NoppesUtilPlayer;
import net.minecraft.world.World;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import java.util.HashMap;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import noppes.npcs.api.handler.data.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;

public class RecipeCarpentry extends ShapedRecipes implements IRecipe
{
    public int id;
    public String name;
    public Availability availability;
    public boolean isGlobal;
    public boolean ignoreDamage;
    public boolean ignoreNBT;
    public boolean savesRecipe;
    
    public RecipeCarpentry(int width, int height, NonNullList<Ingredient> recipe, ItemStack result) {
        super("customnpcs", width, height, (NonNullList)recipe, result);
        this.id = -1;
        this.name = "";
        this.availability = new Availability();
        this.isGlobal = false;
        this.ignoreDamage = false;
        this.ignoreNBT = false;
        this.savesRecipe = true;
    }
    
    public RecipeCarpentry(String name) {
        super("customnpcs", 0, 0, NonNullList.create(), ItemStack.EMPTY);
        this.id = -1;
        this.name = "";
        this.availability = new Availability();
        this.isGlobal = false;
        this.ignoreDamage = false;
        this.ignoreNBT = false;
        this.savesRecipe = true;
        this.name = name;
    }
    
    public static RecipeCarpentry read(NBTTagCompound compound) {
        RecipeCarpentry recipe = new RecipeCarpentry(compound.getInteger("Width"), compound.getInteger("Height"), NBTTags.getIngredientList(compound.getTagList("Materials", 10)), new ItemStack(compound.getCompoundTag("Item")));
        recipe.name = compound.getString("Name");
        recipe.id = compound.getInteger("ID");
        recipe.availability.readFromNBT(compound.getCompoundTag("Availability"));
        recipe.ignoreDamage = compound.getBoolean("IgnoreDamage");
        recipe.ignoreNBT = compound.getBoolean("IgnoreNBT");
        recipe.isGlobal = compound.getBoolean("Global");
        return recipe;
    }
    
    public NBTTagCompound writeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", this.id);
        compound.setInteger("Width", this.recipeWidth);
        compound.setInteger("Height", this.recipeHeight);
        if (this.getRecipeOutput() != null) {
            compound.setTag("Item", (NBTBase)this.getRecipeOutput().writeToNBT(new NBTTagCompound()));
        }
        compound.setTag("Materials", (NBTBase)NBTTags.nbtIngredientList((NonNullList<Ingredient>)this.recipeItems));
        compound.setTag("Availability", (NBTBase)this.availability.writeToNBT(new NBTTagCompound()));
        compound.setString("Name", this.name);
        compound.setBoolean("Global", this.isGlobal);
        compound.setBoolean("IgnoreDamage", this.ignoreDamage);
        compound.setBoolean("IgnoreNBT", this.ignoreNBT);
        return compound;
    }
    
    public static RecipeCarpentry createRecipe(RecipeCarpentry recipe, ItemStack par1ItemStack, Object... par2ArrayOfObj) {
        String var3 = "";
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;
        if (par2ArrayOfObj[var4] instanceof String[]) {
            String[] var8;
            String[] var7 = var8 = (String[])par2ArrayOfObj[var4++];
            for (int var9 = var7.length, var10 = 0; var10 < var9; ++var10) {
                String var11 = var8[var10];
                ++var6;
                var5 = var11.length();
                var3 += var11;
            }
        }
        else {
            while (par2ArrayOfObj[var4] instanceof String) {
                String var12 = (String)par2ArrayOfObj[var4++];
                ++var6;
                var5 = var12.length();
                var3 += var12;
            }
        }
        HashMap var13 = new HashMap();
        while (var4 < par2ArrayOfObj.length) {
            Character var14 = (Character)par2ArrayOfObj[var4];
            ItemStack var15 = ItemStack.EMPTY;
            if (par2ArrayOfObj[var4 + 1] instanceof Item) {
                var15 = new ItemStack((Item)par2ArrayOfObj[var4 + 1]);
            }
            else if (par2ArrayOfObj[var4 + 1] instanceof Block) {
                var15 = new ItemStack((Block)par2ArrayOfObj[var4 + 1], 1, -1);
            }
            else if (par2ArrayOfObj[var4 + 1] instanceof ItemStack) {
                var15 = (ItemStack)par2ArrayOfObj[var4 + 1];
            }
            var13.put(var14, var15);
            var4 += 2;
        }
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (int var9 = 0; var9 < var5 * var6; ++var9) {
            char var16 = var3.charAt(var9);
            if (var13.containsKey(var16)) {
                ingredients.add(var9, Ingredient.fromStacks(new ItemStack[] { ((ItemStack) var13.get(var16)).copy() }));
            }
            else {
                ingredients.add(var9, Ingredient.EMPTY);
            }
        }
        RecipeCarpentry newrecipe = new RecipeCarpentry(var5, var6, ingredients, par1ItemStack);
        newrecipe.copy(recipe);
        if (var5 == 4 || var6 == 4) {
            newrecipe.isGlobal = false;
        }
        return newrecipe;
    }
    
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        for (int i = 0; i <= 4 - this.recipeWidth; ++i) {
            for (int j = 0; j <= 4 - this.recipeHeight; ++j) {
                if (this.checkMatch(inventoryCrafting, i, j, true)) {
                    return true;
                }
                if (this.checkMatch(inventoryCrafting, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        if (this.getRecipeOutput().isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.getRecipeOutput().copy();
    }
    
    private boolean checkMatch(InventoryCrafting inventoryCrafting, int par2, int par3, boolean par4) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int var7 = i - par2;
                int var8 = j - par3;
                Ingredient ingredient = Ingredient.EMPTY;
                if (var7 >= 0 && var8 >= 0 && var7 < this.recipeWidth && var8 < this.recipeHeight) {
                    if (par4) {
                        ingredient = (Ingredient)this.recipeItems.get(this.recipeWidth - var7 - 1 + var8 * this.recipeWidth);
                    }
                    else {
                        ingredient = (Ingredient)this.recipeItems.get(var7 + var8 * this.recipeWidth);
                    }
                }
                ItemStack var9 = inventoryCrafting.getStackInRowAndColumn(i, j);
                if (!var9.isEmpty() || ingredient.getMatchingStacks().length == 0) {
                    return false;
                }
                ItemStack var10 = ingredient.getMatchingStacks()[0];
                if ((!var9.isEmpty() || !var10.isEmpty()) && !NoppesUtilPlayer.compareItems(var10, var9, this.ignoreDamage, this.ignoreNBT)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventoryCrafting) {
        NonNullList<ItemStack> list = NonNullList.withSize(inventoryCrafting.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = inventoryCrafting.getStackInSlot(i);
            list.set(i, ForgeHooks.getContainerItem(itemstack));
        }
        return list;
    }
    
    public void copy(RecipeCarpentry recipe) {
        this.id = recipe.id;
        this.name = recipe.name;
        this.availability = recipe.availability;
        this.isGlobal = recipe.isGlobal;
        this.ignoreDamage = recipe.ignoreDamage;
        this.ignoreNBT = recipe.ignoreNBT;
    }
    
    public ItemStack getCraftingItem(int i) {
        if (this.recipeItems == null || i >= this.recipeItems.size()) {
            return ItemStack.EMPTY;
        }
        Ingredient ingredients = (Ingredient)this.recipeItems.get(i);
        if (ingredients.getMatchingStacks().length == 0) {
            return ItemStack.EMPTY;
        }
        return ingredients.getMatchingStacks()[0];
    }
    
    public boolean isValid() {
        if (this.recipeItems.size() == 0 || this.getRecipeOutput().isEmpty()) {
            return false;
        }
        for (Ingredient ingredient : this.recipeItems) {
            if (ingredient.getMatchingStacks().length > 0) {
                return true;
            }
        }
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ItemStack getResult() {
        return this.getRecipeOutput();
    }
    
    public boolean isGlobal() {
        return this.isGlobal;
    }
    
    public void setIsGlobal(boolean bo) {
        this.isGlobal = bo;
    }
    
    public boolean getIgnoreNBT() {
        return this.ignoreNBT;
    }
    
    public void setIgnoreNBT(boolean bo) {
        this.ignoreNBT = bo;
    }
    
    public boolean getIgnoreDamage() {
        return this.ignoreDamage;
    }
    
    public void setIgnoreDamage(boolean bo) {
        this.ignoreDamage = bo;
    }
    
    public void save() {
        try {
            RecipeController.instance.saveRecipe(this);
        }
        catch (IOException ex) {}
    }
    
    public void delete() {
        RecipeController.instance.delete(this.id);
    }
    
    public int getWidth() {
        return this.recipeWidth;
    }
    
    public int getHeight() {
        return this.recipeHeight;
    }
    
    public ItemStack[] getRecipe() {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (Ingredient ingredient : this.recipeItems) {
            if (ingredient.getMatchingStacks().length > 0) {
                list.add(ingredient.getMatchingStacks()[0]);
            }
        }
        return list.toArray(new ItemStack[list.size()]);
    }
    
    public void saves(boolean bo) {
        this.savesRecipe = bo;
    }
    
    public boolean saves() {
        return this.savesRecipe;
    }
    
    public int getId() {
        return this.id;
    }
}
