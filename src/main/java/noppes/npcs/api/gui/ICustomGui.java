package noppes.npcs.api.gui;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemStack;
import java.util.List;

public interface ICustomGui
{
    int getID();
    
    int getWidth();
    
    int getHeight();
    
    List<ICustomGuiComponent> getComponents();
    
    List<IItemSlot> getSlots();
    
    void setSize(int p0, int p1);
    
    void setDoesPauseGame(boolean p0);
    
    void setBackgroundTexture(String p0);
    
    IButton addButton(int p0, String p1, int p2, int p3);
    
    IButton addButton(int p0, String p1, int p2, int p3, int p4, int p5);
    
    IButton addTexturedButton(int p0, String p1, int p2, int p3, int p4, int p5, String p6);
    
    IButton addTexturedButton(int p0, String p1, int p2, int p3, int p4, int p5, String p6, int p7, int p8);
    
    ILabel addLabel(int p0, String p1, int p2, int p3, int p4, int p5);
    
    ILabel addLabel(int p0, String p1, int p2, int p3, int p4, int p5, int p6);
    
    ITextField addTextField(int p0, int p1, int p2, int p3, int p4);
    
    ITexturedRect addTexturedRect(int p0, String p1, int p2, int p3, int p4, int p5);
    
    ITexturedRect addTexturedRect(int p0, String p1, int p2, int p3, int p4, int p5, int p6, int p7);
    
    IScroll addScroll(int p0, int p1, int p2, int p3, int p4, String[] p5);
    
    IItemSlot addItemSlot(int p0, int p1);
    
    IItemSlot addItemSlot(int p0, int p1, IItemStack p2);
    
    void showPlayerInventory(int p0, int p1);
    
    ICustomGuiComponent getComponent(int p0);
    
    void removeComponent(int p0);
    
    void updateComponent(ICustomGuiComponent p0);
    
    void update(IPlayer p0);
}
