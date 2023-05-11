package noppes.npcs.api.block;

import noppes.npcs.api.ITimers;

public interface IBlockScriptedDoor extends IBlock
{
    ITimers getTimers();
    
    boolean getOpen();
    
    void setOpen(boolean p0);
    
    void setBlockModel(String p0);
    
    String getBlockModel();
    
    float getHardness();
    
    void setHardness(float p0);
    
    float getResistance();
    
    void setResistance(float p0);
}
