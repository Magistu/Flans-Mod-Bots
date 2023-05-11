package noppes.npcs.quests;

import net.minecraft.util.text.translation.I18n;
import noppes.npcs.api.CustomNPCsException;
import java.util.List;
import java.util.ArrayList;
import noppes.npcs.api.handler.data.IQuestObjective;
import java.util.Iterator;
import java.util.HashMap;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import java.util.Map;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.TreeMap;

public class QuestKill extends QuestInterface
{
    public TreeMap<String, Integer> targets;
    
    public QuestKill() {
        this.targets = new TreeMap<String, Integer>();
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.targets = new TreeMap<String, Integer>(NBTTags.getStringIntegerMap(compound.getTagList("QuestDialogs", 10)));
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("QuestDialogs", (NBTBase)NBTTags.nbtStringIntegerMap(this.targets));
    }
    
    @Override
    public boolean isCompleted(EntityPlayer player) {
        PlayerQuestData playerdata = PlayerData.get(player).questData;
        QuestData data = playerdata.activeQuests.get(this.questId);
        if (data == null) {
            return false;
        }
        HashMap<String, Integer> killed = this.getKilled(data);
        if (killed.size() != this.targets.size()) {
            return false;
        }
        for (String entity : killed.keySet()) {
            if (!this.targets.containsKey(entity) || this.targets.get(entity) > killed.get(entity)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void handleComplete(EntityPlayer player) {
    }
    
    public HashMap<String, Integer> getKilled(QuestData data) {
        return NBTTags.getStringIntegerMap(data.extraData.getTagList("Killed", 10));
    }
    
    public void setKilled(QuestData data, HashMap<String, Integer> killed) {
        data.extraData.setTag("Killed", (NBTBase)NBTTags.nbtStringIntegerMap(killed));
    }
    
    @Override
    public IQuestObjective[] getObjectives(EntityPlayer player) {
        List<IQuestObjective> list = new ArrayList<IQuestObjective>();
        for (Map.Entry<String, Integer> entry : this.targets.entrySet()) {
            list.add(new QuestKillObjective(player, entry.getKey(), entry.getValue()));
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }
    
    class QuestKillObjective implements IQuestObjective
    {
        private EntityPlayer player;
        private String entity;
        private int amount;
        
        public QuestKillObjective(EntityPlayer player, String entity, int amount) {
            this.player = player;
            this.entity = entity;
            this.amount = amount;
        }
        
        @Override
        public int getProgress() {
            PlayerData data = PlayerData.get(this.player);
            PlayerQuestData playerdata = data.questData;
            QuestData questdata = playerdata.activeQuests.get(QuestKill.this.questId);
            HashMap<String, Integer> killed = QuestKill.this.getKilled(questdata);
            if (!killed.containsKey(this.entity)) {
                return 0;
            }
            return killed.get(this.entity);
        }
        
        @Override
        public void setProgress(int progress) {
            if (progress < 0 || progress > this.amount) {
                throw new CustomNPCsException("Progress has to be between 0 and " + this.amount, new Object[0]);
            }
            PlayerData data = PlayerData.get(this.player);
            PlayerQuestData playerdata = data.questData;
            QuestData questdata = playerdata.activeQuests.get(QuestKill.this.questId);
            HashMap<String, Integer> killed = QuestKill.this.getKilled(questdata);
            if (killed.containsKey(this.entity) && killed.get(this.entity) == progress) {
                return;
            }
            killed.put(this.entity, progress);
            QuestKill.this.setKilled(questdata, killed);
            data.questData.checkQuestCompletion(this.player, 2);
            data.questData.checkQuestCompletion(this.player, 4);
            data.updateClient = true;
        }
        
        @Override
        public int getMaxProgress() {
            return this.amount;
        }
        
        @Override
        public boolean isCompleted() {
            return this.getProgress() >= this.amount;
        }
        
        @Override
        public String getText() {
            String name = "entity." + this.entity + ".name";
            String transName = I18n.translateToLocal(name);
            if (name.equals(transName)) {
                transName = this.entity;
            }
            return transName + ": " + this.getProgress() + "/" + this.getMaxProgress();
        }
    }
}
