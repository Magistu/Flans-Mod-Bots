package noppes.npcs.client.gui.util;

import java.util.Comparator;
import java.util.Collections;
import noppes.npcs.util.NaturalOrderComparator;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.gui.GuiSlot;

public class GuiNPCStringSlot extends GuiSlot
{
    private List<String> list;
    public String selected;
    public HashSet<String> selectedList;
    private boolean multiSelect;
    private GuiNPCInterface parent;
    public int size;
    
    public GuiNPCStringSlot(Collection<String> list, GuiNPCInterface parent, boolean multiSelect, int size) {
        super(Minecraft.getMinecraft(), parent.width, parent.height, 32, parent.height - 64, size);
        this.selectedList = new HashSet<String>();
        this.parent = parent;
        Collections.sort(this.list = new ArrayList<String>(list), new NaturalOrderComparator());
        this.multiSelect = multiSelect;
        this.size = size;
    }
    
    public void setList(List<String> list) {
        Collections.sort(list, new NaturalOrderComparator());
        this.list = list;
        this.selected = "";
    }
    
    protected int getSize() {
        return this.list.size();
    }
    
    protected void elementClicked(int i, boolean flag, int j, int k) {
        if (this.selected != null && this.selected.equals(this.list.get(i)) && flag) {
            this.parent.doubleClicked();
        }
        this.selected = this.list.get(i);
        if (this.selectedList.contains(this.selected)) {
            this.selectedList.remove(this.selected);
        }
        else {
            this.selectedList.add(this.selected);
        }
        this.parent.elementClicked();
    }
    
    protected boolean isSelected(int i) {
        if (!this.multiSelect) {
            return this.selected != null && this.selected.equals(this.list.get(i));
        }
        return this.selectedList.contains(this.list.get(i));
    }
    
    protected int getContentHeight() {
        return this.list.size() * this.size;
    }
    
    protected void drawBackground() {
        this.parent.drawDefaultBackground();
    }
    
    protected void drawSlot(int i, int j, int k, int l, int var6, int var7, float partialTick) {
        String s = this.list.get(i);
        this.parent.drawString(this.parent.getFontRenderer(), s, j + 50, k + 3, 16777215);
    }
    
    public void clear() {
        this.list.clear();
    }
}
