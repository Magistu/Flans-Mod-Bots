package noppes.npcs;

import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.controllers.data.ForgeScriptData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldProvider;
import java.util.Set;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.controllers.data.DialogOption;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import java.util.Map;
import net.minecraftforge.common.DimensionManager;
import java.util.HashMap;
import noppes.npcs.controllers.ScriptController;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.EntityList;
import java.util.List;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.passive.EntityVillager;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.roles.JobSpawner;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.SpawnData;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.BankController;
import noppes.npcs.constants.EnumPacketClient;
import java.util.ArrayList;
import noppes.npcs.controllers.LinkedNpcController;
import net.minecraft.command.ICommandSender;
import noppes.npcs.entity.data.DataScenes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import noppes.npcs.util.IPermission;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.item.ItemStack;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class PacketHandlerServer
{
    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).player;
        if (CustomNpcs.OpsOnly && !NoppesUtilServer.isOp((EntityPlayer)player)) {
            this.warn((EntityPlayer)player, "tried to use custom npcs without being an op");
            return;
        }
        ByteBuf buffer = event.getPacket().payload();
        player.getServer().addScheduledTask(() -> {
        	EnumPacketServer type = null;
            try {
                type = EnumPacketServer.values()[buffer.readInt()];
                LogWriter.debug("Received: " + type);
                ItemStack item = player.inventory.getCurrentItem();
                EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
                if (!type.needsNpc || npc != null) {
                    if (!type.hasPermission() || CustomNpcsPermissions.hasPermission((EntityPlayer)player, type.permission)) {
                        if (!type.isExempt() && !this.allowItem(item, type)) {
                            this.warn((EntityPlayer)player, "tried to use custom npcs without a tool in hand, possibly a hacker");
                        }
                        else {
                            this.handlePacket(type, buffer, player, npc);
                        }
                    }
                }
            }
            catch (Exception e) {
                LogWriter.error("Error with EnumPacketServer." + type, e);
            }
            finally {
                buffer.release();
            }
        });
    }
    
    private boolean allowItem(ItemStack stack, EnumPacketServer type) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        Item item = stack.getItem();
        IPermission permission = null;
        if (item instanceof IPermission) {
            permission = (IPermission)item;
        }
        else if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof IPermission) {
            permission = (IPermission)((ItemBlock)item).getBlock();
        }
        return permission != null && permission.isAllowed(type);
    }
    
    private void handlePacket(EnumPacketServer type, ByteBuf buffer, EntityPlayerMP player, EntityNPCInterface npc) throws Exception {
        if (type == EnumPacketServer.Delete) {
            npc.delete();
            NoppesUtilServer.deleteNpc(npc, (EntityPlayer)player);
        }
        else if (type == EnumPacketServer.SceneStart) {
            if (CustomNpcs.SceneButtonsEnabled) {
                DataScenes.Toggle((ICommandSender)player, buffer.readInt() + "btn");
            }
        }
        else if (type == EnumPacketServer.SceneReset) {
            if (CustomNpcs.SceneButtonsEnabled) {
                DataScenes.Reset((ICommandSender)player, null);
            }
        }
        else if (type == EnumPacketServer.LinkedAdd) {
            LinkedNpcController.Instance.addData(Server.readString(buffer));
            List<String> list = new ArrayList<String>();
            for (LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list) {
                list.add(data.name);
            }
            Server.sendData(player, EnumPacketClient.SCROLL_LIST, list);
        }
        else if (type == EnumPacketServer.LinkedRemove) {
            LinkedNpcController.Instance.removeData(Server.readString(buffer));
            List<String> list = new ArrayList<String>();
            for (LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list) {
                list.add(data.name);
            }
            Server.sendData(player, EnumPacketClient.SCROLL_LIST, list);
        }
        else if (type == EnumPacketServer.LinkedGetAll) {
            List<String> list = new ArrayList<String>();
            for (LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list) {
                list.add(data.name);
            }
            Server.sendData(player, EnumPacketClient.SCROLL_LIST, list);
            if (npc != null) {
                Server.sendData(player, EnumPacketClient.SCROLL_SELECTED, npc.linkedName);
            }
        }
        else if (type == EnumPacketServer.LinkedSet) {
            npc.linkedName = Server.readString(buffer);
            LinkedNpcController.Instance.loadNpcData(npc);
        }
        else if (type == EnumPacketServer.NpcMenuClose) {
            npc.reset();
            if (npc.linkedData != null) {
                LinkedNpcController.Instance.saveNpcData(npc);
            }
            NoppesUtilServer.setEditingNpc((EntityPlayer)player, null);
        }
        else if (type == EnumPacketServer.BanksGet) {
            NoppesUtilServer.sendBankDataAll(player);
        }
        else if (type == EnumPacketServer.BankGet) {
            Bank bank = BankController.getInstance().getBank(buffer.readInt());
            NoppesUtilServer.sendBank(player, bank);
        }
        else if (type == EnumPacketServer.BankSave) {
            Bank bank = new Bank();
            bank.readEntityFromNBT(Server.readNBT(buffer));
            BankController.getInstance().saveBank(bank);
            NoppesUtilServer.sendBankDataAll(player);
            NoppesUtilServer.sendBank(player, bank);
        }
        else if (type == EnumPacketServer.BankRemove) {
            BankController.getInstance().removeBank(buffer.readInt());
            NoppesUtilServer.sendBankDataAll(player);
            NoppesUtilServer.sendBank(player, new Bank());
        }
        else if (type == EnumPacketServer.RemoteMainMenu) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                return;
            }
            NoppesUtilServer.sendOpenGui((EntityPlayer)player, EnumGuiType.MainMenuDisplay, (EntityNPCInterface)entity);
        }
        else if (type == EnumPacketServer.RemoteDelete) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                return;
            }
            npc = (EntityNPCInterface)entity;
            npc.delete();
            NoppesUtilServer.deleteNpc(npc, (EntityPlayer)player);
            NoppesUtilServer.sendNearbyNpcs(player);
        }
        else if (type == EnumPacketServer.RemoteNpcsGet) {
            NoppesUtilServer.sendNearbyNpcs(player);
            Server.sendData(player, EnumPacketClient.SCROLL_SELECTED, CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs");
        }
        else if (type == EnumPacketServer.RemoteFreeze) {
            CustomNpcs.FreezeNPCs = !CustomNpcs.FreezeNPCs;
            Server.sendData(player, EnumPacketClient.SCROLL_SELECTED, CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs");
        }
        else if (type == EnumPacketServer.RemoteReset) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                return;
            }
            npc = (EntityNPCInterface)entity;
            npc.reset();
        }
        else if (type == EnumPacketServer.RemoteTpToNpc) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                return;
            }
            npc = (EntityNPCInterface)entity;
            player.connection.setPlayerLocation(npc.posX, npc.posY, npc.posZ, 0.0f, 0.0f);
        }
        else if (type == EnumPacketServer.Gui) {
            EnumGuiType gui = EnumGuiType.values()[buffer.readInt()];
            int i = buffer.readInt();
            int j = buffer.readInt();
            int k = buffer.readInt();
            NoppesUtilServer.sendOpenGui((EntityPlayer)player, gui, npc, i, j, k);
        }
        else if (type == EnumPacketServer.RecipesGet) {
            NoppesUtilServer.sendRecipeData(player, buffer.readInt());
        }
        else if (type == EnumPacketServer.RecipeGet) {
            RecipeCarpentry recipe = RecipeController.instance.getRecipe(buffer.readInt());
            NoppesUtilServer.setRecipeGui(player, recipe);
        }
        else if (type == EnumPacketServer.RecipeRemove) {
            RecipeCarpentry recipe = RecipeController.instance.delete(buffer.readInt());
            NoppesUtilServer.sendRecipeData(player, recipe.isGlobal ? 3 : 4);
            NoppesUtilServer.setRecipeGui(player, new RecipeCarpentry(""));
        }
        else if (type == EnumPacketServer.RecipeSave) {
            RecipeCarpentry recipe = RecipeCarpentry.read(Server.readNBT(buffer));
            RecipeController.instance.saveRecipe(recipe);
            NoppesUtilServer.sendRecipeData(player, recipe.isGlobal ? 3 : 4);
            NoppesUtilServer.setRecipeGui(player, recipe);
        }
        else if (type == EnumPacketServer.NaturalSpawnGetAll) {
            NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
        }
        else if (type == EnumPacketServer.NaturalSpawnGet) {
            SpawnData spawn = SpawnController.instance.getSpawnData(buffer.readInt());
            if (spawn != null) {
                Server.sendData(player, EnumPacketClient.GUI_DATA, spawn.writeNBT(new NBTTagCompound()));
            }
        }
        else if (type == EnumPacketServer.NaturalSpawnSave) {
            SpawnData data2 = new SpawnData();
            data2.readNBT(Server.readNBT(buffer));
            SpawnController.instance.saveSpawnData(data2);
            NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
        }
        else if (type == EnumPacketServer.NaturalSpawnRemove) {
            SpawnController.instance.removeSpawnData(buffer.readInt());
            NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
        }
        else if (type == EnumPacketServer.DialogCategorySave) {
            DialogCategory category = new DialogCategory();
            category.readNBT(Server.readNBT(buffer));
            DialogController.instance.saveCategory(category);
            Server.sendData(player, EnumPacketClient.GUI_UPDATE, new Object[0]);
        }
        else if (type == EnumPacketServer.DialogCategoryRemove) {
            DialogController.instance.removeCategory(buffer.readInt());
            Server.sendData(player, EnumPacketClient.GUI_UPDATE, new Object[0]);
        }
        else if (type == EnumPacketServer.DialogSave) {
            DialogCategory category = DialogController.instance.categories.get(buffer.readInt());
            if (category == null) {
                return;
            }
            Dialog dialog = new Dialog(category);
            dialog.readNBT(Server.readNBT(buffer));
            DialogController.instance.saveDialog(category, dialog);
            Server.sendData(player, EnumPacketClient.GUI_UPDATE, new Object[0]);
        }
        else if (type == EnumPacketServer.DialogRemove) {
            Dialog dialog2 = DialogController.instance.dialogs.get(buffer.readInt());
            if (dialog2 != null && dialog2.category != null) {
                DialogController.instance.removeDialog(dialog2);
                Server.sendData(player, EnumPacketClient.GUI_UPDATE, new Object[0]);
            }
        }
        else if (type == EnumPacketServer.QuestOpenGui) {
            Quest quest = new Quest(null);
            int gui2 = buffer.readInt();
            quest.readNBT(Server.readNBT(buffer));
            NoppesUtilServer.setEditingQuest((EntityPlayer)player, quest);
            player.openGui((Object)CustomNpcs.instance, gui2, player.world, 0, 0, 0);
        }
        else if (type == EnumPacketServer.DialogNpcGet) {
            NoppesUtilServer.sendNpcDialogs((EntityPlayer)player);
        }
        else if (type == EnumPacketServer.DialogNpcSet) {
            int slot = buffer.readInt();
            int dialog3 = buffer.readInt();
            DialogOption option = NoppesUtilServer.setNpcDialog(slot, dialog3, (EntityPlayer)player);
            if (option != null && option.hasDialog()) {
                NBTTagCompound compound = option.writeNBT();
                compound.setInteger("Position", slot);
                Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
            }
        }
        else if (type == EnumPacketServer.DialogNpcRemove) {
            npc.dialogs.remove(buffer.readInt());
        }
        else if (type == EnumPacketServer.QuestCategorySave) {
            QuestCategory category2 = new QuestCategory();
            category2.readNBT(Server.readNBT(buffer));
            QuestController.instance.saveCategory(category2);
            Server.sendData(player, EnumPacketClient.GUI_UPDATE, new Object[0]);
        }
        else if (type == EnumPacketServer.QuestCategoryRemove) {
            QuestController.instance.removeCategory(buffer.readInt());
            Server.sendData(player, EnumPacketClient.GUI_UPDATE, new Object[0]);
        }
        else if (type == EnumPacketServer.QuestSave) {
            QuestCategory category2 = QuestController.instance.categories.get(buffer.readInt());
            if (category2 == null) {
                return;
            }
            Quest quest2 = new Quest(category2);
            quest2.readNBT(Server.readNBT(buffer));
            QuestController.instance.saveQuest(category2, quest2);
            Server.sendData(player, EnumPacketClient.GUI_UPDATE, new Object[0]);
        }
        else if (type == EnumPacketServer.QuestDialogGetTitle) {
            Dialog quest3 = DialogController.instance.dialogs.get(buffer.readInt());
            Dialog quest4 = DialogController.instance.dialogs.get(buffer.readInt());
            Dialog quest5 = DialogController.instance.dialogs.get(buffer.readInt());
            NBTTagCompound compound = new NBTTagCompound();
            if (quest3 != null) {
                compound.setString("1", quest3.title);
            }
            if (quest4 != null) {
                compound.setString("2", quest4.title);
            }
            if (quest5 != null) {
                compound.setString("3", quest5.title);
            }
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
        }
        else if (type == EnumPacketServer.QuestRemove) {
            Quest quest = QuestController.instance.quests.get(buffer.readInt());
            if (quest != null) {
                QuestController.instance.removeQuest(quest);
                Server.sendData(player, EnumPacketClient.GUI_UPDATE, new Object[0]);
            }
        }
        else if (type == EnumPacketServer.TransportCategoriesGet) {
            NoppesUtilServer.sendTransportCategoryData(player);
        }
        else if (type == EnumPacketServer.TransportCategorySave) {
            TransportController.getInstance().saveCategory(Server.readString(buffer), buffer.readInt());
        }
        else if (type == EnumPacketServer.TransportCategoryRemove) {
            TransportController.getInstance().removeCategory(buffer.readInt());
            NoppesUtilServer.sendTransportCategoryData(player);
        }
        else if (type == EnumPacketServer.TransportRemove) {
            int id = buffer.readInt();
            TransportLocation loc = TransportController.getInstance().removeLocation(id);
            if (loc != null) {
                NoppesUtilServer.sendTransportData(player, loc.category.id);
            }
        }
        else if (type == EnumPacketServer.TransportsGet) {
            NoppesUtilServer.sendTransportData(player, buffer.readInt());
        }
        else if (type == EnumPacketServer.TransportSave) {
            int cat = buffer.readInt();
            TransportLocation location = TransportController.getInstance().saveLocation(cat, Server.readNBT(buffer), player, npc);
            if (location != null) {
                if (npc.advanced.role != 4) {
                    return;
                }
                RoleTransporter role = (RoleTransporter)npc.roleInterface;
                role.setTransport(location);
            }
        }
        else if (type == EnumPacketServer.TransportGetLocation) {
            if (npc.advanced.role != 4) {
                return;
            }
            RoleTransporter role2 = (RoleTransporter)npc.roleInterface;
            if (role2.hasTransport()) {
                Server.sendData(player, EnumPacketClient.GUI_DATA, role2.getLocation().writeNBT());
                Server.sendData(player, EnumPacketClient.SCROLL_SELECTED, role2.getLocation().category.title);
            }
        }
        else if (type == EnumPacketServer.FactionSet) {
            npc.setFaction(buffer.readInt());
        }
        else if (type == EnumPacketServer.FactionSave) {
            Faction faction = new Faction();
            faction.readNBT(Server.readNBT(buffer));
            FactionController.instance.saveFaction(faction);
            NoppesUtilServer.sendFactionDataAll(player);
            NBTTagCompound compound2 = new NBTTagCompound();
            faction.writeNBT(compound2);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound2);
        }
        else if (type == EnumPacketServer.FactionRemove) {
            FactionController.instance.delete(buffer.readInt());
            NoppesUtilServer.sendFactionDataAll(player);
            NBTTagCompound compound3 = new NBTTagCompound();
            new Faction().writeNBT(compound3);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound3);
        }
        else if (type == EnumPacketServer.PlayerDataGet) {
            int id = buffer.readInt();
            if (EnumPlayerData.values().length <= id) {
                return;
            }
            String name = null;
            EnumPlayerData datatype = EnumPlayerData.values()[id];
            if (datatype != EnumPlayerData.Players) {
                name = Server.readString(buffer);
            }
            NoppesUtilServer.sendPlayerData(datatype, player, name);
        }
        else if (type == EnumPacketServer.PlayerDataRemove) {
            NoppesUtilServer.removePlayerData(buffer, player);
        }
        else if (type == EnumPacketServer.MainmenuDisplayGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.display.writeToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPacketServer.MainmenuDisplaySave) {
            npc.display.readToNBT(Server.readNBT(buffer));
            npc.updateClient = true;
        }
        else if (type == EnumPacketServer.MainmenuStatsGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.stats.writeToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPacketServer.MainmenuStatsSave) {
            npc.stats.readToNBT(Server.readNBT(buffer));
            npc.updateClient = true;
        }
        else if (type == EnumPacketServer.MainmenuInvGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.inventory.writeEntityToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPacketServer.MainmenuInvSave) {
            npc.inventory.readEntityFromNBT(Server.readNBT(buffer));
            npc.updateAI = true;
            npc.updateClient = true;
        }
        else if (type == EnumPacketServer.MainmenuAIGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.ais.writeToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPacketServer.MainmenuAISave) {
            npc.ais.readToNBT(Server.readNBT(buffer));
            npc.setHealth(npc.getMaxHealth());
            npc.updateAI = true;
            npc.updateClient = true;
        }
        else if (type == EnumPacketServer.MainmenuAdvancedGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.advanced.writeToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPacketServer.MainmenuAdvancedSave) {
            npc.advanced.readToNBT(Server.readNBT(buffer));
            npc.updateAI = true;
            npc.updateClient = true;
        }
        else if (type == EnumPacketServer.MainmenuAdvancedMarkData) {
            MarkData data3 = MarkData.get((EntityLivingBase)npc);
            data3.setNBT(Server.readNBT(buffer));
            data3.syncClients();
        }
        else if (type == EnumPacketServer.JobSave) {
            NBTTagCompound original = npc.jobInterface.writeToNBT(new NBTTagCompound());
            NBTTagCompound compound2 = Server.readNBT(buffer);
            Set<String> names = (Set<String>)compound2.getKeySet();
            for (String name2 : names) {
                original.setTag(name2, compound2.getTag(name2));
            }
            npc.jobInterface.readFromNBT(original);
            npc.updateClient = true;
        }
        else if (type == EnumPacketServer.JobGet) {
            if (npc.jobInterface == null) {
                return;
            }
            NBTTagCompound compound3 = new NBTTagCompound();
            compound3.setBoolean("JobData", true);
            npc.jobInterface.writeToNBT(compound3);
            if (npc.advanced.job == 6) {
                ((JobSpawner)npc.jobInterface).cleanCompound(compound3);
            }
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound3);
            if (npc.advanced.job == 6) {
                Server.sendData(player, EnumPacketClient.GUI_DATA, ((JobSpawner)npc.jobInterface).getTitles());
            }
        }
        else if (type == EnumPacketServer.JobSpawnerAdd) {
            if (npc.advanced.job != 6) {
                return;
            }
            JobSpawner job = (JobSpawner)npc.jobInterface;
            if (buffer.readBoolean()) {
                NBTTagCompound compound2 = ServerCloneController.Instance.getCloneData(null, Server.readString(buffer), buffer.readInt());
                job.setJobCompound(buffer.readInt(), compound2);
            }
            else {
                job.setJobCompound(buffer.readInt(), Server.readNBT(buffer));
            }
            Server.sendData(player, EnumPacketClient.GUI_DATA, job.getTitles());
        }
        else if (type == EnumPacketServer.RoleCompanionUpdate) {
            if (npc.advanced.role != 6) {
                return;
            }
            ((RoleCompanion)npc.roleInterface).matureTo(EnumCompanionStage.values()[buffer.readInt()]);
            npc.updateClient = true;
        }
        else if (type == EnumPacketServer.JobSpawnerRemove) {
            if (npc.advanced.job != 6) {
                return;
            }
        }
        else if (type == EnumPacketServer.RoleSave) {
            npc.roleInterface.readFromNBT(Server.readNBT(buffer));
            npc.updateClient = true;
        }
        else if (type == EnumPacketServer.RoleGet) {
            if (npc.roleInterface == null) {
                return;
            }
            NBTTagCompound compound3 = new NBTTagCompound();
            compound3.setBoolean("RoleData", true);
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.roleInterface.writeToNBT(compound3));
        }
        else if (type == EnumPacketServer.MerchantUpdate) {
            Entity entity = player.world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityVillager)) {
                return;
            }
            MerchantRecipeList list2 = MerchantRecipeList.readFromBuf(new PacketBuffer(buffer));
            ((EntityVillager)entity).setRecipes(list2);
        }
        else if (type == EnumPacketServer.ModelDataSave) {
            if (npc instanceof EntityCustomNpc) {
                ((EntityCustomNpc)npc).modelData.readFromNBT(Server.readNBT(buffer));
            }
        }
        else if (type == EnumPacketServer.MailOpenSetup) {
            PlayerMail mail = new PlayerMail();
            mail.readNBT(Server.readNBT(buffer));
            ContainerMail.staticmail = mail;
            player.openGui((Object)CustomNpcs.instance, EnumGuiType.PlayerMailman.ordinal(), player.world, 1, 0, 0);
        }
        else if (type == EnumPacketServer.TransformSave) {
            boolean isValid = npc.transform.isValid();
            npc.transform.readOptions(Server.readNBT(buffer));
            if (isValid != npc.transform.isValid()) {
                npc.updateAI = true;
            }
        }
        else if (type == EnumPacketServer.TransformGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.transform.writeOptions(new NBTTagCompound()));
        }
        else if (type == EnumPacketServer.TransformLoad) {
            if (npc.transform.isValid()) {
                npc.transform.transform(buffer.readBoolean());
            }
        }
        else if (type == EnumPacketServer.TraderMarketSave) {
            String market = Server.readString(buffer);
            boolean bo = buffer.readBoolean();
            if (npc.roleInterface instanceof RoleTrader) {
                if (bo) {
                    RoleTrader.setMarket(npc, market);
                }
                else {
                    RoleTrader.save((RoleTrader)npc.roleInterface, market);
                }
            }
        }
        else if (type == EnumPacketServer.MovingPathGet) {
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.ais.writeToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPacketServer.MovingPathSave) {
            npc.ais.setMovingPath(NBTTags.getIntegerArraySet(Server.readNBT(buffer).getTagList("MovingPathNew", 10)));
        }
        else if (type == EnumPacketServer.SpawnRider) {
            Entity entity = EntityList.createEntityFromNBT(Server.readNBT(buffer), player.world);
            player.world.spawnEntity(entity);
            entity.startRiding(ServerEventsHandler.mounted, true);
        }
        else if (type == EnumPacketServer.PlayerRider) {
            player.startRiding(ServerEventsHandler.mounted, true);
        }
        else if (type == EnumPacketServer.SpawnMob) {
            boolean server = buffer.readBoolean();
            int x = buffer.readInt();
            int y = buffer.readInt();
            int z = buffer.readInt();
            NBTTagCompound compound4;
            if (server) {
                compound4 = ServerCloneController.Instance.getCloneData((ICommandSender)player, Server.readString(buffer), buffer.readInt());
            }
            else {
                compound4 = Server.readNBT(buffer);
            }
            if (compound4 == null) {
                return;
            }
            Entity entity2 = NoppesUtilServer.spawnClone(compound4, x + 0.5, y + 1, z + 0.5, player.world);
            if (entity2 == null) {
                player.sendMessage((ITextComponent)new TextComponentString("Failed to create an entity out of your clone"));
            }
        }
        else if (type == EnumPacketServer.MobSpawner) {
            boolean server = buffer.readBoolean();
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            NBTTagCompound compound5;
            if (server) {
                compound5 = ServerCloneController.Instance.getCloneData((ICommandSender)player, Server.readString(buffer), buffer.readInt());
            }
            else {
                compound5 = Server.readNBT(buffer);
            }
            if (compound5 != null) {
                NoppesUtilServer.createMobSpawner(pos, compound5, (EntityPlayer)player);
            }
        }
        else if (type == EnumPacketServer.ClonePreSave) {
            boolean bo2 = ServerCloneController.Instance.getCloneData(null, Server.readString(buffer), buffer.readInt()) != null;
            NBTTagCompound compound2 = new NBTTagCompound();
            compound2.setBoolean("NameExists", bo2);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound2);
        }
        else if (type == EnumPacketServer.CloneSave) {
            PlayerData data4 = PlayerData.get((EntityPlayer)player);
            if (data4.cloned == null) {
                return;
            }
            ServerCloneController.Instance.addClone(data4.cloned, Server.readString(buffer), buffer.readInt());
        }
        else if (type == EnumPacketServer.CloneRemove) {
            int tab = buffer.readInt();
            ServerCloneController.Instance.removeClone(Server.readString(buffer), tab);
            NBTTagList list3 = new NBTTagList();
            for (String name3 : ServerCloneController.Instance.getClones(tab)) {
                list3.appendTag((NBTBase)new NBTTagString(name3));
            }
            NBTTagCompound compound5 = new NBTTagCompound();
            compound5.setTag("List", (NBTBase)list3);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound5);
        }
        else if (type == EnumPacketServer.CloneList) {
            NBTTagList list4 = new NBTTagList();
            for (String name4 : ServerCloneController.Instance.getClones(buffer.readInt())) {
                list4.appendTag((NBTBase)new NBTTagString(name4));
            }
            NBTTagCompound compound2 = new NBTTagCompound();
            compound2.setTag("List", (NBTBase)list4);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound2);
        }
        else if (type == EnumPacketServer.ScriptDataSave) {
            npc.script.readFromNBT(Server.readNBT(buffer));
            npc.updateAI = true;
            npc.script.lastInited = -1L;
        }
        else if (type == EnumPacketServer.ScriptDataGet) {
            NBTTagCompound compound3 = npc.script.writeToNBT(new NBTTagCompound());
            compound3.setTag("Languages", (NBTBase)ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound3);
        }
        else if (type == EnumPacketServer.DimensionsGet) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            for (int id2 : DimensionManager.getStaticDimensionIDs()) {
                WorldProvider provider = DimensionManager.createProviderFor(id2);
                map.put(provider.getDimensionType().getName(), id2);
            }
            NoppesUtilServer.sendScrollData(player, map);
        }
        else if (type == EnumPacketServer.DimensionTeleport) {
            int dimension = buffer.readInt();
            WorldServer world = player.getServer().getWorld(dimension);
            BlockPos coords = world.getSpawnCoordinate();
            if (coords == null) {
                coords = world.getSpawnPoint();
                if (!world.isAirBlock(coords)) {
                    coords = world.getTopSolidOrLiquidBlock(coords);
                }
                else {
                    while (world.isAirBlock(coords) && coords.getY() > 0) {
                        coords = coords.down();
                    }
                    if (coords.getY() == 0) {
                        coords = world.getTopSolidOrLiquidBlock(coords);
                    }
                }
            }
            NoppesUtilPlayer.teleportPlayer(player, coords.getX(), coords.getY(), coords.getZ(), dimension);
        }
        else if (type == EnumPacketServer.ScriptBlockDataGet) {
            TileEntity tile = player.world.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            if (!(tile instanceof TileScripted)) {
                return;
            }
            NBTTagCompound compound2 = ((TileScripted)tile).getNBT(new NBTTagCompound());
            compound2.setTag("Languages", (NBTBase)ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound2);
        }
        else if (type == EnumPacketServer.ScriptItemDataGet) {
            ItemScriptedWrapper iw = (ItemScriptedWrapper)NpcAPI.Instance().getIItemStack(player.getHeldItemMainhand());
            NBTTagCompound compound2 = iw.getMCNbt();
            compound2.setTag("Languages", (NBTBase)ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound2);
        }
        else if (type == EnumPacketServer.ScriptItemDataSave) {
            if (!player.isCreative()) {
                return;
            }
            NBTTagCompound compound3 = Server.readNBT(buffer);
            ItemStack item = player.getHeldItemMainhand();
            ItemScriptedWrapper wrapper = (ItemScriptedWrapper)NpcAPI.Instance().getIItemStack(player.getHeldItemMainhand());
            wrapper.setMCNbt(compound3);
            wrapper.lastInited = -1L;
            wrapper.saveScriptData();
            wrapper.updateClient = true;
            player.sendContainerToPlayer(player.inventoryContainer);
        }
        else if (type == EnumPacketServer.ScriptForgeGet) {
            ForgeScriptData data5 = ScriptController.Instance.forgeScripts;
            NBTTagCompound compound2 = data5.writeToNBT(new NBTTagCompound());
            compound2.setTag("Languages", (NBTBase)ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound2);
        }
        else if (type == EnumPacketServer.ScriptForgeSave) {
            ScriptController.Instance.setForgeScripts(Server.readNBT(buffer));
        }
        else if (type == EnumPacketServer.ScriptPlayerGet) {
            NBTTagCompound compound3 = ScriptController.Instance.playerScripts.writeToNBT(new NBTTagCompound());
            compound3.setTag("Languages", (NBTBase)ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound3);
        }
        else if (type == EnumPacketServer.ScriptPlayerSave) {
            ScriptController.Instance.setPlayerScripts(Server.readNBT(buffer));
        }
        else if (type == EnumPacketServer.FactionsGet) {
            NoppesUtilServer.sendFactionDataAll(player);
        }
        else if (type == EnumPacketServer.FactionGet) {
            NBTTagCompound compound3 = new NBTTagCompound();
            Faction faction2 = FactionController.instance.getFaction(buffer.readInt());
            faction2.writeNBT(compound3);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound3);
        }
        else if (type == EnumPacketServer.SaveTileEntity) {
            NoppesUtilServer.saveTileEntity(player, Server.readNBT(buffer));
        }
        else if (type == EnumPacketServer.GetTileEntity) {
            BlockPos pos2 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileEntity tile2 = player.world.getTileEntity(pos2);
            NBTTagCompound compound5 = new NBTTagCompound();
            tile2.writeToNBT(compound5);
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound5);
        }
        else if (type == EnumPacketServer.ScriptBlockDataSave) {
            TileEntity tile = player.world.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            if (!(tile instanceof TileScripted)) {
                return;
            }
            TileScripted script = (TileScripted)tile;
            script.setNBT(Server.readNBT(buffer));
            script.lastInited = -1L;
        }
        else if (type == EnumPacketServer.ScriptDoorDataSave) {
            TileEntity tile = player.world.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            if (!(tile instanceof TileScriptedDoor)) {
                return;
            }
            TileScriptedDoor script2 = (TileScriptedDoor)tile;
            script2.setNBT(Server.readNBT(buffer));
            script2.lastInited = -1L;
        }
        else if (type == EnumPacketServer.ScriptDoorDataGet) {
            TileEntity tile = player.world.getTileEntity(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            if (!(tile instanceof TileScriptedDoor)) {
                return;
            }
            NBTTagCompound compound2 = ((TileScriptedDoor)tile).getNBT(new NBTTagCompound());
            compound2.setTag("Languages", (NBTBase)ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, compound2);
        }
        else if (type == EnumPacketServer.SchematicsTile) {
            BlockPos pos2 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileBuilder tile3 = (TileBuilder)player.world.getTileEntity(pos2);
            if (tile3 == null) {
                return;
            }
            Server.sendData(player, EnumPacketClient.GUI_DATA, tile3.writePartNBT(new NBTTagCompound()));
            Server.sendData(player, EnumPacketClient.SCROLL_LIST, SchematicController.Instance.list());
            if (tile3.hasSchematic()) {
                Server.sendData(player, EnumPacketClient.GUI_DATA, tile3.getSchematic().getNBTSmall());
            }
        }
        else if (type == EnumPacketServer.SchematicsSet) {
            BlockPos pos2 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileBuilder tile3 = (TileBuilder)player.world.getTileEntity(pos2);
            String name4 = Server.readString(buffer);
            tile3.setSchematic(SchematicController.Instance.load(name4));
            if (tile3.hasSchematic()) {
                Server.sendData(player, EnumPacketClient.GUI_DATA, tile3.getSchematic().getNBTSmall());
            }
        }
        else if (type == EnumPacketServer.SchematicsBuild) {
            BlockPos pos2 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileBuilder tile3 = (TileBuilder)player.world.getTileEntity(pos2);
            SchematicWrapper schem = tile3.getSchematic();
            schem.init(pos2.add(1, tile3.yOffest, 1), player.world, tile3.rotation * 90);
            SchematicController.Instance.build(tile3.getSchematic(), (ICommandSender)player);
            player.world.setBlockToAir(pos2);
        }
        else if (type == EnumPacketServer.SchematicsTileSave) {
            BlockPos pos2 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            TileBuilder tile3 = (TileBuilder)player.world.getTileEntity(pos2);
            if (tile3 != null) {
                tile3.readPartNBT(Server.readNBT(buffer));
            }
        }
        else if (type == EnumPacketServer.SchematicStore) {
            String name5 = Server.readString(buffer);
            int t = buffer.readInt();
            TileCopy tile4 = (TileCopy)NoppesUtilServer.saveTileEntity(player, Server.readNBT(buffer));
            if (tile4 == null || name5.isEmpty()) {
                return;
            }
            SchematicController.Instance.save((ICommandSender)player, name5, t, tile4.getPos(), tile4.height, tile4.width, tile4.length);
        }
        else if (type == EnumPacketServer.NbtBookSaveBlock) {
            BlockPos pos2 = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            NBTTagCompound compound2 = Server.readNBT(buffer);
            TileEntity tile5 = player.world.getTileEntity(pos2);
            if (tile5 != null) {
                tile5.readFromNBT(compound2);
                tile5.markDirty();
            }
        }
        else if (type == EnumPacketServer.NbtBookSaveEntity) {
            int entityId = buffer.readInt();
            NBTTagCompound compound2 = Server.readNBT(buffer);
            Entity entity3 = player.world.getEntityByID(entityId);
            if (entity3 != null) {
                entity3.readFromNBT(compound2);
            }
        }
    }
    
    private void warn(EntityPlayer player, String warning) {
        player.getServer().logWarning(player.getName() + ": " + warning);
    }
}
