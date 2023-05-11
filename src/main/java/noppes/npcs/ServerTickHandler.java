package noppes.npcs;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.client.AnalyticsTracking;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.NonNullList;
import net.minecraft.inventory.IInventory;
import noppes.npcs.controllers.data.PlayerQuestData;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import noppes.npcs.controllers.data.Availability;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import java.util.Iterator;
import java.util.ArrayList;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.SchematicController;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerTickHandler
{
    public int ticks;
    
    public ServerTickHandler() {
        this.ticks = 0;
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.START) {
            return;
        }
        EntityPlayer player = event.player;
        PlayerData data = PlayerData.get(player);
        if (player.getEntityWorld().getWorldTime() % 24000L == 1L || player.getEntityWorld().getWorldTime() % 240000L == 12001L) {
            VisibilityController.instance.onUpdate((EntityPlayerMP)player);
        }
        if (data.updateClient) {
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.SYNC_END, 8, data.getSyncNBT());
            VisibilityController.instance.onUpdate((EntityPlayerMP)player);
            data.updateClient = false;
        }
        if (data.prevHeldItem != player.getHeldItemMainhand() && (data.prevHeldItem.getItem() == CustomItems.wand || player.getHeldItemMainhand().getItem() == CustomItems.wand)) {
            VisibilityController.instance.onUpdate((EntityPlayerMP)player);
        }
        data.prevHeldItem = player.getHeldItemMainhand();
    }
    
    @SubscribeEvent
    public void onServerTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            NPCSpawning.findChunksForSpawning((WorldServer)event.world);
        }
    }
    
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START && this.ticks++ >= 20) {
            SchematicController.Instance.updateBuilding();
            MassBlockController.Update();
            this.ticks = 0;
            for (DataScenes.SceneState state : DataScenes.StartedScenes.values()) {
                if (!state.paused) {
                    DataScenes.SceneState sceneState = state;
                    ++sceneState.ticks;
                }
            }
            for (DataScenes.SceneContainer entry : DataScenes.ScenesToRun) {
                entry.update();
            }
            DataScenes.ScenesToRun = new ArrayList<DataScenes.SceneContainer>();
        }
    }
    
    @SubscribeEvent
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayerMP player = (EntityPlayerMP)event.player;
        MinecraftServer server = event.player.getServer();
        for (WorldServer world : server.worlds) {
            ServerScoreboard board = (ServerScoreboard)world.getScoreboard();
            for (String objective : Availability.scores) {
                ScoreObjective so = board.getObjective(objective);
                if (so != null) {
                    if (board.getObjectiveDisplaySlotCount(so) == 0) {
                        player.connection.sendPacket((Packet)new SPacketScoreboardObjective(so, 0));
                    }
                    Score sco = board.getOrCreateScore(player.getName(), so);
                    player.connection.sendPacket((Packet)new SPacketUpdateScore(sco));
                }
            }
        }
        event.player.inventoryContainer.addListener((IContainerListener)new IContainerListener() {
            public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
            }
            
            public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
                if (player.world.isRemote) {
                    return;
                }
                PlayerQuestData playerdata = PlayerData.get(event.player).questData;
                playerdata.checkQuestCompletion((EntityPlayer)player, 0);
            }
            
            public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
            }
            
            public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
            }
        });
        if (server.isSnooperEnabled()) {
            String serverName = null;
            if (server.isDedicatedServer()) {
                serverName = "server";
            }
            else {
                serverName = (((IntegratedServer)server).getPublic() ? "lan" : "local");
            }
            AnalyticsTracking.sendData(event.player, "join", serverName);
        }
        SyncController.syncPlayer(player);
    }
}
