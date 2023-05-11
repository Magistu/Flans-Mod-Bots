package noppes.npcs.api;

public interface IScoreboardObjective
{
    String getName();
    
    String getDisplayName();
    
    void setDisplayName(String p0);
    
    String getCriteria();
    
    boolean isReadyOnly();
    
    IScoreboardScore[] getScores();
    
    IScoreboardScore getScore(String p0);
    
    boolean hasScore(String p0);
    
    IScoreboardScore createScore(String p0);
    
    void removeScore(String p0);
}
