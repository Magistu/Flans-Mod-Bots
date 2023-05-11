package noppes.npcs.roles;

import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.HashMap;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.data.role.IJobSpawner;

public class JobSpawner extends JobInterface implements IJobSpawner
{
    public NBTTagCompound compound6;
    public NBTTagCompound compound5;
    public NBTTagCompound compound4;
    public NBTTagCompound compound3;
    public NBTTagCompound compound2;
    public NBTTagCompound compound1;
    public String title1;
    public String title2;
    public String title3;
    public String title4;
    public String title5;
    public String title6;
    private int number;
    public List<EntityLivingBase> spawned;
    private Map<String, Long> cooldown;
    private String id;
    public boolean doesntDie;
    public int spawnType;
    public int xOffset;
    public int yOffset;
    public int zOffset;
    private EntityLivingBase target;
    public boolean despawnOnTargetLost;
    
    public JobSpawner(EntityNPCInterface npc) {
        super(npc);
        this.number = 0;
        this.spawned = new ArrayList<EntityLivingBase>();
        this.cooldown = new HashMap<String, Long>();
        this.id = RandomStringUtils.random(8, true, true);
        this.doesntDie = false;
        this.spawnType = 0;
        this.xOffset = 0;
        this.yOffset = 0;
        this.zOffset = 0;
        this.despawnOnTargetLost = true;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        this.saveCompound(this.compound1, "SpawnerNBT1", compound);
        this.saveCompound(this.compound2, "SpawnerNBT2", compound);
        this.saveCompound(this.compound3, "SpawnerNBT3", compound);
        this.saveCompound(this.compound4, "SpawnerNBT4", compound);
        this.saveCompound(this.compound5, "SpawnerNBT5", compound);
        this.saveCompound(this.compound6, "SpawnerNBT6", compound);
        compound.setString("SpawnerId", this.id);
        compound.setBoolean("SpawnerDoesntDie", this.doesntDie);
        compound.setInteger("SpawnerType", this.spawnType);
        compound.setInteger("SpawnerXOffset", this.xOffset);
        compound.setInteger("SpawnerYOffset", this.yOffset);
        compound.setInteger("SpawnerZOffset", this.zOffset);
        compound.setBoolean("DespawnOnTargetLost", this.despawnOnTargetLost);
        return compound;
    }
    
    public NBTTagCompound getTitles() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Title1", this.getTitle(this.compound1));
        compound.setString("Title2", this.getTitle(this.compound2));
        compound.setString("Title3", this.getTitle(this.compound3));
        compound.setString("Title4", this.getTitle(this.compound4));
        compound.setString("Title5", this.getTitle(this.compound5));
        compound.setString("Title6", this.getTitle(this.compound6));
        return compound;
    }
    
    private String getTitle(NBTTagCompound compound) {
        if (compound != null && compound.hasKey("ClonedName")) {
            return compound.getString("ClonedName");
        }
        return "gui.selectnpc";
    }
    
    private void saveCompound(NBTTagCompound save, String name, NBTTagCompound compound) {
        if (save != null) {
            compound.setTag(name, (NBTBase)save);
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.compound1 = compound.getCompoundTag("SpawnerNBT1");
        this.compound2 = compound.getCompoundTag("SpawnerNBT2");
        this.compound3 = compound.getCompoundTag("SpawnerNBT3");
        this.compound4 = compound.getCompoundTag("SpawnerNBT4");
        this.compound5 = compound.getCompoundTag("SpawnerNBT5");
        this.compound6 = compound.getCompoundTag("SpawnerNBT6");
        this.id = compound.getString("SpawnerId");
        this.doesntDie = compound.getBoolean("SpawnerDoesntDie");
        this.spawnType = compound.getInteger("SpawnerType");
        this.xOffset = compound.getInteger("SpawnerXOffset");
        this.yOffset = compound.getInteger("SpawnerYOffset");
        this.zOffset = compound.getInteger("SpawnerZOffset");
        this.despawnOnTargetLost = compound.getBoolean("DespawnOnTargetLost");
    }
    
    public void cleanCompound(NBTTagCompound compound) {
        compound.removeTag("SpawnerNBT1");
        compound.removeTag("SpawnerNBT2");
        compound.removeTag("SpawnerNBT3");
        compound.removeTag("SpawnerNBT4");
        compound.removeTag("SpawnerNBT5");
        compound.removeTag("SpawnerNBT6");
    }
    
    public void setJobCompound(int i, NBTTagCompound compound) {
        if (i == 1) {
            this.compound1 = compound;
        }
        if (i == 2) {
            this.compound2 = compound;
        }
        if (i == 3) {
            this.compound3 = compound;
        }
        if (i == 4) {
            this.compound4 = compound;
        }
        if (i == 5) {
            this.compound5 = compound;
        }
        if (i == 6) {
            this.compound6 = compound;
        }
    }
    
    @Override
    public void aiUpdateTask() {
        if (this.spawned.isEmpty()) {
            if (this.spawnType == 0 && this.spawnEntity(this.number + 1) == null && !this.doesntDie) {
                this.npc.setDead();
            }
            if (this.spawnType == 1) {
                if (this.number >= 6 && !this.doesntDie) {
                    this.npc.setDead();
                }
                else {
                    this.spawnEntity(this.compound1);
                    this.spawnEntity(this.compound2);
                    this.spawnEntity(this.compound3);
                    this.spawnEntity(this.compound4);
                    this.spawnEntity(this.compound5);
                    this.spawnEntity(this.compound6);
                    this.number = 6;
                }
            }
            if (this.spawnType == 2) {
                ArrayList<NBTTagCompound> list = new ArrayList<NBTTagCompound>();
                if (this.compound1 != null && this.compound1.hasKey("id")) {
                    list.add(this.compound1);
                }
                if (this.compound2 != null && this.compound2.hasKey("id")) {
                    list.add(this.compound2);
                }
                if (this.compound3 != null && this.compound3.hasKey("id")) {
                    list.add(this.compound3);
                }
                if (this.compound4 != null && this.compound4.hasKey("id")) {
                    list.add(this.compound4);
                }
                if (this.compound5 != null && this.compound5.hasKey("id")) {
                    list.add(this.compound5);
                }
                if (this.compound6 != null && this.compound6.hasKey("id")) {
                    list.add(this.compound6);
                }
                if (!list.isEmpty()) {
                    NBTTagCompound compound = list.get(this.npc.getRNG().nextInt(list.size()));
                    this.spawnEntity(compound);
                }
                else if (!this.doesntDie) {
                    this.npc.setDead();
                }
            }
        }
        else {
            this.checkSpawns();
        }
    }
    
    public void checkSpawns() {
        Iterator<EntityLivingBase> iterator = this.spawned.iterator();
        while (iterator.hasNext()) {
            EntityLivingBase spawn = iterator.next();
            if (this.shouldDelete(spawn)) {
                spawn.isDead = true;
                iterator.remove();
            }
            else {
                this.checkTarget(spawn);
            }
        }
    }
    
    public void checkTarget(EntityLivingBase entity) {
        if (entity instanceof EntityLiving) {
            EntityLiving liv = (EntityLiving)entity;
            if (liv.getAttackTarget() == null || this.npc.getRNG().nextInt(100) == 1) {
                liv.setAttackTarget(this.target);
            }
        }
        else if (entity.getRevengeTarget() == null || this.npc.getRNG().nextInt(100) == 1) {
            entity.setRevengeTarget(this.target);
        }
    }
    
    public boolean shouldDelete(EntityLivingBase entity) {
        return !this.npc.isInRange((Entity)entity, 60.0) || entity.isDead || entity.getHealth() <= 0.0f || (this.despawnOnTargetLost && this.target == null);
    }
    
    private EntityLivingBase getTarget() {
        EntityLivingBase target = this.getTarget((EntityLivingBase)this.npc);
        if (target != null) {
            return target;
        }
        for (EntityLivingBase entity : this.spawned) {
            target = this.getTarget(entity);
            if (target != null) {
                return target;
            }
        }
        return null;
    }
    
    private EntityLivingBase getTarget(EntityLivingBase entity) {
        if (entity instanceof EntityLiving) {
            this.target = ((EntityLiving)entity).getAttackTarget();
            if (this.target != null && !this.target.isDead && this.target.getHealth() > 0.0f) {
                return this.target;
            }
        }
        this.target = entity.getRevengeTarget();
        if (this.target != null && !this.target.isDead && this.target.getHealth() > 0.0f) {
            return this.target;
        }
        return null;
    }
    
    private boolean isEmpty() {
        return (this.compound1 == null || !this.compound1.hasKey("id")) && (this.compound2 == null || !this.compound2.hasKey("id")) && (this.compound3 == null || !this.compound3.hasKey("id")) && (this.compound4 == null || !this.compound4.hasKey("id")) && (this.compound5 == null || !this.compound5.hasKey("id")) && (this.compound6 == null || !this.compound6.hasKey("id"));
    }
    
    private void setTarget(EntityLivingBase base, EntityLivingBase target) {
        if (base instanceof EntityLiving) {
            ((EntityLiving)base).setAttackTarget(target);
        }
        else {
            base.setRevengeTarget(target);
        }
    }
    
    @Override
    public boolean aiShouldExecute() {
        if (this.isEmpty() || this.npc.isKilled()) {
            return false;
        }
        this.target = this.getTarget();
        if (this.npc.getRNG().nextInt(30) == 1 && this.spawned.isEmpty()) {
            this.spawned = this.getNearbySpawned();
        }
        if (!this.spawned.isEmpty()) {
            this.checkSpawns();
        }
        return this.target != null;
    }
    
    @Override
    public boolean aiContinueExecute() {
        return this.aiShouldExecute();
    }
    
    @Override
    public void resetTask() {
        this.reset();
    }
    
    @Override
    public void aiStartExecuting() {
        this.number = 0;
        for (EntityLivingBase entity : this.spawned) {
            int i = entity.getEntityData().getInteger("NpcSpawnerNr");
            if (i > this.number) {
                this.number = i;
            }
            this.setTarget(entity, this.npc.getAttackTarget());
        }
    }
    
    @Override
    public void reset() {
        this.number = 0;
        if (this.spawned.isEmpty()) {
            this.spawned = this.getNearbySpawned();
        }
        this.target = null;
        this.checkSpawns();
    }
    
    @Override
    public void killed() {
        this.reset();
    }
    
    private EntityLivingBase spawnEntity(NBTTagCompound compound) {
        if (compound == null || !compound.hasKey("id")) {
            return null;
        }
        double x = this.npc.posX + this.xOffset - 0.5 + this.npc.getRNG().nextFloat();
        double y = this.npc.posY + this.yOffset;
        double z = this.npc.posZ + this.zOffset - 0.5 + this.npc.getRNG().nextFloat();
        Entity entity = NoppesUtilServer.spawnClone(compound, x, y, z, this.npc.world);
        if (entity == null || !(entity instanceof EntityLivingBase)) {
            return null;
        }
        EntityLivingBase living = (EntityLivingBase)entity;
        living.getEntityData().setString("NpcSpawnerId", this.id);
        living.getEntityData().setInteger("NpcSpawnerNr", this.number);
        this.setTarget(living, this.npc.getAttackTarget());
        living.setPosition(x, y, z);
        if (living instanceof EntityNPCInterface) {
            EntityNPCInterface snpc = (EntityNPCInterface)living;
            snpc.stats.spawnCycle = 4;
            snpc.stats.respawnTime = 0;
            snpc.ais.returnToStart = false;
        }
        this.spawned.add(living);
        return living;
    }
    
    private NBTTagCompound getCompound(int i) {
        if (i <= 1 && this.compound1 != null && this.compound1.hasKey("id")) {
            this.number = 1;
            return this.compound1;
        }
        if (i <= 2 && this.compound2 != null && this.compound2.hasKey("id")) {
            this.number = 2;
            return this.compound2;
        }
        if (i <= 3 && this.compound3 != null && this.compound3.hasKey("id")) {
            this.number = 3;
            return this.compound3;
        }
        if (i <= 4 && this.compound4 != null && this.compound4.hasKey("id")) {
            this.number = 4;
            return this.compound4;
        }
        if (i <= 5 && this.compound5 != null && this.compound5.hasKey("id")) {
            this.number = 5;
            return this.compound5;
        }
        if (i <= 6 && this.compound6 != null && this.compound6.hasKey("id")) {
            this.number = 6;
            return this.compound6;
        }
        return null;
    }
    
    private List<EntityLivingBase> getNearbySpawned() {
        List<EntityLivingBase> spawnList = new ArrayList<EntityLivingBase>();
        List<EntityLivingBase> list = (List<EntityLivingBase>)this.npc.world.getEntitiesWithinAABB((Class)EntityLivingBase.class, this.npc.getEntityBoundingBox().grow(40.0, 40.0, 40.0));
        for (EntityLivingBase entity : list) {
            if (entity.getEntityData().getString("NpcSpawnerId").equals(this.id) && !entity.isDead) {
                spawnList.add(entity);
            }
        }
        return spawnList;
    }
    
    public boolean isOnCooldown(String name) {
        if (!this.cooldown.containsKey(name)) {
            return false;
        }
        long time = this.cooldown.get(name);
        return System.currentTimeMillis() < time + 1200000L;
    }
    
    public boolean hasPixelmon() {
        return this.compound1 != null && this.compound1.getString("id").equals("pixelmontainer");
    }
    
    @Override
    public IEntityLivingBase spawnEntity(int i) {
        NBTTagCompound compound = this.getCompound(i + 1);
        if (compound == null) {
            return null;
        }
        EntityLivingBase base = this.spawnEntity(compound);
        if (base == null) {
            return null;
        }
        return (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)base);
    }
    
    @Override
    public void removeAllSpawned() {
        for (EntityLivingBase entity : this.spawned) {
            entity.isDead = true;
        }
        this.spawned = new ArrayList<EntityLivingBase>();
    }
}
