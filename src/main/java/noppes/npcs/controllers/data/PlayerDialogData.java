package noppes.npcs.controllers.data;

import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashSet;

public class PlayerDialogData
{
    public HashSet<Integer> dialogsRead;
    
    public PlayerDialogData() {
        this.dialogsRead = new HashSet<Integer>();
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        HashSet<Integer> dialogsRead = new HashSet<Integer>();
        if (compound == null) {
            return;
        }
        NBTTagList list = compound.getTagList("DialogData", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
            dialogsRead.add(nbttagcompound.getInteger("Dialog"));
        }
        this.dialogsRead = dialogsRead;
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int dia : this.dialogsRead) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("Dialog", dia);
            list.appendTag((NBTBase)nbttagcompound);
        }
        compound.setTag("DialogData", (NBTBase)list);
    }
}
