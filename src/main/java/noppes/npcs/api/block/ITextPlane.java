package noppes.npcs.api.block;

public interface ITextPlane
{
    String getText();
    
    void setText(String p0);
    
    int getRotationX();
    
    int getRotationY();
    
    int getRotationZ();
    
    void setRotationX(int p0);
    
    void setRotationY(int p0);
    
    void setRotationZ(int p0);
    
    float getOffsetX();
    
    float getOffsetY();
    
    float getOffsetZ();
    
    void setOffsetX(float p0);
    
    void setOffsetY(float p0);
    
    void setOffsetZ(float p0);
    
    float getScale();
    
    void setScale(float p0);
}
