package noppes.npcs;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.DamageSource;
import java.util.List;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTBase;
import java.text.DecimalFormat;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.containers.ContainerManageRecipes;
import net.minecraft.tileentity.TileEntity;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.SyncController;
import java.io.File;
import io.netty.buffer.ByteBuf;
import noppes.npcs.controllers.data.PlayerFactionData;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.constants.EnumPlayerData;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.roles.RoleTransporter;
import java.util.ArrayList;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.constants.EnumGuiType;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.controllers.data.PlayerDialogData;
import noppes.npcs.controllers.PlayerDataController;
import net.minecraft.command.ICommandSender;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.BankController;
import java.util.Iterator;
import java.util.Map;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.FactionController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.Quest;
import java.util.UUID;
import java.util.HashMap;

public class NoppesUtilServer
{
    private static HashMap<UUID, Quest> editingQuests;
    private static HashMap<UUID, Quest> editingQuestsClient;
    
    public static void setEditingNpc(EntityPlayer player, EntityNPCInterface npc) {
        PlayerData data = PlayerData.get(player);
        data.editingNpc = npc;
        if (npc != null) {
            Server.sendDataChecked((EntityPlayerMP)player, EnumPacketClient.EDIT_NPC, npc.getEntityId());
        }
    }
    
    public static EntityNPCInterface getEditingNpc(EntityPlayer player) {
        PlayerData data = PlayerData.get(player);
        return data.editingNpc;
    }
    
    public static void setEditingQuest(EntityPlayer player, Quest quest) {
        if (player.world.isRemote) {
            NoppesUtilServer.editingQuestsClient.put(player.getUniqueID(), quest);
        }
        else {
            NoppesUtilServer.editingQuests.put(player.getUniqueID(), quest);
        }
    }
    
    public static Quest getEditingQuest(EntityPlayer player) {
        if (player.world.isRemote) {
            return NoppesUtilServer.editingQuestsClient.get(player.getUniqueID());
        }
        return NoppesUtilServer.editingQuests.get(player.getUniqueID());
    }
    
    public static void sendRoleData(EntityPlayer player, EntityNPCInterface npc) {
        if (npc.advanced.role == 0) {
            return;
        }
        NBTTagCompound comp = new NBTTagCompound();
        npc.roleInterface.writeToNBT(comp);
        comp.setInteger("EntityId", npc.getEntityId());
        comp.setInteger("Role", npc.advanced.role);
        Server.sendData((EntityPlayerMP)player, EnumPacketClient.ROLE, comp);
    }
    
    public static void sendFactionDataAll(EntityPlayerMP player) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Faction faction : FactionController.instance.factions.values()) {
            map.put(faction.name, faction.id);
        }
        sendScrollData(player, map);
    }
    
    public static void sendBankDataAll(EntityPlayerMP player) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Bank bank : BankController.getInstance().banks.values()) {
            map.put(bank.name, bank.id);
        }
        sendScrollData(player, map);
    }
    
    public static void openDialog(EntityPlayer player, EntityNPCInterface npc, Dialog dia) {
        Dialog dialog = dia.copy(player);
        PlayerData playerdata = PlayerData.get(player);
        if (EventHooks.onNPCDialog(npc, player, dialog)) {
            playerdata.dialogId = -1;
            return;
        }
        playerdata.dialogId = dialog.id;
        if (npc instanceof EntityDialogNpc || dia.id < 0) {
            dialog.hideNPC = true;
            Server.sendDataDelayed((EntityPlayerMP)player, EnumPacketClient.DIALOG_DUMMY, 100, npc.getName(), dialog.writeToNBT(new NBTTagCompound()));
        }
        else {
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.DIALOG, npc.getEntityId(), dialog.id);
        }
        dia.factionOptions.addPoints(player);
        if (dialog.hasQuest()) {
            PlayerQuestController.addActiveQuest(dialog.getQuest(), player);
        }
        if (!dialog.command.isEmpty()) {
            runCommand((ICommandSender)npc, npc.getName(), dialog.command, player);
        }
        if (dialog.mail.isValid()) {
            PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName(), dialog.mail);
        }
        PlayerDialogData data = playerdata.dialogData;
        if (!data.dialogsRead.contains(dialog.id) && dialog.id >= 0) {
            data.dialogsRead.add(dialog.id);
            playerdata.updateClient = true;
        }
        setEditingNpc(player, npc);
        playerdata.questData.checkQuestCompletion(player, 1);
    }
    
    public static String runCommand(ICommandSender executer, String name, String command, EntityPlayer player) {
        return runCommand(executer.getEntityWorld(), executer.getPosition(), name, command, player, executer);
    }
    
    public static String runCommand(World world, BlockPos pos, String name, String command, EntityPlayer player, ICommandSender executer) {
        if (!world.getMinecraftServer().isCommandBlockEnabled()) {
            LogWriter.warn("Cant run commands if CommandBlocks are disabled");
            return "Cant run commands if CommandBlocks are disabled";
        }
        if (player != null) {
            command = command.replace("@dp", player.getName());
        }
        command = command.replace("@npc", name);
        TextComponentString output = new TextComponentString("");
        ICommandSender icommandsender = (ICommandSender)new RConConsoleSource(world.getMinecraftServer()) {
            public String getName() {
                return "@CustomNPCs-" + name;
            }
            
            public ITextComponent getDisplayName() {
                return (ITextComponent)new TextComponentString(this.getName());
            }
            
            public void sendMessage(ITextComponent component) {
                output.appendSibling(component);
            }
            
            public boolean canUseCommand(int permLevel, String commandName) {
                return CustomNpcs.NpcUseOpCommands || permLevel <= 2;
            }
            
            public BlockPos getPosition() {
                return pos;
            }
            
            public Vec3d getPositionVector() {
                return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            }
            
            public World getEntityWorld() {
                return world;
            }
            
            public Entity getCommandSenderEntity() {
                if (executer == null) {
                    return null;
                }
                return executer.getCommandSenderEntity();
            }
            
            public boolean sendCommandFeedback() {
                return this.getServer().worlds[0].getGameRules().getBoolean("commandBlockOutput");
            }
        };
        ICommandManager icommandmanager = world.getMinecraftServer().getCommandManager();
        icommandmanager.executeCommand(icommandsender, command);
        if (output.getUnformattedText().isEmpty()) {
            return null;
        }
        return output.getUnformattedText();
    }
    
    public static void consumeItemStack(int i, EntityPlayer player) {
        ItemStack item = player.inventory.getCurrentItem();
        if (player.capabilities.isCreativeMode || item == null || item.isEmpty()) {
            return;
        }
        item.shrink(1);
        if (item.getCount() <= 0) {
            player.setHeldItem(EnumHand.MAIN_HAND, (ItemStack)null);
        }
    }
    
    public static DataOutputStream getDataOutputStream(ByteArrayOutputStream stream) throws IOException {
        return new DataOutputStream(new GZIPOutputStream(stream));
    }
    
    public static void sendOpenGui(EntityPlayer player, EnumGuiType gui, EntityNPCInterface npc) {
        sendOpenGui(player, gui, npc, 0, 0, 0);
    }
    
    public static void sendOpenGui(EntityPlayer player, EnumGuiType gui, EntityNPCInterface npc, int i, int j, int k) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        setEditingNpc(player, npc);
        sendExtraData(player, npc, gui, i, j, k);
        CustomNPCsScheduler.runTack(() -> {
            if (CustomNpcs.proxy.getServerGuiElement(gui.ordinal(), player, player.world, i, j, k) != null) {
                player.openGui((Object)CustomNpcs.instance, gui.ordinal(), player.world, i, j, k);
            }
            else {
                Server.sendDataChecked((EntityPlayerMP)player, EnumPacketClient.GUI, gui.ordinal(), i, j, k);
                ArrayList<String> list = getScrollData(player, gui, npc);
                if (list != null && !list.isEmpty()) {
                    Server.sendData((EntityPlayerMP)player, EnumPacketClient.SCROLL_LIST, list);
                }
            }
        }, 200);
    }
    
    private static void sendExtraData(EntityPlayer player, EntityNPCInterface npc, EnumGuiType gui, int i, int j, int k) {
        if (gui == EnumGuiType.PlayerFollower || gui == EnumGuiType.PlayerFollowerHire || gui == EnumGuiType.PlayerTrader || gui == EnumGuiType.PlayerTransporter) {
            sendRoleData(player, npc);
        }
    }
    
    private static ArrayList<String> getScrollData(EntityPlayer player, EnumGuiType gui, EntityNPCInterface npc) {
        if (gui == EnumGuiType.PlayerTransporter) {
            RoleTransporter role = (RoleTransporter)npc.roleInterface;
            ArrayList<String> list = new ArrayList<String>();
            TransportLocation location = role.getLocation();
            String name = role.getLocation().name;
            for (TransportLocation loc : location.category.getDefaultLocations()) {
                if (!list.contains(loc.name)) {
                    list.add(loc.name);
                }
            }
            PlayerTransportData playerdata = PlayerData.get(player).transportData;
            for (int i : playerdata.transports) {
                TransportLocation loc2 = TransportController.getInstance().getTransport(i);
                if (loc2 != null && location.category.locations.containsKey(loc2.id) && !list.contains(loc2.name)) {
                    list.add(loc2.name);
                }
            }
            list.remove(name);
            return list;
        }
        return null;
    }
    
    public static void spawnParticle(Entity entity, String particle, int dimension) {
        Server.sendAssociatedData(entity, EnumPacketClient.PARTICLE, entity.posX, entity.posY, entity.posZ, entity.height, entity.width, particle);
    }
    
    public static void deleteNpc(EntityNPCInterface npc, EntityPlayer player) {
        Server.sendAssociatedData((Entity)npc, EnumPacketClient.DELETE_NPC, npc.getEntityId());
    }
    
    public static void createMobSpawner(BlockPos pos, NBTTagCompound comp, EntityPlayer player) {
        ServerCloneController.Instance.cleanTags(comp);
        if (comp.getString("id").equalsIgnoreCase("entityhorse")) {
            player.sendMessage((ITextComponent)new TextComponentTranslation("Currently you cant create horse spawner, its a minecraft bug", new Object[0]));
            return;
        }
        player.world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState());
        TileEntityMobSpawner tile = (TileEntityMobSpawner)player.world.getTileEntity(pos);
        MobSpawnerBaseLogic logic = tile.getSpawnerBaseLogic();
        if (!comp.hasKey("id", 8)) {
            comp.setString("id", "Pig");
        }
        comp.setIntArray("StartPosNew", new int[] { pos.getX(), pos.getY(), pos.getZ() });
        logic.setNextSpawnData(new WeightedSpawnerEntity(1, comp));
    }
    
    public static void sendPlayerData(EnumPlayerData type, EntityPlayerMP player, String name) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        if (type == EnumPlayerData.Players) {
            for (String username : PlayerDataController.instance.nameUUIDs.keySet()) {
                map.put(username, 0);
            }
            for (String username2 : player.getServer().getPlayerList().getOnlinePlayerNames()) {
                map.put(username2, 0);
            }
        }
        else {
            PlayerData playerdata = PlayerDataController.instance.getDataFromUsername(player.getServer(), name);
            if (type == EnumPlayerData.Dialog) {
                PlayerDialogData data = playerdata.dialogData;
                for (int questId : data.dialogsRead) {
                    Dialog dialog = DialogController.instance.dialogs.get(questId);
                    if (dialog == null) {
                        continue;
                    }
                    map.put(dialog.category.title + ": " + dialog.title, questId);
                }
            }
            else if (type == EnumPlayerData.Quest) {
                PlayerQuestData data2 = playerdata.questData;
                for (int questId : data2.activeQuests.keySet()) {
                    Quest quest = QuestController.instance.quests.get(questId);
                    if (quest == null) {
                        continue;
                    }
                    map.put(quest.category.title + ": " + quest.title + "(Active quest)", questId);
                }
                for (int questId : data2.finishedQuests.keySet()) {
                    Quest quest = QuestController.instance.quests.get(questId);
                    if (quest == null) {
                        continue;
                    }
                    map.put(quest.category.title + ": " + quest.title + "(Finished quest)", questId);
                }
            }
            else if (type == EnumPlayerData.Transport) {
                PlayerTransportData data3 = playerdata.transportData;
                for (int questId : data3.transports) {
                    TransportLocation location = TransportController.getInstance().getTransport(questId);
                    if (location == null) {
                        continue;
                    }
                    map.put(location.category.title + ": " + location.name, questId);
                }
            }
            else if (type == EnumPlayerData.Bank) {
                PlayerBankData data4 = playerdata.bankData;
                for (int bankId : data4.banks.keySet()) {
                    Bank bank = BankController.getInstance().banks.get(bankId);
                    if (bank == null) {
                        continue;
                    }
                    map.put(bank.name, bankId);
                }
            }
            else if (type == EnumPlayerData.Factions) {
                PlayerFactionData data5 = playerdata.factionData;
                for (int factionId : data5.factionData.keySet()) {
                    Faction faction = FactionController.instance.factions.get(factionId);
                    if (faction == null) {
                        continue;
                    }
                    map.put(faction.name + "(" + data5.getFactionPoints((EntityPlayer)player, factionId) + ")", factionId);
                }
            }
        }
        sendScrollData(player, map);
    }
    
    public static void removePlayerData(ByteBuf buffer, EntityPlayerMP player) throws IOException {
        int id = buffer.readInt();
        if (EnumPlayerData.values().length <= id) {
            return;
        }
        String name = Server.readString(buffer);
        if (name == null || name.isEmpty()) {
            return;
        }
        EnumPlayerData type = EnumPlayerData.values()[id];
        EntityPlayer pl = (EntityPlayer)player.getServer().getPlayerList().getPlayerByUsername(name);
        PlayerData playerdata = null;
        if (pl == null) {
            playerdata = PlayerDataController.instance.getDataFromUsername(player.getServer(), name);
        }
        else {
            playerdata = PlayerData.get(pl);
        }
        if (type == EnumPlayerData.Players) {
            File file = new File(CustomNpcs.getWorldSaveDirectory("playerdata"), playerdata.uuid + ".json");
            if (file.exists()) {
                file.delete();
            }
            if (pl != null) {
                playerdata.setNBT(new NBTTagCompound());
                sendPlayerData(type, player, name);
                playerdata.save(true);
                return;
            }
            PlayerDataController.instance.nameUUIDs.remove(name);
        }
        if (type == EnumPlayerData.Quest) {
            PlayerQuestData data = playerdata.questData;
            int questId = buffer.readInt();
            data.activeQuests.remove(questId);
            data.finishedQuests.remove(questId);
            playerdata.save(true);
        }
        if (type == EnumPlayerData.Dialog) {
            PlayerDialogData data2 = playerdata.dialogData;
            data2.dialogsRead.remove(buffer.readInt());
            playerdata.save(true);
        }
        if (type == EnumPlayerData.Transport) {
            PlayerTransportData data3 = playerdata.transportData;
            data3.transports.remove(buffer.readInt());
            playerdata.save(true);
        }
        if (type == EnumPlayerData.Bank) {
            PlayerBankData data4 = playerdata.bankData;
            data4.banks.remove(buffer.readInt());
            playerdata.save(true);
        }
        if (type == EnumPlayerData.Factions) {
            PlayerFactionData data5 = playerdata.factionData;
            data5.factionData.remove(buffer.readInt());
            playerdata.save(true);
        }
        if (pl != null) {
            SyncController.syncPlayer((EntityPlayerMP)pl);
        }
        sendPlayerData(type, player, name);
    }
    
    public static void sendRecipeData(EntityPlayerMP player, int size) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        if (size == 3) {
            for (RecipeCarpentry recipe : RecipeController.instance.globalRecipes.values()) {
                map.put(recipe.name, recipe.id);
            }
        }
        else {
            for (RecipeCarpentry recipe : RecipeController.instance.anvilRecipes.values()) {
                map.put(recipe.name, recipe.id);
            }
        }
        sendScrollData(player, map);
    }
    
    public static void sendScrollData(EntityPlayerMP player, Map<String, Integer> map) {
        Map<String, Integer> send = new HashMap<String, Integer>();
        for (String key : map.keySet()) {
            send.put(key, map.get(key));
            if (send.size() == 100) {
                Server.sendData(player, EnumPacketClient.SCROLL_DATA_PART, send);
                send = new HashMap<String, Integer>();
            }
        }
        Server.sendData(player, EnumPacketClient.SCROLL_DATA, send);
    }
    
    public static void sendTransportCategoryData(EntityPlayerMP player) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (TransportCategory category : TransportController.getInstance().categories.values()) {
            map.put(category.title, category.id);
        }
        sendScrollData(player, map);
    }
    
    public static void sendTransportData(EntityPlayerMP player, int categoryid) {
        TransportCategory category = TransportController.getInstance().categories.get(categoryid);
        if (category == null) {
            return;
        }
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (TransportLocation transport : category.locations.values()) {
            map.put(transport.name, transport.id);
        }
        sendScrollData(player, map);
    }
    
    public static void sendNpcDialogs(EntityPlayer player) {
        EntityNPCInterface npc = getEditingNpc(player);
        if (npc == null) {
            return;
        }
        for (int pos : npc.dialogs.keySet()) {
            DialogOption option = npc.dialogs.get(pos);
            if (option != null) {
                if (!option.hasDialog()) {
                    continue;
                }
                NBTTagCompound compound = option.writeNBT();
                compound.setInteger("Position", pos);
                Server.sendData((EntityPlayerMP)player, EnumPacketClient.GUI_DATA, compound);
            }
        }
    }
    
    public static DialogOption setNpcDialog(int slot, int dialogId, EntityPlayer player) {
        EntityNPCInterface npc = getEditingNpc(player);
        if (npc == null) {
            return null;
        }
        if (!npc.dialogs.containsKey(slot)) {
            npc.dialogs.put(slot, new DialogOption());
        }
        DialogOption option = npc.dialogs.get(slot);
        option.dialogId = dialogId;
        option.optionType = 1;
        if (option.hasDialog()) {
            option.title = option.getDialog().title;
        }
        return option;
    }
    
    public static TileEntity saveTileEntity(EntityPlayerMP player, NBTTagCompound compound) {
        int x = compound.getInteger("x");
        int y = compound.getInteger("y");
        int z = compound.getInteger("z");
        TileEntity tile = player.world.getTileEntity(new BlockPos(x, y, z));
        if (tile != null) {
            tile.readFromNBT(compound);
        }
        return tile;
    }
    
    public static void setRecipeGui(EntityPlayerMP player, RecipeCarpentry recipe) {
        if (recipe == null) {
            return;
        }
        if (!(player.openContainer instanceof ContainerManageRecipes)) {
            return;
        }
        ContainerManageRecipes container = (ContainerManageRecipes)player.openContainer;
        container.setRecipe(recipe);
        Server.sendData(player, EnumPacketClient.GUI_DATA, recipe.writeNBT());
    }
    
    public static void sendBank(EntityPlayerMP player, Bank bank) {
        NBTTagCompound compound = new NBTTagCompound();
        bank.writeEntityToNBT(compound);
        Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        if (player.openContainer instanceof ContainerManageBanks) {
            ((ContainerManageBanks)player.openContainer).setBank(bank);
        }
        player.sendAllContents(player.openContainer, player.openContainer.getInventory());
    }
    
    public static void sendNearbyNpcs(EntityPlayerMP player) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Entity entity : player.world.loadedEntityList) {
            if (entity instanceof EntityNPCInterface) {
                EntityNPCInterface npc = (EntityNPCInterface)entity;
                if (npc.isDead) {
                    continue;
                }
                float distance = player.getDistance((Entity)npc);
                DecimalFormat df = new DecimalFormat("#.#");
                String s = df.format(distance);
                if (distance < 10.0f) {
                    s = "0" + s;
                }
                map.put(s + " : " + npc.display.getName(), npc.getEntityId());
            }
        }
        sendScrollData(player, map);
    }
    
    public static void sendGuiError(EntityPlayer player, int i) {
        Server.sendData((EntityPlayerMP)player, EnumPacketClient.GUI_ERROR, i, new NBTTagCompound());
    }
    
    public static void sendGuiClose(EntityPlayerMP player, int i, NBTTagCompound comp) {
        Server.sendData(player, EnumPacketClient.GUI_CLOSE, i, comp);
    }
    
    public static Entity spawnClone(NBTTagCompound compound, double x, double y, double z, World world) {
        ServerCloneController.Instance.cleanTags(compound);
        compound.setTag("Pos", (NBTBase)NBTTags.nbtDoubleList(x, y, z));
        Entity entity = EntityList.createEntityFromNBT(compound, world);
        if (entity == null) {
            return null;
        }
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            npc.ais.setStartPos(new BlockPos((Entity)npc));
        }
        world.spawnEntity(entity);
        return entity;
    }
    
    public static boolean isOp(EntityPlayer player) {
        return player.getServer().getPlayerList().canSendCommands(player.getGameProfile());
    }
    
    public static void GivePlayerItem(Entity entity, EntityPlayer player, ItemStack item) {
        if (entity.world.isRemote || item == null || item.isEmpty()) {
            return;
        }
        item = item.copy();
        float f = 0.7f;
        double d = entity.world.rand.nextFloat() * f + (double)(1.0f - f);
        double d2 = entity.world.rand.nextFloat() * f + (double)(1.0f - f);
        double d3 = entity.world.rand.nextFloat() * f + (double)(1.0f - f);
        EntityItem entityitem = new EntityItem(entity.world, entity.posX + d, entity.posY + d2, entity.posZ + d3, item);
        entityitem.setPickupDelay(2);
        entity.world.spawnEntity((Entity)entityitem);
        int i = item.getCount();
        if (player.inventory.addItemStackToInventory(item)) {
            entity.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.onItemPickup((Entity)entityitem, i);
            PlayerQuestData playerdata = PlayerData.get(player).questData;
            playerdata.checkQuestCompletion(player, 0);
            if (item.getCount() <= 0) {
                entityitem.setDead();
            }
        }
    }
    
    public static BlockPos GetClosePos(BlockPos origin, World world) {
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                for (int y = 2; y >= -2; --y) {
                    BlockPos pos = origin.add(x, y, z);
                    if (world.isSideSolid(pos, EnumFacing.UP) && world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2))) {
                        return pos.up();
                    }
                }
            }
        }
        return world.getTopSolidOrLiquidBlock(origin);
    }
    
    public static void NotifyOPs(String message, Object... obs) {
        TextComponentTranslation chatcomponenttranslation = new TextComponentTranslation(message, obs);
        chatcomponenttranslation.getStyle().setColor(TextFormatting.GRAY);
        chatcomponenttranslation.getStyle().setItalic(Boolean.valueOf(true));
        for (EntityPlayer entityplayer : CustomNpcs.Server.getPlayerList().getPlayers()) {
            if (entityplayer.sendCommandFeedback() && isOp(entityplayer)) {
                entityplayer.sendMessage((ITextComponent)chatcomponenttranslation);
            }
        }
        if (CustomNpcs.Server.worlds[0].getGameRules().getBoolean("logAdminCommands")) {
            LogWriter.info(chatcomponenttranslation.getUnformattedText());
        }
    }
    
    public static void playSound(EntityLivingBase entity, SoundEvent sound, float volume, float pitch) {
        entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, sound, SoundCategory.NEUTRAL, volume, pitch);
    }
    
    public static void playSound(World world, BlockPos pos, SoundEvent sound, SoundCategory cat, float volume, float pitch) {
        world.playSound((EntityPlayer)null, pos, sound, cat, volume, pitch);
    }
    
    public static EntityPlayer getPlayer(MinecraftServer minecraftserver, UUID id) {
        List<EntityPlayerMP> list = (List<EntityPlayerMP>)minecraftserver.getPlayerList().getPlayers();
        for (EntityPlayer player : list) {
            if (id.equals(player.getUniqueID())) {
                return player;
            }
        }
        return null;
    }
    
    public static Entity GetDamageSourcee(DamageSource damagesource) {
        Entity entity = damagesource.getTrueSource();
        if (entity == null) {
            entity = damagesource.getImmediateSource();
        }
        if (entity instanceof EntityArrow && ((EntityArrow)entity).shootingEntity instanceof EntityLivingBase) {
            entity = ((EntityArrow)entity).shootingEntity;
        }
        else if (entity instanceof EntityThrowable) {
            entity = (Entity)((EntityThrowable)entity).getThrower();
        }
        return entity;
    }
    
    public static boolean IsItemStackNull(ItemStack is) {
        return is == null || is.isEmpty() || is == ItemStack.EMPTY || is.getItem() == null;
    }
    
    public static ItemStack ChangeItemStack(ItemStack is, Item item) {
        NBTTagCompound comp = is.writeToNBT(new NBTTagCompound());
        ResourceLocation resourcelocation = (ResourceLocation)Item.REGISTRY.getNameForObject(item);
        comp.setString("id", (resourcelocation == null) ? "minecraft:air" : resourcelocation.toString());
        return new ItemStack(comp);
    }
    
    static {
        NoppesUtilServer.editingQuests = new HashMap<UUID, Quest>();
        NoppesUtilServer.editingQuestsClient = new HashMap<UUID, Quest>();
    }
}
