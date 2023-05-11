package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.entity.data.ILine;

public class Line implements ILine
{
    protected String text;
    protected String sound;
    private boolean showText;
    
    public Line() {
        this.text = "";
        this.sound = "";
        this.showText = true;
    }
    
    public Line(String text) {
        this.text = "";
        this.sound = "";
        this.showText = true;
        this.text = text;
    }
    
    public Line copy() {
        Line line = new Line(this.text);
        line.sound = this.sound;
        line.showText = this.showText;
        return line;
    }
    
    public static Line formatTarget(Line line, EntityLivingBase entity) {
        if (entity == null) {
            return line;
        }
        Line line2 = line.copy();
        if (entity instanceof EntityPlayer) {
            line2.text = line2.text.replace("@target", ((EntityPlayer)entity).getDisplayNameString());
        }
        else {
            line2.text = line2.text.replace("@target", entity.getName());
        }
        return line;
    }
    
    @Override
    public String getText() {
        return this.text;
    }
    
    @Override
    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        this.text = text;
    }
    
    @Override
    public String getSound() {
        return this.sound;
    }
    
    @Override
    public void setSound(String sound) {
        if (sound == null) {
            sound = "";
        }
        this.sound = sound;
    }
    
    @Override
    public boolean getShowText() {
        return this.showText;
    }
    
    @Override
    public void setShowText(boolean show) {
        this.showText = show;
    }
}
