package noppes.npcs.controllers;

import net.minecraft.nbt.NBTTagList;
import java.io.IOException;
import net.minecraft.nbt.NBTTagString;
import io.netty.buffer.ByteBuf;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.event.CustomGuiEvent;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.containers.ContainerCustomGui;
import net.minecraft.world.World;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomNpcs;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;

public class CustomGuiController
{
    public static void openGui(final PlayerWrapper player, final CustomGuiWrapper gui) {
        ((EntityPlayer) player.getMCEntity()).openGui(CustomNpcs.instance, EnumGuiType.CustomGui.ordinal(), (World)player.getWorld().getMCWorld(), gui.getSlots().size(), 0, 0);
        ((ContainerCustomGui) ((EntityPlayer) player.getMCEntity()).openContainer).setGui(gui, ((EntityPlayer) player.getMCEntity()));
        Server.sendDataDelayed((EntityPlayerMP) player.getMCEntity(), EnumPacketClient.GUI_DATA, 100, gui.toNBT());
    }
    
    public static boolean updateGui(final PlayerWrapper player, final CustomGuiWrapper gui) {
        if (((EntityPlayer) player.getMCEntity()).openContainer instanceof ContainerCustomGui) {
            Server.sendData((EntityPlayerMP) player.getMCEntity(), EnumPacketClient.GUI_DATA, gui.toNBT());
            return true;
        }
        return false;
    }
    
    static boolean checkGui(CustomGuiEvent event) {
        EntityPlayer player = event.player.getMCEntity();
        return player.openContainer instanceof ContainerCustomGui && ((ContainerCustomGui)player.openContainer).customGui.getID() == event.gui.getID();
    }
    
    public static IItemStack[] getSlotContents(EntityPlayer player) {
        IItemStack[] slotContents = new IItemStack[0];
        if (player.openContainer instanceof ContainerCustomGui) {
            ContainerCustomGui container = (ContainerCustomGui)player.openContainer;
            slotContents = new IItemStack[container.guiInventory.getSizeInventory()];
            for (int i = 0; i < container.guiInventory.getSizeInventory(); ++i) {
                slotContents[i] = NpcAPI.Instance().getIItemStack(container.guiInventory.getStackInSlot(i));
            }
        }
        return slotContents;
    }
    
    public static void onButton(CustomGuiEvent.ButtonEvent event) {
        EntityPlayer player = event.player.getMCEntity();
        if (checkGui(event) && getOpenGui(player).getScriptHandler() != null) {
            getOpenGui(player).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_BUTTON, event);
        }
        WrapperNpcAPI.EVENT_BUS.post((Event)event);
    }
    
    public static void onSlotChange(CustomGuiEvent.SlotEvent event) {
        EntityPlayer player = event.player.getMCEntity();
        if (checkGui(event) && getOpenGui(player).getScriptHandler() != null) {
            getOpenGui(player).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_SLOT, event);
        }
        WrapperNpcAPI.EVENT_BUS.post((Event)event);
    }
    
    public static void onScrollClick(CustomGuiEvent.ScrollEvent event) {
        EntityPlayer player = event.player.getMCEntity();
        if (checkGui(event) && getOpenGui(player).getScriptHandler() != null) {
            getOpenGui(player).getScriptHandler().run(EnumScriptType.CUSTOM_GUI_SCROLL, event);
        }
        WrapperNpcAPI.EVENT_BUS.post((Event)event);
    }
    
    public static void onClose(CustomGuiEvent.CloseEvent event) {
        EntityPlayer player = event.player.getMCEntity();
        CustomGuiWrapper gui = getOpenGui(player);
        if (checkGui(event) && getOpenGui(player).getScriptHandler() != null) {
            gui.getScriptHandler().run(EnumScriptType.CUSTOM_GUI_CLOSED, event);
        }
        WrapperNpcAPI.EVENT_BUS.post((Event)event);
    }
    
    public static CustomGuiWrapper getOpenGui(EntityPlayer player) {
        if (player.openContainer instanceof ContainerCustomGui) {
            return ((ContainerCustomGui)player.openContainer).customGui;
        }
        return null;
    }
    
    public static String[] readScrollSelection(ByteBuf buffer) {
        try {
            NBTTagList list = Server.readNBT(buffer).getTagList("selection", 8);
            String[] selection = new String[list.tagCount()];
            for (int i = 0; i < list.tagCount(); ++i) {
                selection[i] = ((NBTTagString)list.get(i)).getString();
            }
            return selection;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
