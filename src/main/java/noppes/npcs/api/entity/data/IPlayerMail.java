package noppes.npcs.api.entity.data;

import noppes.npcs.api.IContainer;
import noppes.npcs.api.handler.data.IQuest;

public interface IPlayerMail
{
    String getSender();
    
    void setSender(String p0);
    
    String getSubject();
    
    void setSubject(String p0);
    
    String[] getText();
    
    void setText(String[] p0);
    
    IQuest getQuest();
    
    void setQuest(int p0);
    
    IContainer getContainer();
}
