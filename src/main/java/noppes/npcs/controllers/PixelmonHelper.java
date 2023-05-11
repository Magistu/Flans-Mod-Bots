package noppes.npcs.controllers;

import net.minecraftforge.fml.common.Loader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import java.util.ArrayList;
import java.util.List;
import noppes.npcs.LogWriter;
import net.minecraft.entity.player.EntityPlayerMP;
import java.lang.reflect.Method;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class PixelmonHelper
{
    public static boolean Enabled;
    public static EventBus EVENT_BUS;
    public static Object storageManager;
    private static Method getPartyStorage;
    private static Method getPcStorage;
    private static Method getPokemonData;
    private static Method getPixelmonModel;
    private static Class modelSetupClass;
    private static Method modelSetupMethod;
    private static Class pixelmonClass;
    
    public static void load() {
        if (!PixelmonHelper.Enabled) {
            return;
        }
        try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
            PixelmonHelper.storageManager = c.getDeclaredField("storageManager").get(null);
            PixelmonHelper.EVENT_BUS = (EventBus)c.getDeclaredField("EVENT_BUS").get(null);
            c = Class.forName("com.pixelmonmod.pixelmon.api.storage.IStorageManager");
            PixelmonHelper.getPartyStorage = c.getMethod("getParty", EntityPlayerMP.class);
            PixelmonHelper.getPcStorage = c.getMethod("getPCForPlayer", EntityPlayerMP.class);
            PixelmonHelper.pixelmonClass = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity1Base");
            PixelmonHelper.getPokemonData = PixelmonHelper.pixelmonClass.getMethod("getPokemonData", (Class[])new Class[0]);
        }
        catch (Exception e) {
            LogWriter.except(e);
            PixelmonHelper.Enabled = false;
        }
    }
    
    public static void loadClient() {
        if (!PixelmonHelper.Enabled) {
            return;
        }
        try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity2Client");
            PixelmonHelper.getPixelmonModel = c.getMethod("getModel", (Class[])new Class[0]);
            PixelmonHelper.modelSetupClass = Class.forName("com.pixelmonmod.pixelmon.client.models.PixelmonModelSmd");
            PixelmonHelper.modelSetupMethod = PixelmonHelper.modelSetupClass.getMethod("setupForRender", c);
        }
        catch (Exception e) {
            LogWriter.except(e);
            PixelmonHelper.Enabled = false;
        }
    }
    
    public static List<String> getPixelmonList() {
        List<String> list = new ArrayList<String>();
        if (!PixelmonHelper.Enabled) {
            return list;
        }
        try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.enums.EnumPokemonModel");
            Object[] enumConstants;
            Object[] array = enumConstants = c.getEnumConstants();
            for (Object ob : enumConstants) {
                list.add(ob.toString());
            }
        }
        catch (Exception e) {
            LogManager.getLogger().error("getPixelmonList", (Throwable)e);
        }
        return list;
    }
    
    public static boolean isPixelmon(Entity entity) {
        if (!PixelmonHelper.Enabled) {
            return false;
        }
        String s = EntityList.getEntityString(entity);
        return s != null && s.contains("Pixelmon");
    }
    
    public static String getName(EntityLivingBase entity) {
        if (!PixelmonHelper.Enabled || !isPixelmon((Entity)entity)) {
            return "";
        }
        try {
            Method m = entity.getClass().getMethod("getName", (Class<?>[])new Class[0]);
            return m.invoke(entity, new Object[0]).toString();
        }
        catch (Exception e) {
            LogManager.getLogger().error("getName", (Throwable)e);
            return "";
        }
    }
    
    public static Object getModel(EntityLivingBase entity) {
        try {
            return PixelmonHelper.getPixelmonModel.invoke(entity, new Object[0]);
        }
        catch (Exception e) {
            LogManager.getLogger().error("getModel", (Throwable)e);
            return null;
        }
    }
    
    public static void setupModel(EntityLivingBase entity, Object model) {
        try {
            if (PixelmonHelper.modelSetupClass.isAssignableFrom(model.getClass())) {
                PixelmonHelper.modelSetupMethod.invoke(model, entity);
            }
        }
        catch (Exception e) {
            LogManager.getLogger().error("setupModel", (Throwable)e);
        }
    }
    
    public static Object getPokemonData(Entity entity) {
        try {
            return PixelmonHelper.getPokemonData.invoke(entity, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object getParty(EntityPlayerMP player) {
        try {
            return PixelmonHelper.getPartyStorage.invoke(PixelmonHelper.storageManager, player);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object getPc(EntityPlayerMP player) {
        try {
            return PixelmonHelper.getPcStorage.invoke(PixelmonHelper.storageManager, player);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Class getPixelmonClass() {
        return PixelmonHelper.pixelmonClass;
    }
    
    static {
        PixelmonHelper.Enabled = Loader.isModLoaded("pixelmon");
        PixelmonHelper.getPixelmonModel = null;
    }
}
