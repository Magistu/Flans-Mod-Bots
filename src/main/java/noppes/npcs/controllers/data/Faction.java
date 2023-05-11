package noppes.npcs.controllers.data;

import noppes.npcs.controllers.FactionController;
import noppes.npcs.api.CustomNPCsException;
import java.util.Iterator;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import java.util.Collection;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashSet;
import noppes.npcs.api.handler.data.IFaction;

public class Faction implements IFaction
{
    public String name;
    public int color;
    public HashSet<Integer> attackFactions;
    public int id;
    public int neutralPoints;
    public int friendlyPoints;
    public int defaultPoints;
    public boolean hideFaction;
    public boolean getsAttacked;
    
    public Faction() {
        this.name = "";
        this.color = Integer.parseInt("FF00", 16);
        this.id = -1;
        this.neutralPoints = 500;
        this.friendlyPoints = 1500;
        this.defaultPoints = 1000;
        this.hideFaction = false;
        this.getsAttacked = false;
        this.attackFactions = new HashSet<Integer>();
    }
    
    public Faction(int id, String name, int color, int defaultPoints) {
        this.name = "";
        this.color = Integer.parseInt("FF00", 16);
        this.id = -1;
        this.neutralPoints = 500;
        this.friendlyPoints = 1500;
        this.defaultPoints = 1000;
        this.hideFaction = false;
        this.getsAttacked = false;
        this.name = name;
        this.color = color;
        this.defaultPoints = defaultPoints;
        this.id = id;
        this.attackFactions = new HashSet<Integer>();
    }
    
    public static String formatName(String name) {
        name = name.toLowerCase().trim();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    
    public void readNBT(NBTTagCompound compound) {
        this.name = compound.getString("Name");
        this.color = compound.getInteger("Color");
        this.id = compound.getInteger("Slot");
        this.neutralPoints = compound.getInteger("NeutralPoints");
        this.friendlyPoints = compound.getInteger("FriendlyPoints");
        this.defaultPoints = compound.getInteger("DefaultPoints");
        this.hideFaction = compound.getBoolean("HideFaction");
        this.getsAttacked = compound.getBoolean("GetsAttacked");
        this.attackFactions = NBTTags.getIntegerSet(compound.getTagList("AttackFactions", 10));
    }
    
    public NBTTagCompound writeNBT(NBTTagCompound compound) {
        compound.setInteger("Slot", this.id);
        compound.setString("Name", this.name);
        compound.setInteger("Color", this.color);
        compound.setInteger("NeutralPoints", this.neutralPoints);
        compound.setInteger("FriendlyPoints", this.friendlyPoints);
        compound.setInteger("DefaultPoints", this.defaultPoints);
        compound.setBoolean("HideFaction", this.hideFaction);
        compound.setBoolean("GetsAttacked", this.getsAttacked);
        compound.setTag("AttackFactions", (NBTBase)NBTTags.nbtIntegerCollection(this.attackFactions));
        return compound;
    }
    
    public boolean isFriendlyToPlayer(EntityPlayer player) {
        PlayerFactionData data = PlayerData.get(player).factionData;
        return data.getFactionPoints(player, this.id) >= this.friendlyPoints;
    }
    
    public boolean isAggressiveToPlayer(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return false;
        }
        PlayerFactionData data = PlayerData.get(player).factionData;
        return data.getFactionPoints(player, this.id) < this.neutralPoints;
    }
    
    public boolean isNeutralToPlayer(EntityPlayer player) {
        PlayerFactionData data = PlayerData.get(player).factionData;
        int points = data.getFactionPoints(player, this.id);
        return points >= this.neutralPoints && points < this.friendlyPoints;
    }
    
    public boolean isAggressiveToNpc(EntityNPCInterface entity) {
        return this.attackFactions.contains(entity.faction.id);
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public int getDefaultPoints() {
        return this.defaultPoints;
    }
    
    @Override
    public int getColor() {
        return this.color;
    }
    
    @Override
    public int playerStatus(IPlayer player) {
        PlayerFactionData data = PlayerData.get(player.getMCEntity()).factionData;
        int points = data.getFactionPoints(player.getMCEntity(), this.id);
        if (points >= this.friendlyPoints) {
            return 1;
        }
        if (points < this.neutralPoints) {
            return -1;
        }
        return 0;
    }
    
    @Override
    public boolean hostileToNpc(ICustomNpc npc) {
        return this.attackFactions.contains(npc.getFaction().getId());
    }
    
    @Override
    public void setDefaultPoints(int points) {
        this.defaultPoints = points;
    }
    
    @Override
    public boolean hostileToFaction(int factionId) {
        return this.attackFactions.contains(factionId);
    }
    
    @Override
    public int[] getHostileList() {
        int[] a = new int[this.attackFactions.size()];
        int i = 0;
        for (Integer val : this.attackFactions) {
            a[i++] = val;
        }
        return a;
    }
    
    @Override
    public void addHostile(int id) {
        if (this.attackFactions.contains(id)) {
            throw new CustomNPCsException("Faction " + this.id + " is already hostile to " + id, new Object[0]);
        }
        this.attackFactions.add(id);
    }
    
    @Override
    public void removeHostile(int id) {
        this.attackFactions.remove(id);
    }
    
    @Override
    public boolean hasHostile(int id) {
        return this.attackFactions.contains(id);
    }
    
    @Override
    public boolean getIsHidden() {
        return this.hideFaction;
    }
    
    @Override
    public void setIsHidden(boolean bo) {
        this.hideFaction = bo;
    }
    
    @Override
    public boolean getAttackedByMobs() {
        return this.getsAttacked;
    }
    
    @Override
    public void setAttackedByMobs(boolean bo) {
        this.getsAttacked = bo;
    }
    
    @Override
    public void save() {
        FactionController.instance.saveFaction(this);
    }
}
