package noppes.npcs.roles;

import net.minecraft.entity.Entity;
import java.util.Collection;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.item.Item;
import noppes.npcs.controllers.data.PlayerItemGiverData;
import noppes.npcs.controllers.data.Line;
import net.minecraft.item.ItemStack;
import java.util.Vector;
import noppes.npcs.controllers.data.PlayerData;
import java.util.Iterator;
import net.minecraft.nbt.NBTTagList;
import java.util.HashMap;
import noppes.npcs.controllers.GlobalDataController;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.controllers.data.Availability;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import noppes.npcs.NpcMiscInventory;

public class JobItemGiver extends JobInterface
{
    public int cooldownType;
    public int givingMethod;
    public int cooldown;
    public NpcMiscInventory inventory;
    public int itemGiverId;
    public List<String> lines;
    private int ticks;
    private List<EntityPlayer> recentlyChecked;
    private List<EntityPlayer> toCheck;
    public Availability availability;
    
    public JobItemGiver(EntityNPCInterface npc) {
        super(npc);
        this.cooldownType = 0;
        this.givingMethod = 0;
        this.cooldown = 10;
        this.itemGiverId = 0;
        this.lines = new ArrayList<String>();
        this.ticks = 10;
        this.recentlyChecked = new ArrayList<EntityPlayer>();
        this.availability = new Availability();
        this.inventory = new NpcMiscInventory(9);
        this.lines.add("Have these items {player}");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("igCooldownType", this.cooldownType);
        nbttagcompound.setInteger("igGivingMethod", this.givingMethod);
        nbttagcompound.setInteger("igCooldown", this.cooldown);
        nbttagcompound.setInteger("ItemGiverId", this.itemGiverId);
        nbttagcompound.setTag("igLines", (NBTBase)NBTTags.nbtStringList(this.lines));
        nbttagcompound.setTag("igJobInventory", (NBTBase)this.inventory.getToNBT());
        nbttagcompound.setTag("igAvailability", (NBTBase)this.availability.writeToNBT(new NBTTagCompound()));
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.itemGiverId = nbttagcompound.getInteger("ItemGiverId");
        this.cooldownType = nbttagcompound.getInteger("igCooldownType");
        this.givingMethod = nbttagcompound.getInteger("igGivingMethod");
        this.cooldown = nbttagcompound.getInteger("igCooldown");
        this.lines = NBTTags.getStringList(nbttagcompound.getTagList("igLines", 10));
        this.inventory.setFromNBT(nbttagcompound.getCompoundTag("igJobInventory"));
        if (this.itemGiverId == 0 && GlobalDataController.instance != null) {
            this.itemGiverId = GlobalDataController.instance.incrementItemGiverId();
        }
        this.availability.readFromNBT(nbttagcompound.getCompoundTag("igAvailability"));
    }
    
    public NBTTagList newHashMapNBTList(HashMap<String, Long> lines) {
        NBTTagList nbttaglist = new NBTTagList();
        HashMap<String, Long> lines2 = lines;
        for (String s : lines2.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("Line", s);
            nbttagcompound.setLong("Time", (long)lines.get(s));
            nbttaglist.appendTag((NBTBase)nbttagcompound);
        }
        return nbttaglist;
    }
    
    public HashMap<String, Long> getNBTLines(NBTTagList tagList) {
        HashMap<String, Long> map = new HashMap<String, Long>();
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            String line = nbttagcompound.getString("Line");
            long time = nbttagcompound.getLong("Time");
            map.put(line, time);
        }
        return map;
    }
    
    private boolean giveItems(EntityPlayer player) {
        PlayerItemGiverData data = PlayerData.get(player).itemgiverData;
        if (!this.canPlayerInteract(data)) {
            return false;
        }
        Vector<ItemStack> items = new Vector<ItemStack>();
        Vector<ItemStack> toGive = new Vector<ItemStack>();
        for (ItemStack is : this.inventory.items) {
            if (!is.isEmpty()) {
                items.add(is.copy());
            }
        }
        if (items.isEmpty()) {
            return false;
        }
        if (this.isAllGiver()) {
            toGive = items;
        }
        else if (this.isRemainingGiver()) {
            for (ItemStack is : items) {
                if (!this.playerHasItem(player, is.getItem())) {
                    toGive.add(is);
                }
            }
        }
        else if (this.isRandomGiver()) {
            toGive.add(items.get(this.npc.world.rand.nextInt(items.size())).copy());
        }
        else if (this.isGiverWhenNotOwnedAny()) {
            boolean ownsItems = false;
            for (ItemStack is2 : items) {
                if (this.playerHasItem(player, is2.getItem())) {
                    ownsItems = true;
                    break;
                }
            }
            if (ownsItems) {
                return false;
            }
            toGive = items;
        }
        else if (this.isChainedGiver()) {
            int itemIndex = data.getItemIndex(this);
            int i = 0;
            for (ItemStack item : this.inventory.items) {
                if (i == itemIndex) {
                    toGive.add(item);
                    break;
                }
                ++i;
            }
        }
        if (toGive.isEmpty()) {
            return false;
        }
        if (this.givePlayerItems(player, toGive)) {
            if (!this.lines.isEmpty()) {
                this.npc.say(player, new Line(this.lines.get(this.npc.getRNG().nextInt(this.lines.size()))));
            }
            if (this.isDaily()) {
                data.setTime(this, this.getDay());
            }
            else {
                data.setTime(this, System.currentTimeMillis());
            }
            if (this.isChainedGiver()) {
                data.setItemIndex(this, (data.getItemIndex(this) + 1) % this.inventory.items.size());
            }
            return true;
        }
        return false;
    }
    
    private int getDay() {
        return (int)(this.npc.world.getTotalWorldTime() / 24000L);
    }
    
    private boolean canPlayerInteract(PlayerItemGiverData data) {
        if (this.inventory.items.isEmpty()) {
            return false;
        }
        if (this.isOnTimer()) {
            return !data.hasInteractedBefore(this) || data.getTime(this) + this.cooldown * 1000 < System.currentTimeMillis();
        }
        if (this.isGiveOnce()) {
            return !data.hasInteractedBefore(this);
        }
        return this.isDaily() && (!data.hasInteractedBefore(this) || this.getDay() > data.getTime(this));
    }
    
    private boolean givePlayerItems(EntityPlayer player, Vector<ItemStack> toGive) {
        if (toGive.isEmpty()) {
            return false;
        }
        if (this.freeInventorySlots(player) < toGive.size()) {
            return false;
        }
        for (ItemStack is : toGive) {
            this.npc.givePlayerItem(player, is);
        }
        return true;
    }
    
    private boolean playerHasItem(EntityPlayer player, Item item) {
        for (ItemStack is : player.inventory.mainInventory) {
            if (!is.isEmpty() && is.getItem() == item) {
                return true;
            }
        }
        for (ItemStack is : player.inventory.armorInventory) {
            if (!is.isEmpty() && is.getItem() == item) {
                return true;
            }
        }
        return false;
    }
    
    private int freeInventorySlots(EntityPlayer player) {
        int i = 0;
        for (ItemStack is : player.inventory.mainInventory) {
            if (NoppesUtilServer.IsItemStackNull(is)) {
                ++i;
            }
        }
        return i;
    }
    
    private boolean isRandomGiver() {
        return this.givingMethod == 0;
    }
    
    private boolean isAllGiver() {
        return this.givingMethod == 1;
    }
    
    private boolean isRemainingGiver() {
        return this.givingMethod == 2;
    }
    
    private boolean isGiverWhenNotOwnedAny() {
        return this.givingMethod == 3;
    }
    
    private boolean isChainedGiver() {
        return this.givingMethod == 4;
    }
    
    public boolean isOnTimer() {
        return this.cooldownType == 0;
    }
    
    private boolean isGiveOnce() {
        return this.cooldownType == 1;
    }
    
    private boolean isDaily() {
        return this.cooldownType == 2;
    }
    
    @Override
    public boolean aiShouldExecute() {
        if (this.npc.isAttacking()) {
            return false;
        }
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.ticks = 10;
        (this.toCheck = (List<EntityPlayer>)this.npc.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.npc.getEntityBoundingBox().grow(3.0, 3.0, 3.0))).removeAll(this.recentlyChecked);
        List<EntityPlayer> listMax = (List<EntityPlayer>)this.npc.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.npc.getEntityBoundingBox().grow(10.0, 10.0, 10.0));
        this.recentlyChecked.retainAll(listMax);
        this.recentlyChecked.addAll(this.toCheck);
        return this.toCheck.size() > 0;
    }
    
    @Override
    public boolean aiContinueExecute() {
        return false;
    }
    
    @Override
    public void aiStartExecuting() {
        for (EntityPlayer player : this.toCheck) {
            if (this.npc.canSee((Entity)player) && this.availability.isAvailable(player)) {
                this.recentlyChecked.add(player);
                this.interact(player);
            }
        }
    }
    
    @Override
    public void killed() {
    }
    
    private boolean interact(EntityPlayer player) {
        if (!this.giveItems(player)) {
            this.npc.say(player, this.npc.advanced.getInteractLine());
        }
        return true;
    }
    
    @Override
    public void delete() {
    }
}
