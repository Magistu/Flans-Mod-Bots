package noppes.npcs.api.gui;

public interface ILabel extends ICustomGuiComponent
{
    String getText();
    
    ILabel setText(String p0);
    
    int getWidth();
    
    int getHeight();
    
    ILabel setSize(int p0, int p1);
    
    int getColor();
    
    ILabel setColor(int p0);
    
    float getScale();
    
    ILabel setScale(float p0);
}
