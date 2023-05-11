package noppes.npcs.entity.data;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.data.INPCMelee;

public class DataMelee implements INPCMelee
{
    private EntityNPCInterface npc;
    private int attackStrength;
    private int attackSpeed;
    private int attackRange;
    private int knockback;
    private int potionType;
    private int potionDuration;
    private int potionAmp;
    
    public DataMelee(EntityNPCInterface npc) {
        this.attackStrength = 5;
        this.attackSpeed = 20;
        this.attackRange = 2;
        this.knockback = 0;
        this.potionType = 0;
        this.potionDuration = 5;
        this.potionAmp = 0;
        this.npc = npc;
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        this.attackSpeed = compound.getInteger("AttackSpeed");
        this.setStrength(compound.getInteger("AttackStrenght"));
        this.attackRange = compound.getInteger("AttackRange");
        this.knockback = compound.getInteger("KnockBack");
        this.potionType = compound.getInteger("PotionEffect");
        this.potionDuration = compound.getInteger("PotionDuration");
        this.potionAmp = compound.getInteger("PotionAmp");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("AttackStrenght", this.attackStrength);
        compound.setInteger("AttackSpeed", this.attackSpeed);
        compound.setInteger("AttackRange", this.attackRange);
        compound.setInteger("KnockBack", this.knockback);
        compound.setInteger("PotionEffect", this.potionType);
        compound.setInteger("PotionDuration", this.potionDuration);
        compound.setInteger("PotionAmp", this.potionAmp);
        return compound;
    }
    
    @Override
    public int getStrength() {
        return this.attackStrength;
    }
    
    @Override
    public void setStrength(int strength) {
        this.attackStrength = strength;
        this.npc.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)this.attackStrength);
    }
    
    @Override
    public int getDelay() {
        return this.attackSpeed;
    }
    
    @Override
    public void setDelay(int speed) {
        this.attackSpeed = speed;
    }
    
    @Override
    public int getRange() {
        return this.attackRange;
    }
    
    @Override
    public void setRange(int range) {
        this.attackRange = range;
    }
    
    @Override
    public int getKnockback() {
        return this.knockback;
    }
    
    @Override
    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }
    
    @Override
    public int getEffectType() {
        return this.potionType;
    }
    
    @Override
    public int getEffectTime() {
        return this.potionDuration;
    }
    
    @Override
    public int getEffectStrength() {
        return this.potionAmp;
    }
    
    @Override
    public void setEffect(int type, int strength, int time) {
        this.potionType = type;
        this.potionDuration = time;
        this.potionAmp = strength;
    }
}
