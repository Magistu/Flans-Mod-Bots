package noppes.npcs.api.gui;

public interface ITexturedButton extends IButton
{
    String getTexture();
    
    ITexturedButton setTexture(String p0);
    
    int getTextureX();
    
    int getTextureY();
    
    ITexturedButton setTextureOffset(int p0, int p1);
}
