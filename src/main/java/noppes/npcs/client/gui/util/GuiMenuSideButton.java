package noppes.npcs.client.gui.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiMenuSideButton extends GuiNpcButton
{
    public static ResourceLocation resource;
    public boolean active;
    
    public GuiMenuSideButton(int i, int j, int k, String s) {
        this(i, j, k, 200, 20, s);
    }
    
    public GuiMenuSideButton(int i, int j, int k, int l, int i1, String s) {
        super(i, j, k, l, i1, s);
        this.active = false;
    }
    
    public int getHoverState(boolean flag) {
        if (this.active) {
            return 0;
        }
        return 1;
    }
    
    public void drawButton(Minecraft minecraft, int i, int j, float partialTicks) {
        if (!this.visible) {
            return;
        }
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.renderEngine.bindTexture(GuiMenuSideButton.resource);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int width = this.width + (this.active ? 2 : 0);
        this.hovered = (i >= this.x && j >= this.y && i < this.x + width && j < this.y + this.height);
        int k = this.getHoverState(this.hovered);
        this.drawTexturedModalRect(this.x, this.y, 0, k * 22, width, this.height);
        this.mouseDragged(minecraft, i, j);
        String text = "";
        float maxWidth = width * 0.75f;
        if (fontrenderer.getStringWidth(this.displayString) > maxWidth) {
            for (int h = 0; h < this.displayString.length(); ++h) {
                char c = this.displayString.charAt(h);
                if (fontrenderer.getStringWidth(text + c) > maxWidth) {
                    break;
                }
                text += c;
            }
            text += "...";
        }
        else {
            text = this.displayString;
        }
        if (this.active) {
            this.drawCenteredString(fontrenderer, text, this.x + width / 2, this.y + (this.height - 8) / 2, 16777120);
        }
        else if (this.hovered) {
            this.drawCenteredString(fontrenderer, text, this.x + width / 2, this.y + (this.height - 8) / 2, 16777120);
        }
        else {
            this.drawCenteredString(fontrenderer, text, this.x + width / 2, this.y + (this.height - 8) / 2, 14737632);
        }
    }
    
    protected void mouseDragged(Minecraft minecraft, int i, int j) {
    }
    
    public void mouseReleased(int i, int j) {
    }
    
    @Override
    public boolean mousePressed(Minecraft minecraft, int i, int j) {
        return !this.active && this.visible && this.hovered;
    }
    
    static {
        resource = new ResourceLocation("customnpcs", "textures/gui/menusidebutton.png");
    }
}
