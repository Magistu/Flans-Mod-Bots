package noppes.npcs.api.item;

public interface IItemBook extends IItemStack
{
    String[] getText();
    
    void setText(String[] p0);
    
    String getAuthor();
    
    void setAuthor(String p0);
    
    String getTitle();
    
    void setTitle(String p0);
}
