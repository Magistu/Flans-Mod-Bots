package noppes.npcs.api.event;

import net.minecraftforge.event.world.WorldEvent;
import noppes.npcs.api.IWorld;
import net.minecraftforge.event.entity.EntityEvent;
import noppes.npcs.api.entity.IEntity;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ForgeEvent extends CustomNPCsEvent
{
    public Event event;
    
    public ForgeEvent(Event event) {
        this.event = event;
    }
    
    public static class InitEvent extends ForgeEvent
    {
        public InitEvent() {
            super(null);
        }
    }
    
    @Cancelable
    public static class EntityEvent extends ForgeEvent
    {
        public IEntity entity;
        
        public EntityEvent(net.minecraftforge.event.entity.EntityEvent event, IEntity entity) {
            super((Event)event);
            this.entity = entity;
        }
    }
    
    @Cancelable
    public static class WorldEvent extends ForgeEvent
    {
        public IWorld world;
        
        public WorldEvent(net.minecraftforge.event.world.WorldEvent event, IWorld world) {
            super((Event)event);
            this.world = world;
        }
    }
}
