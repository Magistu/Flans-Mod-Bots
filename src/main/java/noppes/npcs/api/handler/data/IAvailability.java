package noppes.npcs.api.handler.data;

import noppes.npcs.api.entity.IPlayer;

public interface IAvailability
{
    boolean isAvailable(IPlayer p0);
    
    int getDaytime();
    
    void setDaytime(int p0);
    
    int getMinPlayerLevel();
    
    void setMinPlayerLevel(int p0);
    
    int getDialog(int p0);
    
    void setDialog(int p0, int p1, int p2);
    
    void removeDialog(int p0);
    
    int getQuest(int p0);
    
    void setQuest(int p0, int p1, int p2);
    
    void removeQuest(int p0);
    
    void setFaction(int p0, int p1, int p2, int p3);
    
    void removeFaction(int p0);
    
    void setScoreboard(int p0, String p1, int p2, int p3);
}
