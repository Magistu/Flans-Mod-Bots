package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.NoppesUtil;
import java.io.IOException;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import java.util.Iterator;
import noppes.npcs.client.gui.util.GuiNpcButton;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import noppes.npcs.util.NaturalOrderComparator;
import java.util.Vector;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.entity.EntityNPCInterface;
import java.util.HashSet;
import noppes.npcs.client.AssetsBrowser;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNPCStringSlot;
import noppes.npcs.client.gui.util.GuiNPCInterface;

public abstract class GuiNpcSelectionInterface extends GuiNPCInterface
{
    public GuiNPCStringSlot slot;
    public GuiScreen parent;
    private String up;
    private String root;
    public AssetsBrowser assets;
    private HashSet<String> dataFolder;
    protected HashSet<String> dataTextures;
    
    public GuiNpcSelectionInterface(EntityNPCInterface npc, GuiScreen parent, String sound) {
        super(npc);
        this.up = "..<" + I18n.translateToLocal("gui.up") + ">..";
        this.root = "";
        this.dataFolder = new HashSet<String>();
        this.dataTextures = new HashSet<String>();
        this.root = AssetsBrowser.getRoot(sound);
        this.assets = new AssetsBrowser(this.root, this.getExtension());
        this.drawDefaultBackground = false;
        this.title = "";
        this.parent = parent;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.dataFolder.clear();
        String ss = "Current Folder: /assets" + this.root;
        this.addLabel(new GuiNpcLabel(0, ss, this.width / 2 - this.fontRenderer.getStringWidth(ss) / 2, 20, 16777215));
        Vector<String> list = new Vector<String>();
        if (!this.assets.isRoot) {
            list.add(this.up);
        }
        for (String folder : this.assets.folders) {
            list.add("/" + folder);
            this.dataFolder.add("/" + folder);
        }
        for (String texture : this.assets.files) {
            list.add(texture);
            this.dataTextures.add(texture);
        }
        Collections.sort(list, new NaturalOrderComparator());
        (this.slot = new GuiNPCStringSlot(list, this, false, 18)).registerScrollButtons(4, 5);
        this.addButton(new GuiNpcButton(2, this.width / 2 - 100, this.height - 44, 98, 20, "gui.back"));
        this.addButton(new GuiNpcButton(3, this.width / 2 + 2, this.height - 44, 98, 20, "gui.up"));
        this.getButton(3).enabled = !this.assets.isRoot;
    }
    
    @Override
    public void drawScreen(int i, int j, float f) {
        this.slot.drawScreen(i, j, f);
        super.drawScreen(i, j, f);
    }
    
    @Override
    public void elementClicked() {
        if (this.slot.selected != null && this.dataTextures.contains(this.slot.selected)) {
            if (this.parent instanceof GuiNPCInterface) {
                ((GuiNPCInterface)this.parent).elementClicked();
            }
            else if (this.parent instanceof GuiNPCInterface2) {
                ((GuiNPCInterface2)this.parent).elementClicked();
            }
        }
    }
    
    public void handleMouseInput() throws IOException {
        this.slot.handleMouseInput();
        super.handleMouseInput();
    }
    
    @Override
    public void doubleClicked() {
        if (this.slot.selected.equals(this.up)) {
            this.root = this.root.substring(0, this.root.lastIndexOf("/"));
            this.assets = new AssetsBrowser(this.root, this.getExtension());
            this.initGui();
        }
        else if (this.dataFolder.contains(this.slot.selected)) {
            this.root += this.slot.selected;
            this.assets = new AssetsBrowser(this.root, this.getExtension());
            this.initGui();
        }
        else {
            this.close();
            NoppesUtil.openGUI((EntityPlayer)this.player, this.parent);
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (id == 2) {
            this.close();
            NoppesUtil.openGUI((EntityPlayer)this.player, this.parent);
        }
        if (id == 3) {
            this.root = this.root.substring(0, this.root.lastIndexOf("/"));
            this.assets = new AssetsBrowser(this.root, this.getExtension());
            this.initGui();
        }
    }
    
    @Override
    public void save() {
    }
    
    public String getSelected() {
        return this.assets.getAsset(this.slot.selected);
    }
    
    public abstract String[] getExtension();
}
