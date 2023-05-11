package noppes.npcs.api.handler.data;

import noppes.npcs.api.IContainer;
import noppes.npcs.api.entity.IPlayer;

public interface IQuest
{
    int getId();
    
    String getName();
    
    void setName(String p0);
    
    int getType();
    
    void setType(int p0);
    
    String getLogText();
    
    void setLogText(String p0);
    
    String getCompleteText();
    
    void setCompleteText(String p0);
    
    IQuest getNextQuest();
    
    void setNextQuest(IQuest p0);
    
    IQuestObjective[] getObjectives(IPlayer p0);
    
    IQuestCategory getCategory();
    
    IContainer getRewards();
    
    String getNpcName();
    
    void setNpcName(String p0);
    
    void save();
    
    boolean getIsRepeatable();
}
