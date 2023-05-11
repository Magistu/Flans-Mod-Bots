package noppes.npcs.client;

import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.input.Keyboard;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.gui.player.GuiQuestLog;
import noppes.npcs.constants.EnumPacketServer;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.constants.EnumPlayerPacket;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.world.World;

public class ClientTickHandler
{
    private World prevWorld;
    private boolean otherContainer;
    private int buttonPressed;
    private long buttonTime;
    private int[] ignoreKeys;
    
    public ClientTickHandler() {
        this.otherContainer = false;
        this.buttonPressed = -1;
        this.buttonTime = 0L;
        this.ignoreKeys = new int[] { 157, 29, 54, 42, 184, 56, 220, 219 };
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.player.openContainer instanceof ContainerPlayer) {
            if (this.otherContainer) {
                NoppesUtilPlayer.sendData(EnumPlayerPacket.CheckQuestCompletion, new Object[0]);
                this.otherContainer = false;
            }
        }
        else {
            this.otherContainer = true;
        }
        ++CustomNpcs.ticks;
        ++RenderNPCInterface.LastTextureTick;
        if (this.prevWorld != mc.world) {
            this.prevWorld = (World)mc.world;
            MusicController.Instance.stopMusic();
        }
    }
    
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (CustomNpcs.SceneButtonsEnabled) {
            if (ClientProxy.Scene1.isPressed()) {
                Client.sendData(EnumPacketServer.SceneStart, 1);
            }
            if (ClientProxy.Scene2.isPressed()) {
                Client.sendData(EnumPacketServer.SceneStart, 2);
            }
            if (ClientProxy.Scene3.isPressed()) {
                Client.sendData(EnumPacketServer.SceneStart, 3);
            }
            if (ClientProxy.SceneReset.isPressed()) {
                Client.sendData(EnumPacketServer.SceneReset, new Object[0]);
            }
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (ClientProxy.QuestLog.isPressed()) {
            if (mc.currentScreen == null) {
                NoppesUtil.openGUI((EntityPlayer)mc.player, new GuiQuestLog((EntityPlayer)mc.player));
            }
            else if (mc.currentScreen instanceof GuiQuestLog) {
                mc.setIngameFocus();
            }
        }
        int key = Keyboard.getEventKey();
        long time = Keyboard.getEventNanoseconds();
        if (Keyboard.getEventKeyState()) {
            if (!this.isIgnoredKey(key)) {
                this.buttonTime = time;
                this.buttonPressed = key;
            }
        }
        else {
            if (key == this.buttonPressed && time - this.buttonTime < 500000000L && mc.currentScreen == null) {
                boolean isCtrlPressed = Keyboard.isKeyDown(157) || Keyboard.isKeyDown(29);
                boolean isShiftPressed = Keyboard.isKeyDown(54) || Keyboard.isKeyDown(42);
                boolean isAltPressed = Keyboard.isKeyDown(184) || Keyboard.isKeyDown(56);
                boolean isMetaPressed = Keyboard.isKeyDown(220) || Keyboard.isKeyDown(219);
                NoppesUtilPlayer.sendData(EnumPlayerPacket.KeyPressed, key, isCtrlPressed, isShiftPressed, isAltPressed, isMetaPressed);
            }
            this.buttonPressed = -1;
            this.buttonTime = 0L;
        }
    }
    
    @SubscribeEvent
    public void invoke(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }
        NoppesUtilPlayer.sendData(EnumPlayerPacket.LeftClick, new Object[0]);
    }
    
    private boolean isIgnoredKey(int key) {
        for (int i : this.ignoreKeys) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }
}
