package noppes.npcs;

import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;

public class Resistances
{
    public float knockback;
    public float arrow;
    public float melee;
    public float explosion;
    
    public Resistances() {
        this.knockback = 1.0f;
        this.arrow = 1.0f;
        this.melee = 1.0f;
        this.explosion = 1.0f;
    }
    
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setFloat("Knockback", this.knockback);
        compound.setFloat("Arrow", this.arrow);
        compound.setFloat("Melee", this.melee);
        compound.setFloat("Explosion", this.explosion);
        return compound;
    }
    
    public void readToNBT(NBTTagCompound compound) {
        this.knockback = compound.getFloat("Knockback");
        this.arrow = compound.getFloat("Arrow");
        this.melee = compound.getFloat("Melee");
        this.explosion = compound.getFloat("Explosion");
    }
    
    public float applyResistance(DamageSource source, float damage) {
        if (source.damageType.equals("arrow") || source.damageType.equals("thrown") || source.isProjectile()) {
            damage *= 2.0f - this.arrow;
        }
        else if (source.damageType.equals("player") || source.damageType.equals("mob")) {
            damage *= 2.0f - this.melee;
        }
        else if (source.damageType.equals("explosion") || source.damageType.equals("explosion.player")) {
            damage *= 2.0f - this.explosion;
        }
        return damage;
    }
}
