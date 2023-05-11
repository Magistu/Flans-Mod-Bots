package noppes.npcs.api.block;

import noppes.npcs.api.ITimers;
import noppes.npcs.api.item.IItemStack;

public interface IBlockScripted extends IBlock
{
    void setModel(IItemStack p0);
    
    void setModel(String p0);
    
    IItemStack getModel();
    
    ITimers getTimers();
    
    void setRedstonePower(int p0);
    
    int getRedstonePower();
    
    void setIsLadder(boolean p0);
    
    boolean getIsLadder();
    
    void setLight(int p0);
    
    int getLight();
    
    void setScale(float p0, float p1, float p2);
    
    float getScaleX();
    
    float getScaleY();
    
    float getScaleZ();
    
    void setRotation(int p0, int p1, int p2);
    
    int getRotationX();
    
    int getRotationY();
    
    int getRotationZ();
    
    String executeCommand(String p0);
    
    boolean getIsPassible();
    
    void setIsPassible(boolean p0);
    
    float getHardness();
    
    void setHardness(float p0);
    
    float getResistance();
    
    void setResistance(float p0);
    
    ITextPlane getTextPlane();
    
    ITextPlane getTextPlane2();
    
    ITextPlane getTextPlane3();
    
    ITextPlane getTextPlane4();
    
    ITextPlane getTextPlane5();
    
    ITextPlane getTextPlane6();
}
