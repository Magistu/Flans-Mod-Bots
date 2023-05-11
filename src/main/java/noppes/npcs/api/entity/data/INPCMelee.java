package noppes.npcs.api.entity.data;

public interface INPCMelee
{
    int getStrength();
    
    void setStrength(int p0);
    
    int getDelay();
    
    void setDelay(int p0);
    
    int getRange();
    
    void setRange(int p0);
    
    int getKnockback();
    
    void setKnockback(int p0);
    
    int getEffectType();
    
    int getEffectTime();
    
    int getEffectStrength();
    
    void setEffect(int p0, int p1, int p2);
}
