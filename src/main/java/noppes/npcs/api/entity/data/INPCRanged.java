package noppes.npcs.api.entity.data;

public interface INPCRanged
{
    int getStrength();
    
    void setStrength(int p0);
    
    int getSpeed();
    
    void setSpeed(int p0);
    
    int getBurst();
    
    void setBurst(int p0);
    
    int getBurstDelay();
    
    void setBurstDelay(int p0);
    
    int getKnockback();
    
    void setKnockback(int p0);
    
    int getSize();
    
    void setSize(int p0);
    
    boolean getRender3D();
    
    void setRender3D(boolean p0);
    
    boolean getSpins();
    
    void setSpins(boolean p0);
    
    boolean getSticks();
    
    void setSticks(boolean p0);
    
    boolean getHasGravity();
    
    void setHasGravity(boolean p0);
    
    boolean getAccelerate();
    
    void setAccelerate(boolean p0);
    
    int getExplodeSize();
    
    void setExplodeSize(int p0);
    
    int getEffectType();
    
    int getEffectTime();
    
    int getEffectStrength();
    
    void setEffect(int p0, int p1, int p2);
    
    boolean getGlows();
    
    void setGlows(boolean p0);
    
    int getParticle();
    
    void setParticle(int p0);
    
    String getSound(int p0);
    
    void setSound(int p0, String p1);
    
    int getShotCount();
    
    void setShotCount(int p0);
    
    boolean getHasAimAnimation();
    
    void setHasAimAnimation(boolean p0);
    
    int getAccuracy();
    
    void setAccuracy(int p0);
    
    int getRange();
    
    void setRange(int p0);
    
    int getDelayMin();
    
    int getDelayMax();
    
    int getDelayRNG();
    
    void setDelay(int p0, int p1);
    
    int getFireType();
    
    void setFireType(int p0);
    
    int getMeleeRange();
    
    void setMeleeRange(int p0);
}
