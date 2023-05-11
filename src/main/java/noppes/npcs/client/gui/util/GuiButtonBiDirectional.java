package noppes.npcs.client.gui.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiButtonBiDirectional extends GuiNpcButton
{
    public static ResourceLocation resource;
    private int color;
    
    public GuiButtonBiDirectional(int id, int x, int y, int width, int height, String[] arr, int current) {
        super(id, x, y, width, height, arr, current);
        this.color = 16777215;
    }
    
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        boolean hoverL = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 14 && mouseY < this.y + this.height;
        boolean hoverR = !hoverL && mouseX >= this.x + this.width - 14 && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(GuiButtonBiDirectional.resource);
        this.drawTexturedModalRect(this.x, this.y, 0, hoverL ? 40 : 20, 11, 20);
        this.drawTexturedModalRect(this.x + this.width - 11, this.y, 11, ((hover && !hoverL) || hoverR) ? 40 : 20, 11, 20);
        int l = this.color;
        if (this.packedFGColour != 0) {
            l = this.packedFGColour;
        }
        else if (!this.enabled) {
            l = 10526880;
        }
        else if (hover) {
            l = 16777120;
        }
        String text = "";
        float maxWidth = (float)(this.width - 36);
        if (mc.fontRenderer.getStringWidth(this.displayString) > maxWidth) {
            for (int h = 0; h < this.displayString.length(); ++h) {
                char c = this.displayString.charAt(h);
                text += c;
                if (mc.fontRenderer.getStringWidth(text) > maxWidth) {
                    break;
                }
            }
            text += "...";
        }
        else {
            text = this.displayString;
        }
        if (hover) {
            text = "§n" + text;
        }
        this.drawCenteredString(mc.fontRenderer, text, this.x + this.width / 2, this.y + (this.height - 8) / 2, l);
    }
    
    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        int value = this.getValue();
        boolean bo = super.mousePressed(minecraft, mouseX, mouseY);
        if (bo && this.display != null && this.display.length != 0) {
            boolean hoverL = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 14 && mouseY < this.y + this.height;
            boolean hoverR = !hoverL && mouseX >= this.x + 14 && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (hoverR) {
                value = (value + 1) % this.display.length;
            }
            if (hoverL) {
                if (value <= 0) {
                    value = this.display.length;
                }
                --value;
            }
            this.setDisplay(value);
        }
        return bo;
    }
    
    static {
        resource = new ResourceLocation("customnpcs:textures/gui/arrowbuttons.png");
    }
}
