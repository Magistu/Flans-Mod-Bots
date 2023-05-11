package com.magistumod.event;

import com.magistumod.common.ServerHelper;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FriendlyFireEvent
{
	@SubscribeEvent
	public void onEntityHit(LivingHurtEvent event) {
		if (event.getSource() != null) {
			if (!ServerHelper.canDamage(event.getSource().getTrueSource(), event.getEntityLiving(), true)) {
				event.setCanceled(true);
			}
		}
	}
}
