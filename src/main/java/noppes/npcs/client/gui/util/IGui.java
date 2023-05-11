package noppes.npcs.client.gui.util;

public interface IGui
{
    int getID();
    
    void drawScreen(int p0, int p1);
    
    void updateScreen();
    
    boolean isActive();
}
