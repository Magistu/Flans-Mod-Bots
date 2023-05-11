package com.magistumod.common.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class CoalitionDataManager
	extends WorldSavedData
{
	private static final String NAME = "coalitionsmod";
	
	public CoalitionDataManager() {
		super("coalitionsmod");
	}
	
	public CoalitionDataManager(String name) {
		super(name);
		markDirty();
	}
	
	public static CoalitionDataManager get(World world) {
		MapStorage storage = world.getMapStorage();
		CoalitionDataManager data = (CoalitionDataManager)storage.getOrLoadData(CoalitionDataManager.class, "coalitionsmod");
		if (data == null) {
			data = new CoalitionDataManager();
			world.setData("coalitionsmod", data);
		} 
		return data;
	}

	public void readFromNBT(@Nonnull NBTTagCompound nbt) {
		StorageHandler.readFromNBT(nbt);
	}

	@Nonnull
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
		return StorageHandler.writeToNBT(compound);
	}

	public void addCoalition(String name, EntityPlayer player) {
		List<UUID> tempList = new ArrayList<>();
		if (player != null) {
			tempList.add(player.getUniqueID());
			StorageHandler.uuidToCoalitionMap.put(player.getUniqueID(), name);
		}
		StorageHandler.coalitionToUuidsMap.put(name, tempList);
		Map<String, Boolean> newSettingsMap = new HashMap<>();
		newSettingsMap.put("disableAdvancementSync", false);
		newSettingsMap.put("enableFriendlyFire", false);
		StorageHandler.coalitionSettingsMap.put(name, newSettingsMap);
		
		markDirty();
	}
	
	public void addCoalition(String name) {
		List<UUID> tempList = new ArrayList<>();
		StorageHandler.coalitionToUuidsMap.put(name, tempList);
		Map<String, Boolean> newSettingsMap = new HashMap<>();
		newSettingsMap.put("disableAdvancementSync", false);
		newSettingsMap.put("enableFriendlyFire", false);
		StorageHandler.coalitionSettingsMap.put(name, newSettingsMap);
		
		markDirty();
	}

	public void addPlayer(String coalition, UUID uid) {
		((List<UUID>)StorageHandler.coalitionToUuidsMap.get(coalition)).add(uid);
		StorageHandler.uuidToCoalitionMap.put(uid, coalition);
		markDirty();
	}

	public void removePlayer(String coalition, UUID uid) {
		((List)StorageHandler.coalitionToUuidsMap.get(coalition)).remove(uid);
		StorageHandler.uuidToCoalitionMap.remove(uid);
		markDirty();
	}

	public void removeCoalition(String name) {
		for (UUID id : StorageHandler.coalitionToUuidsMap.get(name)) {
			StorageHandler.uuidToCoalitionMap.remove(id);
		}
		StorageHandler.coalitionToUuidsMap.remove(name);
		StorageHandler.coalitionSettingsMap.remove(name);
		
		markDirty();
	}
}
