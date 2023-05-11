package noppes.npcs.controllers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagList;
import javax.script.ScriptEngine;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import noppes.npcs.util.NBTJsonUtil;
import java.io.IOException;
import noppes.npcs.api.wrapper.WorldWrapper;
import java.util.Iterator;
import javax.script.Invocable;
import noppes.npcs.LogWriter;
import net.minecraft.launchwrapper.Launch;
import noppes.npcs.CustomNpcs;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import java.io.File;
import noppes.npcs.controllers.data.ForgeScriptData;
import noppes.npcs.controllers.data.PlayerScriptData;
import javax.script.ScriptEngineFactory;
import java.util.Map;
import javax.script.ScriptEngineManager;

public class ScriptController
{
    public static ScriptController Instance;
    public static boolean HasStart;
    private ScriptEngineManager manager;
    public Map<String, String> languages;
    public Map<String, ScriptEngineFactory> factories;
    public Map<String, String> scripts;
    public PlayerScriptData playerScripts;
    public ForgeScriptData forgeScripts;
    public long lastLoaded;
    public long lastPlayerUpdate;
    public File dir;
    public NBTTagCompound compound;
    private boolean loaded;
    public boolean shouldSave;
    
    public ScriptController() {
        this.languages = new HashMap<String, String>();
        this.factories = new HashMap<String, ScriptEngineFactory>();
        this.scripts = new HashMap<String, String>();
        this.playerScripts = new PlayerScriptData(null);
        this.forgeScripts = new ForgeScriptData();
        this.lastLoaded = 0L;
        this.lastPlayerUpdate = 0L;
        this.compound = new NBTTagCompound();
        this.loaded = false;
        this.shouldSave = false;
        this.loaded = false;
        ScriptController.Instance = this;
        if (!CustomNpcs.NashorArguments.isEmpty()) {
            System.setProperty("nashorn.args", CustomNpcs.NashorArguments);
        }
        this.manager = new ScriptEngineManager();
        try {
            if (this.manager.getEngineByName("ecmascript") == null) {
                Launch.classLoader.addClassLoaderExclusion("jdk.nashorn.");
                Launch.classLoader.addClassLoaderExclusion("jdk.internal.dynalink");
                Class c = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
                ScriptEngineFactory factory = (ScriptEngineFactory) c.newInstance();
                factory.getScriptEngine();
                this.manager.registerEngineName("ecmascript", factory);
                this.manager.registerEngineExtension("js", factory);
                this.manager.registerEngineMimeType("application/ecmascript", factory);
                this.languages.put(factory.getLanguageName(), ".js");
                this.factories.put(factory.getLanguageName().toLowerCase(), factory);
            }
        }
        catch (Throwable t) {}
        try {
            Class c = Class.forName("org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory");
            ScriptEngineFactory factory = (ScriptEngineFactory) c.newInstance();
            factory.getScriptEngine();
            this.manager.registerEngineName("kotlin", factory);
            this.manager.registerEngineExtension("ktl", factory);
            this.manager.registerEngineMimeType("application/kotlin", factory);
            this.languages.put(factory.getLanguageName(), ".ktl");
            this.factories.put(factory.getLanguageName().toLowerCase(), factory);
        }
        catch (Throwable t2) {}
        LogWriter.info("Script Engines Available:");
        for (ScriptEngineFactory fac : this.manager.getEngineFactories()) {
            try {
                if (fac.getExtensions().isEmpty()) {
                    continue;
                }
                if (!(fac.getScriptEngine() instanceof Invocable) && !fac.getLanguageName().equals("lua")) {
                    continue;
                }
                String ext = "." + fac.getExtensions().get(0).toLowerCase();
                LogWriter.info(fac.getLanguageName() + ": " + ext);
                this.languages.put(fac.getLanguageName(), ext);
                this.factories.put(fac.getLanguageName().toLowerCase(), fac);
            }
            catch (Throwable t3) {}
        }
    }
    
    public void loadCategories() {
        this.dir = new File(CustomNpcs.getWorldSaveDirectory(), "scripts");
        if (!this.dir.exists()) {
            this.dir.mkdirs();
        }
        if (!this.worldDataFile().exists()) {
            this.shouldSave = true;
        }
        WorldWrapper.tempData.clear();
        this.scripts.clear();
        for (String language : this.languages.keySet()) {
            String ext = this.languages.get(language);
            File scriptDir = new File(this.dir, language.toLowerCase());
            if (!scriptDir.exists()) {
                scriptDir.mkdir();
            }
            else {
                this.loadDir(scriptDir, "", ext);
            }
        }
        this.lastLoaded = System.currentTimeMillis();
    }
    
    private void loadDir(File dir, String name, String ext) {
        for (File file : dir.listFiles()) {
            String filename = name + file.getName().toLowerCase();
            if (file.isDirectory()) {
                this.loadDir(file, filename + "/", ext);
            }
            else if (filename.endsWith(ext)) {
                try {
                    this.scripts.put(filename, this.readFile(file));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean loadStoredData() {
        this.compound = new NBTTagCompound();
        File file = this.worldDataFile();
        try {
            if (!file.exists()) {
                return false;
            }
            this.compound = NBTJsonUtil.LoadFile(file);
            this.shouldSave = false;
        }
        catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            return false;
        }
        return true;
    }
    
    private File worldDataFile() {
        return new File(this.dir, "world_data.json");
    }
    
    private File playerScriptsFile() {
        return new File(this.dir, "player_scripts.json");
    }
    
    private File forgeScriptsFile() {
        return new File(this.dir, "forge_scripts.json");
    }
    
    public boolean loadPlayerScripts() {
        this.playerScripts.clear();
        File file = this.playerScriptsFile();
        try {
            if (!file.exists()) {
                return false;
            }
            this.playerScripts.readFromNBT(NBTJsonUtil.LoadFile(file));
        }
        catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            return false;
        }
        return true;
    }
    
    public void setPlayerScripts(NBTTagCompound compound) {
        this.playerScripts.readFromNBT(compound);
        File file = this.playerScriptsFile();
        try {
            NBTJsonUtil.SaveFile(file, compound);
            this.lastPlayerUpdate = System.currentTimeMillis();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NBTJsonUtil.JsonException e2) {
            e2.printStackTrace();
        }
    }
    
    public boolean loadForgeScripts() {
        this.forgeScripts.clear();
        File file = this.forgeScriptsFile();
        try {
            if (!file.exists()) {
                return false;
            }
            this.forgeScripts.readFromNBT(NBTJsonUtil.LoadFile(file));
        }
        catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            return false;
        }
        return true;
    }
    
    public void setForgeScripts(NBTTagCompound compound) {
        this.forgeScripts.readFromNBT(compound);
        File file = this.forgeScriptsFile();
        try {
            NBTJsonUtil.SaveFile(file, compound);
            this.forgeScripts.lastInited = -1L;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NBTJsonUtil.JsonException e2) {
            e2.printStackTrace();
        }
    }
    
    private String readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        try {
            StringBuilder sb = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        }
        finally {
            br.close();
        }
    }
    
    public ScriptEngine getEngineByName(String language) {
        ScriptEngineFactory fac = this.factories.get(language.toLowerCase());
        if (fac == null) {
            return null;
        }
        return fac.getScriptEngine();
    }
    
    public NBTTagList nbtLanguages() {
        NBTTagList list = new NBTTagList();
        for (String language : this.languages.keySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagList scripts = new NBTTagList();
            for (String script : this.getScripts(language)) {
                scripts.appendTag((NBTBase)new NBTTagString(script));
            }
            compound.setTag("Scripts", (NBTBase)scripts);
            compound.setString("Language", language);
            list.appendTag((NBTBase)compound);
        }
        return list;
    }
    
    private List<String> getScripts(String language) {
        List<String> list = new ArrayList<String>();
        String ext = this.languages.get(language);
        if (ext == null) {
            return list;
        }
        for (String script : this.scripts.keySet()) {
            if (script.endsWith(ext)) {
                list.add(script);
            }
        }
        return list;
    }
    
    @SubscribeEvent
    public void saveWorld(WorldEvent.Save event) {
        if (!this.shouldSave || event.getWorld().isRemote || event.getWorld() != event.getWorld().getMinecraftServer().worlds[0]) {
            return;
        }
        try {
            NBTJsonUtil.SaveFile(this.worldDataFile(), this.compound.copy());
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
        this.shouldSave = false;
    }
    
    static {
        ScriptController.HasStart = false;
    }
}
