package noppes.npcs.api.item;

public interface IItemScripted extends IItemStack
{
    boolean hasTexture(int p0);
    
    String getTexture(int p0);
    
    void setTexture(int p0, String p1);
    
    void setMaxStackSize(int p0);
    
    double getDurabilityValue();
    
    void setDurabilityValue(float p0);
    
    boolean getDurabilityShow();
    
    void setDurabilityShow(boolean p0);
    
    int getDurabilityColor();
    
    void setDurabilityColor(int p0);
    
    int getColor();
    
    void setColor(int p0);
}
