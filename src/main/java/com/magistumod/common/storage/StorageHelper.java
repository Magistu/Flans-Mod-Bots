package com.magistumod.common.storage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StorageHelper
{
	public static String getCoalition(UUID playerId)
	{
		return StorageHandler.uuidToCoalitionMap.get(playerId);
	}
	
	public static UUID getCoalitionOwner(String coalitionName) 
	{
		return ((List<UUID>)StorageHandler.coalitionToUuidsMap.get(coalitionName)).get(0);
	}
	
	public static List<UUID> getCoalitionPlayers(String coalitionName) 
	{
		return StorageHandler.coalitionToUuidsMap.get(coalitionName);
	}
	
	public static Set<String> getCoalitionSet() 
	{
		return StorageHandler.coalitionSettingsMap.keySet();
	}
	
	public static boolean getCoalitionSetting(String coalitionName, String setting) 
	{
		return ((Boolean)((Map)StorageHandler.coalitionSettingsMap.get(coalitionName)).get(setting)).booleanValue();
	}
	
	public static void setCoalitionSetting(String coalitionName, String setting, boolean value) 
	{
		((Map<String, Boolean>)StorageHandler.coalitionSettingsMap.get(coalitionName)).put(setting, Boolean.valueOf(value));
	}
	
	public static boolean isPlayerInCoalition(UUID playerId) 
	{
		return StorageHandler.uuidToCoalitionMap.containsKey(playerId);
	}
	
	public static boolean doesCoalitionExist(String coalitionName) 
	{
		return (coalitionName == null) ? false : StorageHandler.coalitionToUuidsMap.containsKey(coalitionName);
	}
}