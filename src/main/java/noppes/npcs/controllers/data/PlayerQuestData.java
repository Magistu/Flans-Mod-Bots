package noppes.npcs.controllers.data;

import noppes.npcs.quests.QuestInterface;
import noppes.npcs.EventHooks;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.controllers.QuestController;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;

public class PlayerQuestData
{
    public HashMap<Integer, QuestData> activeQuests;
    public HashMap<Integer, Long> finishedQuests;
    
    public PlayerQuestData() {
        this.activeQuests = new HashMap<Integer, QuestData>();
        this.finishedQuests = new HashMap<Integer, Long>();
    }
    
    public void loadNBTData(NBTTagCompound mainCompound) {
        if (mainCompound == null) {
            return;
        }
        NBTTagCompound compound = mainCompound.getCompoundTag("QuestData");
        NBTTagList list = compound.getTagList("CompletedQuests", 10);
        if (list != null) {
            HashMap<Integer, Long> finishedQuests = new HashMap<Integer, Long>();
            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
                finishedQuests.put(nbttagcompound.getInteger("Quest"), nbttagcompound.getLong("Date"));
            }
            this.finishedQuests = finishedQuests;
        }
        NBTTagList list2 = compound.getTagList("ActiveQuests", 10);
        if (list2 != null) {
            HashMap<Integer, QuestData> activeQuests = new HashMap<Integer, QuestData>();
            for (int j = 0; j < list2.tagCount(); ++j) {
                NBTTagCompound nbttagcompound2 = list2.getCompoundTagAt(j);
                int id = nbttagcompound2.getInteger("Quest");
                Quest quest = QuestController.instance.quests.get(id);
                if (quest != null) {
                    QuestData data = new QuestData(quest);
                    data.readEntityFromNBT(nbttagcompound2);
                    activeQuests.put(id, data);
                }
            }
            this.activeQuests = activeQuests;
        }
    }
    
    public void saveNBTData(NBTTagCompound maincompound) {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (int quest : this.finishedQuests.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Quest", quest);
            nbttagcompound.setLong("Date", (long)this.finishedQuests.get(quest));
            list.appendTag((NBTBase)nbttagcompound);
        }
        compound.setTag("CompletedQuests", (NBTBase)list);
        NBTTagList list2 = new NBTTagList();
        for (int quest2 : this.activeQuests.keySet()) {
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            nbttagcompound2.setInteger("Quest", quest2);
            this.activeQuests.get(quest2).writeEntityToNBT(nbttagcompound2);
            list2.appendTag((NBTBase)nbttagcompound2);
        }
        compound.setTag("ActiveQuests", (NBTBase)list2);
        maincompound.setTag("QuestData", (NBTBase)compound);
    }
    
    public QuestData getQuestCompletion(EntityPlayer player, EntityNPCInterface npc) {
        for (QuestData data : this.activeQuests.values()) {
            Quest quest = data.quest;
            if (quest != null && quest.completion == EnumQuestCompletion.Npc && quest.completerNpc.equals(npc.getName()) && quest.questInterface.isCompleted(player)) {
                return data;
            }
        }
        return null;
    }
    
    public boolean checkQuestCompletion(EntityPlayer player, int type) {
        boolean bo = false;
        for (QuestData data : this.activeQuests.values()) {
            if (data.quest.type != type && type >= 0) {
                continue;
            }
            QuestInterface inter = data.quest.questInterface;
            if (inter.isCompleted(player)) {
                if (data.isCompleted) {
                    continue;
                }
                if (!data.quest.complete(player, data)) {
                    Server.sendData((EntityPlayerMP)player, EnumPacketClient.MESSAGE, "quest.completed", data.quest.title, 2);
                    Server.sendData((EntityPlayerMP)player, EnumPacketClient.CHAT, "quest.completed", ": ", data.quest.title);
                }
                data.isCompleted = true;
                bo = true;
                EventHooks.onQuestFinished(PlayerData.get(player).scriptData, data.quest);
            }
            else {
                data.isCompleted = false;
            }
        }
        return bo;
    }
}
