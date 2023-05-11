package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.data.INPCJob;

public interface IJobPuppet extends INPCJob
{
    boolean getIsAnimated();
    
    void setIsAnimated(boolean p0);
    
    int getAnimationSpeed();
    
    void setAnimationSpeed(int p0);
    
    IJobPuppetPart getPart(int p0);
    
    public interface IJobPuppetPart
    {
        int getRotationX();
        
        int getRotationY();
        
        int getRotationZ();
        
        void setRotation(int p0, int p1, int p2);
    }
}
