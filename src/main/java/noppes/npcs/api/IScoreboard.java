package noppes.npcs.api;

public interface IScoreboard
{
    IScoreboardObjective[] getObjectives();
    
    IScoreboardObjective getObjective(String p0);
    
    boolean hasObjective(String p0);
    
    void removeObjective(String p0);
    
    IScoreboardObjective addObjective(String p0, String p1);
    
    void setPlayerScore(String p0, String p1, int p2, String p3);
    
    int getPlayerScore(String p0, String p1, String p2);
    
    boolean hasPlayerObjective(String p0, String p1, String p2);
    
    void deletePlayerScore(String p0, String p1, String p2);
    
    IScoreboardTeam[] getTeams();
    
    boolean hasTeam(String p0);
    
    IScoreboardTeam addTeam(String p0);
    
    IScoreboardTeam getTeam(String p0);
    
    void removeTeam(String p0);
    
    IScoreboardTeam getPlayerTeam(String p0);
    
    void removePlayerTeam(String p0);
    
    String[] getPlayerList();
}
