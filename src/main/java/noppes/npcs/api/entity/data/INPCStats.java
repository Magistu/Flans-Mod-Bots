package noppes.npcs.api.entity.data;

public interface INPCStats
{
    int getMaxHealth();
    
    void setMaxHealth(int p0);
    
    float getResistance(int p0);
    
    void setResistance(int p0, float p1);
    
    int getCombatRegen();
    
    void setCombatRegen(int p0);
    
    int getHealthRegen();
    
    void setHealthRegen(int p0);
    
    INPCMelee getMelee();
    
    INPCRanged getRanged();
    
    boolean getImmune(int p0);
    
    void setImmune(int p0, boolean p1);
    
    void setCreatureType(int p0);
    
    int getCreatureType();
    
    int getRespawnType();
    
    void setRespawnType(int p0);
    
    int getRespawnTime();
    
    void setRespawnTime(int p0);
    
    boolean getHideDeadBody();
    
    void setHideDeadBody(boolean p0);
    
    int getAggroRange();
    
    void setAggroRange(int p0);
    
    // new
    int getLevel();
    
    void setLevel(int lv);

    int getType();
    
    void setType(int t);
    
}
