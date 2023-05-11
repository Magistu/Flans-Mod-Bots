package noppes.npcs.api.gui;

public interface IScroll extends ICustomGuiComponent
{
    int getWidth();
    
    int getHeight();
    
    IScroll setSize(int p0, int p1);
    
    String[] getList();
    
    IScroll setList(String[] p0);
    
    int getDefaultSelection();
    
    IScroll setDefaultSelection(int p0);
    
    boolean isMultiSelect();
    
    IScroll setMultiSelect(boolean p0);
}
