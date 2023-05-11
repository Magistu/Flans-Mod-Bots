package noppes.npcs.ai.target;

import noppes.npcs.constants.AiMutex;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIOwnerHurtTarget extends EntityAITarget
{
    EntityNPCInterface npc;
    EntityLivingBase theTarget;
    private int timestamp;
    
    public EntityAIOwnerHurtTarget(EntityNPCInterface npc) {
        super((EntityCreature)npc, false);
        this.npc = npc;
        this.setMutexBits((int)AiMutex.PASSIVE);
    }
    
    public boolean shouldExecute() {
        if (!this.npc.isFollower() || this.npc.roleInterface == null || !this.npc.roleInterface.defendOwner()) {
            return false;
        }
        EntityLivingBase entitylivingbase = this.npc.getOwner();
        if (entitylivingbase == null) {
            return false;
        }
        this.theTarget = entitylivingbase.getLastAttackedEntity();
        int i = entitylivingbase.getLastAttackedEntityTime();
        return i != this.timestamp && this.isSuitableTarget(this.theTarget, false);
    }
    
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.theTarget);
        EntityLivingBase entitylivingbase = this.npc.getOwner();
        if (entitylivingbase != null) {
            this.timestamp = entitylivingbase.getLastAttackedEntityTime();
        }
        super.startExecuting();
    }
}
