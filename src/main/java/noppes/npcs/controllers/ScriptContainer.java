package noppes.npcs.controllers;

import noppes.npcs.LogWriter;
import noppes.npcs.NoppesStringUtils;
import java.util.function.Function;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.constants.ParticleType;
import net.minecraft.potion.PotionType;
import noppes.npcs.api.constants.TacticalType;
import noppes.npcs.api.constants.SideType;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.NoppesUtilServer;
import javax.script.Invocable;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import noppes.npcs.CustomNpcs;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.constants.EnumScriptType;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import java.util.Map;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import javax.script.ScriptEngine;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.HashMap;

public class ScriptContainer
{
    private static String lock = "lock";
    public static ScriptContainer Current;
    private static String CurrentType;
    private static HashMap<String, Object> Data;
    public String fullscript;
    public String script;
    public TreeMap<Long, String> console;
    public boolean errored;
    public List<String> scripts;
    private HashSet<String> unknownFunctions;
    public long lastCreated;
    private String currentScriptLanguage;
    private ScriptEngine engine;
    private IScriptHandler handler;
    private boolean init;
    private static Method luaCoerce;
    private static Method luaCall;
    
    private static void FillMap(Class c) {
        try {
            ScriptContainer.Data.put(c.getSimpleName(), c.newInstance());
        }
        catch (Exception ex) {}
        Field[] declaredFields2;
        Field[] declaredFields = declaredFields2 = c.getDeclaredFields();
        for (Field field : declaredFields2) {
            try {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == Integer.TYPE) {
                    ScriptContainer.Data.put(c.getSimpleName() + "_" + field.getName(), field.getInt(null));
                }
            }
            catch (Exception ex2) {}
        }
    }
    
    public ScriptContainer(IScriptHandler handler) {
        this.fullscript = "";
        this.script = "";
        this.console = new TreeMap<Long, String>();
        this.errored = false;
        this.scripts = new ArrayList<String>();
        this.unknownFunctions = new HashSet<String>();
        this.lastCreated = 0L;
        this.currentScriptLanguage = null;
        this.engine = null;
        this.handler = null;
        this.init = false;
        this.handler = handler;
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        this.script = compound.getString("Script");
        this.console = NBTTags.GetLongStringMap(compound.getTagList("Console", 10));
        this.scripts = NBTTags.getStringList(compound.getTagList("ScriptList", 10));
        this.lastCreated = 0L;
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("Script", this.script);
        compound.setTag("Console", (NBTBase)NBTTags.NBTLongStringMap(this.console));
        compound.setTag("ScriptList", (NBTBase)NBTTags.nbtStringList(this.scripts));
        return compound;
    }
    
    private String getFullCode() {
        if (!this.init) {
            this.fullscript = this.script;
            if (!this.fullscript.isEmpty()) {
                this.fullscript += "\n";
            }
            for (String loc : this.scripts) {
                String code = ScriptController.Instance.scripts.get(loc);
                if (code != null && !code.isEmpty()) {
                    this.fullscript = this.fullscript + code + "\n";
                }
            }
            this.unknownFunctions = new HashSet<String>();
        }
        return this.fullscript;
    }
    
    public void run(EnumScriptType type, Event event) {
        this.run(type.function, event);
    }
    
    public void run(String type, Object event) {
        if (this.errored || !this.hasCode() || this.unknownFunctions.contains(type) || !CustomNpcs.EnableScripting) {
            return;
        }
        this.setEngine(this.handler.getLanguage());
        if (this.engine == null) {
            return;
        }
        if (ScriptController.Instance.lastLoaded > this.lastCreated) {
            this.lastCreated = ScriptController.Instance.lastLoaded;
            this.init = false;
        }
        synchronized ("lock") {
            ScriptContainer.Current = this;
            ScriptContainer.CurrentType = type;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            this.engine.getContext().setWriter(pw);
            this.engine.getContext().setErrorWriter(pw);
            try {
                if (!this.init) {
                    this.engine.eval(this.getFullCode());
                    this.init = true;
                }
                if (this.engine.getFactory().getLanguageName().equals("lua")) {
                    Object ob = this.engine.get(type);
                    if (ob != null) {
                        if (ScriptContainer.luaCoerce == null) {
                            ScriptContainer.luaCoerce = Class.forName("org.luaj.vm2.lib.jse.CoerceJavaToLua").getMethod("coerce", Object.class);
                            ScriptContainer.luaCall = ob.getClass().getMethod("call", Class.forName("org.luaj.vm2.LuaValue"));
                        }
                        ScriptContainer.luaCall.invoke(ob, ScriptContainer.luaCoerce.invoke(null, event));
                    }
                    else {
                        this.unknownFunctions.add(type);
                    }
                }
                else {
                    ((Invocable)this.engine).invokeFunction(type, event);
                }
            }
            catch (NoSuchMethodException e2) {
                this.unknownFunctions.add(type);
            }
            catch (Throwable e) {
                this.errored = true;
                e.printStackTrace(pw);
                NoppesUtilServer.NotifyOPs(this.handler.noticeString() + " script errored", new Object[0]);
            }
            finally {
                this.appandConsole(sw.getBuffer().toString().trim());
                pw.close();
                ScriptContainer.Current = null;
            }
        }
    }
    
    public void appandConsole(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        long time = System.currentTimeMillis();
        if (this.console.containsKey(time)) {
            message = this.console.get(time) + "\n" + message;
        }
        this.console.put(time, message);
        while (this.console.size() > 40) {
            this.console.remove(this.console.firstKey());
        }
    }
    
    public boolean hasCode() {
        return !this.getFullCode().isEmpty();
    }
    
    public void setEngine(String scriptLanguage) {
        if (this.currentScriptLanguage != null && this.currentScriptLanguage.equals(scriptLanguage)) {
            return;
        }
        this.engine = ScriptController.Instance.getEngineByName(scriptLanguage);
        if (this.engine == null) {
            this.errored = true;
            return;
        }
        for (Map.Entry<String, Object> entry : ScriptContainer.Data.entrySet()) {
            this.engine.put(entry.getKey(), entry.getValue());
        }
        this.engine.put("dump", new Dump());
        this.engine.put("log", new Log());
        this.currentScriptLanguage = scriptLanguage;
        this.init = false;
    }
    
    public boolean isValid() {
        return this.init && !this.errored;
    }
    
    static {
        Data = new HashMap<String, Object>();
        FillMap(AnimationType.class);
        FillMap(EntityType.class);
        FillMap(RoleType.class);
        FillMap(JobType.class);
        FillMap(SideType.class);
        FillMap(TacticalType.class);
        FillMap(PotionType.class);
        FillMap(ParticleType.class);
        ScriptContainer.Data.put("PosZero", new BlockPosWrapper(BlockPos.ORIGIN));
    }
    
    private class Dump implements Function<Object, String>
    {
        @Override
        public String apply(Object o) {
            if (o == null) {
                return "null";
            }
            StringBuilder builder = new StringBuilder();
            builder.append(o + ":" + NoppesStringUtils.newLine());
            for (Field field : o.getClass().getFields()) {
                try {
                    builder.append(field.getName() + " - " + field.getType().getSimpleName() + ", ");
                }
                catch (IllegalArgumentException ex) {}
            }
            for (Method method : o.getClass().getMethods()) {
                try {
                    String s = method.getName() + "(";
                    for (Class c : method.getParameterTypes()) {
                        s = s + c.getSimpleName() + ", ";
                    }
                    if (s.endsWith(", ")) {
                        s = s.substring(0, s.length() - 2);
                    }
                    builder.append(s + "), ");
                }
                catch (IllegalArgumentException ex2) {}
            }
            return builder.toString();
        }
    }
    
    private class Log implements Function<Object, Void>
    {
        @Override
        public Void apply(Object o) {
            ScriptContainer.this.appandConsole(o + "");
            LogWriter.info(o + "");
            return null;
        }
    }
}
