package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.INPCRole;

public interface IRoleFollower extends INPCRole
{
    int getDays();
    
    void addDays(int p0);
    
    boolean getInfinite();
    
    void setInfinite(boolean p0);
    
    boolean getGuiDisabled();
    
    void setGuiDisabled(boolean p0);
    
    IPlayer getFollowing();
    
    void setFollowing(IPlayer p0);
    
    boolean isFollowing();
    
    void reset();
    
    void setRefuseSoulstone(boolean p0);
    
    boolean getRefuseSoulstone();
}
