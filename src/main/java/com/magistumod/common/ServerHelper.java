package com.magistumod.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.magistumod.common.storage.StorageHelper;
import com.magistumod.entity.EnumCoalitions;
import com.magistumod.entity.FlansModShooter;
import com.mojang.realmsclient.util.Pair;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.IOUtils;


public class ServerHelper 
{
	
	public static boolean canDamage(Entity source, Entity target, boolean trueifnull)
	{
		if (target != null && source != null)
		{
			EnumCoalitions targetCoalition = null;
			EnumCoalitions sourceCoalition = null;
			
			if (target instanceof FlansModShooter)
			{
				targetCoalition = ((FlansModShooter)target).getCoalition();
			}
			
			if (target instanceof EntityPlayer && StorageHelper.getCoalition(((EntityPlayer)target).getUniqueID()) != null)
			{
				targetCoalition = EnumCoalitions.getCoalition(StorageHelper.getCoalition(((EntityPlayer)target).getUniqueID()));
			}
			
			if (source instanceof FlansModShooter) 
			{
				sourceCoalition = ((FlansModShooter)source).getCoalition();
			}
			
			if (source instanceof EntityPlayer && StorageHelper.getCoalition(((EntityPlayer)source).getUniqueID()) != null)
			{
				sourceCoalition = EnumCoalitions.getCoalition(StorageHelper.getCoalition(((EntityPlayer)source).getUniqueID()));
			}
			
			if ((targetCoalition == null || sourceCoalition == null) && trueifnull || (targetCoalition != null && sourceCoalition != null && targetCoalition != sourceCoalition))
			{
				return true;
			}
		}
		return false;
	}
}
