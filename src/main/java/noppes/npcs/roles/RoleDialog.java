package noppes.npcs.roles;

import noppes.npcs.api.CustomNPCsException;
import java.util.Iterator;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.Dialog;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Map;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import java.util.HashMap;
import noppes.npcs.api.entity.data.role.IRoleDialog;

public class RoleDialog extends RoleInterface implements IRoleDialog
{
    public String dialog;
    public int questId;
    public HashMap<Integer, String> options;
    public HashMap<Integer, String> optionsTexts;
    
    public RoleDialog(EntityNPCInterface npc) {
        super(npc);
        this.dialog = "";
        this.questId = -1;
        this.options = new HashMap<Integer, String>();
        this.optionsTexts = new HashMap<Integer, String>();
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("RoleQuestId", this.questId);
        compound.setString("RoleDialog", this.dialog);
        compound.setTag("RoleOptions", NBTTags.nbtIntegerStringMap(this.options));
        compound.setTag("RoleOptionTexts", NBTTags.nbtIntegerStringMap(this.optionsTexts));
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.questId = compound.getInteger("RoleQuestId");
        this.dialog = compound.getString("RoleDialog");
        this.options = NBTTags.getIntegerStringMap(compound.getTagList("RoleOptions", 10));
        this.optionsTexts = NBTTags.getIntegerStringMap(compound.getTagList("RoleOptionTexts", 10));
    }
    
    @Override
    public void interact(EntityPlayer player) {
        if (this.dialog.isEmpty()) {
            this.npc.say(player, this.npc.advanced.getInteractLine());
        }
        else {
            Dialog d = new Dialog(null);
            d.text = this.dialog;
            for (Map.Entry<Integer, String> entry : this.options.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }
                DialogOption option = new DialogOption();
                String text = this.optionsTexts.get(entry.getKey());
                if (text != null && !text.isEmpty()) {
                    option.optionType = 3;
                }
                else {
                    option.optionType = 0;
                }
                option.title = entry.getValue();
                d.options.put(entry.getKey(), option);
            }
            NoppesUtilServer.openDialog(player, this.npc, d);
        }
        Quest quest = QuestController.instance.quests.get(this.questId);
        if (quest != null) {
            PlayerQuestController.addActiveQuest(quest, player);
        }
    }
    
    @Override
    public String getDialog() {
        return this.dialog;
    }
    
    @Override
    public void setDialog(String text) {
        this.dialog = text;
    }
    
    @Override
    public String getOption(int option) {
        return this.options.get(option);
    }
    
    @Override
    public void setOption(int option, String text) {
        if (option < 1 || option > 6) {
            throw new CustomNPCsException("Wrong dialog option slot given: " + option, new Object[0]);
        }
        this.options.put(option, text);
    }
    
    @Override
    public String getOptionDialog(int option) {
        return this.optionsTexts.get(option);
    }
    
    @Override
    public void setOptionDialog(int option, String text) {
        if (option < 1 || option > 6) {
            throw new CustomNPCsException("Wrong dialog option slot given: " + option, new Object[0]);
        }
        this.optionsTexts.put(option, text);
    }
}
