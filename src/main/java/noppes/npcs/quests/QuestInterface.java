package noppes.npcs.quests;

import noppes.npcs.api.handler.data.IQuestObjective;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class QuestInterface
{
    public int questId;
    
    public abstract void writeEntityToNBT(NBTTagCompound p0);
    
    public abstract void readEntityFromNBT(NBTTagCompound p0);
    
    public abstract boolean isCompleted(EntityPlayer p0);
    
    public abstract void handleComplete(EntityPlayer p0);
    
    public abstract IQuestObjective[] getObjectives(EntityPlayer p0);
}
