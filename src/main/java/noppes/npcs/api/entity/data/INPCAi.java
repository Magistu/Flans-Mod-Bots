package noppes.npcs.api.entity.data;

public interface INPCAi
{
    int getAnimation();
    
    void setAnimation(int p0);
    
    int getCurrentAnimation();
    
    void setReturnsHome(boolean p0);
    
    boolean getReturnsHome();
    
    int getRetaliateType();
    
    void setRetaliateType(int p0);
    
    int getMovingType();
    
    void setMovingType(int p0);
    
    int getNavigationType();
    
    void setNavigationType(int p0);
    
    int getStandingType();
    
    void setStandingType(int p0);
    
    boolean getAttackInvisible();
    
    void setAttackInvisible(boolean p0);
    
    int getWanderingRange();
    
    void setWanderingRange(int p0);
    
    boolean getInteractWithNPCs();
    
    void setInteractWithNPCs(boolean p0);
    
    boolean getStopOnInteract();
    
    void setStopOnInteract(boolean p0);
    
    int getWalkingSpeed();
    
    void setWalkingSpeed(int p0);
    
    int getMovingPathType();
    
    boolean getMovingPathPauses();
    
    void setMovingPathType(int p0, boolean p1);
    
    int getDoorInteract();
    
    void setDoorInteract(int p0);
    
    boolean getCanSwim();
    
    void setCanSwim(boolean p0);
    
    int getSheltersFrom();
    
    void setSheltersFrom(int p0);
    
    boolean getAttackLOS();
    
    void setAttackLOS(boolean p0);
    
    boolean getAvoidsWater();
    
    void setAvoidsWater(boolean p0);
    
    boolean getLeapAtTarget();
    
    void setLeapAtTarget(boolean p0);
    
    int getTacticalType();
    
    void setTacticalType(int p0);
    
    int getTacticalRange();
    
    void setTacticalRange(int p0);
}
