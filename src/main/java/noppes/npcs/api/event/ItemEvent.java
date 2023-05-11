package noppes.npcs.api.event;

import noppes.npcs.api.entity.IEntityItem;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemScripted;

public class ItemEvent extends CustomNPCsEvent
{
    public IItemScripted item;
    
    public ItemEvent(IItemScripted item) {
        this.item = item;
    }
    
    public static class InitEvent extends ItemEvent
    {
        public InitEvent(IItemScripted item) {
            super(item);
        }
    }
    
    public static class UpdateEvent extends ItemEvent
    {
        public IPlayer player;
        
        public UpdateEvent(IItemScripted item, IPlayer player) {
            super(item);
            this.player = player;
        }
    }
    
    @Cancelable
    public static class SpawnEvent extends ItemEvent
    {
        public IEntityItem entity;
        
        public SpawnEvent(IItemScripted item, IEntityItem entity) {
            super(item);
            this.entity = entity;
        }
    }
    
    @Cancelable
    public static class TossedEvent extends ItemEvent
    {
        public IEntityItem entity;
        public IPlayer player;
        
        public TossedEvent(IItemScripted item, IPlayer player, IEntityItem entity) {
            super(item);
            this.entity = entity;
            this.player = player;
        }
    }
    
    public static class PickedUpEvent extends ItemEvent
    {
        public IEntityItem entity;
        public IPlayer player;
        
        public PickedUpEvent(IItemScripted item, IPlayer player, IEntityItem entity) {
            super(item);
            this.entity = entity;
            this.player = player;
        }
    }
    
    @Cancelable
    public static class InteractEvent extends ItemEvent
    {
        public int type;
        public Object target;
        public IPlayer player;
        
        public InteractEvent(IItemScripted item, IPlayer player, int type, Object target) {
            super(item);
            this.type = type;
            this.target = target;
            this.player = player;
        }
    }
    
    @Cancelable
    public static class AttackEvent extends ItemEvent
    {
        public int type;
        public Object target;
        public IPlayer player;
        
        public AttackEvent(IItemScripted item, IPlayer player, int type, Object target) {
            super(item);
            this.type = type;
            this.target = target;
            this.player = player;
        }
    }
}
