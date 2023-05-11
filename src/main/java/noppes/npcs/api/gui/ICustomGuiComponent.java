package noppes.npcs.api.gui;

public interface ICustomGuiComponent
{
    int getID();
    
    ICustomGuiComponent setID(int p0);
    
    int getPosX();
    
    int getPosY();
    
    ICustomGuiComponent setPos(int p0, int p1);
    
    boolean hasHoverText();
    
    String[] getHoverText();
    
    ICustomGuiComponent setHoverText(String p0);
    
    ICustomGuiComponent setHoverText(String[] p0);
}
