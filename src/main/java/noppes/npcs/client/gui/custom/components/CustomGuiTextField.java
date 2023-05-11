package noppes.npcs.client.gui.custom.components;

import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.api.gui.ICustomGuiComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IKeyListener;
import noppes.npcs.client.gui.custom.interfaces.IClickListener;
import noppes.npcs.client.gui.custom.interfaces.IDataHolder;
import net.minecraft.client.gui.GuiTextField;

public class CustomGuiTextField extends GuiTextField implements IDataHolder, IClickListener, IKeyListener
{
    GuiCustom parent;
    String[] hoverText;
    public int id;
    
    public CustomGuiTextField(int id, int x, int y, int width, int height) {
        super(id, Minecraft.getMinecraft().fontRenderer, GuiCustom.guiLeft + x, GuiCustom.guiTop + y, width, height);
        this.id = id;
        this.setMaxStringLength(500);
    }
    
    public void keyTyped(char typedChar, int keyCode) {
        this.textboxKeyTyped(typedChar, keyCode);
    }
    
    public void onRender(Minecraft mc, int mouseX, int mouseY, int mouseWheel, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, (float)this.id);
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        this.drawTextBox();
        if (hovered && this.hoverText != null && this.hoverText.length > 0) {
            this.parent.hoverText = this.hoverText;
        }
        GlStateManager.popMatrix();
    }
    
    public void setParent(GuiCustom parent) {
        this.parent = parent;
    }
    
    public int getID() {
        return this.id;
    }
    
    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", this.id);
        tag.setString("text", this.getText());
        return tag;
    }
    
    public ICustomGuiComponent toComponent() {
        CustomGuiTextFieldWrapper component = new CustomGuiTextFieldWrapper(this.id, this.x - GuiCustom.guiLeft, this.y - GuiCustom.guiTop, this.width, this.height);
        component.setText(this.getText());
        component.setHoverText(this.hoverText);
        return component;
    }
    
    public static CustomGuiTextField fromComponent(CustomGuiTextFieldWrapper component) {
        CustomGuiTextField txt = new CustomGuiTextField(component.getID(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight());
        if (component.hasHoverText()) {
            txt.hoverText = component.getHoverText();
        }
        if (component.getText() != null && !component.getText().isEmpty()) {
            txt.setText(component.getText());
        }
        return txt;
    }
    
    public boolean mouseClicked(GuiCustom gui, int mouseX, int mouseY, int mouseButton) {
        return this.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
