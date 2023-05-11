package noppes.npcs.util;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.common.io.Files;
import org.apache.commons.io.Charsets;
import java.io.File;
import java.util.Iterator;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import java.util.List;
import net.minecraft.nbt.NBTBase;
import java.util.ArrayList;
import net.minecraft.nbt.NBTTagCompound;

public class NBTJsonUtil
{
    public static String Convert(NBTTagCompound compound) {
        List<JsonLine> list = new ArrayList<JsonLine>();
        JsonLine line = ReadTag("", (NBTBase)compound, list);
        line.removeComma();
        return ConvertList(list);
    }
    
    public static NBTTagCompound Convert(String json) throws JsonException {
        json = json.trim();
        JsonFile file = new JsonFile(json);
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new JsonException("Not properly incapsulated between { }", file);
        }
        NBTTagCompound compound = new NBTTagCompound();
        FillCompound(compound, file);
        return compound;
    }
    
    public static void FillCompound(NBTTagCompound compound, JsonFile json) throws JsonException {
        if (json.startsWith("{") || json.startsWith(",")) {
            json.cut(1);
        }
        if (json.startsWith("}")) {
            return;
        }
        int index = json.keyIndex();
        if (index < 1) {
            throw new JsonException("Expected key after ,", json);
        }
        String key = json.substring(0, index);
        json.cut(index + 1);
        NBTBase base = ReadValue(json);
        if (base == null) {
            base = (NBTBase)new NBTTagString();
        }
        if (key.startsWith("\"")) {
            key = key.substring(1);
        }
        if (key.endsWith("\"")) {
            key = key.substring(0, key.length() - 1);
        }
        compound.setTag(key, base);
        if (json.startsWith(",")) {
            FillCompound(compound, json);
        }
    }
    
    public static NBTBase ReadValue(JsonFile json) throws JsonException {
        if (json.startsWith("{")) {
            NBTTagCompound compound = new NBTTagCompound();
            FillCompound(compound, json);
            if (!json.startsWith("}")) {
                throw new JsonException("Expected }", json);
            }
            json.cut(1);
            return (NBTBase)compound;
        }
        else if (json.startsWith("[")) {
            json.cut(1);
            NBTTagList list = new NBTTagList();
            if (json.startsWith("B;") || json.startsWith("I;") || json.startsWith("L;")) {
                json.cut(2);
            }
            for (NBTBase value = ReadValue(json); value != null; value = ReadValue(json)) {
                list.appendTag(value);
                if (!json.startsWith(",")) {
                    break;
                }
                json.cut(1);
            }
            if (!json.startsWith("]")) {
                throw new JsonException("Expected ]", json);
            }
            json.cut(1);
            if (list.getTagType() == 3) {
                int[] arr = new int[list.tagCount()];
                int i = 0;
                while (list.tagCount() > 0) {
                    arr[i] = ((NBTTagInt)list.removeTag(0)).getInt();
                    ++i;
                }
                return (NBTBase)new NBTTagIntArray(arr);
            }
            if (list.getTagType() == 1) {
                byte[] arr2 = new byte[list.tagCount()];
                int i = 0;
                while (list.tagCount() > 0) {
                    arr2[i] = ((NBTTagByte)list.removeTag(0)).getByte();
                    ++i;
                }
                return (NBTBase)new NBTTagByteArray(arr2);
            }
            if (list.getTagType() == 4) {
                long[] arr3 = new long[list.tagCount()];
                int i = 0;
                while (list.tagCount() > 0) {
                    arr3[i] = ((NBTTagLong)list.removeTag(0)).getByte();
                    ++i;
                }
                return (NBTBase)new NBTTagLongArray(arr3);
            }
            return (NBTBase)list;
        }
        else {
            if (json.startsWith("\"")) {
                json.cut(1);
                String s = "";
                String cut;
                for (boolean ignore = false; !json.startsWith("\"") || ignore; ignore = cut.equals("\\"), s += cut) {
                    cut = json.cutDirty(1);
                }
                json.cut(1);
                return (NBTBase)new NBTTagString(s.replace("\\\\", "\\").replace("\\\"", "\""));
            }
            String s = "";
            while (!json.startsWith(",", "]", "}")) {
                s += json.cut(1);
            }
            s = s.trim().toLowerCase();
            if (s.isEmpty()) {
                return null;
            }
            try {
                if (s.endsWith("d")) {
                    return (NBTBase)new NBTTagDouble(Double.parseDouble(s.substring(0, s.length() - 1)));
                }
                if (s.endsWith("f")) {
                    return (NBTBase)new NBTTagFloat(Float.parseFloat(s.substring(0, s.length() - 1)));
                }
                if (s.endsWith("b")) {
                    return (NBTBase)new NBTTagByte(Byte.parseByte(s.substring(0, s.length() - 1)));
                }
                if (s.endsWith("s")) {
                    return (NBTBase)new NBTTagShort(Short.parseShort(s.substring(0, s.length() - 1)));
                }
                if (s.endsWith("l")) {
                    return (NBTBase)new NBTTagLong(Long.parseLong(s.substring(0, s.length() - 1)));
                }
                if (s.contains(".")) {
                    return (NBTBase)new NBTTagDouble(Double.parseDouble(s));
                }
                return (NBTBase)new NBTTagInt(Integer.parseInt(s));
            }
            catch (NumberFormatException ex) {
                throw new JsonException("Unable to convert: " + s + " to a number", json);
            }
        }
    }
    
    private static List<NBTBase> getListData(NBTTagList list) {
        return (List<NBTBase>)ObfuscationReflectionHelper.getPrivateValue((Class)NBTTagList.class, (Object)list, 1);
    }
    
    private static JsonLine ReadTag(String name, NBTBase base, List<JsonLine> list) {
        if (!name.isEmpty()) {
            name = "\"" + name + "\": ";
        }
        if (base.getId() == 9) {
            list.add(new JsonLine(name + "["));
            NBTTagList tags = (NBTTagList)base;
            JsonLine line = null;
            List<NBTBase> data = getListData(tags);
            for (NBTBase b : data) {
                line = ReadTag("", b, list);
            }
            if (line != null) {
                line.removeComma();
            }
            list.add(new JsonLine("]"));
        }
        else if (base.getId() == 10) {
            list.add(new JsonLine(name + "{"));
            NBTTagCompound compound = (NBTTagCompound)base;
            JsonLine line = null;
            for (Object key : compound.getKeySet()) {
                line = ReadTag(key.toString(), compound.getTag(key.toString()), list);
            }
            if (line != null) {
                line.removeComma();
            }
            list.add(new JsonLine("}"));
        }
        else if (base.getId() == 11) {
            list.add(new JsonLine(name + base.toString().replaceFirst(",]", "]")));
        }
        else {
            list.add(new JsonLine(name + base));
        }
        JsonLine jsonLine;
        JsonLine line2 = jsonLine = list.get(list.size() - 1);
        jsonLine.line += ",";
        return line2;
    }
    
    private static String ConvertList(List<JsonLine> list) {
        String json = "";
        int tab = 0;
        for (JsonLine tag : list) {
            if (tag.reduceTab()) {
                --tab;
            }
            for (int i = 0; i < tab; ++i) {
                json += "    ";
            }
            json = json + tag + "\n";
            if (tag.increaseTab()) {
                ++tab;
            }
        }
        return json;
    }
    
    public static NBTTagCompound LoadFile(File file) throws IOException, JsonException {
        return Convert(Files.toString(file, Charsets.UTF_8));
    }
    
    public static void SaveFile(File file, NBTTagCompound compound) throws IOException, JsonException {
        String json = Convert(compound);
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
            writer.write(json);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    public static void main(String[] args) {
        NBTTagCompound comp = new NBTTagCompound();
        NBTTagCompound comp2 = new NBTTagCompound();
        comp2.setByteArray("test", new byte[] { 0, 0, 1, 1, 0 });
        comp.setTag("comp", (NBTBase)comp2);
        System.out.println(Convert(comp));
    }
    
    static class JsonLine
    {
        private String line;
        
        public JsonLine(String line) {
            this.line = line;
        }
        
        public void removeComma() {
            if (this.line.endsWith(",")) {
                this.line = this.line.substring(0, this.line.length() - 1);
            }
        }
        
        public boolean reduceTab() {
            int length = this.line.length();
            return (length == 1 && (this.line.endsWith("}") || this.line.endsWith("]"))) || (length == 2 && (this.line.endsWith("},") || this.line.endsWith("],")));
        }
        
        public boolean increaseTab() {
            return this.line.endsWith("{") || this.line.endsWith("[");
        }
        
        @Override
        public String toString() {
            return this.line;
        }
    }
    
    static class JsonFile
    {
        private String original;
        private String text;
        
        public JsonFile(String text) {
            this.text = text;
            this.original = text;
        }
        
        public int keyIndex() {
            boolean hasQuote = false;
            for (int i = 0; i < this.text.length(); ++i) {
                char c = this.text.charAt(i);
                if (i == 0 && c == '\"') {
                    hasQuote = true;
                }
                else if (hasQuote && c == '\"') {
                    hasQuote = false;
                }
                if (!hasQuote && c == ':') {
                    return i;
                }
            }
            return -1;
        }
        
        public String cutDirty(int i) {
            String s = this.text.substring(0, i);
            this.text = this.text.substring(i);
            return s;
        }
        
        public String cut(int i) {
            String s = this.text.substring(0, i);
            this.text = this.text.substring(i).trim();
            return s;
        }
        
        public String substring(int beginIndex, int endIndex) {
            return this.text.substring(beginIndex, endIndex);
        }
        
        public int indexOf(String s) {
            return this.text.indexOf(s);
        }
        
        public String getCurrentPos() {
            int lengthOr = this.original.length();
            int lengthCur = this.text.length();
            int currentPos = lengthOr - lengthCur;
            String done = this.original.substring(0, currentPos);
            String[] lines = done.split("\r\n|\r|\n");
            int pos = 0;
            String line = "";
            if (lines.length > 0) {
                pos = lines[lines.length - 1].length();
                line = this.original.split("\r\n|\r|\n")[lines.length - 1].trim();
            }
            return "Line: " + lines.length + ", Pos: " + pos + ", Text: " + line;
        }
        
        public boolean startsWith(String... ss) {
            for (String s : ss) {
                if (this.text.startsWith(s)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean endsWith(String s) {
            return this.text.endsWith(s);
        }
    }
    
    public static class JsonException extends Exception
    {
        public JsonException(String message, JsonFile json) {
            super(message + ": " + json.getCurrentPos());
        }
    }
}
