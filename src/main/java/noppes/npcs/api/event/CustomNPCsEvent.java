package noppes.npcs.api.event;

import noppes.npcs.api.NpcAPI;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CustomNPCsEvent extends Event
{
    public NpcAPI API;
    
    public CustomNPCsEvent() {
        this.API = NpcAPI.Instance();
    }
}
