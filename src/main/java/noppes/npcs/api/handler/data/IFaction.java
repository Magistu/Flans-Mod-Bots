package noppes.npcs.api.handler.data;

import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;

public interface IFaction
{
    int getId();
    
    String getName();
    
    int getDefaultPoints();
    
    void setDefaultPoints(int p0);
    
    int getColor();
    
    int playerStatus(IPlayer p0);
    
    boolean hostileToNpc(ICustomNpc p0);
    
    boolean hostileToFaction(int p0);
    
    int[] getHostileList();
    
    void addHostile(int p0);
    
    void removeHostile(int p0);
    
    boolean hasHostile(int p0);
    
    boolean getIsHidden();
    
    void setIsHidden(boolean p0);
    
    boolean getAttackedByMobs();
    
    void setAttackedByMobs(boolean p0);
    
    void save();
}
