package noppes.npcs.api.gui;

public interface IButton extends ICustomGuiComponent
{
    int getWidth();
    
    int getHeight();
    
    IButton setSize(int p0, int p1);
    
    String getLabel();
    
    IButton setLabel(String p0);
    
    String getTexture();
    
    boolean hasTexture();
    
    IButton setTexture(String p0);
    
    int getTextureX();
    
    int getTextureY();
    
    IButton setTextureOffset(int p0, int p1);
}
