package noppes.npcs.api.handler.data;

public interface IQuestObjective
{
    int getProgress();
    
    void setProgress(int p0);
    
    int getMaxProgress();
    
    boolean isCompleted();
    
    String getText();
}
