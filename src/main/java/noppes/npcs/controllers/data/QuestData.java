package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class QuestData
{
    public Quest quest;
    public boolean isCompleted;
    public NBTTagCompound extraData;
    
    public QuestData(Quest quest) {
        this.extraData = new NBTTagCompound();
        this.quest = quest;
    }
    
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("QuestCompleted", this.isCompleted);
        nbttagcompound.setTag("ExtraData", (NBTBase)this.extraData);
    }
    
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        this.isCompleted = nbttagcompound.getBoolean("QuestCompleted");
        this.extraData = nbttagcompound.getCompoundTag("ExtraData");
    }
}
