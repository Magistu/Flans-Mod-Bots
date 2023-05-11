package noppes.npcs.client;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import noppes.npcs.client.gui.player.GuiDialogInteract;
import noppes.npcs.controllers.data.Dialog;
import java.util.Collection;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import net.minecraft.client.gui.GuiScreen;
import java.util.Vector;
import noppes.npcs.client.gui.util.IScrollData;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import org.lwjgl.Sys;
import java.net.URI;
import net.minecraft.util.Util;
import java.io.File;
import noppes.npcs.CustomNpcs;
import net.minecraft.entity.player.EntityPlayer;
import java.io.IOException;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.client.Minecraft;
import noppes.npcs.Server;
import io.netty.buffer.ByteBuf;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.constants.EnumGuiType;
import java.util.HashMap;
import noppes.npcs.entity.EntityNPCInterface;

public class NoppesUtil
{
    private static EntityNPCInterface lastNpc;
    private static HashMap<String, Integer> data;
    
    public static void requestOpenGUI(EnumGuiType gui) {
        requestOpenGUI(gui, 0, 0, 0);
    }
    
    public static void requestOpenGUI(EnumGuiType gui, int i, int j, int k) {
        Client.sendData(EnumPacketServer.Gui, gui.ordinal(), i, j, k);
    }
    
    public static void spawnParticle(ByteBuf buffer) throws IOException {
        double posX = buffer.readDouble();
        double posY = buffer.readDouble();
        double posZ = buffer.readDouble();
        float height = buffer.readFloat();
        float width = buffer.readFloat();
        String particle = Server.readString(buffer);
        World world = (World)Minecraft.getMinecraft().world;
        Random rand = world.rand;
        if (particle.equals("heal")) {
            for (int k = 0; k < 6; ++k) {
                world.spawnParticle(EnumParticleTypes.SPELL_INSTANT, posX + (rand.nextDouble() - 0.5) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5) * width, 0.0, 0.0, 0.0, new int[0]);
                world.spawnParticle(EnumParticleTypes.SPELL, posX + (rand.nextDouble() - 0.5) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5) * width, 0.0, 0.0, 0.0, new int[0]);
            }
        }
    }
    
    public static EntityNPCInterface getLastNpc() {
        return NoppesUtil.lastNpc;
    }
    
    public static void setLastNpc(EntityNPCInterface npc) {
        NoppesUtil.lastNpc = npc;
    }
    
    public static void openGUI(EntityPlayer player, Object guiscreen) {
        CustomNpcs.proxy.openGui(player, guiscreen);
    }
    
    public static void openFolder(File dir) {
        String s = dir.getAbsolutePath();
        Label_0072: {
            if (Util.getOSType() == Util.EnumOS.OSX) {
                try {
                    Runtime.getRuntime().exec(new String[] { "/usr/bin/open", s });
                    return;
                }
                catch (IOException ex) {
                    break Label_0072;
                }
            }
            if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                String s2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);
                try {
                    Runtime.getRuntime().exec(s2);
                    return;
                }
                catch (IOException ex2) {}
            }
        }
        boolean flag = false;
        try {
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", (Class[])new Class[0]).invoke(null, new Object[0]);
            oclass.getMethod("browse", URI.class).invoke(object, dir.toURI());
        }
        catch (Throwable throwable) {
            flag = true;
        }
        if (flag) {
            Sys.openURL("file://" + s);
        }
    }
    
    public static void setScrollList(ByteBuf buffer) {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof GuiNPCInterface && ((GuiNPCInterface)gui).hasSubGui()) {
            gui = ((GuiNPCInterface)gui).getSubGui();
        }
        if (gui == null || !(gui instanceof IScrollData)) {
            return;
        }
        Vector<String> data = new Vector<String>();
        try {
            for (int size = buffer.readInt(), i = 0; i < size; ++i) {
                data.add(Server.readString(buffer));
            }
        }
        catch (Exception ex) {}
        ((IScrollData)gui).setData(data, null);
    }
    
    public static void addScrollData(ByteBuf buffer) {
        try {
            for (int size = buffer.readInt(), i = 0; i < size; ++i) {
                int id = buffer.readInt();
                String name = Server.readString(buffer);
                NoppesUtil.data.put(name, id);
            }
        }
        catch (Exception ex) {}
    }
    
    public static void setScrollData(ByteBuf buffer) {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui == null) {
            return;
        }
        try {
            for (int size = buffer.readInt(), i = 0; i < size; ++i) {
                int id = buffer.readInt();
                String name = Server.readString(buffer);
                NoppesUtil.data.put(name, id);
            }
        }
        catch (Exception ex) {}
        if (gui instanceof GuiNPCInterface && ((GuiNPCInterface)gui).hasSubGui()) {
            gui = ((GuiNPCInterface)gui).getSubGui();
        }
        if (gui instanceof GuiContainerNPCInterface && ((GuiContainerNPCInterface)gui).hasSubGui()) {
            gui = ((GuiContainerNPCInterface)gui).getSubGui();
        }
        if (gui instanceof IScrollData) {
            ((IScrollData)gui).setData(new Vector<String>(NoppesUtil.data.keySet()), NoppesUtil.data);
        }
        NoppesUtil.data = new HashMap<String, Integer>();
    }
    
    public static void openDialog(Dialog dialog, EntityNPCInterface npc, EntityPlayer player) {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui == null || !(gui instanceof GuiDialogInteract)) {
            CustomNpcs.proxy.openGui(player, new GuiDialogInteract(npc, dialog));
        }
        else {
            GuiDialogInteract dia = (GuiDialogInteract)gui;
            dia.appendDialog(dialog);
        }
    }
    
    public static void clickSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
    
    static {
        NoppesUtil.data = new HashMap<String, Integer>();
    }
}
