package noppes.npcs.client.gui.util;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.containers.ContainerEmpty;
import org.lwjgl.input.Mouse;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import java.util.HashMap;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;

public abstract class GuiContainerNPCInterface extends GuiContainer
{
    public boolean drawDefaultBackground;
    public int guiLeft;
    public int guiTop;
    public EntityPlayerSP player;
    public EntityNPCInterface npc;
    private HashMap<Integer, GuiNpcButton> buttons;
    private HashMap<Integer, GuiMenuTopButton> topbuttons;
    private HashMap<Integer, GuiNpcTextField> textfields;
    private HashMap<Integer, GuiNpcLabel> labels;
    private HashMap<Integer, GuiCustomScroll> scrolls;
    private HashMap<Integer, GuiNpcSlider> sliders;
    public String title;
    public boolean closeOnEsc;
    private SubGuiInterface subgui;
    public int mouseX;
    public int mouseY;
    
    public GuiContainerNPCInterface(EntityNPCInterface npc, Container cont) {
        super(cont);
        this.drawDefaultBackground = false;
        this.buttons = new HashMap<Integer, GuiNpcButton>();
        this.topbuttons = new HashMap<Integer, GuiMenuTopButton>();
        this.textfields = new HashMap<Integer, GuiNpcTextField>();
        this.labels = new HashMap<Integer, GuiNpcLabel>();
        this.scrolls = new HashMap<Integer, GuiCustomScroll>();
        this.sliders = new HashMap<Integer, GuiNpcSlider>();
        this.closeOnEsc = false;
        this.player = Minecraft.getMinecraft().player;
        this.npc = npc;
        this.title = "Npc Mainmenu";
        this.mc = Minecraft.getMinecraft();
        this.itemRender = this.mc.getRenderItem();
        this.fontRenderer = this.mc.fontRenderer;
    }
    
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        this.initPacket();
    }
    
    public void initPacket() {
    }
    
    public void initGui() {
        super.initGui();
        GuiNpcTextField.unfocus();
        this.buttonList.clear();
        this.buttons.clear();
        this.topbuttons.clear();
        this.scrolls.clear();
        this.sliders.clear();
        this.labels.clear();
        this.textfields.clear();
        Keyboard.enableRepeatEvents(true);
        if (this.subgui != null) {
            this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
            this.subgui.initGui();
        }
        this.buttonList.clear();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }
    
    public ResourceLocation getResource(String texture) {
        return new ResourceLocation("customnpcs", "textures/gui/" + texture);
    }
    
    public void updateScreen() {
        for (GuiNpcTextField tf : new ArrayList<GuiNpcTextField>(this.textfields.values())) {
            if (tf.enabled) {
                tf.updateCursorCounter();
            }
        }
        super.updateScreen();
    }
    
    protected void mouseClicked(int i, int j, int k) throws IOException {
        if (this.subgui != null) {
            this.subgui.mouseClicked(i, j, k);
        }
        else {
            for (GuiNpcTextField tf : new ArrayList<GuiNpcTextField>(this.textfields.values())) {
                if (tf.enabled) {
                    tf.mouseClicked(i, j, k);
                }
            }
            if (k == 0) {
                for (GuiCustomScroll scroll : new ArrayList<GuiCustomScroll>(this.scrolls.values())) {
                    scroll.mouseClicked(i, j, k);
                }
            }
            this.mouseEvent(i, j, k);
            super.mouseClicked(i, j, k);
        }
    }
    
    public void mouseEvent(int i, int j, int k) {
    }
    
    protected void keyTyped(char c, int i) {
        if (this.subgui != null) {
            this.subgui.keyTyped(c, i);
        }
        else {
            for (GuiNpcTextField tf : new ArrayList<GuiNpcTextField>(this.textfields.values())) {
                tf.textboxKeyTyped(c, i);
            }
            if (this.closeOnEsc && (i == 1 || (i == this.mc.gameSettings.keyBindInventory.getKeyCode() && !GuiNpcTextField.isActive()))) {
                this.close();
            }
        }
    }
    
    protected void actionPerformed(GuiButton guibutton) {
        if (this.subgui != null) {
            this.subgui.buttonEvent(guibutton);
        }
        else {
            this.buttonEvent(guibutton);
        }
    }
    
    public void buttonEvent(GuiButton guibutton) {
    }
    
    public void close() {
        GuiNpcTextField.unfocus();
        this.save();
        this.player.closeScreen();
        this.displayGuiScreen(null);
        this.mc.setIngameFocus();
    }
    
    public void addButton(GuiNpcButton button) {
        this.buttons.put(button.id, button);
        this.buttonList.add(button);
    }
    
    public void addTopButton(GuiMenuTopButton button) {
        this.topbuttons.put(button.id, button);
        this.buttonList.add(button);
    }
    
    public GuiNpcButton getButton(int i) {
        return this.buttons.get(i);
    }
    
    public void addTextField(GuiNpcTextField tf) {
        this.textfields.put(tf.id, tf);
    }
    
    public GuiNpcTextField getTextField(int i) {
        return this.textfields.get(i);
    }
    
    public void addLabel(GuiNpcLabel label) {
        this.labels.put(label.id, label);
    }
    
    public GuiNpcLabel getLabel(int i) {
        return this.labels.get(i);
    }
    
    public GuiMenuTopButton getTopButton(int i) {
        return this.topbuttons.get(i);
    }
    
    public void addSlider(GuiNpcSlider slider) {
        this.sliders.put(slider.id, slider);
        this.buttonList.add(slider);
    }
    
    public GuiNpcSlider getSlider(int i) {
        return this.sliders.get(i);
    }
    
    public void addScroll(GuiCustomScroll scroll) {
        scroll.setWorldAndResolution(this.mc, scroll.width, scroll.height);
        this.scrolls.put(scroll.id, scroll);
    }
    
    public GuiCustomScroll getScroll(int id) {
        return this.scrolls.get(id);
    }
    
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    }
    
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        this.drawCenteredString(this.fontRenderer, I18n.translateToLocal(this.title), this.width / 2, this.guiTop - 8, 16777215);
        for (GuiNpcLabel label : new ArrayList<GuiNpcLabel>(this.labels.values())) {
            label.drawLabel((GuiScreen)this, this.fontRenderer);
        }
        for (GuiNpcTextField tf : new ArrayList<GuiNpcTextField>(this.textfields.values())) {
            tf.drawTextBox(i, j);
        }
        for (GuiCustomScroll scroll : new ArrayList<GuiCustomScroll>(this.scrolls.values())) {
            scroll.drawScreen(i, j, f, this.hasSubGui() ? 0 : Mouse.getDWheel());
        }
    }
    
    public abstract void save();
    
    public void drawScreen(int i, int j, float f) {
        this.mouseX = i;
        this.mouseY = j;
        Container container = this.inventorySlots;
        if (this.subgui != null) {
            this.inventorySlots = new ContainerEmpty();
        }
        super.drawScreen(i, j, f);
        this.zLevel = 0.0f;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.subgui != null) {
            this.inventorySlots = container;
            RenderHelper.disableStandardItemLighting();
            this.subgui.drawScreen(i, j, f);
        }
        else {
            this.renderHoveredToolTip(this.mouseX, this.mouseY);
        }
    }
    
    public void drawDefaultBackground() {
        if (this.drawDefaultBackground && this.subgui == null) {
            super.drawDefaultBackground();
        }
    }
    
    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }
    
    public void closeSubGui(SubGuiInterface gui) {
        this.subgui = null;
    }
    
    public boolean hasSubGui() {
        return this.subgui != null;
    }
    
    public SubGuiInterface getSubGui() {
        if (this.hasSubGui() && this.subgui.hasSubGui()) {
            return this.subgui.getSubGui();
        }
        return this.subgui;
    }
    
    public void displayGuiScreen(GuiScreen gui) {
        this.mc.displayGuiScreen(gui);
    }
    
    public void setSubGui(SubGuiInterface gui) {
        (this.subgui = gui).setWorldAndResolution(this.mc, this.width, this.height);
        ((GuiContainerNPCInterface)(this.subgui.parent = (GuiScreen)this)).initGui();
    }
    
    public void drawNpc(int x, int y) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.guiLeft + x), (float)(this.guiTop + y), 50.0f);
        float scale = 1.0f;
        if (this.npc.height > 2.4) {
            scale = 2.0f / this.npc.height;
        }
        GlStateManager.scale(-30.0f * scale, 30.0f * scale, 30.0f * scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float f2 = this.npc.renderYawOffset;
        float f3 = this.npc.rotationYaw;
        float f4 = this.npc.rotationPitch;
        float f5 = this.npc.rotationYawHead;
        float f6 = this.guiLeft + x - (float)this.mouseX;
        float f7 = this.guiTop + y - 50 - (float)this.mouseY;
        int orientation = 0;
        if (this.npc != null) {
            orientation = this.npc.ais.orientation;
            this.npc.ais.orientation = 0;
        }
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float)Math.atan(f7 / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        this.npc.renderYawOffset = (float)Math.atan(f6 / 40.0f) * 20.0f;
        this.npc.rotationYaw = (float)Math.atan(f6 / 40.0f) * 40.0f;
        this.npc.rotationPitch = -(float)Math.atan(f7 / 40.0f) * 20.0f;
        this.npc.rotationYawHead = this.npc.rotationYaw;
        this.mc.getRenderManager().playerViewY = 180.0f;
        this.mc.getRenderManager().renderEntity((Entity)this.npc, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        this.npc.renderYawOffset = f2;
        this.npc.rotationYaw = f3;
        this.npc.rotationPitch = f4;
        this.npc.rotationYawHead = f5;
        if (this.npc != null) {
            this.npc.ais.orientation = orientation;
        }
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
