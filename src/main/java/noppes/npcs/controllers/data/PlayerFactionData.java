package noppes.npcs.controllers.data;

import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.event.PlayerEvent;
import net.minecraft.entity.Entity;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.FactionController;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;

public class PlayerFactionData
{
    public HashMap<Integer, Integer> factionData;
    
    public PlayerFactionData() {
        this.factionData = new HashMap<Integer, Integer>();
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        HashMap<Integer, Integer> factionData = new HashMap<Integer, Integer>();
        if (compound == null) {
            return;
        }
        NBTTagList list = compound.getTagList("FactionData", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
            factionData.put(nbttagcompound.getInteger("Faction"), nbttagcompound.getInteger("Points"));
        }
        this.factionData = factionData;
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int faction : this.factionData.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Faction", faction);
            nbttagcompound.setInteger("Points", (int)this.factionData.get(faction));
            list.appendTag((NBTBase)nbttagcompound);
        }
        compound.setTag("FactionData", (NBTBase)list);
    }
    
    public int getFactionPoints(EntityPlayer player, int factionId) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null) {
            return 0;
        }
        if (!this.factionData.containsKey(factionId)) {
            if (player.world.isRemote) {
                return faction.defaultPoints;
            }
            PlayerScriptData handler = PlayerData.get(player).scriptData;
            PlayerWrapper wrapper = (PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player);
            PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, faction.defaultPoints, true);
            EventHooks.OnPlayerFactionChange(handler, event);
            this.factionData.put(factionId, event.points);
        }
        return this.factionData.get(factionId);
    }
    
    public void increasePoints(EntityPlayer player, int factionId, int points) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null || player == null || player.world.isRemote) {
            return;
        }
        PlayerScriptData handler = PlayerData.get(player).scriptData;
        PlayerWrapper wrapper = (PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player);
        if (!this.factionData.containsKey(factionId)) {
            PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, faction.defaultPoints, true);
            EventHooks.OnPlayerFactionChange(handler, event);
            this.factionData.put(factionId, event.points);
        }
        PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, points, false);
        EventHooks.OnPlayerFactionChange(handler, event);
        this.factionData.put(factionId, this.factionData.get(factionId) + points);
    }
    
    public NBTTagCompound getPlayerGuiData() {
        NBTTagCompound compound = new NBTTagCompound();
        this.saveNBTData(compound);
        NBTTagList list = new NBTTagList();
        for (int id : this.factionData.keySet()) {
            Faction faction = FactionController.instance.getFaction(id);
            if (faction != null) {
                if (faction.hideFaction) {
                    continue;
                }
                NBTTagCompound com = new NBTTagCompound();
                faction.writeNBT(com);
                list.appendTag((NBTBase)com);
            }
        }
        compound.setTag("FactionList", (NBTBase)list);
        return compound;
    }
}
