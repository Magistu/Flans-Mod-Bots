package noppes.npcs.controllers;

import java.util.Vector;
import java.util.Iterator;
import noppes.npcs.quests.QuestDialog;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.EventHooks;
import noppes.npcs.LogWriter;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerQuestController
{
    public static boolean hasActiveQuests(EntityPlayer player) {
        PlayerQuestData data = PlayerData.get(player).questData;
        return !data.activeQuests.isEmpty();
    }
    
    public static boolean isQuestActive(EntityPlayer player, int quest) {
        PlayerQuestData data = PlayerData.get(player).questData;
        return data.activeQuests.containsKey(quest);
    }
    
    public static boolean isQuestCompleted(EntityPlayer player, int quest) {
        PlayerQuestData data = PlayerData.get(player).questData;
        QuestData q = data.activeQuests.get(quest);
        return q != null && q.isCompleted;
    }
    
    public static boolean isQuestFinished(EntityPlayer player, int questid) {
        PlayerQuestData data = PlayerData.get(player).questData;
        return data.finishedQuests.containsKey(questid);
    }
    
    public static boolean canQuestBeAccepted(EntityPlayer player, int questId) {
        Quest quest = QuestController.instance.quests.get(questId);
        if (quest == null) {
            return false;
        }
        PlayerQuestData data = PlayerData.get(player).questData;
        if (data.activeQuests.containsKey(quest.id)) {
            return false;
        }
        if (!data.finishedQuests.containsKey(quest.id) || quest.repeat == EnumQuestRepeat.REPEATABLE) {
            return true;
        }
        if (quest.repeat == EnumQuestRepeat.NONE) {
            return false;
        }
        long questTime = data.finishedQuests.get(quest.id);
        if (quest.repeat == EnumQuestRepeat.MCDAILY) {
            return player.world.getTotalWorldTime() - questTime >= 24000L;
        }
        if (quest.repeat == EnumQuestRepeat.MCWEEKLY) {
            return player.world.getTotalWorldTime() - questTime >= 168000L;
        }
        if (quest.repeat == EnumQuestRepeat.RLDAILY) {
            return System.currentTimeMillis() - questTime >= 86400000L;
        }
        return quest.repeat == EnumQuestRepeat.RLWEEKLY && System.currentTimeMillis() - questTime >= 604800000L;
    }
    
    public static void addActiveQuest(Quest quest, EntityPlayer player) {
        PlayerData playerdata = PlayerData.get(player);
        LogWriter.debug("AddActiveQuest: " + quest.title + " + " + playerdata);
        PlayerQuestData data = playerdata.questData;
        if (playerdata.scriptData.getPlayer().canQuestBeAccepted(quest.id)) {
            if (EventHooks.onQuestStarted(playerdata.scriptData, quest)) {
                return;
            }
            data.activeQuests.put(quest.id, new QuestData(quest));
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.MESSAGE, "quest.newquest", quest.title, 2);
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.CHAT, "quest.newquest", ": ", quest.title);
            playerdata.updateClient = true;
        }
    }
    
    public static void setQuestFinished(Quest quest, EntityPlayer player) {
        PlayerData playerdata = PlayerData.get(player);
        PlayerQuestData data = playerdata.questData;
        data.activeQuests.remove(quest.id);
        if (quest.repeat == EnumQuestRepeat.RLDAILY || quest.repeat == EnumQuestRepeat.RLWEEKLY) {
            data.finishedQuests.put(quest.id, System.currentTimeMillis());
        }
        else {
            data.finishedQuests.put(quest.id, player.world.getTotalWorldTime());
        }
        if (quest.repeat != EnumQuestRepeat.NONE && quest.type == 1) {
            QuestDialog questdialog = (QuestDialog)quest.questInterface;
            for (int dialog : questdialog.dialogs.values()) {
                playerdata.dialogData.dialogsRead.remove(dialog);
            }
        }
        playerdata.updateClient = true;
    }
    
    public static Vector<Quest> getActiveQuests(EntityPlayer player) {
        Vector<Quest> quests = new Vector<Quest>();
        PlayerQuestData data = PlayerData.get(player).questData;
        for (QuestData questdata : data.activeQuests.values()) {
            if (questdata.quest == null) {
                continue;
            }
            quests.add(questdata.quest);
        }
        return quests;
    }
}
