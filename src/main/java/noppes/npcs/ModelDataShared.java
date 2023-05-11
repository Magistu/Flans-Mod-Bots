package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.Iterator;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.constants.EnumParts;
import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityLivingBase;

public class ModelDataShared
{
    protected ModelPartConfig arm1;
    protected ModelPartConfig arm2;
    protected ModelPartConfig body;
    protected ModelPartConfig leg1;
    protected ModelPartConfig leg2;
    protected ModelPartConfig head;
    protected ModelPartData legParts;
    public ModelEyeData eyes;
    public Class<? extends EntityLivingBase> entityClass;
    protected EntityLivingBase entity;
    public NBTTagCompound extra;
    protected HashMap<EnumParts, ModelPartData> parts;
    
    public ModelDataShared() {
        this.arm1 = new ModelPartConfig();
        this.arm2 = new ModelPartConfig();
        this.body = new ModelPartConfig();
        this.leg1 = new ModelPartConfig();
        this.leg2 = new ModelPartConfig();
        this.head = new ModelPartConfig();
        this.legParts = new ModelPartData("legs");
        this.eyes = new ModelEyeData();
        this.extra = new NBTTagCompound();
        this.parts = new HashMap<EnumParts, ModelPartData>();
    }
    
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        if (this.entityClass != null) {
            compound.setString("EntityClass", this.entityClass.getCanonicalName());
        }
        compound.setTag("ArmsConfig", (NBTBase)this.arm1.writeToNBT());
        compound.setTag("BodyConfig", (NBTBase)this.body.writeToNBT());
        compound.setTag("LegsConfig", (NBTBase)this.leg1.writeToNBT());
        compound.setTag("HeadConfig", (NBTBase)this.head.writeToNBT());
        compound.setTag("LegParts", (NBTBase)this.legParts.writeToNBT());
        compound.setTag("Eyes", (NBTBase)this.eyes.writeToNBT());
        compound.setTag("ExtraData", (NBTBase)this.extra);
        NBTTagList list = new NBTTagList();
        for (EnumParts e : this.parts.keySet()) {
            NBTTagCompound item = this.parts.get(e).writeToNBT();
            item.setString("PartName", e.name);
            list.appendTag((NBTBase)item);
        }
        compound.setTag("Parts", (NBTBase)list);
        return compound;
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        this.setEntityName(compound.getString("EntityClass"));
        this.arm1.readFromNBT(compound.getCompoundTag("ArmsConfig"));
        this.body.readFromNBT(compound.getCompoundTag("BodyConfig"));
        this.leg1.readFromNBT(compound.getCompoundTag("LegsConfig"));
        this.head.readFromNBT(compound.getCompoundTag("HeadConfig"));
        this.legParts.readFromNBT(compound.getCompoundTag("LegParts"));
        this.eyes.readFromNBT(compound.getCompoundTag("Eyes"));
        this.extra = compound.getCompoundTag("ExtraData");
        HashMap<EnumParts, ModelPartData> parts = new HashMap<EnumParts, ModelPartData>();
        NBTTagList list = compound.getTagList("Parts", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            String name = item.getString("PartName");
            ModelPartData part = new ModelPartData(name);
            part.readFromNBT(item);
            EnumParts e = EnumParts.FromName(name);
            if (e != null) {
                parts.put(e, part);
            }
        }
        this.parts = parts;
        this.updateTransate();
    }
    
    private void updateTransate() {
        for (EnumParts part : EnumParts.values()) {
            ModelPartConfig config = this.getPartConfig(part);
            if (config != null) {
                if (part == EnumParts.HEAD) {
                    config.setTranslate(0.0f, this.getBodyY(), 0.0f);
                }
                else if (part == EnumParts.ARM_LEFT) {
                    ModelPartConfig body = this.getPartConfig(EnumParts.BODY);
                    float x = (1.0f - body.scaleX) * 0.25f + (1.0f - config.scaleX) * 0.075f;
                    float y = this.getBodyY() + (1.0f - config.scaleY) * -0.1f;
                    config.setTranslate(-x, y, 0.0f);
                    if (!config.notShared) {
                        ModelPartConfig arm = this.getPartConfig(EnumParts.ARM_RIGHT);
                        arm.copyValues(config);
                    }
                }
                else if (part == EnumParts.ARM_RIGHT) {
                    ModelPartConfig body = this.getPartConfig(EnumParts.BODY);
                    float x = (1.0f - body.scaleX) * 0.25f + (1.0f - config.scaleX) * 0.075f;
                    float y = this.getBodyY() + (1.0f - config.scaleY) * -0.1f;
                    config.setTranslate(x, y, 0.0f);
                }
                else if (part == EnumParts.LEG_LEFT) {
                    config.setTranslate(config.scaleX * 0.125f - 0.113f, this.getLegsY(), 0.0f);
                    if (!config.notShared) {
                        ModelPartConfig leg = this.getPartConfig(EnumParts.LEG_RIGHT);
                        leg.copyValues(config);
                    }
                }
                else if (part == EnumParts.LEG_RIGHT) {
                    config.setTranslate((1.0f - config.scaleX) * 0.125f, this.getLegsY(), 0.0f);
                }
                else if (part == EnumParts.BODY) {
                    config.setTranslate(0.0f, this.getBodyY(), 0.0f);
                }
            }
        }
    }
    
    public void setEntityName(String string) {
        this.entityClass = null;
        this.entity = null;
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
            try {
                Class<? extends Entity> c = (Class<? extends Entity>)ent.getEntityClass();
                if (c.getCanonicalName().equals(string) && EntityLivingBase.class.isAssignableFrom(c)) {
                    this.entityClass = c.asSubclass(EntityLivingBase.class);
                    break;
                }
                continue;
            }
            catch (Throwable t) {}
        }
    }
    
    public void setEntityClass(Class<? extends EntityLivingBase> entityClass) {
        this.entityClass = entityClass;
        this.entity = null;
        this.extra = new NBTTagCompound();
    }
    
    public Class<? extends EntityLivingBase> getEntityClass() {
        return this.entityClass;
    }
    
    public float offsetY() {
        if (this.entity == null) {
            return -this.getBodyY();
        }
        return this.entity.height - 1.8f;
    }
    
    public void clearEntity() {
        this.entity = null;
    }
    
    public ModelPartData getPartData(EnumParts type) {
        if (type == EnumParts.LEGS) {
            return this.legParts;
        }
        if (type == EnumParts.EYES) {
            return this.eyes;
        }
        return this.parts.get(type);
    }
    
    public ModelPartConfig getPartConfig(EnumParts type) {
        if (type == EnumParts.BODY) {
            return this.body;
        }
        if (type == EnumParts.ARM_LEFT) {
            return this.arm1;
        }
        if (type == EnumParts.ARM_RIGHT) {
            return this.arm2;
        }
        if (type == EnumParts.LEG_LEFT) {
            return this.leg1;
        }
        if (type == EnumParts.LEG_RIGHT) {
            return this.leg2;
        }
        return this.head;
    }
    
    public void removePart(EnumParts type) {
        this.parts.remove(type);
    }
    
    public ModelPartData getOrCreatePart(EnumParts type) {
        if (type == null) {
            return null;
        }
        if (type == EnumParts.EYES) {
            return this.eyes;
        }
        ModelPartData part = this.getPartData(type);
        if (part == null) {
            this.parts.put(type, part = new ModelPartData(type.name));
        }
        return part;
    }
    
    public float getBodyY() {
        return (1.0f - this.body.scaleY) * 0.75f + this.getLegsY();
    }
    
    public float getLegsY() {
        ModelPartConfig legs = this.leg1;
        if (this.leg2.notShared && this.leg2.scaleY > this.leg1.scaleY) {
            legs = this.leg2;
        }
        return (1.0f - legs.scaleY) * 0.75f;
    }
}
