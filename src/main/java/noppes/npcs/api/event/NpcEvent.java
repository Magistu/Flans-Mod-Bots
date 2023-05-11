package noppes.npcs.api.event;

import java.util.ArrayList;
import noppes.npcs.api.entity.IProjectile;
import java.util.List;
import net.minecraft.util.DamageSource;
import noppes.npcs.api.entity.data.ILine;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.IDamageSource;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;
import net.minecraft.entity.Entity;
import noppes.npcs.api.NpcAPI;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.entity.IEntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.ICustomNpc;

public class NpcEvent extends CustomNPCsEvent
{
    public ICustomNpc npc;
    
    public NpcEvent(ICustomNpc npc) {
        this.npc = npc;
    }
    
    public static class InitEvent extends NpcEvent
    {
        public InitEvent(ICustomNpc npc) {
            super(npc);
        }
    }
    
    public static class UpdateEvent extends NpcEvent
    {
        public UpdateEvent(ICustomNpc npc) {
            super(npc);
        }
    }
    
    @Cancelable
    public static class TargetEvent extends NpcEvent
    {
        public IEntityLivingBase entity;
        
        public TargetEvent(ICustomNpc npc, EntityLivingBase entity) {
            super(npc);
            this.entity = (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)entity);
        }
    }
    
    @Cancelable
    public static class TargetLostEvent extends NpcEvent
    {
        public IEntityLivingBase entity;
        
        public TargetLostEvent(ICustomNpc npc, EntityLivingBase entity) {
            super(npc);
            this.entity = (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)entity);
        }
    }
    
    @Cancelable
    public static class InteractEvent extends NpcEvent
    {
        public IPlayer player;
        
        public InteractEvent(ICustomNpc npc, EntityPlayer player) {
            super(npc);
            this.player = (IPlayer)NpcAPI.Instance().getIEntity((Entity)player);
        }
    }
    
    public static class DiedEvent extends NpcEvent
    {
        public IDamageSource damageSource;
        public String type;
        public IEntity source;
        public IItemStack[] droppedItems;
        public int expDropped;
        public ILine line;
        
        public DiedEvent(ICustomNpc npc, DamageSource damagesource, Entity entity) {
            super(npc);
            this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
            this.type = damagesource.damageType;
            this.source = NpcAPI.Instance().getIEntity(entity);
        }
    }
    
    public static class KilledEntityEvent extends NpcEvent
    {
        public IEntityLivingBase entity;
        
        public KilledEntityEvent(ICustomNpc npc, EntityLivingBase entity) {
            super(npc);
            this.entity = (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)entity);
        }
    }
    
    @Cancelable
    public static class MeleeAttackEvent extends NpcEvent
    {
        public IEntityLivingBase target;
        public float damage;
        
        public MeleeAttackEvent(ICustomNpc npc, EntityLivingBase target, float damage) {
            super(npc);
            this.target = (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)target);
            this.damage = damage;
        }
    }
    
    public static class RangedLaunchedEvent extends NpcEvent
    {
        public IEntityLivingBase target;
        public float damage;
        public List<IProjectile> projectiles;
        
        public RangedLaunchedEvent(ICustomNpc npc, EntityLivingBase target, float damage) {
            super(npc);
            this.projectiles = new ArrayList<IProjectile>();
            this.target = (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)target);
            this.damage = damage;
        }
    }
    
    @Cancelable
    public static class DamagedEvent extends NpcEvent
    {
        public IDamageSource damageSource;
        public IEntity source;
        public float damage;
        public boolean clearTarget;
        
        public DamagedEvent(ICustomNpc npc, Entity source, float damage, DamageSource damagesource) {
            super(npc);
            this.clearTarget = false;
            this.source = NpcAPI.Instance().getIEntity(source);
            this.damage = damage;
            this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
        }
    }
    
    public static class CollideEvent extends NpcEvent
    {
        public IEntity entity;
        
        public CollideEvent(ICustomNpc npc, Entity entity) {
            super(npc);
            this.entity = NpcAPI.Instance().getIEntity(entity);
        }
    }
    
    public static class TimerEvent extends NpcEvent
    {
        public int id;
        
        public TimerEvent(ICustomNpc npc, int id) {
            super(npc);
            this.id = id;
        }
    }
}
