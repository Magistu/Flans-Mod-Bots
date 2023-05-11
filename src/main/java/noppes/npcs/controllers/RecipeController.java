package noppes.npcs.controllers;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.item.ItemStack;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.world.World;
import net.minecraft.inventory.InventoryCrafting;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.FileOutputStream;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.io.InputStream;
import net.minecraft.nbt.CompressedStreamTools;
import java.io.FileInputStream;
import noppes.npcs.controllers.data.RecipesDefault;
import java.io.File;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import noppes.npcs.controllers.data.RecipeCarpentry;
import java.util.HashMap;
import noppes.npcs.api.handler.IRecipeHandler;

public class RecipeController implements IRecipeHandler
{
    public HashMap<Integer, RecipeCarpentry> globalRecipes;
    public HashMap<Integer, RecipeCarpentry> anvilRecipes;
    public static RecipeController instance;
    public static int version = 1;
    public int nextId;
    public static HashMap<Integer, RecipeCarpentry> syncRecipes;
    public static IForgeRegistry<IRecipe> Registry;
    
    public RecipeController() {
        this.globalRecipes = new HashMap<Integer, RecipeCarpentry>();
        this.anvilRecipes = new HashMap<Integer, RecipeCarpentry>();
        this.nextId = 1;
        RecipeController.instance = this;
    }
    
    public void load() {
        this.loadCategories();
        this.reloadGlobalRecipes();
        EventHooks.onGlobalRecipesLoaded(this);
    }
    
    public void reloadGlobalRecipes() {
    }
    
    private void loadCategories() {
        File saveDir = CustomNpcs.getWorldSaveDirectory();
        try {
            File file = new File(saveDir, "recipes.dat");
            if (file.exists()) {
                this.loadCategories(file);
            }
            else {
                this.globalRecipes.clear();
                this.anvilRecipes.clear();
                this.loadDefaultRecipes(-1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                File file2 = new File(saveDir, "recipes.dat_old");
                if (file2.exists()) {
                    this.loadCategories(file2);
                }
            }
            catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadDefaultRecipes(int i) {
        if (i == 1) {
            return;
        }
        RecipesDefault.loadDefaultRecipes(i);
        this.saveCategories();
    }
    
    private void loadCategories(File file) throws Exception {
        NBTTagCompound nbttagcompound1 = CompressedStreamTools.readCompressed((InputStream)new FileInputStream(file));
        this.nextId = nbttagcompound1.getInteger("LastId");
        NBTTagList list = nbttagcompound1.getTagList("Data", 10);
        HashMap<Integer, RecipeCarpentry> globalRecipes = new HashMap<Integer, RecipeCarpentry>();
        HashMap<Integer, RecipeCarpentry> anvilRecipes = new HashMap<Integer, RecipeCarpentry>();
        if (list != null) {
            for (int i = 0; i < list.tagCount(); ++i) {
                RecipeCarpentry recipe = RecipeCarpentry.read(list.getCompoundTagAt(i));
                if (recipe.isGlobal) {
                    globalRecipes.put(recipe.id, recipe);
                }
                else {
                    anvilRecipes.put(recipe.id, recipe);
                }
                if (recipe.id > this.nextId) {
                    this.nextId = recipe.id;
                }
            }
        }
        this.anvilRecipes = anvilRecipes;
        this.globalRecipes = globalRecipes;
        this.loadDefaultRecipes(nbttagcompound1.getInteger("Version"));
    }
    
    private void saveCategories() {
        try {
            File saveDir = CustomNpcs.getWorldSaveDirectory();
            NBTTagList list = new NBTTagList();
            for (RecipeCarpentry recipe : this.globalRecipes.values()) {
                if (recipe.savesRecipe) {
                    list.appendTag((NBTBase)recipe.writeNBT());
                }
            }
            for (RecipeCarpentry recipe : this.anvilRecipes.values()) {
                if (recipe.savesRecipe) {
                    list.appendTag((NBTBase)recipe.writeNBT());
                }
            }
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("Data", (NBTBase)list);
            nbttagcompound.setInteger("LastId", this.nextId);
            nbttagcompound.setInteger("Version", 1);
            File file = new File(saveDir, "recipes.dat_new");
            File file2 = new File(saveDir, "recipes.dat_old");
            File file3 = new File(saveDir, "recipes.dat");
            CompressedStreamTools.writeCompressed(nbttagcompound, (OutputStream)new FileOutputStream(file));
            if (file2.exists()) {
                file2.delete();
            }
            file3.renameTo(file2);
            if (file3.exists()) {
                file3.delete();
            }
            file.renameTo(file3);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public RecipeCarpentry findMatchingRecipe(InventoryCrafting inventoryCrafting) {
        for (RecipeCarpentry recipe : this.anvilRecipes.values()) {
            if (recipe.isValid() && recipe.matches(inventoryCrafting, null)) {
                return recipe;
            }
        }
        return null;
    }
    
    public RecipeCarpentry getRecipe(int id) {
        if (this.globalRecipes.containsKey(id)) {
            return this.globalRecipes.get(id);
        }
        if (this.anvilRecipes.containsKey(id)) {
            return this.anvilRecipes.get(id);
        }
        return null;
    }
    
    public RecipeCarpentry saveRecipe(RecipeCarpentry recipe) throws IOException {
        RecipeCarpentry current = this.getRecipe(recipe.id);
        if (current != null && !current.name.equals(recipe.name)) {
            while (this.containsRecipeName(recipe.name)) {
                recipe.name += "_";
            }
        }
        if (recipe.id == -1) {
            recipe.id = this.getUniqueId();
            while (this.containsRecipeName(recipe.name)) {
                recipe.name += "_";
            }
        }
        if (recipe.isGlobal) {
            this.anvilRecipes.remove(recipe.id);
            this.globalRecipes.put(recipe.id, recipe);
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, 6, recipe.writeNBT());
        }
        else {
            this.globalRecipes.remove(recipe.id);
            this.anvilRecipes.put(recipe.id, recipe);
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, 7, recipe.writeNBT());
        }
        this.saveCategories();
        this.reloadGlobalRecipes();
        return recipe;
    }
    
    private int getUniqueId() {
        return ++this.nextId;
    }
    
    private boolean containsRecipeName(String name) {
        name = name.toLowerCase();
        for (RecipeCarpentry recipe : this.globalRecipes.values()) {
            if (recipe.name.toLowerCase().equals(name)) {
                return true;
            }
        }
        for (RecipeCarpentry recipe : this.anvilRecipes.values()) {
            if (recipe.name.toLowerCase().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public RecipeCarpentry delete(int id) {
        RecipeCarpentry recipe = this.getRecipe(id);
        if (recipe == null) {
            return null;
        }
        this.globalRecipes.remove(recipe.id);
        this.anvilRecipes.remove(recipe.id);
        if (recipe.isGlobal) {
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, 6, id);
        }
        else {
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, 7, id);
        }
        this.saveCategories();
        this.reloadGlobalRecipes();
        recipe.id = -1;
        return recipe;
    }
    
    @Override
    public List<noppes.npcs.api.handler.data.IRecipe> getGlobalList() {
        return new ArrayList<noppes.npcs.api.handler.data.IRecipe>(this.globalRecipes.values());
    }
    
    @Override
    public List<noppes.npcs.api.handler.data.IRecipe> getCarpentryList() {
        return new ArrayList<noppes.npcs.api.handler.data.IRecipe>(this.anvilRecipes.values());
    }
    
    @Override
    public noppes.npcs.api.handler.data.IRecipe addRecipe(String name, boolean global, ItemStack result, Object... objects) {
        RecipeCarpentry recipe = new RecipeCarpentry(name);
        recipe.isGlobal = global;
        recipe = RecipeCarpentry.createRecipe(recipe, result, objects);
        try {
            return this.saveRecipe(recipe);
        }
        catch (IOException e) {
            e.printStackTrace();
            return recipe;
        }
    }
    
    @Override
    public noppes.npcs.api.handler.data.IRecipe addRecipe(String name, boolean global, ItemStack result, int width, int height, ItemStack... objects) {
        NonNullList<Ingredient> list = NonNullList.create();
        for (ItemStack item : objects) {
            if (!item.isEmpty()) {
                list.add(Ingredient.fromStacks(new ItemStack[] { item }));
            }
        }
        RecipeCarpentry recipe = new RecipeCarpentry(width, height, list, result);
        recipe.isGlobal = global;
        recipe.name = name;
        try {
            return this.saveRecipe(recipe);
        }
        catch (IOException e) {
            e.printStackTrace();
            return recipe;
        }
    }
    
    static {
        RecipeController.syncRecipes = new HashMap<Integer, RecipeCarpentry>();
    }
}
