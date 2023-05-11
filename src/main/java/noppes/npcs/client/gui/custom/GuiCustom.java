package noppes.npcs.client.gui.custom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import java.io.IOException;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.constants.EnumPlayerPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.custom.components.CustomGuiScrollComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiScrollWrapper;
import noppes.npcs.client.gui.custom.components.CustomGuiTexturedRect;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.client.gui.custom.components.CustomGuiTextField;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.client.gui.custom.components.CustomGuiLabel;
import noppes.npcs.api.wrapper.gui.CustomGuiLabelWrapper;
import noppes.npcs.client.gui.custom.components.CustomGuiButton;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;
import java.util.Arrays;
import org.lwjgl.input.Mouse;
import net.minecraft.client.gui.GuiTextField;
import java.util.Iterator;
import noppes.npcs.api.gui.ICustomGuiComponent;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.inventory.Container;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.client.gui.custom.interfaces.IDataHolder;
import noppes.npcs.client.gui.custom.interfaces.IKeyListener;
import noppes.npcs.client.gui.custom.interfaces.IClickListener;
import java.util.List;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiCustom extends GuiContainer implements ICustomScrollListener, IGuiData
{
    CustomGuiWrapper gui;
    int xSize;
    int ySize;
    public static int guiLeft;
    public static int guiTop;
    ResourceLocation background;
    public String[] hoverText;
    Map<Integer, IGuiComponent> components;
    List<IClickListener> clickListeners;
    List<IKeyListener> keyListeners;
    List<IDataHolder> dataHolders;
    
    public GuiCustom(ContainerCustomGui container) {
        super((Container)container);
        this.components = new HashMap<Integer, IGuiComponent>();
        this.clickListeners = new ArrayList<IClickListener>();
        this.keyListeners = new ArrayList<IKeyListener>();
        this.dataHolders = new ArrayList<IDataHolder>();
    }
    
    public void initGui() {
        super.initGui();
        if (this.gui != null) {
            GuiCustom.guiLeft = (this.width - this.xSize) / 2;
            GuiCustom.guiTop = (this.height - this.ySize) / 2;
            this.components.clear();
            this.clickListeners.clear();
            this.keyListeners.clear();
            this.dataHolders.clear();
            for (ICustomGuiComponent c : this.gui.getComponents()) {
                this.addComponent(c);
            }
        }
    }
    
    public void updateScreen() {
        super.updateScreen();
        for (IDataHolder component : this.dataHolders) {
            if (component instanceof GuiTextField) {
                ((GuiTextField)component).updateCursorCounter();
            }
        }
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoverText = null;
        this.drawDefaultBackground();
        if (this.background != null) {
            this.drawBackgroundTexture();
        }
        for (IGuiComponent component : this.components.values()) {
            component.onRender(this.mc, mouseX, mouseY, Mouse.getDWheel(), partialTicks);
        }
        if (this.hoverText != null) {
            this.drawHoveringText((List)Arrays.asList(this.hoverText), mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    }
    
    void drawBackgroundTexture() {
        this.mc.getTextureManager().bindTexture(this.background);
        this.drawTexturedModalRect(GuiCustom.guiLeft, GuiCustom.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    private void addComponent(ICustomGuiComponent component) {
        CustomGuiComponentWrapper c = (CustomGuiComponentWrapper)component;
        switch (c.getType()) {
            case 0: {
                CustomGuiButton button = CustomGuiButton.fromComponent((CustomGuiButtonWrapper)component);
                button.setParent(this);
                this.components.put(button.getID(), button);
                this.addClickListener(button);
                break;
            }
            case 1: {
                CustomGuiLabel lbl = CustomGuiLabel.fromComponent((CustomGuiLabelWrapper)component);
                lbl.setParent(this);
                this.components.put(lbl.getID(), lbl);
                break;
            }
            case 3: {
                CustomGuiTextField textField = CustomGuiTextField.fromComponent((CustomGuiTextFieldWrapper)component);
                textField.setParent(this);
                this.components.put(textField.id, textField);
                this.addDataHolder(textField);
                this.addClickListener(textField);
                this.addKeyListener(textField);
                break;
            }
            case 2: {
                CustomGuiTexturedRect rect = CustomGuiTexturedRect.fromComponent((CustomGuiTexturedRectWrapper)component);
                rect.setParent(this);
                this.components.put(rect.getID(), rect);
                break;
            }
            case 4: {
                CustomGuiScrollComponent scroll = new CustomGuiScrollComponent(this.mc, (GuiScreen)this, component.getID(), (CustomGuiScrollWrapper)component);
                scroll.fromComponent((CustomGuiScrollWrapper)component);
                scroll.setParent(this);
                this.components.put(scroll.getID(), scroll);
                this.addDataHolder(scroll);
                this.addClickListener(scroll);
                break;
            }
        }
    }
    
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        NoppesUtilPlayer.sendData(EnumPlayerPacket.CustomGuiButton, this.updateGui().toNBT(), button.id);
    }
    
    public void buttonClick(CustomGuiButton button) {
        NoppesUtilPlayer.sendData(EnumPlayerPacket.CustomGuiButton, this.updateGui().toNBT(), button.id);
    }
    
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        NoppesUtilPlayer.sendData(EnumPlayerPacket.CustomGuiScrollClick, this.updateGui().toNBT(), scroll.id, scroll.selected, this.getScrollSelection((CustomGuiScrollComponent)scroll), false);
    }
    
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        NoppesUtilPlayer.sendData(EnumPlayerPacket.CustomGuiScrollClick, this.updateGui().toNBT(), scroll.id, scroll.selected, this.getScrollSelection((CustomGuiScrollComponent)scroll), true);
    }
    
    public void onGuiClosed() {
        super.onGuiClosed();
    }
    
    CustomGuiWrapper updateGui() {
        for (IDataHolder component : this.dataHolders) {
            this.gui.updateComponent(component.toComponent());
        }
        return this.gui;
    }
    
    NBTTagCompound getScrollSelection(CustomGuiScrollComponent scroll) {
        NBTTagList list = new NBTTagList();
        if (scroll.component.isMultiSelect()) {
            for (String s : scroll.getSelectedList()) {
                list.appendTag((NBTBase)new NBTTagString(s));
            }
        }
        else {
            list.appendTag((NBTBase)new NBTTagString(scroll.getSelected()));
        }
        NBTTagCompound selection = new NBTTagCompound();
        selection.setTag("selection", (NBTBase)list);
        return selection;
    }
    
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (IKeyListener listener : this.keyListeners) {
            listener.keyTyped(typedChar, keyCode);
        }
        if (this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            return;
        }
        if (keyCode == 1 && this.gui != null) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.CustomGuiClose, this.updateGui().toNBT());
        }
        super.keyTyped(typedChar, keyCode);
    }
    
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (IClickListener listener : this.clickListeners) {
            listener.mouseClicked(this, mouseX, mouseY, mouseButton);
        }
    }
    
    public boolean doesGuiPauseGame() {
        return this.gui == null || this.gui.getDoesPauseGame();
    }
    
    public void addDataHolder(IDataHolder component) {
        this.dataHolders.add(component);
    }
    
    public void addKeyListener(IKeyListener component) {
        this.keyListeners.add(component);
    }
    
    public void addClickListener(IClickListener component) {
        this.clickListeners.add(component);
    }
    
    public void setGuiData(NBTTagCompound compound) {
        Minecraft mc = Minecraft.getMinecraft();
        CustomGuiWrapper gui = (CustomGuiWrapper)new CustomGuiWrapper().fromNBT(compound);
        ((ContainerCustomGui)this.inventorySlots).setGui(gui, (EntityPlayer)mc.player);
        this.gui = gui;
        this.xSize = gui.getWidth();
        this.ySize = gui.getHeight();
        if (!gui.getBackgroundTexture().isEmpty()) {
            this.background = new ResourceLocation(gui.getBackgroundTexture());
        }
        this.initGui();
    }
}
