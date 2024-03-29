package com.flansmod.common.eventhandlers;

import java.util.ArrayList;

import com.flansmod.client.FlansModClient;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.ItemGun;

import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class ServerTickEvent 
{
	
	public static ArrayList<EntityPlayerMP> nightVisionPlayers = new ArrayList<EntityPlayerMP>();
	
	public ServerTickEvent() 
	{
		FMLCommonHandler.instance().bus().register(this);
	}
	
	
    int tickCount = 0;
    
    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event)
    {
        switch(event.phase)
        {
        case START :
        {
            break;
        }
        case END :
        {
            if(tickCount >= 20)
            {
                // INPUT CHECK FOR NIGHT VISION SCOPE ITEM
                // IF NO NIGHT VISION ITEM IN HAND GET RID NIGHT VISION
            	ArrayList<EntityPlayerMP> playersToRemove = new ArrayList<EntityPlayerMP>();
            	
            	for(EntityPlayerMP player : nightVisionPlayers)
            	{
            		if(player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemGun)
            		{
            			ItemGun itemGun = (ItemGun)player.getHeldItem(EnumHand.MAIN_HAND).getItem();
            			ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
            			AttachmentType scope = itemGun.type.getScope(itemstack);
            			//Apply night vision while scoped if attachment.hasNightVision or gun.allowNightvision
                		if(scope == null && !itemGun.type.allowNightVision || (scope != null && !scope.hasNightVision && !itemGun.type.allowNightVision))
                		{
                			player.removePotionEffect(Potion.getPotionById(16));
                			playersToRemove.add(player);
                		}	
            		}
            		else
            		{
            			player.removePotionEffect(Potion.getPotionById(16));
            			playersToRemove.add(player);
            		}
            	}
                tickCount = 0;
                for(EntityPlayerMP player : playersToRemove)
                {
                	nightVisionPlayers.remove(player);
                }
            }
            tickCount++;
            break;
        }        
        }
    }
}
