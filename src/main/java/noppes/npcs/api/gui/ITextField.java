package noppes.npcs.api.gui;

public interface ITextField extends ICustomGuiComponent
{
    int getWidth();
    
    int getHeight();
    
    ITextField setSize(int p0, int p1);
    
    String getText();
    
    ITextField setText(String p0);
}
