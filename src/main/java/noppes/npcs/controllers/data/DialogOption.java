package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.DialogController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.handler.data.IDialogOption;

public class DialogOption implements IDialogOption
{
    public int dialogId;
    public String title;
    public int optionType;
    public int optionColor;
    public String command;
    public int slot;
    
    public DialogOption() {
        this.dialogId = -1;
        this.title = "Talk";
        this.optionType = 1;
        this.optionColor = 14737632;
        this.command = "";
        this.slot = -1;
    }
    
    public void readNBT(NBTTagCompound compound) {
        if (compound == null) {
            return;
        }
        this.title = compound.getString("Title");
        this.dialogId = compound.getInteger("Dialog");
        this.optionColor = compound.getInteger("DialogColor");
        this.optionType = compound.getInteger("OptionType");
        this.command = compound.getString("DialogCommand");
        if (this.optionColor == 0) {
            this.optionColor = 14737632;
        }
    }
    
    public NBTTagCompound writeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Title", this.title);
        compound.setInteger("OptionType", this.optionType);
        compound.setInteger("Dialog", this.dialogId);
        compound.setInteger("DialogColor", this.optionColor);
        compound.setString("DialogCommand", this.command);
        return compound;
    }
    
    public boolean hasDialog() {
        if (this.dialogId <= 0 || this.optionType != 1) {
            return false;
        }
        if (!DialogController.instance.hasDialog(this.dialogId)) {
            this.dialogId = -1;
            return false;
        }
        return true;
    }
    
    public Dialog getDialog() {
        if (!this.hasDialog()) {
            return null;
        }
        return DialogController.instance.dialogs.get(this.dialogId);
    }
    
    public boolean isAvailable(EntityPlayer player) {
        if (this.optionType == 2) {
            return false;
        }
        if (this.optionType != 1) {
            return true;
        }
        Dialog dialog = this.getDialog();
        return dialog != null && dialog.availability.isAvailable(player);
    }
    
    @Override
    public int getSlot() {
        return this.slot;
    }
    
    @Override
    public String getName() {
        return this.title;
    }
    
    @Override
    public int getType() {
        return this.optionType;
    }
}
