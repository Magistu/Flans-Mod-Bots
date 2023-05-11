package noppes.npcs.ai.target;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIClearTarget extends EntityAIBase
{
    private EntityNPCInterface npc;
    private EntityLivingBase target;
    
    public EntityAIClearTarget(EntityNPCInterface npc) {
        this.npc = npc;
    }
    
    public boolean shouldExecute() {
        this.target = this.npc.getAttackTarget();
        return this.target != null && ((this.npc.getOwner() != null && !this.npc.isInRange((Entity)this.npc.getOwner(), this.npc.stats.aggroRange * 2)) || this.npc.combatHandler.checkTarget());
    }
    
    public void startExecuting() {
        this.npc.setAttackTarget(null);
        if (this.target == this.npc.getRevengeTarget()) {
            this.npc.setRevengeTarget((EntityLivingBase)null);
        }
        super.startExecuting();
    }
    
    public void resetTask() {
        this.npc.getNavigator().clearPath();
    }
}
