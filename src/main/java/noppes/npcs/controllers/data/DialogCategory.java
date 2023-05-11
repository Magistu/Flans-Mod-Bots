package noppes.npcs.controllers.data;

import java.util.Collection;
import java.util.ArrayList;
import noppes.npcs.api.handler.data.IDialog;
import java.util.List;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;
import noppes.npcs.api.handler.data.IDialogCategory;

public class DialogCategory implements IDialogCategory
{
    public int id;
    public String title;
    public HashMap<Integer, Dialog> dialogs;
    
    public DialogCategory() {
        this.id = -1;
        this.title = "";
        this.dialogs = new HashMap<Integer, Dialog>();
    }
    
    public void readNBT(NBTTagCompound compound) {
        this.id = compound.getInteger("Slot");
        this.title = compound.getString("Title");
        NBTTagList dialogsList = compound.getTagList("Dialogs", 10);
        if (dialogsList != null) {
            for (int ii = 0; ii < dialogsList.tagCount(); ++ii) {
                Dialog dialog = new Dialog(this);
                NBTTagCompound comp = dialogsList.getCompoundTagAt(ii);
                dialog.readNBT(comp);
                dialog.id = comp.getInteger("DialogId");
                this.dialogs.put(dialog.id, dialog);
            }
        }
    }
    
    public NBTTagCompound writeNBT(NBTTagCompound nbtfactions) {
        nbtfactions.setInteger("Slot", this.id);
        nbtfactions.setString("Title", this.title);
        NBTTagList dialogs = new NBTTagList();
        for (Dialog dialog : this.dialogs.values()) {
            dialogs.appendTag((NBTBase)dialog.writeToNBT(new NBTTagCompound()));
        }
        nbtfactions.setTag("Dialogs", (NBTBase)dialogs);
        return nbtfactions;
    }
    
    @Override
    public List<IDialog> dialogs() {
        return new ArrayList<IDialog>(this.dialogs.values());
    }
    
    @Override
    public String getName() {
        return this.title;
    }
    
    @Override
    public IDialog create() {
        return new Dialog(this);
    }
}
