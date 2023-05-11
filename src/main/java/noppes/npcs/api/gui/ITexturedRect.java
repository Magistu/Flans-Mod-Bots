package noppes.npcs.api.gui;

public interface ITexturedRect extends ICustomGuiComponent
{
    String getTexture();
    
    ITexturedRect setTexture(String p0);
    
    int getWidth();
    
    int getHeight();
    
    ITexturedRect setSize(int p0, int p1);
    
    float getScale();
    
    ITexturedRect setScale(float p0);
    
    int getTextureX();
    
    int getTextureY();
    
    ITexturedRect setTextureOffset(int p0, int p1);
}
