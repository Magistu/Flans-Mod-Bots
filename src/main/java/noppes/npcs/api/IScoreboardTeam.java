package noppes.npcs.api;

public interface IScoreboardTeam
{
    String getName();
    
    String getDisplayName();
    
    void setDisplayName(String p0);
    
    void addPlayer(String p0);
    
    boolean hasPlayer(String p0);
    
    void removePlayer(String p0);
    
    String[] getPlayers();
    
    void clearPlayers();
    
    boolean getFriendlyFire();
    
    void setFriendlyFire(boolean p0);
    
    void setColor(String p0);
    
    String getColor();
    
    void setSeeInvisibleTeamPlayers(boolean p0);
    
    boolean getSeeInvisibleTeamPlayers();
}
