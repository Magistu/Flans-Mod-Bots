package noppes.npcs;

import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import noppes.npcs.api.wrapper.WrapperEntityData;
import noppes.npcs.controllers.data.MarkData;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import noppes.npcs.controllers.VisibilityController;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandTime;
import java.util.concurrent.Callable;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.util.concurrent.Executors;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandGive;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.CommandEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.quests.QuestKill;
import noppes.npcs.controllers.data.QuestData;
import net.minecraft.entity.EntityList;
import noppes.npcs.controllers.data.Line;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.items.ItemSoulstoneEmpty;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;

public class ServerEventsHandler
{
    public static EntityVillager Merchant;
    public static Entity mounted;
    
    @SubscribeEvent
    public void invoke(PlayerInteractEvent.EntityInteract event) {
        ItemStack item = event.getEntityPlayer().getHeldItemMainhand();
        if (item == null) {
            return;
        }
        boolean isRemote = event.getEntityPlayer().world.isRemote;
        boolean npcInteracted = event.getTarget() instanceof EntityNPCInterface;
        if (!isRemote && CustomNpcs.OpsOnly && !event.getEntityPlayer().getServer().getPlayerList().canSendCommands(event.getEntityPlayer().getGameProfile())) {
            return;
        }
        if (!isRemote && item.getItem() == CustomItems.soulstoneEmpty && event.getTarget() instanceof EntityLivingBase) {
            ((ItemSoulstoneEmpty)item.getItem()).store((EntityLivingBase)event.getTarget(), item, event.getEntityPlayer());
        }
        if (item.getItem() == CustomItems.wand && npcInteracted && !isRemote) {
            CustomNpcsPermissions instance = CustomNpcsPermissions.Instance;
            if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.NPC_GUI)) {
                return;
            }
            event.setCanceled(true);
            NoppesUtilServer.sendOpenGui(event.getEntityPlayer(), EnumGuiType.MainMenuDisplay, (EntityNPCInterface)event.getTarget());
        }
        else if (item.getItem() == CustomItems.cloner && !isRemote && !(event.getTarget() instanceof EntityPlayer)) {
            NBTTagCompound compound = new NBTTagCompound();
            if (!event.getTarget().writeToNBTAtomically(compound)) {
                return;
            }
            PlayerData data = PlayerData.get(event.getEntityPlayer());
            ServerCloneController.Instance.cleanTags(compound);
            if (!Server.sendDataChecked((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.CLONE, compound)) {
                event.getEntityPlayer().sendMessage((ITextComponent)new TextComponentString("Entity too big to clone"));
            }
            data.cloned = compound;
            event.setCanceled(true);
        }
        else if (item.getItem() == CustomItems.scripter && !isRemote && npcInteracted) {
            CustomNpcsPermissions instance2 = CustomNpcsPermissions.Instance;
            if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.NPC_GUI)) {
                return;
            }
            NoppesUtilServer.setEditingNpc(event.getEntityPlayer(), (EntityNPCInterface)event.getTarget());
            event.setCanceled(true);
            Server.sendData((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.GUI, EnumGuiType.Script.ordinal(), 0, 0, 0);
        }
        else if (item.getItem() == CustomItems.mount) {
            CustomNpcsPermissions instance3 = CustomNpcsPermissions.Instance;
            if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.TOOL_MOUNTER)) {
                return;
            }
            event.setCanceled(true);
            ServerEventsHandler.mounted = event.getTarget();
            if (isRemote) {
                CustomNpcs.proxy.openGui(MathHelper.floor(ServerEventsHandler.mounted.posX), MathHelper.floor(ServerEventsHandler.mounted.posY), MathHelper.floor(ServerEventsHandler.mounted.posZ), EnumGuiType.MobSpawnerMounter, event.getEntityPlayer());
            }
        }
        else if (item.getItem() == CustomItems.wand && event.getTarget() instanceof EntityVillager) {
            CustomNpcsPermissions instance4 = CustomNpcsPermissions.Instance;
            if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.EDIT_VILLAGER)) {
                return;
            }
            event.setCanceled(true);
            ServerEventsHandler.Merchant = (EntityVillager)event.getTarget();
            if (!isRemote) {
                EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
                player.openGui((Object)CustomNpcs.instance, EnumGuiType.MerchantAdd.ordinal(), player.world, 0, 0, 0);
                MerchantRecipeList merchantrecipelist = ServerEventsHandler.Merchant.getRecipes((EntityPlayer)player);
                if (merchantrecipelist != null) {
                    Server.sendData(player, EnumPacketClient.VILLAGER_LIST, merchantrecipelist);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void invoke(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }
        Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
        if (source != null) {
            if (source instanceof EntityNPCInterface && event.getEntityLiving() != null) {
                EntityNPCInterface npc = (EntityNPCInterface)source;
                Line line = npc.advanced.getKillLine();
                if (line != null) {
                    npc.saySurrounding(Line.formatTarget(line, event.getEntityLiving()));
                }
                EventHooks.onNPCKills(npc, event.getEntityLiving());
            }
            EntityPlayer player = null;
            if (source instanceof EntityPlayer) {
                player = (EntityPlayer)source;
            }
            else if (source instanceof EntityNPCInterface && ((EntityNPCInterface)source).getOwner() instanceof EntityPlayer) {
                player = (EntityPlayer)((EntityNPCInterface)source).getOwner();
            }
            if (player != null) {
                this.doQuest(player, event.getEntityLiving(), true);
                if (event.getEntityLiving() instanceof EntityNPCInterface) {
                    this.doFactionPoints(player, (EntityNPCInterface)event.getEntityLiving());
                }
            }
        }
        if (event.getEntityLiving() instanceof EntityPlayer) {
            PlayerData data = PlayerData.get((EntityPlayer)event.getEntityLiving());
            data.save(false);
        }
    }
    
    private void doFactionPoints(EntityPlayer player, EntityNPCInterface npc) {
        npc.advanced.factions.addPoints(player);
    }
    
    private void doQuest(EntityPlayer player, EntityLivingBase entity, boolean all) {
        PlayerData pdata = PlayerData.get(player);
        PlayerQuestData playerdata = pdata.questData;
        String entityName = EntityList.getEntityString((Entity)entity);
        if (entity instanceof EntityPlayer) {
            entityName = "Player";
        }
        for (QuestData data : playerdata.activeQuests.values()) {
            if (data.quest.type != 2 && data.quest.type != 4) {
                continue;
            }
            if (data.quest.type == 4 && all) {
                List<EntityPlayer> list = (List<EntityPlayer>)player.world.getEntitiesWithinAABB((Class)EntityPlayer.class, entity.getEntityBoundingBox().grow(10.0, 10.0, 10.0));
                for (EntityPlayer pl : list) {
                    if (pl != player) {
                        this.doQuest(pl, entity, false);
                    }
                }
            }
            String name = entityName;
            QuestKill quest = (QuestKill)data.quest.questInterface;
            if (quest.targets.containsKey(entity.getName())) {
                name = entity.getName();
            }
            else if (!quest.targets.containsKey(name)) {
                continue;
            }
            HashMap<String, Integer> killed = quest.getKilled(data);
            if (killed.containsKey(name) && killed.get(name) >= quest.targets.get(name)) {
                continue;
            }
            int amount = 0;
            if (killed.containsKey(name)) {
                amount = killed.get(name);
            }
            killed.put(name, amount + 1);
            quest.setKilled(data, killed);
            pdata.updateClient = true;
        }
        playerdata.checkQuestCompletion(player, 2);
        playerdata.checkQuestCompletion(player, 4);
    }
    
    @SubscribeEvent
    public void commandGive(CommandEvent event) {
        if (!(event.getSender().getEntityWorld() instanceof WorldServer) || !(event.getCommand() instanceof CommandGive)) {
            return;
        }
        try {
            EntityPlayer player = (EntityPlayer)CommandBase.getPlayer(event.getSender().getServer(), event.getSender(), event.getParameters()[0]);
            PlayerQuestData playerdata = PlayerData.get(player).questData;
            player.getServer().futureTaskQueue.add(ListenableFutureTask.create((Callable)Executors.callable(() -> {
                playerdata.checkQuestCompletion(player, 0);
            })));
        }
        catch (Throwable t) {}
    }
    
    @SubscribeEvent
    public void commandTime(CommandEvent event) {
        if (!(event.getCommand() instanceof CommandTime)) {
            return;
        }
        try {
            List<EntityPlayerMP> players = (List<EntityPlayerMP>)FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
            for (EntityPlayerMP playerMP : players) {
                VisibilityController.instance.onUpdate(playerMP);
            }
        }
        catch (Throwable t) {}
    }
    
    @SubscribeEvent
    public void world(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        PlayerData data = PlayerData.get((EntityPlayer)event.getEntity());
        data.updateCompanion(event.getWorld());
    }
    
    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Post event) {
        NPCSpawning.performWorldGenSpawning(event.getWorld(), event.getChunkX(), event.getChunkZ(), event.getRand());
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void attachEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            PlayerData.register(event);
        }
        if (event.getObject() instanceof EntityLivingBase) {
            MarkData.register(event);
        }
        if (((Entity)event.getObject()).world != null && !((Entity)event.getObject()).world.isRemote && ((Entity)event.getObject()).world instanceof WorldServer) {
            WrapperEntityData.register(event);
        }
    }
    
    @SubscribeEvent
    public void attachItem(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStackWrapper.register(event);
    }
    
    @SubscribeEvent
    public void savePlayer(PlayerEvent.SaveToFile event) {
        PlayerData.get(event.getEntityPlayer()).save(false);
    }
    
    @SubscribeEvent
    public void saveChunk(ChunkDataEvent.Save event) {
        for (ClassInheritanceMultiMap<Entity> map : event.getChunk().getEntityLists()) {
            for (Entity e : map) {
                if (e instanceof EntityLivingBase) {
                    MarkData.get((EntityLivingBase)e).save();
                }
            }
        }
    }
    
    @SubscribeEvent
    public void playerTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof EntityLivingBase) || event.getTarget().world.isRemote) {
            return;
        }
        if (event.getTarget() instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)event.getTarget();
            npc.tracking.add(event.getEntityPlayer().getEntityId());
            VisibilityController.checkIsVisible(npc, (EntityPlayerMP)event.getEntityPlayer());
        }
        MarkData data = MarkData.get((EntityLivingBase)event.getTarget());
        if (data.marks.isEmpty()) {
            return;
        }
        Server.sendData((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.MARK_DATA, event.getTarget().getEntityId(), data.getNBT());
    }
    
    @SubscribeEvent
    public void playerStopTracking(PlayerEvent.StopTracking event) {
        if (event.getTarget() instanceof EntityNPCInterface && !event.getTarget().world.isRemote) {
            EntityNPCInterface npc = (EntityNPCInterface)event.getTarget();
            npc.tracking.remove(event.getEntityPlayer().getEntityId());
        }
    }
}
