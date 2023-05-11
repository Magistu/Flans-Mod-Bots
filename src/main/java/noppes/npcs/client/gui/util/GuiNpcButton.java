package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.gui.GuiButton;

public class GuiNpcButton extends GuiButton
{
    protected String[] display;
    private int displayValue;
    public int id;
    
    public GuiNpcButton(int i, int j, int k, String s) {
        super(i, j, k, I18n.translateToLocal(s));
        this.displayValue = 0;
        this.id = i;
    }
    
    public GuiNpcButton(int i, int j, int k, String[] display, int val) {
        this(i, j, k, display[val]);
        this.display = display;
        this.displayValue = val;
    }
    
    public GuiNpcButton(int i, int j, int k, int l, int m, String string) {
        super(i, j, k, l, m, I18n.translateToLocal(string));
        this.displayValue = 0;
        this.id = i;
    }
    
    public GuiNpcButton(int i, int j, int k, int l, int m, String string, boolean enabled) {
        this(i, j, k, l, m, string);
        this.enabled = enabled;
    }
    
    public GuiNpcButton(int i, int j, int k, int l, int m, String[] display, int val) {
        this(i, j, k, l, m, (display.length == 0) ? "" : display[val % display.length]);
        this.display = display;
        this.displayValue = ((display.length == 0) ? 0 : (val % display.length));
    }
    
    public GuiNpcButton(int i, int j, int k, int l, int m, int val, String... display) {
        this(i, j, k, l, m, (display.length == 0) ? "" : display[val % display.length]);
        this.display = display;
        this.displayValue = ((display.length == 0) ? 0 : (val % display.length));
    }
    
    public void setDisplayText(String text) {
        this.displayString = I18n.translateToLocal(text);
    }
    
    public int getValue() {
        return this.displayValue;
    }
    
    public void setEnabled(boolean bo) {
        this.enabled = bo;
    }
    
    public void setVisible(boolean b) {
        this.visible = b;
    }
    
    public boolean getVisible() {
        return this.visible;
    }
    
    public void setDisplay(int value) {
        this.displayValue = value;
        this.setDisplayText(this.display[value]);
    }
    
    public void setTextColor(int color) {
        this.packedFGColour = color;
    }
    
    public boolean mousePressed(Minecraft minecraft, int i, int j) {
        boolean bo = super.mousePressed(minecraft, i, j);
        if (bo && this.display != null && this.display.length != 0) {
            this.displayValue = (this.displayValue + 1) % this.display.length;
            this.setDisplayText(this.display[this.displayValue]);
        }
        return bo;
    }
    
    public int getWidth() {
        return this.width;
    }
}
