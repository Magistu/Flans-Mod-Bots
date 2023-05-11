package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.gui.IScroll;

public class CustomGuiScrollWrapper extends CustomGuiComponentWrapper implements IScroll
{
    int width;
    int height;
    int defaultSelection;
    String[] list;
    boolean multiSelect;
    
    public CustomGuiScrollWrapper() {
        this.defaultSelection = -1;
        this.multiSelect = false;
    }
    
    public CustomGuiScrollWrapper(int id, int x, int y, int width, int height, String[] list) {
        this.defaultSelection = -1;
        this.multiSelect = false;
        this.setID(id);
        this.setPos(x, y);
        this.setSize(width, height);
        this.setList(list);
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public IScroll setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
    
    @Override
    public String[] getList() {
        return this.list;
    }
    
    @Override
    public IScroll setList(String[] list) {
        this.list = list;
        return this;
    }
    
    @Override
    public int getDefaultSelection() {
        return this.defaultSelection;
    }
    
    @Override
    public IScroll setDefaultSelection(int defaultSelection) {
        this.defaultSelection = defaultSelection;
        return this;
    }
    
    @Override
    public boolean isMultiSelect() {
        return this.multiSelect;
    }
    
    @Override
    public IScroll setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
        return this;
    }
    
    @Override
    public int getType() {
        return 4;
    }
    
    @Override
    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        super.toNBT(nbt);
        nbt.setIntArray("size", new int[] { this.width, this.height });
        if (this.defaultSelection >= 0) {
            nbt.setInteger("default", this.defaultSelection);
        }
        NBTTagList list = new NBTTagList();
        for (String s : this.list) {
            list.appendTag((NBTBase)new NBTTagString(s));
        }
        nbt.setTag("list", (NBTBase)list);
        nbt.setBoolean("multiSelect", this.multiSelect);
        return nbt;
    }
    
    @Override
    public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
        super.fromNBT(nbt);
        this.setSize(nbt.getIntArray("size")[0], nbt.getIntArray("size")[1]);
        if (nbt.hasKey("default")) {
            this.setDefaultSelection(nbt.getInteger("default"));
        }
        NBTTagList tagList = nbt.getTagList("list", 8);
        String[] list = new String[tagList.tagCount()];
        for (int i = 0; i < tagList.tagCount(); ++i) {
            list[i] = ((NBTTagString)tagList.get(i)).getString();
        }
        this.setList(list);
        this.setMultiSelect(nbt.getBoolean("multiSelect"));
        return this;
    }
}
