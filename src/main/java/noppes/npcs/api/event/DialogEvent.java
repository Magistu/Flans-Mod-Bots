package noppes.npcs.api.event;

import noppes.npcs.api.handler.data.IDialogOption;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import noppes.npcs.api.NpcAPI;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IDialog;

public class DialogEvent extends NpcEvent
{
    public IDialog dialog;
    public IPlayer player;
    
    public DialogEvent(ICustomNpc npc, EntityPlayer player, IDialog dialog) {
        super(npc);
        this.dialog = dialog;
        this.player = (IPlayer)NpcAPI.Instance().getIEntity((Entity)player);
    }
    
    @Cancelable
    public static class OpenEvent extends DialogEvent
    {
        public OpenEvent(ICustomNpc npc, EntityPlayer player, IDialog dialog) {
            super(npc, player, dialog);
        }
    }
    
    public static class CloseEvent extends DialogEvent
    {
        public CloseEvent(ICustomNpc npc, EntityPlayer player, IDialog dialog) {
            super(npc, player, dialog);
        }
    }
    
    @Cancelable
    public static class OptionEvent extends DialogEvent
    {
        public IDialogOption option;
        
        public OptionEvent(ICustomNpc npc, EntityPlayer player, IDialog dialog, IDialogOption option) {
            super(npc, player, dialog);
            this.option = option;
        }
    }
}
