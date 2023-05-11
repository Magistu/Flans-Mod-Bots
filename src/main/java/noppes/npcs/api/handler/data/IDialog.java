package noppes.npcs.api.handler.data;

import java.util.List;

public interface IDialog
{
    int getId();
    
    String getName();
    
    void setName(String p0);
    
    String getText();
    
    void setText(String p0);
    
    IQuest getQuest();
    
    void setQuest(IQuest p0);
    
    String getCommand();
    
    void setCommand(String p0);
    
    List<IDialogOption> getOptions();
    
    IDialogOption getOption(int p0);
    
    IAvailability getAvailability();
    
    IDialogCategory getCategory();
    
    void save();
}
