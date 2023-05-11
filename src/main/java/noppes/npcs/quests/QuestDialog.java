package noppes.npcs.quests;

import noppes.npcs.api.CustomNPCsException;
import java.util.List;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import java.util.ArrayList;
import noppes.npcs.api.handler.data.IQuestObjective;
import java.util.Iterator;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import java.util.Map;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;

public class QuestDialog extends QuestInterface
{
    public HashMap<Integer, Integer> dialogs;
    
    public QuestDialog() {
        this.dialogs = new HashMap<Integer, Integer>();
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.dialogs = NBTTags.getIntegerIntegerMap(compound.getTagList("QuestDialogs", 10));
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("QuestDialogs", (NBTBase)NBTTags.nbtIntegerIntegerMap(this.dialogs));
    }
    
    @Override
    public boolean isCompleted(EntityPlayer player) {
        for (int dialogId : this.dialogs.values()) {
            if (!PlayerData.get(player).dialogData.dialogsRead.contains(dialogId)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void handleComplete(EntityPlayer player) {
    }
    
    @Override
    public IQuestObjective[] getObjectives(EntityPlayer player) {
        List<IQuestObjective> list = new ArrayList<IQuestObjective>();
        for (int i = 0; i < 3; ++i) {
            if (this.dialogs.containsKey(i)) {
                Dialog dialog = DialogController.instance.dialogs.get(this.dialogs.get(i));
                if (dialog != null) {
                    list.add(new QuestDialogObjective(player, dialog));
                }
            }
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }
    
    class QuestDialogObjective implements IQuestObjective
    {
        private EntityPlayer player;
        private Dialog dialog;
        
        public QuestDialogObjective(EntityPlayer player, Dialog dialog) {
            this.player = player;
            this.dialog = dialog;
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
            boolean completed = data.dialogData.dialogsRead.contains(this.dialog.id);
            if (progress == 0 && completed) {
                data.dialogData.dialogsRead.remove(this.dialog.id);
                data.questData.checkQuestCompletion(this.player, 1);
                data.updateClient = true;
            }
            if (progress == 1 && !completed) {
                data.dialogData.dialogsRead.add(this.dialog.id);
                data.questData.checkQuestCompletion(this.player, 1);
                data.updateClient = true;
            }
        }
        
        @Override
        public int getMaxProgress() {
            return 1;
        }
        
        @Override
        public boolean isCompleted() {
            PlayerData data = PlayerData.get(this.player);
            return data.dialogData.dialogsRead.contains(this.dialog.id);
        }
        
        @Override
        public String getText() {
            return this.dialog.title + (this.isCompleted() ? " (read)" : " (unread)");
        }
    }
}
