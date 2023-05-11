package noppes.npcs;

import java.lang.reflect.Method;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityList;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import noppes.npcs.controllers.PixelmonHelper;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;

public class ModelData extends ModelDataShared
{
    public EntityLivingBase getEntity(EntityNPCInterface npc) {
        if (this.entityClass == null) {
            return null;
        }
        if (this.entity == null) {
            try {
                this.entity = (EntityLivingBase)this.entityClass.getConstructor(World.class).newInstance(npc.world);
                if (PixelmonHelper.isPixelmon((Entity)this.entity) && npc.world.isRemote && !this.extra.hasKey("Name")) {
                    this.extra.setString("Name", "Abra");
                }
                try {
                    this.entity.readEntityFromNBT(this.extra);
                }
                catch (Exception ex) {}
                this.entity.setEntityInvulnerable(true);
                this.entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)npc.getMaxHealth());
                for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                    this.entity.setItemStackToSlot(slot, npc.getItemStackFromSlot(slot));
                }
            }
            catch (Exception ex2) {}
        }
        return this.entity;
    }
    
    public ModelData copy() {
        ModelData data = new ModelData();
        data.readFromNBT(this.writeToNBT());
        return data;
    }
    
    public void setExtra(EntityLivingBase entity, String key, String value) {
        key = key.toLowerCase();
        if (key.equals("breed") && EntityList.getEntityString((Entity)entity).equals("tgvstyle.Dog")) {
            try {
                Method method = entity.getClass().getMethod("getBreedID", (Class<?>[])new Class[0]);
                Enum breed = (Enum)method.invoke(entity, new Object[0]);
                method = entity.getClass().getMethod("setBreedID", breed.getClass());
                method.invoke(entity, ((Enum[])breed.getClass().getEnumConstants())[Integer.parseInt(value)]);
                NBTTagCompound comp = new NBTTagCompound();
                entity.writeEntityToNBT(comp);
                this.extra.setString("EntityData21", comp.getString("EntityData21"));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (key.equalsIgnoreCase("name") && PixelmonHelper.isPixelmon((Entity)entity)) {
            this.extra.setString("Name", value);
        }
        this.clearEntity();
    }
}
