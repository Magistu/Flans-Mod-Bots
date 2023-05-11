package noppes.npcs.ai.target;

import noppes.npcs.constants.AiMutex;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIOwnerHurtByTarget extends EntityAITarget
{
    EntityNPCInterface npc;
    EntityLivingBase theOwnerAttacker;
    private int timer;
    
    public EntityAIOwnerHurtByTarget(EntityNPCInterface npc) {
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
        this.theOwnerAttacker = entitylivingbase.getRevengeTarget();
        int i = entitylivingbase.getRevengeTimer();
        return i != this.timer && this.isSuitableTarget(this.theOwnerAttacker, false);
    }
    
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.theOwnerAttacker);
        EntityLivingBase entitylivingbase = this.npc.getOwner();
        if (entitylivingbase != null) {
            this.timer = entitylivingbase.getRevengeTimer();
        }
        super.startExecuting();
    }
}
