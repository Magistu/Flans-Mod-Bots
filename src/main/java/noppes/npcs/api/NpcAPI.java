package noppes.npcs.api;

import net.minecraftforge.fml.common.Loader;
import java.io.File;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.entity.data.IPlayerMail;
import net.minecraft.util.DamageSource;
import noppes.npcs.api.handler.ICloneHandler;
import noppes.npcs.api.handler.IDialogHandler;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.handler.IFactionHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import noppes.npcs.api.block.IBlock;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.entity.IEntity;
import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.ICustomNpc;
import net.minecraft.world.World;

public abstract class NpcAPI
{
    private static NpcAPI instance;
    
    public abstract ICustomNpc createNPC(World p0);
    
    public abstract ICustomNpc spawnNPC(World p0, int p1, int p2, int p3);
    
    public abstract IEntity getIEntity(Entity p0);
    
    public abstract IBlock getIBlock(World p0, BlockPos p1);
    
    public abstract IContainer getIContainer(IInventory p0);
    
    public abstract IContainer getIContainer(Container p0);
    
    public abstract IItemStack getIItemStack(ItemStack p0);
    
    public abstract IWorld getIWorld(WorldServer p0);
    
    public abstract IWorld getIWorld(int p0);
    
    public abstract IWorld[] getIWorlds();
    
    public abstract INbt getINbt(NBTTagCompound p0);
    
    public abstract IPos getIPos(double p0, double p1, double p2);
    
    public abstract IFactionHandler getFactions();
    
    public abstract IRecipeHandler getRecipes();
    
    public abstract IQuestHandler getQuests();
    
    public abstract IDialogHandler getDialogs();
    
    public abstract ICloneHandler getClones();
    
    public abstract IDamageSource getIDamageSource(DamageSource p0);
    
    public abstract INbt stringToNbt(String p0);
    
    public abstract IPlayerMail createMail(String p0, String p1);
    
    public abstract ICustomGui createCustomGui(int p0, int p1, int p2, boolean p3);
    
    public abstract INbt getRawPlayerData(String p0);
    
    public abstract EventBus events();
    
    public abstract void registerCommand(CommandNoppesBase p0);
    
    public abstract File getGlobalDir();
    
    public abstract File getWorldDir();
    
    public static boolean IsAvailable() {
        return Loader.isModLoaded("customnpcs");
    }
    
    public static NpcAPI Instance() {
        if (NpcAPI.instance != null) {
            return NpcAPI.instance;
        }
        if (!IsAvailable()) {
            return null;
        }
        try {
            Class c = Class.forName("noppes.npcs.api.wrapper.WrapperNpcAPI");
            NpcAPI.instance = (NpcAPI)c.getMethod("Instance", (Class[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return NpcAPI.instance;
    }
    
    public abstract void registerPermissionNode(String p0, int p1);
    
    public abstract boolean hasPermissionNode(String p0);
    
    public abstract String executeCommand(IWorld p0, String p1);
    
    public abstract String getRandomName(int p0, int p1);
    
    static {
        NpcAPI.instance = null;
    }
}
