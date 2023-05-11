package noppes.npcs.api.entity.data;

import noppes.npcs.api.handler.data.IAvailability;

public interface IMark
{
    IAvailability getAvailability();
    
    int getColor();
    
    void setColor(int p0);
    
    int getType();
    
    void setType(int p0);
    
    void update();
}
