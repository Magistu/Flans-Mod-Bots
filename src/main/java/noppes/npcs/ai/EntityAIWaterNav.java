package noppes.npcs.ai;

import net.minecraft.pathfinding.PathNavigateGround;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIWaterNav extends EntityAIBase
{
    private EntityNPCInterface entity;
    
    public EntityAIWaterNav(EntityNPCInterface iNpc) {
        this.entity = iNpc;
        ((PathNavigateGround)iNpc.getNavigator()).setCanSwim(true);
    }
    
    public boolean shouldExecute() {
        return (this.entity.isInWater() || this.entity.isInLava()) && (this.entity.ais.canSwim || this.entity.collidedHorizontally);
    }
    
    public void updateTask() {
        if (this.entity.getRNG().nextFloat() < 0.8f) {
            this.entity.getJumpHelper().setJumping();
        }
    }
}
