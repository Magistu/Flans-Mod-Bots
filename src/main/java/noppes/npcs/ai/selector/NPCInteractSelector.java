package noppes.npcs.ai.selector;

import noppes.npcs.entity.EntityNPCInterface;
import com.google.common.base.Predicate;

public class NPCInteractSelector implements Predicate
{
    private EntityNPCInterface npc;
    
    public NPCInteractSelector(EntityNPCInterface npc) {
        this.npc = npc;
    }
    
    public boolean isEntityApplicable(EntityNPCInterface entity) {
        return entity != this.npc && this.npc.isEntityAlive() && !entity.isAttacking() && !this.npc.getFaction().isAggressiveToNpc(entity) && this.npc.ais.stopAndInteract;
    }
    
    public boolean apply(Object ob) {
        return ob instanceof EntityNPCInterface && this.isEntityApplicable((EntityNPCInterface)ob);
    }
}
