package noppes.npcs.entity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.MoverType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;

public class EntityChairMount extends Entity
{
    public EntityChairMount(World world) {
        super(world);
        this.setSize(0.0f, 0.0f);
    }
    
    public double getMountedYOffset() {
        return 0.5;
    }
    
    protected void entityInit() {
    }
    
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (this.world != null && !this.world.isRemote && this.getPassengers().isEmpty()) {
            this.isDead = true;
        }
    }
    
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }
    
    public boolean isInvisible() {
        return true;
    }
    
    public void move(MoverType type, double x, double y, double z) {
    }
    
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
    }
    
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
    }
    
    public boolean canBeCollidedWith() {
        return false;
    }
    
    public boolean canBePushed() {
        return false;
    }
    
    public void fall(float distance, float damageMultiplier) {
    }
    
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_, boolean bo) {
        this.setPosition(p_70056_1_, p_70056_3_, p_70056_5_);
        this.setRotation(p_70056_7_, p_70056_8_);
    }
}
