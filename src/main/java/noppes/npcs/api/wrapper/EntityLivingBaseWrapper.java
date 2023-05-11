package noppes.npcs.api.wrapper;

import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.api.entity.data.IMark;
import net.minecraft.inventory.EntityEquipmentSlot;
import noppes.npcs.api.CustomNPCsException;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.item.IItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.NpcAPI;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IEntityLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseWrapper<T extends EntityLivingBase> extends EntityWrapper<T> implements IEntityLivingBase
{
    public EntityLivingBaseWrapper(T entity) {
        super(entity);
    }
    
    @Override
    public float getHealth() {
        return this.entity.getHealth();
    }
    
    @Override
    public void setHealth(float health) {
        this.entity.setHealth(health);
    }
    
    @Override
    public float getMaxHealth() {
        return this.entity.getMaxHealth();
    }
    
    @Override
    public void setMaxHealth(float health) {
        if (health < 0.0f) {
            return;
        }
        this.entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)health);
    }
    
    @Override
    public boolean isAttacking() {
        return this.entity.getRevengeTarget() != null;
    }
    
    @Override
    public void setAttackTarget(IEntityLivingBase living) {
        if (living == null) {
            this.entity.setRevengeTarget((EntityLivingBase)null);
        }
        else {
            this.entity.setRevengeTarget(living.getMCEntity());
        }
    }
    
    @Override
    public IEntityLivingBase getAttackTarget() {
        return (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)this.entity.getRevengeTarget());
    }
    
    @Override
    public IEntityLivingBase getLastAttacked() {
        return (IEntityLivingBase)NpcAPI.Instance().getIEntity((Entity)this.entity.getLastAttackedEntity());
    }
    
    @Override
    public int getLastAttackedTime() {
        return this.entity.getLastAttackedEntityTime();
    }
    
    @Override
    public boolean canSeeEntity(IEntity entity) {
        return this.entity.canEntityBeSeen(entity.getMCEntity());
    }
    
    @Override
    public void swingMainhand() {
        this.entity.swingArm(EnumHand.MAIN_HAND);
    }
    
    @Override
    public void swingOffhand() {
        this.entity.swingArm(EnumHand.OFF_HAND);
    }
    
    @Override
    public void addPotionEffect(int effect, int duration, int strength, boolean hideParticles) {
        Potion p = Potion.getPotionById(effect);
        if (p == null) {
            return;
        }
        if (strength < 0) {
            strength = 0;
        }
        else if (strength > 255) {
            strength = 255;
        }
        if (duration < 0) {
            duration = 0;
        }
        else if (duration > 1000000) {
            duration = 1000000;
        }
        if (!p.isInstant()) {
            duration *= 20;
        }
        if (duration == 0) {
            this.entity.removePotionEffect(p);
        }
        else {
            this.entity.addPotionEffect(new PotionEffect(p, duration, strength, false, hideParticles));
        }
    }
    
    @Override
    public void clearPotionEffects() {
        this.entity.clearActivePotions();
    }
    
    @Override
    public int getPotionEffect(int effect) {
        PotionEffect pf = this.entity.getActivePotionEffect(Potion.getPotionById(effect));
        if (pf == null) {
            return -1;
        }
        return pf.getAmplifier();
    }
    
    @Override
    public IItemStack getMainhandItem() {
        return NpcAPI.Instance().getIItemStack(this.entity.getHeldItemMainhand());
    }
    
    @Override
    public void setMainhandItem(IItemStack item) {
        this.entity.setHeldItem(EnumHand.MAIN_HAND, (item == null) ? ItemStack.EMPTY : item.getMCItemStack());
    }
    
    @Override
    public IItemStack getOffhandItem() {
        return NpcAPI.Instance().getIItemStack(this.entity.getHeldItemOffhand());
    }
    
    @Override
    public void setOffhandItem(IItemStack item) {
        this.entity.setHeldItem(EnumHand.OFF_HAND, (item == null) ? ItemStack.EMPTY : item.getMCItemStack());
    }
    
    @Override
    public IItemStack getArmor(int slot) {
        if (slot < 0 || slot > 3) {
            throw new CustomNPCsException("Wrong slot id:" + slot, new Object[0]);
        }
        return NpcAPI.Instance().getIItemStack(this.entity.getItemStackFromSlot(this.getSlot(slot)));
    }
    
    @Override
    public void setArmor(int slot, IItemStack item) {
        if (slot < 0 || slot > 3) {
            throw new CustomNPCsException("Wrong slot id:" + slot, new Object[0]);
        }
        this.entity.setItemStackToSlot(this.getSlot(slot), (item == null) ? ItemStack.EMPTY : item.getMCItemStack());
    }
    
    private EntityEquipmentSlot getSlot(int slot) {
        if (slot == 3) {
            return EntityEquipmentSlot.HEAD;
        }
        if (slot == 2) {
            return EntityEquipmentSlot.CHEST;
        }
        if (slot == 1) {
            return EntityEquipmentSlot.LEGS;
        }
        if (slot == 0) {
            return EntityEquipmentSlot.FEET;
        }
        return null;
    }
    
    @Override
    public float getRotation() {
        return this.entity.renderYawOffset;
    }
    
    @Override
    public void setRotation(float rotation) {
        this.entity.renderYawOffset = rotation;
    }
    
    @Override
    public int getType() {
        return 5;
    }
    
    @Override
    public boolean typeOf(int type) {
        return type == 5 || super.typeOf(type);
    }
    
    @Override
    public boolean isChild() {
        return this.entity.isChild();
    }
    
    @Override
    public IMark addMark(int type) {
        MarkData data = MarkData.get(this.entity);
        return data.addMark(type);
    }
    
    @Override
    public void removeMark(IMark mark) {
        MarkData data = MarkData.get(this.entity);
        data.marks.remove(mark);
        data.syncClients();
    }
    
    @Override
    public IMark[] getMarks() {
        MarkData data = MarkData.get(this.entity);
        return data.marks.toArray(new IMark[data.marks.size()]);
    }
    
    @Override
    public float getMoveForward() {
        return this.entity.moveForward;
    }
    
    @Override
    public void setMoveForward(float move) {
        this.entity.moveForward = move;
    }
    
    @Override
    public float getMoveStrafing() {
        return this.entity.moveStrafing;
    }
    
    @Override
    public void setMoveStrafing(float move) {
        this.entity.moveStrafing = move;
    }
    
    @Override
    public float getMoveVertical() {
        return this.entity.moveVertical;
    }
    
    @Override
    public void setMoveVertical(float move) {
        this.entity.moveVertical = move;
    }
}
