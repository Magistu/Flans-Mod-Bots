package com.magistumod.client;

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


@EventBusSubscriber(modid = "magistumod", value = {Side.CLIENT})
public class ClientHelper 
{
	private static final Map<UUID, String> idtoNameMap = new HashMap<>();
	private static final Map<String, UUID> nametoIdMap = new HashMap<>();
	public static final Map<UUID, Pair<Integer, Vec2f>> idtoPosMap = new HashMap<>();
	public static final Minecraft mc = Minecraft.getMinecraft();
	
	public static ScaledResolution getWindow() 
	{
		return new ScaledResolution(mc);
	}
	
	public static UUID getIdFromName(String name) 
	{
		return nametoIdMap.get(name);
	}

	public static String getNameFromId(UUID uuid) 
	{
		String playerName = getOnlineUsernameFromUUID(uuid);
		if (playerName == null) 
		{
			String uuidString = uuid.toString().replace("-", "");
			String url = "https://api.mojang.com/user/profiles/" + uuidString + "/names";
			try 
			{
				String nameJson = IOUtils.toString(new URL(url), "ANSI");
				JsonArray jsonArray = (new JsonParser()).parse(nameJson).getAsJsonArray();
				playerName = jsonArray.get(0).getAsJsonObject().get("name").getAsString();
			}
			catch (Exception ex) 
			{
				playerName = I18n.format("magistumod.unknownplayer", new Object[0]);
			} 
		} 
		return playerName;
	}

	
	public static String getOnlineUsernameFromUUID(UUID uuid) 
	{
		String playerName = idtoNameMap.get(uuid);
		if (playerName == null) 
		{
			playerName = UsernameCache.getLastKnownUsername(uuid);
		}
		if (playerName == null) 
		{
			try
			{
				playerName = mc.getConnection().getPlayerInfo(uuid).getGameProfile().getName();
			}
			catch (NullPointerException nullPointerException) {}
		}
		return playerName;
	}
	
	public static void addPlayerMapping(String name, UUID playerId)
	{
		idtoNameMap.put(playerId, name);
		nametoIdMap.put(name, playerId);
	}
}
