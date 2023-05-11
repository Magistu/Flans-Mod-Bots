package noppes.npcs.ai;

import java.util.List;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIMovingPath extends EntityAIBase
{
    private EntityNPCInterface npc;
    private int[] pos;
    private int retries;
    
    public EntityAIMovingPath(EntityNPCInterface iNpc) {
        this.retries = 0;
        this.npc = iNpc;
        this.setMutexBits((int)AiMutex.PASSIVE);
    }
    
    public boolean shouldExecute() {
        if (this.npc.isAttacking() || this.npc.isInteracting() || (this.npc.getRNG().nextInt(40) != 0 && this.npc.ais.movingPause) || !this.npc.getNavigator().noPath()) {
            return false;
        }
        List<int[]> list = this.npc.ais.getMovingPath();
        if (list.size() < 2) {
            return false;
        }
        this.npc.ais.incrementMovingPath();
        this.pos = this.npc.ais.getCurrentMovingPath();
        this.retries = 0;
        return true;
    }
    
    public boolean shouldContinueExecuting() {
        if (this.npc.isAttacking() || this.npc.isInteracting()) {
            this.npc.ais.decreaseMovingPath();
            return false;
        }
        if (!this.npc.getNavigator().noPath()) {
            return true;
        }
        this.npc.getNavigator().clearPath();
        if (this.npc.getDistanceSq((double)this.pos[0], (double)this.pos[1], (double)this.pos[2]) < 3.0) {
            return false;
        }
        if (this.retries++ < 3) {
            this.startExecuting();
            return true;
        }
        return false;
    }
    
    public void startExecuting() {
        this.npc.getNavigator().tryMoveToXYZ(this.pos[0] + 0.5, (double)this.pos[1], this.pos[2] + 0.5, 1.0);
    }
}
