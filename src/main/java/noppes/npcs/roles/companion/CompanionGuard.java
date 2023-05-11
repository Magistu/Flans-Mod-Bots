package noppes.npcs.roles.companion;

import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.EntityCreeper;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CompanionGuard extends CompanionJobInterface
{
    public boolean isStanding;
    
    public CompanionGuard() {
        this.isStanding = false;
    }
    
    @Override
    public NBTTagCompound getNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("CompanionGuardStanding", this.isStanding);
        return compound;
    }
    
    @Override
    public void setNBT(NBTTagCompound compound) {
        this.isStanding = compound.getBoolean("CompanionGuardStanding");
    }
    
    public boolean isEntityApplicable(Entity entity) {
        return !(entity instanceof EntityPlayer) && !(entity instanceof EntityNPCInterface) && !(entity instanceof EntityCreeper) && entity instanceof IMob;
    }
    
    @Override
    public boolean isSelfSufficient() {
        return this.isStanding;
    }
}
