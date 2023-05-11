package noppes.npcs.quests;

import net.minecraft.util.text.translation.I18n;
import noppes.npcs.api.CustomNPCsException;
import java.util.List;
import java.util.ArrayList;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class QuestLocation extends QuestInterface
{
    public String location;
    public String location2;
    public String location3;
    
    public QuestLocation() {
        this.location = "";
        this.location2 = "";
        this.location3 = "";
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.location = compound.getString("QuestLocation");
        this.location2 = compound.getString("QuestLocation2");
        this.location3 = compound.getString("QuestLocation3");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setString("QuestLocation", this.location);
        compound.setString("QuestLocation2", this.location2);
        compound.setString("QuestLocation3", this.location3);
    }
    
    @Override
    public boolean isCompleted(EntityPlayer player) {
        PlayerQuestData playerdata = PlayerData.get(player).questData;
        QuestData data = playerdata.activeQuests.get(this.questId);
        return data != null && this.getFound(data, 0);
    }
    
    @Override
    public void handleComplete(EntityPlayer player) {
    }
    
    public boolean getFound(QuestData data, int i) {
        if (i == 1) {
            return data.extraData.getBoolean("LocationFound");
        }
        if (i == 2) {
            return data.extraData.getBoolean("Location2Found");
        }
        if (i == 3) {
            return data.extraData.getBoolean("Location3Found");
        }
        return (this.location.isEmpty() || data.extraData.getBoolean("LocationFound")) && (this.location2.isEmpty() || data.extraData.getBoolean("Location2Found")) && (this.location3.isEmpty() || data.extraData.getBoolean("Location3Found"));
    }
    
    public boolean setFound(QuestData data, String location) {
        if (location.equalsIgnoreCase(this.location) && !data.extraData.getBoolean("LocationFound")) {
            data.extraData.setBoolean("LocationFound", true);
            return true;
        }
        if (location.equalsIgnoreCase(this.location2) && !data.extraData.getBoolean("LocationFound2")) {
            data.extraData.setBoolean("Location2Found", true);
            return true;
        }
        if (location.equalsIgnoreCase(this.location3) && !data.extraData.getBoolean("LocationFound3")) {
            data.extraData.setBoolean("Location3Found", true);
            return true;
        }
        return false;
    }
    
    @Override
    public IQuestObjective[] getObjectives(EntityPlayer player) {
        List<IQuestObjective> list = new ArrayList<IQuestObjective>();
        if (!this.location.isEmpty()) {
            list.add(new QuestLocationObjective(player, this.location, "LocationFound"));
        }
        if (!this.location2.isEmpty()) {
            list.add(new QuestLocationObjective(player, this.location2, "Location2Found"));
        }
        if (!this.location3.isEmpty()) {
            list.add(new QuestLocationObjective(player, this.location3, "Location3Found"));
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }
    
    class QuestLocationObjective implements IQuestObjective
    {
        private EntityPlayer player;
        private String location;
        private String nbtName;
        
        public QuestLocationObjective(EntityPlayer player, String location, String nbtName) {
            this.player = player;
            this.location = location;
            this.nbtName = nbtName;
        }
        
        @Override
        public int getProgress() {
            return this.isCompleted() ? 1 : 0;
        }
        
        @Override
        public void setProgress(int progress) {
            if (progress < 0 || progress > 1) {
                throw new CustomNPCsException("Progress has to be 0 or 1", new Object[0]);
            }
            PlayerData data = PlayerData.get(this.player);
            QuestData questData = data.questData.activeQuests.get(QuestLocation.this.questId);
            boolean completed = questData.extraData.getBoolean(this.nbtName);
            if ((completed && progress == 1) || (!completed && progress == 0)) {
                return;
            }
            questData.extraData.setBoolean(this.nbtName, progress == 1);
            data.questData.checkQuestCompletion(this.player, 3);
            data.updateClient = true;
        }
        
        @Override
        public int getMaxProgress() {
            return 1;
        }
        
        @Override
        public boolean isCompleted() {
            PlayerData data = PlayerData.get(this.player);
            QuestData questData = data.questData.activeQuests.get(QuestLocation.this.questId);
            return questData.extraData.getBoolean(this.nbtName);
        }
        
        @Override
        public String getText() {
            String found = I18n.translateToLocal("quest.found");
            String notfound = I18n.translateToLocal("quest.notfound");
            return this.location + ": " + (this.isCompleted() ? found : notfound);
        }
    }
}
