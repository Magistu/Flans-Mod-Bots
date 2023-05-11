package noppes.npcs;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.ability.IAbilityDamaged;
import noppes.npcs.api.event.NpcEvent;

public class AbilityEventHandler
{
    @SubscribeEvent
    public void invoke(NpcEvent.DamagedEvent event) {
        IAbilityDamaged ab = (IAbilityDamaged)((EntityNPCInterface)event.npc.getMCEntity()).abilities.getAbility(EnumAbilityType.ATTACKED);
        if (ab != null) {
            ab.handleEvent(event);
        }
    }
}
