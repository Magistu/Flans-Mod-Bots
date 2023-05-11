package noppes.npcs.api.wrapper;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import noppes.npcs.entity.EntityProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import noppes.npcs.controllers.PixelmonHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.LogWriter;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.entity.IEntity;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class WrapperEntityData implements ICapabilityProvider
{
    @CapabilityInject(WrapperEntityData.class)
    public static Capability<WrapperEntityData> ENTITYDATA_CAPABILITY;
    public IEntity base;
    private static ResourceLocation key;
    
    public WrapperEntityData(IEntity base) {
        this.base = base;
    }
    
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == WrapperEntityData.ENTITYDATA_CAPABILITY;
    }
    
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (this.hasCapability(capability, facing)) {
            return (T)this;
        }
        return null;
    }
    
    public static IEntity get(Entity entity) {
        if (entity == null) {
            return null;
        }
        WrapperEntityData data = (WrapperEntityData)entity.getCapability((Capability)WrapperEntityData.ENTITYDATA_CAPABILITY, (EnumFacing)null);
        if (data == null) {
            LogWriter.warn("Unable to get EntityData for " + entity);
            return getData(entity).base;
        }
        return data.base;
    }
    
    public static void register(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(WrapperEntityData.key, (ICapabilityProvider)getData((Entity)event.getObject()));
    }
    
    private static WrapperEntityData getData(Entity entity) {
        if (entity == null || entity.world == null || entity.world.isRemote) {
            return null;
        }
        if (entity instanceof EntityPlayerMP) {
            return new WrapperEntityData(new PlayerWrapper((EntityPlayerMP)entity));
        }
        if (PixelmonHelper.isPixelmon(entity)) {
            return new WrapperEntityData(new PixelmonWrapper((EntityTameable)entity));
        }
        if (entity instanceof EntityAnimal) {
            return new WrapperEntityData(new AnimalWrapper((EntityAnimal)entity));
        }
        if (entity instanceof EntityMob) {
            return new WrapperEntityData(new MonsterWrapper((EntityMob)entity));
        }
        if (entity instanceof EntityLiving) {
            return new WrapperEntityData(new EntityLivingWrapper((EntityLiving)entity));
        }
        if (entity instanceof EntityLivingBase) {
            return new WrapperEntityData(new EntityLivingBaseWrapper((EntityLivingBase)entity));
        }
        if (entity instanceof EntityVillager) {
            return new WrapperEntityData(new VillagerWrapper((EntityVillager)entity));
        }
        if (entity instanceof EntityItem) {
            return new WrapperEntityData(new EntityItemWrapper((EntityItem)entity));
        }
        if (entity instanceof EntityProjectile) {
            return new WrapperEntityData(new ProjectileWrapper((EntityProjectile)entity));
        }
        if (entity instanceof EntityThrowable) {
            return new WrapperEntityData(new ThrowableWrapper((EntityThrowable)entity));
        }
        if (entity instanceof EntityArrow) {
            return new WrapperEntityData(new ArrowWrapper((EntityArrow)entity));
        }
        return new WrapperEntityData(new EntityWrapper(entity));
    }
    
    static {
        WrapperEntityData.ENTITYDATA_CAPABILITY = null;
        key = new ResourceLocation("customnpcs", "entitydata");
    }
}
