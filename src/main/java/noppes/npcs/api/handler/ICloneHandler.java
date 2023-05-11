package noppes.npcs.api.handler;

import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.IWorld;

public interface ICloneHandler
{
    IEntity spawn(double p0, double p1, double p2, int p3, String p4, IWorld p5);
    
    IEntity get(int p0, String p1, IWorld p2);
    
    void set(int p0, String p1, IEntity p2);
    
    void remove(int p0, String p1);
}
