package noppes.npcs.ai;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIDodgeShoot extends EntityAIBase
{
    private EntityNPCInterface entity;
    private double x;
    private double y;
    private double zPosition;
    
    public EntityAIDodgeShoot(EntityNPCInterface iNpc) {
        this.entity = iNpc;
        this.setMutexBits((int)AiMutex.PASSIVE);
    }
    
    public boolean shouldExecute() {
        EntityLivingBase var1 = this.entity.getAttackTarget();
        if (var1 == null || !var1.isEntityAlive()) {
            return false;
        }
        if (this.entity.inventory.getProjectile() == null) {
            return false;
        }
        if (this.entity.getRangedTask() == null) {
            return false;
        }
        Vec3d vec = this.entity.getRangedTask().hasFired() ? RandomPositionGenerator.findRandomTarget((EntityCreature)this.entity, 4, 1) : null;
        if (vec == null) {
            return false;
        }
        this.x = vec.x;
        this.y = vec.y;
        this.zPosition = vec.z;
        return true;
    }
    
    public boolean shouldContinueExecuting() {
        return !this.entity.getNavigator().noPath();
    }
    
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.x, this.y, this.zPosition, 1.2);
    }
    
    public void updateTask() {
        if (this.entity.getAttackTarget() != null) {
            this.entity.getLookHelper().setLookPositionWithEntity((Entity)this.entity.getAttackTarget(), 30.0f, 30.0f);
        }
    }
}
