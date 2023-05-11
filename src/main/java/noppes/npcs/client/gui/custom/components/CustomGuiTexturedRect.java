package noppes.npcs.client.gui.custom.components;

import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.api.gui.ICustomGuiComponent;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import net.minecraft.client.gui.Gui;

public class CustomGuiTexturedRect extends Gui implements IGuiComponent
{
    GuiCustom parent;
    ResourceLocation texture;
    int id;
    int x;
    int y;
    int width;
    int height;
    int textureX;
    int textureY;
    float scale;
    String[] hoverText;
    
    public CustomGuiTexturedRect(int id, String texture, int x, int y, int width, int height) {
        this(id, texture, x, y, width, height, 0, 0);
    }
    
    public CustomGuiTexturedRect(int id, String texture, int x, int y, int width, int height, int textureX, int textureY) {
        this.scale = 1.0f;
        this.id = id;
        this.texture = new ResourceLocation(texture);
        this.x = GuiCustom.guiLeft + x;
        this.y = GuiCustom.guiTop + y;
        this.width = width;
        this.height = height;
        this.textureX = textureX;
        this.textureY = textureY;
    }
    
    public void setParent(GuiCustom parent) {
        this.parent = parent;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void onRender(Minecraft mc, int mouseX, int mouseY, int mouseWheel, float partialTicks) {
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(this.texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)this.x, (double)(this.y + this.height * this.scale), (double)this.id).tex((double)((this.textureX + 0) * 0.00390625f), (double)((this.textureY + this.height) * 0.00390625f)).endVertex();
        bufferbuilder.pos((double)(this.x + this.width * this.scale), (double)(this.y + this.height * this.scale), (double)this.id).tex((double)((this.textureX + this.width) * 0.00390625f), (double)((this.textureY + this.height) * 0.00390625f)).endVertex();
        bufferbuilder.pos((double)(this.x + this.width * this.scale), (double)this.y, (double)this.id).tex((double)((this.textureX + this.width) * 0.00390625f), (double)((this.textureY + 0) * 0.00390625f)).endVertex();
        bufferbuilder.pos((double)this.x, (double)this.y, (double)this.id).tex((double)((this.textureX + 0) * 0.00390625f), (double)((this.textureY + 0) * 0.00390625f)).endVertex();
        tessellator.draw();
        if (hovered && this.hoverText != null && this.hoverText.length > 0) {
            this.parent.hoverText = this.hoverText;
        }
        GlStateManager.popMatrix();
    }
    
    public ICustomGuiComponent toComponent() {
        CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(this.id, this.texture.toString(), this.x, this.y, this.width, this.height, this.textureX, this.textureY);
        component.setHoverText(this.hoverText);
        component.setScale(this.scale);
        return component;
    }
    
    public static CustomGuiTexturedRect fromComponent(CustomGuiTexturedRectWrapper component) {
        CustomGuiTexturedRect rect;
        if (component.getTextureX() >= 0 && component.getTextureY() >= 0) {
            rect = new CustomGuiTexturedRect(component.getID(), component.getTexture(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), component.getTextureX(), component.getTextureY());
        }
        else {
            rect = new CustomGuiTexturedRect(component.getID(), component.getTexture(), component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight());
        }
        rect.scale = component.getScale();
        if (component.hasHoverText()) {
            rect.hoverText = component.getHoverText();
        }
        return rect;
    }
}
