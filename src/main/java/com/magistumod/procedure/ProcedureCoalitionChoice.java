package com.magistumod.procedure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.UUID;

import com.magistumod.common.network.NetworkHelper;
import com.magistumod.common.network.messages.AbstractMessage;
import com.magistumod.common.network.messages.MessageSaveData;
import com.magistumod.common.storage.StorageEvents;
import com.magistumod.common.storage.StorageHelper;

import com.magistumod.Elements;

@Elements.ModElement.Tag
public class ProcedureCoalitionChoice extends Elements.ModElement
{
	public ProcedureCoalitionChoice(Elements instance)
	{
		super(instance, 8);
	}

	public static void executeProcedure(java.util.HashMap<String, Object> dependencies)
	{
		Entity entity = (Entity) dependencies.get("entity");
		if (entity instanceof EntityPlayer)
		{
			
			
			String coalition = (String) dependencies.get("coalition");
			EntityPlayer player = (EntityPlayer) entity;
    		UUID uid = player.getUniqueID();
    		
    		if (StorageHelper.isPlayerInCoalition(uid))
			{
				StorageEvents.data.removePlayer(StorageHelper.getCoalition(uid), uid);
			}
			StorageEvents.data.addPlayer(coalition, uid);
			
			NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());

			player.closeScreen();
		}
	}
}
