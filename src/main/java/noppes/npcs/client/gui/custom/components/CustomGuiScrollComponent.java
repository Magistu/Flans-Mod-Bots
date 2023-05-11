package noppes.npcs.client.gui.custom.components;

import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.util.List;
import noppes.npcs.api.gui.ICustomGuiComponent;
import java.util.Arrays;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import noppes.npcs.api.wrapper.gui.CustomGuiScrollWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IClickListener;
import noppes.npcs.client.gui.custom.interfaces.IDataHolder;
import noppes.npcs.client.gui.util.GuiCustomScroll;

public class CustomGuiScrollComponent extends GuiCustomScroll implements IDataHolder, IClickListener
{
    GuiCustom parent;
    public CustomGuiScrollWrapper component;
    
    public CustomGuiScrollComponent(Minecraft mc, GuiScreen parent, int id, CustomGuiScrollWrapper component) {
        super(parent, id, component.isMultiSelect());
        this.mc = mc;
        this.fontRenderer = mc.fontRenderer;
        this.component = component;
    }
    
    public void setParent(GuiCustom parent) {
        this.parent = parent;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void onRender(Minecraft mc, int mouseX, int mouseY, int mouseWheel, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, (float)this.id);
        boolean hovered = mouseX >= this.guiLeft && mouseY >= this.guiTop && mouseX < this.guiLeft + this.getWidth() && mouseY < this.guiTop + this.getHeight();
        super.drawScreen(mouseX, mouseY, partialTicks, mouseWheel);
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverText();
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean mouseClicked(GuiCustom gui, int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        return this.isMouseOver(mouseX, mouseY);
    }
    
    public void fromComponent(CustomGuiScrollWrapper component) {
        this.guiLeft = GuiCustom.guiLeft + component.getPosX();
        this.guiTop = GuiCustom.guiTop + component.getPosY();
        this.setSize(component.getWidth(), component.getHeight());
        this.setUnsortedList(Arrays.asList(component.getList()));
        if (component.getDefaultSelection() >= 0) {
            int defaultSelect = component.getDefaultSelection();
            if (defaultSelect < this.getList().size()) {
                this.selected = defaultSelect;
            }
        }
        if (component.hasHoverText()) {
            component.setHoverText(component.getHoverText());
        }
    }
    
    public ICustomGuiComponent toComponent() {
        List<String> list = this.getList();
        this.component.setList(list.toArray(new String[list.size()]));
        return this.component;
    }
    
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", this.id);
        if (!this.getSelectedList().isEmpty()) {
            NBTTagList tagList = new NBTTagList();
            for (String s : this.getSelectedList()) {
                tagList.appendTag((NBTBase)new NBTTagString(s));
            }
            nbt.setTag("selectedList", (NBTBase)tagList);
        }
        else if (this.getSelected() != null && !this.getSelected().isEmpty()) {
            nbt.setString("selected", this.getSelected());
        }
        else {
            nbt.setString("selected", "Null");
        }
        return nbt;
    }
}
