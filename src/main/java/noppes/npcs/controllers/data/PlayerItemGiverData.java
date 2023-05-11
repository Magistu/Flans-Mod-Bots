package noppes.npcs.controllers.data;

import noppes.npcs.roles.JobItemGiver;
import net.minecraft.nbt.NBTBase;
import java.util.Map;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;

public class PlayerItemGiverData
{
    private HashMap<Integer, Long> itemgivers;
    private HashMap<Integer, Integer> chained;
    
    public PlayerItemGiverData() {
        this.itemgivers = new HashMap<Integer, Long>();
        this.chained = new HashMap<Integer, Integer>();
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        this.chained = NBTTags.getIntegerIntegerMap(compound.getTagList("ItemGiverChained", 10));
        this.itemgivers = NBTTags.getIntegerLongMap(compound.getTagList("ItemGiversList", 10));
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        compound.setTag("ItemGiverChained", (NBTBase)NBTTags.nbtIntegerIntegerMap(this.chained));
        compound.setTag("ItemGiversList", (NBTBase)NBTTags.nbtIntegerLongMap(this.itemgivers));
    }
    
    public boolean hasInteractedBefore(JobItemGiver jobItemGiver) {
        return this.itemgivers.containsKey(jobItemGiver.itemGiverId);
    }
    
    public long getTime(JobItemGiver jobItemGiver) {
        return this.itemgivers.get(jobItemGiver.itemGiverId);
    }
    
    public void setTime(JobItemGiver jobItemGiver, long day) {
        this.itemgivers.put(jobItemGiver.itemGiverId, day);
    }
    
    public int getItemIndex(JobItemGiver jobItemGiver) {
        if (this.chained.containsKey(jobItemGiver.itemGiverId)) {
            return this.chained.get(jobItemGiver.itemGiverId);
        }
        return 0;
    }
    
    public void setItemIndex(JobItemGiver jobItemGiver, int i) {
        this.chained.put(jobItemGiver.itemGiverId, i);
    }
}
