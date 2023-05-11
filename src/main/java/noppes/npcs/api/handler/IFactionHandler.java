package noppes.npcs.api.handler;

import noppes.npcs.api.handler.data.IFaction;
import java.util.List;

public interface IFactionHandler
{
    List<IFaction> list();
    
    IFaction delete(int p0);
    
    IFaction create(String p0, int p1);
    
    IFaction get(int p0);
}
