package noppes.npcs.ai;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLiving;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityMoveHelper;

public class FlyingMoveHelper extends EntityMoveHelper
{
    private EntityNPCInterface entity;
    private int courseChangeCooldown;
    
    public FlyingMoveHelper(EntityNPCInterface entity) {
        super((EntityLiving)entity);
        this.entity = entity;
    }
    
    public void onUpdateMoveHelper() {
        if (this.action == EntityMoveHelper.Action.MOVE_TO && this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown = 4;
            double d0 = this.posX - this.entity.posX;
            double d2 = this.posY - this.entity.posY;
            double d3 = this.posZ - this.entity.posZ;
            double d4 = d0 * d0 + d2 * d2 + d3 * d3;
            d4 = MathHelper.sqrt(d4);
            if (d4 > 0.5 && this.isNotColliding(this.posX, this.posY, this.posZ, d4)) {
                double speed = this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() / 2.5;
                if (d4 < 3.0 && speed > 0.10000000149011612) {
                    speed = 0.10000000149011612;
                }
                EntityNPCInterface entity = this.entity;
                entity.motionX += d0 / d4 * speed;
                EntityNPCInterface entity2 = this.entity;
                entity2.motionY += d2 / d4 * speed;
                EntityNPCInterface entity3 = this.entity;
                entity3.motionZ += d3 / d4 * speed;
                EntityNPCInterface entity4 = this.entity;
                EntityNPCInterface entity5 = this.entity;
                float n = -(float)Math.atan2(this.entity.motionX, this.entity.motionZ) * 180.0f / 3.1415927f;
                entity5.rotationYaw = n;
                entity4.renderYawOffset = n;
            }
            else {
                this.action = EntityMoveHelper.Action.WAIT;
            }
        }
    }
    
    private boolean isNotColliding(double p_179926_1_, double p_179926_3_, double p_179926_5_, double p_179926_7_) {
        double d4 = (p_179926_1_ - this.entity.posX) / p_179926_7_;
        double d5 = (p_179926_3_ - this.entity.posY) / p_179926_7_;
        double d6 = (p_179926_5_ - this.entity.posZ) / p_179926_7_;
        AxisAlignedBB axisalignedbb = this.entity.getEntityBoundingBox();
        for (int i = 1; i < p_179926_7_; ++i) {
            axisalignedbb = axisalignedbb.offset(d4, d5, d6);
            if (!this.entity.world.getCollisionBoxes((Entity)this.entity, axisalignedbb).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
