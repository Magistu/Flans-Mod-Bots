package noppes.npcs.api.event;

import noppes.npcs.api.handler.data.IFaction;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.NpcAPI;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.block.IBlock;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;

public class PlayerEvent extends CustomNPCsEvent
{
    public IPlayer player;
    
    public PlayerEvent(IPlayer player) {
        this.player = player;
    }
    
    public static class InitEvent extends PlayerEvent
    {
        public InitEvent(IPlayer player) {
            super(player);
        }
    }
    
    public static class UpdateEvent extends PlayerEvent
    {
        public UpdateEvent(IPlayer player) {
            super(player);
        }
    }
    
    @Cancelable
    public static class InteractEvent extends PlayerEvent
    {
        public int type;
        public Object target;
        
        public InteractEvent(IPlayer player, int type, Object target) {
            super(player);
            this.type = type;
            this.target = target;
        }
    }
    
    @Cancelable
    public static class AttackEvent extends PlayerEvent
    {
        public int type;
        public Object target;
        
        public AttackEvent(IPlayer player, int type, Object target) {
            super(player);
            this.type = type;
            this.target = target;
        }
    }
    
    @Cancelable
    public static class BreakEvent extends PlayerEvent
    {
        public IBlock block;
        public int exp;
        
        public BreakEvent(IPlayer player, IBlock block, int exp) {
            super(player);
            this.block = block;
            this.exp = exp;
        }
    }
    
    @Cancelable
    public static class TossEvent extends PlayerEvent
    {
        public IItemStack item;
        
        public TossEvent(IPlayer player, IItemStack item) {
            super(player);
            this.item = item;
        }
    }
    
    @Cancelable
    public static class PickUpEvent extends PlayerEvent
    {
        public IItemStack item;
        
        public PickUpEvent(IPlayer player, IItemStack item) {
            super(player);
            this.item = item;
        }
    }
    
    public static class ContainerOpen extends PlayerEvent
    {
        public IContainer container;
        
        public ContainerOpen(IPlayer player, IContainer container) {
            super(player);
            this.container = container;
        }
    }
    
    public static class ContainerClosed extends PlayerEvent
    {
        public IContainer container;
        
        public ContainerClosed(IPlayer player, IContainer container) {
            super(player);
            this.container = container;
        }
    }
    
    @Cancelable
    public static class DamagedEntityEvent extends PlayerEvent
    {
        public IDamageSource damageSource;
        public IEntity target;
        public float damage;
        
        public DamagedEntityEvent(IPlayer player, Entity target, float damage, DamageSource damagesource) {
            super(player);
            this.target = NpcAPI.Instance().getIEntity(target);
            this.damage = damage;
            this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
        }
    }
    
    @Cancelable
    public static class RangedLaunchedEvent extends PlayerEvent
    {
        public RangedLaunchedEvent(IPlayer player) {
            super(player);
        }
    }
    
    @Cancelable
    public static class DiedEvent extends PlayerEvent
    {
        public IDamageSource damageSource;
        public String type;
        public IEntity source;
        
        public DiedEvent(IPlayer player, DamageSource damagesource, Entity entity) {
            super(player);
            this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
            this.type = damagesource.damageType;
            this.source = NpcAPI.Instance().getIEntity(entity);
        }
    }
    
    public static class KilledEntityEvent extends PlayerEvent
    {
        public IEntityLivingBase entity;
        
        public KilledEntityEvent(IPlayer player, EntityLivingBase entity) {
            super(player);
            this.entity = (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)entity);
        }
    }
    
    @Cancelable
    public static class DamagedEvent extends PlayerEvent
    {
        public IDamageSource damageSource;
        public IEntity source;
        public float damage;
        public boolean clearTarget;
        
        public DamagedEvent(IPlayer player, Entity source, float damage, DamageSource damagesource) {
            super(player);
            this.clearTarget = false;
            this.source = NpcAPI.Instance().getIEntity(source);
            this.damage = damage;
            this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
        }
    }
    
    public static class TimerEvent extends PlayerEvent
    {
        public int id;
        
        public TimerEvent(IPlayer player, int id) {
            super(player);
            this.id = id;
        }
    }
    
    public static class LoginEvent extends PlayerEvent
    {
        public LoginEvent(IPlayer player) {
            super(player);
        }
    }
    
    public static class LogoutEvent extends PlayerEvent
    {
        public LogoutEvent(IPlayer player) {
            super(player);
        }
    }
    
    public static class LevelUpEvent extends PlayerEvent
    {
        public int change;
        
        public LevelUpEvent(IPlayer player, int change) {
            super(player);
            this.change = change;
        }
    }
    
    public static class KeyPressedEvent extends PlayerEvent
    {
        public int key;
        public boolean isCtrlPressed;
        public boolean isAltPressed;
        public boolean isShiftPressed;
        public boolean isMetaPressed;
        
        public KeyPressedEvent(IPlayer player, int key, boolean isCtrlPressed, boolean isAltPressed, boolean isShiftPressed, boolean isMetaPressed) {
            super(player);
            this.key = key;
            this.isCtrlPressed = isCtrlPressed;
            this.isAltPressed = isAltPressed;
            this.isShiftPressed = isShiftPressed;
            this.isMetaPressed = isMetaPressed;
        }
    }
    
    @Cancelable
    public static class ChatEvent extends PlayerEvent
    {
        public String message;
        
        public ChatEvent(IPlayer player, String message) {
            super(player);
            this.message = message;
        }
    }
    
    public static class FactionUpdateEvent extends PlayerEvent
    {
        public IFaction faction;
        public int points;
        public boolean init;
        
        public FactionUpdateEvent(IPlayer player, IFaction faction, int points, boolean init) {
            super(player);
            this.faction = faction;
            this.points = points;
            this.init = init;
        }
    }
}
