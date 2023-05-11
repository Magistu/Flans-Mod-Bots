package noppes.npcs.ability;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.constants.EnumAbilityType;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;

public class AbilityBlock extends AbstractAbility implements IAbilityDamaged
{
    public AbilityBlock(EntityNPCInterface npc) {
        super(npc);
    }
    
    @Override
    public boolean canRun(EntityLivingBase target) {
        return super.canRun(target);
    }
    
    @Override
    public boolean isType(EnumAbilityType type) {
        return type == EnumAbilityType.ATTACKED;
    }
    
    @Override
    public void handleEvent(NpcEvent.DamagedEvent event) {
        WorldServer world = (WorldServer)this.npc.getEntityWorld();
        world.setEntityState((Entity)this.npc, (byte)29);
        event.setCanceled(true);
        this.endAbility();
    }
}
