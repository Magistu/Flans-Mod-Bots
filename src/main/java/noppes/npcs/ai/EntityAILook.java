package noppes.npcs.ai;

import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.constants.AiMutex;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAILook extends EntityAIBase
{
    private EntityNPCInterface npc;
    private int idle;
    private double lookX;
    private double lookZ;
    boolean rotatebody;
    private boolean forced;
    private Entity forcedEntity;
    
    public EntityAILook(EntityNPCInterface npc) {
        this.idle = 0;
        this.forced = false;
        this.forcedEntity = null;
        this.npc = npc;
        this.setMutexBits((int)AiMutex.LOOK);
    }
    
    public boolean shouldExecute() {
        return !this.npc.isAttacking() && this.npc.getNavigator().noPath() && !this.npc.isPlayerSleeping() && this.npc.isEntityAlive();
    }
    
    public void startExecuting() {
        this.rotatebody = (this.npc.ais.getStandingType() == 0 || this.npc.ais.getStandingType() == 3);
    }
    
    public void rotate(Entity entity) {
        this.forced = true;
        this.forcedEntity = entity;
    }
    
    public void rotate(int degrees) {
        this.forced = true;
        EntityNPCInterface npc = this.npc;
        EntityNPCInterface npc2 = this.npc;
        EntityNPCInterface npc3 = this.npc;
        float rotationYawHead = (float)degrees;
        npc3.renderYawOffset = rotationYawHead;
        npc2.rotationYaw = rotationYawHead;
        npc.rotationYawHead = rotationYawHead;
    }
    
    public void resetTask() {
        this.rotatebody = false;
        this.forced = false;
        this.forcedEntity = null;
    }
    
    public void updateTask() {
        Entity lookat = null;
        if (this.forced && this.forcedEntity != null) {
            lookat = this.forcedEntity;
        }
        else if (this.npc.isInteracting()) {
            Iterator<EntityLivingBase> ita = this.npc.interactingEntities.iterator();
            double closestDistance = 12.0;
            while (ita.hasNext()) {
                EntityLivingBase entity = ita.next();
                double distance = entity.getDistance((Entity)this.npc);
                if (distance < closestDistance) {
                    closestDistance = entity.getDistance((Entity)this.npc);
                    lookat = (Entity)entity;
                }
                else {
                    if (distance <= 12.0) {
                        continue;
                    }
                    ita.remove();
                }
            }
        }
        else if (this.npc.ais.getStandingType() == 2) {
            lookat = (Entity)this.npc.world.getClosestPlayerToEntity((Entity)this.npc, 16.0);
        }
        if (lookat != null) {
            this.npc.getLookHelper().setLookPositionWithEntity(lookat, 10.0f, (float)this.npc.getVerticalFaceSpeed());
            return;
        }
        if (this.rotatebody) {
            if (this.idle == 0 && this.npc.getRNG().nextFloat() < 0.004f) {
                double var1 = 6.283185307179586 * this.npc.getRNG().nextDouble();
                if (this.npc.ais.getStandingType() == 3) {
                    var1 = 0.017453292519943295 * this.npc.ais.orientation + 0.6283185307179586 + 1.8849555921538759 * this.npc.getRNG().nextDouble();
                }
                this.lookX = Math.cos(var1);
                this.lookZ = Math.sin(var1);
                this.idle = 20 + this.npc.getRNG().nextInt(20);
            }
            if (this.idle > 0) {
                --this.idle;
                this.npc.getLookHelper().setLookPosition(this.npc.posX + this.lookX, this.npc.posY + this.npc.getEyeHeight(), this.npc.posZ + this.lookZ, 10.0f, (float)this.npc.getVerticalFaceSpeed());
            }
        }
        if (this.npc.ais.getStandingType() == 1 && !this.forced) {
            EntityNPCInterface npc = this.npc;
            EntityNPCInterface npc2 = this.npc;
            EntityNPCInterface npc3 = this.npc;
            float rotationYawHead = (float)this.npc.ais.orientation;
            npc3.renderYawOffset = rotationYawHead;
            npc2.rotationYaw = rotationYawHead;
            npc.rotationYawHead = rotationYawHead;
        }
    }
}
