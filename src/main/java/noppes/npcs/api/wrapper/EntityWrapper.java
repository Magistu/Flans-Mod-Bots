package noppes.npcs.api.wrapper;

import noppes.npcs.api.IPos;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.api.INbt;
import java.util.UUID;
import java.util.Collection;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.api.IRayTrace;
import java.util.List;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.item.IItemStack;
import net.minecraft.block.material.Material;
import noppes.npcs.api.CustomNPCsException;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.NpcAPI;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.IWorld;
import java.util.Map;
import noppes.npcs.api.entity.IEntity;
import net.minecraft.entity.Entity;

public class EntityWrapper<T extends Entity> implements IEntity
{
    protected T entity;
    private Map<String, Object> tempData;
    private IWorld worldWrapper;
    private IData tempdata;
    private IData storeddata;
    
    public EntityWrapper(T entity) {
        this.tempData = new HashMap<String, Object>();
        this.tempdata = new IData() {
            @Override
            public void put(String key, Object value) {
                EntityWrapper.this.tempData.put(key, value);
            }
            
            @Override
            public Object get(String key) {
                return EntityWrapper.this.tempData.get(key);
            }
            
            @Override
            public void remove(String key) {
                EntityWrapper.this.tempData.remove(key);
            }
            
            @Override
            public boolean has(String key) {
                return EntityWrapper.this.tempData.containsKey(key);
            }
            
            @Override
            public void clear() {
                EntityWrapper.this.tempData.clear();
            }
            
            @Override
            public String[] getKeys() {
                return (String[])EntityWrapper.this.tempData.keySet().toArray(new String[EntityWrapper.this.tempData.size()]);
            }
        };
        this.storeddata = new IData() {
            @Override
            public void put(String key, Object value) {
                NBTTagCompound compound = this.getStoredCompound();
                if (value instanceof Number) {
                    compound.setDouble(key, ((Number)value).doubleValue());
                }
                else if (value instanceof String) {
                    compound.setString(key, (String)value);
                }
                this.saveStoredCompound(compound);
            }
            
            @Override
            public Object get(String key) {
                NBTTagCompound compound = this.getStoredCompound();
                if (!compound.hasKey(key)) {
                    return null;
                }
                NBTBase base = compound.getTag(key);
                if (base instanceof NBTPrimitive) {
                    return ((NBTPrimitive)base).getDouble();
                }
                return ((NBTTagString)base).getString();
            }
            
            @Override
            public void remove(String key) {
                NBTTagCompound compound = this.getStoredCompound();
                compound.removeTag(key);
                this.saveStoredCompound(compound);
            }
            
            @Override
            public boolean has(String key) {
                return this.getStoredCompound().hasKey(key);
            }
            
            @Override
            public void clear() {
                EntityWrapper.this.entity.getEntityData().removeTag("CNPCStoredData");
            }
            
            private NBTTagCompound getStoredCompound() {
                NBTTagCompound compound = EntityWrapper.this.entity.getEntityData().getCompoundTag("CNPCStoredData");
                if (compound == null) {
                    EntityWrapper.this.entity.getEntityData().setTag("CNPCStoredData", (NBTBase)(compound = new NBTTagCompound()));
                }
                return compound;
            }
            
            private void saveStoredCompound(NBTTagCompound compound) {
                EntityWrapper.this.entity.getEntityData().setTag("CNPCStoredData", (NBTBase)compound);
            }
            
            @Override
            public String[] getKeys() {
                NBTTagCompound compound = this.getStoredCompound();
                return compound.getKeySet().toArray(new String[compound.getKeySet().size()]);
            }
        };
        this.entity = entity;
        this.worldWrapper = NpcAPI.Instance().getIWorld((WorldServer)entity.world);
    }
    
    @Override
    public double getX() {
        return this.entity.posX;
    }
    
    @Override
    public void setX(double x) {
        this.entity.posX = x;
    }
    
    @Override
    public double getY() {
        return this.entity.posY;
    }
    
    @Override
    public void setY(double y) {
        this.entity.posY = y;
    }
    
    @Override
    public double getZ() {
        return this.entity.posZ;
    }
    
    @Override
    public void setZ(double z) {
        this.entity.posZ = z;
    }
    
    @Override
    public int getBlockX() {
        return MathHelper.floor(this.entity.posX);
    }
    
    @Override
    public int getBlockY() {
        return MathHelper.floor(this.entity.posY);
    }
    
    @Override
    public int getBlockZ() {
        return MathHelper.floor(this.entity.posZ);
    }
    
    @Override
    public String getEntityName() {
        String s = EntityList.getEntityString((Entity)this.entity);
        if (s == null) {
            s = "generic";
        }
        return I18n.translateToLocal("entity." + s + ".name");
    }
    
    @Override
    public String getName() {
        return this.entity.getName();
    }
    
    @Override
    public void setName(String name) {
        this.entity.setCustomNameTag(name);
    }
    
    @Override
    public boolean hasCustomName() {
        return this.entity.hasCustomName();
    }
    
    @Override
    public void setPosition(double x, double y, double z) {
        this.entity.setPosition(x, y, z);
    }
    
    @Override
    public IWorld getWorld() {
        if (this.entity.world != this.worldWrapper.getMCWorld()) {
            this.worldWrapper = NpcAPI.Instance().getIWorld((WorldServer)this.entity.world);
        }
        return this.worldWrapper;
    }
    
    @Override
    public boolean isAlive() {
        return this.entity.isEntityAlive();
    }
    
    @Override
    public IData getTempdata() {
        return this.tempdata;
    }
    
    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }
    
    @Override
    public long getAge() {
        return this.entity.ticksExisted;
    }
    
    @Override
    public void damage(float amount) {
        this.entity.attackEntityFrom(DamageSource.GENERIC, amount);
    }
    
    @Override
    public void despawn() {
        this.entity.isDead = true;
    }
    
    @Override
    public void spawn() {
        if (this.worldWrapper.getMCWorld().getEntityFromUuid(this.entity.getUniqueID()) != null) {
            throw new CustomNPCsException("Entity is already spawned", new Object[0]);
        }
        this.entity.isDead = false;
        this.worldWrapper.getMCWorld().spawnEntity((Entity)this.entity);
    }
    
    @Override
    public void kill() {
        this.entity.setDead();
    }
    
    @Override
    public boolean inWater() {
        return this.entity.isInsideOfMaterial(Material.WATER);
    }
    
    @Override
    public boolean inLava() {
        return this.entity.isInsideOfMaterial(Material.LAVA);
    }
    
    @Override
    public boolean inFire() {
        return this.entity.isInsideOfMaterial(Material.FIRE);
    }
    
    @Override
    public boolean isBurning() {
        return this.entity.isBurning();
    }
    
    @Override
    public void setBurning(int ticks) {
        this.entity.setFire(ticks);
    }
    
    @Override
    public void extinguish() {
        this.entity.extinguish();
    }
    
    @Override
    public String getTypeName() {
        return EntityList.getEntityString((Entity)this.entity);
    }
    
    @Override
    public IEntityItem dropItem(IItemStack item) {
        return (IEntityItem)NpcAPI.Instance().getIEntity((Entity)this.entity.entityDropItem(item.getMCItemStack(), 0.0f));
    }
    
    @Override
    public IEntity[] getRiders() {
        List<Entity> list = (List<Entity>)this.entity.getPassengers();
        IEntity[] riders = new IEntity[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            riders[i] = NpcAPI.Instance().getIEntity(list.get(i));
        }
        return riders;
    }
    
    @Override
    public IRayTrace rayTraceBlock(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
        Vec3d vec3d = this.entity.getPositionEyes(1.0f);
        Vec3d vec3d2 = this.entity.getLook(1.0f);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
        RayTraceResult result = this.entity.world.rayTraceBlocks(vec3d, vec3d3, stopOnLiquid, ignoreBlockWithoutBoundingBox, true);
        if (result == null) {
            return null;
        }
        return new RayTraceWrapper(NpcAPI.Instance().getIBlock(this.entity.world, result.getBlockPos()), result.sideHit.getIndex());
    }
    
    @Override
    public IEntity[] rayTraceEntities(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
        Vec3d vec3d = this.entity.getPositionEyes(1.0f);
        Vec3d vec3d2 = this.entity.getLook(1.0f);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
        RayTraceResult result = this.entity.world.rayTraceBlocks(vec3d, vec3d3, stopOnLiquid, ignoreBlockWithoutBoundingBox, false);
        if (result != null) {
            vec3d3 = new Vec3d(result.hitVec.x, result.hitVec.y, result.hitVec.z);
        }
        return this.findEntityOnPath(distance, vec3d, vec3d3);
    }
    
    private IEntity[] findEntityOnPath(double distance, Vec3d vec3d, Vec3d vec3d1) {
        List<Entity> list = (List<Entity>)this.entity.world.getEntitiesWithinAABBExcludingEntity((Entity)this.entity, this.entity.getEntityBoundingBox().grow(distance));
        List<IEntity> result = new ArrayList<IEntity>();
        for (Entity entity1 : list) {
            if (entity1.canBeCollidedWith() && entity1 != this.entity) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
                RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
                if (raytraceresult1 == null) {
                    continue;
                }
                result.add(NpcAPI.Instance().getIEntity(entity1));
            }
        }
        result.sort((o1, o2) -> {
        	double d1 = this.entity.getDistance(o1.getMCEntity());
        	double d2 = this.entity.getDistance(o2.getMCEntity());
            if (d1 == d2) {
                return 0;
            }
            else {
                return (d1 > d2) ? 1 : -1;
            }
        });
        return result.toArray(new IEntity[result.size()]);
    }
    
    @Override
    public IEntity[] getAllRiders() {
        List<Entity> list = new ArrayList<Entity>(this.entity.getRecursivePassengers());
        IEntity[] riders = new IEntity[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            riders[i] = NpcAPI.Instance().getIEntity(list.get(i));
        }
        return riders;
    }
    
    @Override
    public void addRider(IEntity entity) {
        if (entity != null) {
            entity.getMCEntity().startRiding((Entity)this.entity, true);
        }
    }
    
    @Override
    public void clearRiders() {
        this.entity.removePassengers();
    }
    
    @Override
    public IEntity getMount() {
        return NpcAPI.Instance().getIEntity(this.entity.getRidingEntity());
    }
    
    @Override
    public void setMount(IEntity entity) {
        if (entity == null) {
            this.entity.dismountRidingEntity();
        }
        else {
            this.entity.startRiding(entity.getMCEntity(), true);
        }
    }
    
    @Override
    public void setRotation(float rotation) {
        this.entity.rotationYaw = rotation;
    }
    
    @Override
    public float getRotation() {
        return this.entity.rotationYaw;
    }
    
    @Override
    public void setPitch(float rotation) {
        this.entity.rotationPitch = rotation;
    }
    
    @Override
    public float getPitch() {
        return this.entity.rotationPitch;
    }
    
    @Override
    public void knockback(int power, float direction) {
        float v = direction * 3.1415927f / 180.0f;
        this.entity.addVelocity((double)(-MathHelper.sin(v) * power), 0.1 + power * 0.04f, (double)(MathHelper.cos(v) * power));
        Entity entity = this.entity;
        entity.motionX *= 0.6;
        Entity entity2 = this.entity;
        entity2.motionZ *= 0.6;
        this.entity.velocityChanged = true;
    }
    
    @Override
    public boolean isSneaking() {
        return this.entity.isSneaking();
    }
    
    @Override
    public boolean isSprinting() {
        return this.entity.isSprinting();
    }
    
    @Override
    public T getMCEntity() {
        return this.entity;
    }
    
    @Override
    public int getType() {
        return 0;
    }
    
    @Override
    public boolean typeOf(int type) {
        return type == this.getType();
    }
    
    @Override
    public String getUUID() {
        return this.entity.getUniqueID().toString();
    }
    
    @Override
    public String generateNewUUID() {
        UUID id = UUID.randomUUID();
        this.entity.setUniqueId(id);
        return id.toString();
    }
    
    @Override
    public INbt getNbt() {
        return NpcAPI.Instance().getINbt(this.entity.getEntityData());
    }
    
    @Override
    public void storeAsClone(int tab, String name) {
        NBTTagCompound compound = new NBTTagCompound();
        if (!this.entity.writeToNBTAtomically(compound)) {
            throw new CustomNPCsException("Cannot store dead entities", new Object[0]);
        }
        ServerCloneController.Instance.addClone(compound, name, tab);
    }
    
    @Override
    public INbt getEntityNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        this.entity.writeToNBT(compound);
        ResourceLocation resourcelocation = EntityList.getKey((Entity)this.entity);
        if (this.getType() == 1) {
            resourcelocation = new ResourceLocation("player");
        }
        if (resourcelocation != null) {
            compound.setString("id", resourcelocation.toString());
        }
        return NpcAPI.Instance().getINbt(compound);
    }
    
    @Override
    public void setEntityNbt(INbt nbt) {
        this.entity.readFromNBT(nbt.getMCNBT());
    }
    
    @Override
    public void playAnimation(int type) {
        this.worldWrapper.getMCWorld().getEntityTracker().sendToTrackingAndSelf((Entity)this.entity, (Packet)new SPacketAnimation((Entity)this.entity, type));
    }
    
    @Override
    public float getHeight() {
        return this.entity.height;
    }
    
    @Override
    public float getEyeHeight() {
        return this.entity.getEyeHeight();
    }
    
    @Override
    public float getWidth() {
        return this.entity.width;
    }
    
    @Override
    public IPos getPos() {
        return new BlockPosWrapper(this.entity.getPosition());
    }
    
    @Override
    public void setPos(IPos pos) {
        this.entity.setPosition((double)(pos.getX() + 0.5f), (double)pos.getY(), (double)(pos.getZ() + 0.5f));
    }
    
    @Override
    public String[] getTags() {
        return this.entity.getTags().toArray(new String[this.entity.getTags().size()]);
    }
    
    @Override
    public void addTag(String tag) {
        this.entity.addTag(tag);
    }
    
    @Override
    public boolean hasTag(String tag) {
        return this.entity.getTags().contains(tag);
    }
    
    @Override
    public void removeTag(String tag) {
        this.entity.removeTag(tag);
    }
    
    @Override
    public double getMotionX() {
        return this.entity.motionX;
    }
    
    @Override
    public double getMotionY() {
        return this.entity.motionY;
    }
    
    @Override
    public double getMotionZ() {
        return this.entity.motionZ;
    }
    
    @Override
    public void setMotionX(double motion) {
        if (this.entity.motionX == motion) {
            return;
        }
        this.entity.motionX = motion;
        this.entity.velocityChanged = true;
    }
    
    @Override
    public void setMotionY(double motion) {
        if (this.entity.motionY == motion) {
            return;
        }
        this.entity.motionY = motion;
        this.entity.velocityChanged = true;
    }
    
    @Override
    public void setMotionZ(double motion) {
        if (this.entity.motionZ == motion) {
            return;
        }
        this.entity.motionZ = motion;
        this.entity.velocityChanged = true;
    }
}
