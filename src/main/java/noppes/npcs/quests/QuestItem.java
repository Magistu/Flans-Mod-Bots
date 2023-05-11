package noppes.npcs.quests;

import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.util.ValueUtil;
import java.util.ArrayList;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.NoppesUtilServer;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import noppes.npcs.NoppesUtilPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NpcMiscInventory;

public class QuestItem extends QuestInterface
{
    public NpcMiscInventory items;
    public boolean leaveItems;
    public boolean ignoreDamage;
    public boolean ignoreNBT;
    
    public QuestItem() {
        this.items = new NpcMiscInventory(3);
        this.leaveItems = false;
        this.ignoreDamage = false;
        this.ignoreNBT = false;
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.items.setFromNBT(compound.getCompoundTag("Items"));
        this.leaveItems = compound.getBoolean("LeaveItems");
        this.ignoreDamage = compound.getBoolean("IgnoreDamage");
        this.ignoreNBT = compound.getBoolean("IgnoreNBT");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("Items", (NBTBase)this.items.getToNBT());
        compound.setBoolean("LeaveItems", this.leaveItems);
        compound.setBoolean("IgnoreDamage", this.ignoreDamage);
        compound.setBoolean("IgnoreNBT", this.ignoreNBT);
    }
    
    @Override
    public boolean isCompleted(EntityPlayer player) {
        List<ItemStack> questItems = NoppesUtilPlayer.countStacks((IInventory)this.items, this.ignoreDamage, this.ignoreNBT);
        for (ItemStack reqItem : questItems) {
            if (!NoppesUtilPlayer.compareItems(player, reqItem, this.ignoreDamage, this.ignoreNBT)) {
                return false;
            }
        }
        return true;
    }
    
    public Map<ItemStack, Integer> getProgressSet(EntityPlayer player) {
        HashMap<ItemStack, Integer> map = new HashMap<ItemStack, Integer>();
        List<ItemStack> questItems = NoppesUtilPlayer.countStacks((IInventory)this.items, this.ignoreDamage, this.ignoreNBT);
        for (ItemStack item : questItems) {
            if (NoppesUtilServer.IsItemStackNull(item)) {
                continue;
            }
            map.put(item, 0);
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack item = player.inventory.getStackInSlot(i);
            if (!NoppesUtilServer.IsItemStackNull(item)) {
                for (Map.Entry<ItemStack, Integer> questItem : map.entrySet()) {
                    if (NoppesUtilPlayer.compareItems(questItem.getKey(), item, this.ignoreDamage, this.ignoreNBT)) {
                        map.put(questItem.getKey(), questItem.getValue() + item.getCount());
                    }
                }
            }
        }
        return map;
    }
    
    @Override
    public void handleComplete(EntityPlayer player) {
        if (this.leaveItems) {
            return;
        }
        for (ItemStack questitem : this.items.items) {
            if (questitem.isEmpty()) {
                continue;
            }
            int stacksize = questitem.getCount();
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack item = player.inventory.getStackInSlot(i);
                if (!NoppesUtilServer.IsItemStackNull(item)) {
                    if (NoppesUtilPlayer.compareItems(item, questitem, this.ignoreDamage, this.ignoreNBT)) {
                        int size = item.getCount();
                        if (stacksize - size >= 0) {
                            player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                            item.splitStack(size);
                        }
                        else {
                            item.splitStack(stacksize);
                        }
                        stacksize -= size;
                        if (stacksize <= 0) {
                            break;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public IQuestObjective[] getObjectives(EntityPlayer player) {
        List<IQuestObjective> list = new ArrayList<IQuestObjective>();
        List<ItemStack> questItems = NoppesUtilPlayer.countStacks((IInventory)this.items, this.ignoreDamage, this.ignoreNBT);
        for (ItemStack stack : questItems) {
            if (!stack.isEmpty()) {
                list.add(new QuestItemObjective(player, stack));
            }
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }
    
    class QuestItemObjective implements IQuestObjective
    {
        private EntityPlayer player;
        private ItemStack questItem;
        
        public QuestItemObjective(EntityPlayer player, ItemStack item) {
            this.player = player;
            this.questItem = item;
        }
        
        @Override
        public int getProgress() {
            int count = 0;
            for (int i = 0; i < this.player.inventory.getSizeInventory(); ++i) {
                ItemStack item = this.player.inventory.getStackInSlot(i);
                if (!NoppesUtilServer.IsItemStackNull(item)) {
                    if (NoppesUtilPlayer.compareItems(this.questItem, item, QuestItem.this.ignoreDamage, QuestItem.this.ignoreNBT)) {
                        count += item.getCount();
                    }
                }
            }
            return ValueUtil.CorrectInt(count, 0, this.questItem.getCount());
        }
        
        @Override
        public void setProgress(int progress) {
            throw new CustomNPCsException("Cant set the progress of ItemQuests", new Object[0]);
        }
        
        @Override
        public int getMaxProgress() {
            return this.questItem.getCount();
        }
        
        @Override
        public boolean isCompleted() {
            return NoppesUtilPlayer.compareItems(this.player, this.questItem, QuestItem.this.ignoreDamage, QuestItem.this.ignoreNBT);
        }
        
        @Override
        public String getText() {
            return this.questItem.getDisplayName() + ": " + this.getProgress() + "/" + this.getMaxProgress();
        }
    }
}
