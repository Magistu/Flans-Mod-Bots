package com.magistumod.common.storage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.magistumod.client.ClientHelper;
import com.magistumod.common.ServerHelper;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class StorageHandler 
{
	public static final Map<UUID, String> uuidToCoalitionMap = new HashMap<>();
	public static final Map<String, List<UUID>> coalitionToUuidsMap = new HashMap<>();
	static final Map<String, Map<String, Boolean>> coalitionSettingsMap = new HashMap<>();
	
	public static void syncPlayers(String coalition, EntityPlayerMP player) 
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer() && player != null) 
		{
			Iterable<Advancement> advancements = FMLCommonHandler.instance().getMinecraftServerInstance().getAdvancementManager().getAdvancements();
			for (Advancement adv : advancements) 
			{
				for (UUID id : coalitionToUuidsMap.get(coalition)) 
				{
					EntityPlayerMP coalitionmate = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(id);
					if (coalitionmate != null) 
					{
						if (coalitionmate.getAdvancements().getProgress(adv).isDone()) 
						{
							for (String s : coalitionmate.getAdvancements().getProgress(adv).getCompletedCriteria())
								player.getAdvancements().grantCriterion(adv, s);  continue;
						} 
						if (player.getAdvancements().getProgress(adv).isDone()) 
						{
							for (String s : player.getAdvancements().getProgress(adv).getCompletedCriteria()) 
							{
								coalitionmate.getAdvancements().grantCriterion(adv, s);
							}
						}
					}
				}
			}
		} 
	}
	
	public static void readFromNBT(NBTTagCompound nbt) {
		try {
			for (NBTBase nbtBase : nbt.getTagList("Coalitions", 10)) {
				NBTTagCompound coalitionTag = (NBTTagCompound)nbtBase;
				String coalitionName = coalitionTag.getString("Coalition Name");
				InventoryEnderChest inventoryEnderChest = new InventoryEnderChest();
				NBTTagList playersTag = coalitionTag.getTagList("Player List", 10);
				if (playersTag.tagCount() == 0) {
				continue;
				}
				readPlayers(coalitionTag, coalitionName);
				readSettings(coalitionTag, coalitionName);
			}
		} catch (Exception exception) {}
	}

	private static void readSettings(NBTTagCompound coalitionTag, String coalitionName) {
		NBTTagCompound coalitionSettings = (NBTTagCompound)coalitionTag.getTag("Settings");
		Map<String, Boolean> settingsMap = new HashMap<>();
		if (coalitionSettings == null) {
			settingsMap.put("disableAdvancementSync", Boolean.valueOf(false));
			settingsMap.put("enableFriendlyFire", Boolean.valueOf(false));
		} else {
			settingsMap.put("disableAdvancementSync", Boolean.valueOf(coalitionSettings.getBoolean("disableAdvancementSync")));
			settingsMap.put("enableFriendlyFire", Boolean.valueOf(coalitionSettings.getBoolean("enableFriendlyFire")));
		}
		coalitionSettingsMap.put(coalitionName, settingsMap);
	}

	private static void readPlayers(NBTTagCompound coalitionTag, String coalitionName) {
		Iterator<NBTBase> playerTagListIterator = coalitionTag.getTagList("Player List", 10).iterator();
			List<UUID> uuidList = new ArrayList<>();
			while (playerTagListIterator.hasNext()) {
				NBTTagCompound playerTag = (NBTTagCompound)playerTagListIterator.next();
				UUID playerId = UUID.fromString(playerTag.getString("uuid"));
				addPlayerMapping(playerId);
				uuidToCoalitionMap.put(playerId, coalitionName);
				uuidList.add(playerId);
			}
		coalitionToUuidsMap.put(coalitionName, uuidList);
	}
	
	private static void addPlayerMapping(UUID playerId) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			try {
				String name = Minecraft.getMinecraft().getConnection().getPlayerInfo(playerId).getGameProfile().getName();
				ClientHelper.addPlayerMapping(name, playerId);
			} catch (NullPointerException nullPointerException) {}
		}
	}

	public static NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList tagList = new NBTTagList();
		for (String coalitionName : coalitionToUuidsMap.keySet()) {
			NBTTagCompound coalitionTag = new NBTTagCompound();
			coalitionTag.setString("Coalition Name", coalitionName);
			coalitionTag.setTag("Player List", (NBTBase)writePlayers(coalitionName));
			coalitionTag.setTag("Settings", (NBTBase)writeSettings(coalitionName));
			tagList.appendTag((NBTBase)coalitionTag);
		}
		compound.setTag("Coalitions", (NBTBase)tagList);
		return compound;
	}

	private static NBTTagList writePlayers(String coalitionName) {
		NBTTagList playerListTag = new NBTTagList();
		for (UUID id : coalitionToUuidsMap.get(coalitionName)) {
			NBTTagCompound playerTag = new NBTTagCompound();
			playerTag.setString("uuid", id.toString());
			playerListTag.appendTag((NBTBase)playerTag);
		}
		return playerListTag;
	}

	private static NBTTagCompound writeSettings(String coalitionName) {
		NBTTagCompound coalitionSettings = new NBTTagCompound();
		coalitionSettings.setBoolean("disableAdvancementSync", ((Boolean)((Map)coalitionSettingsMap.get(coalitionName)).get("disableAdvancementSync")).booleanValue());
		coalitionSettings.setBoolean("enableFriendlyFire", ((Boolean)((Map)coalitionSettingsMap.get(coalitionName)).get("enableFriendlyFire")).booleanValue());
		return coalitionSettings;
	}
}