package noppes.npcs.controllers.data;

import java.util.Map;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;
import java.util.Random;

public class Lines
{
    private static Random random;
    private int lastLine;
    public HashMap<Integer, Line> lines;
    
    public Lines() {
        this.lastLine = -1;
        this.lines = new HashMap<Integer, Line>();
    }
    
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        for (int slot : this.lines.keySet()) {
            Line line = this.lines.get(slot);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Slot", slot);
            nbttagcompound.setString("Line", line.getText());
            nbttagcompound.setString("Song", line.getSound());
            nbttaglist.appendTag((NBTBase)nbttagcompound);
        }
        compound.setTag("Lines", (NBTBase)nbttaglist);
        return compound;
    }
    
    public void readNBT(NBTTagCompound compound) {
        NBTTagList nbttaglist = compound.getTagList("Lines", 10);
        HashMap<Integer, Line> map = new HashMap<Integer, Line>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            Line line = new Line();
            line.setText(nbttagcompound.getString("Line"));
            line.setSound(nbttagcompound.getString("Song"));
            map.put(nbttagcompound.getInteger("Slot"), line);
        }
        this.lines = map;
    }
    
    public Line getLine(boolean isRandom) {
        if (this.lines.isEmpty()) {
            return null;
        }
        if (isRandom) {
            int i = Lines.random.nextInt(this.lines.size());
            for (Map.Entry<Integer, Line> e : this.lines.entrySet()) {
                if (--i < 0) {
                    return e.getValue().copy();
                }
            }
        }
        ++this.lastLine;
        Line line;
        while (true) {
            this.lastLine %= 8;
            line = this.lines.get(this.lastLine);
            if (line != null) {
                break;
            }
            ++this.lastLine;
        }
        return line.copy();
    }
    
    public boolean isEmpty() {
        return this.lines.isEmpty();
    }
    
    static {
        random = new Random();
    }
}
