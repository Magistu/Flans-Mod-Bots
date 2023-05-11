package noppes.npcs.controllers.data;

import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;

public class PlayerMailData
{
    public ArrayList<PlayerMail> playermail;
    
    public PlayerMailData() {
        this.playermail = new ArrayList<PlayerMail>();
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        ArrayList<PlayerMail> newmail = new ArrayList<PlayerMail>();
        NBTTagList list = compound.getTagList("MailData", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.tagCount(); ++i) {
            PlayerMail mail = new PlayerMail();
            mail.readNBT(list.getCompoundTagAt(i));
            newmail.add(mail);
        }
        this.playermail = newmail;
    }
    
    public NBTTagCompound saveNBTData(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (PlayerMail mail : this.playermail) {
            list.appendTag((NBTBase)mail.writeNBT());
        }
        compound.setTag("MailData", (NBTBase)list);
        return compound;
    }
    
    public boolean hasMail() {
        for (PlayerMail mail : this.playermail) {
            if (!mail.beenRead) {
                return true;
            }
        }
        return false;
    }
}
