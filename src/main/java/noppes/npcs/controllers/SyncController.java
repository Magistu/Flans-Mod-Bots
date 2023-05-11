package noppes.npcs.controllers;

import java.util.Collection;
import io.netty.buffer.ByteBuf;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.Quest;
import java.util.HashMap;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.items.ItemScripted;
import net.minecraft.server.MinecraftServer;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.controllers.data.Faction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.entity.player.EntityPlayerMP;

public class SyncController
{
    public static void syncPlayer(EntityPlayerMP player) {
        NBTTagList list = new NBTTagList();
        NBTTagCompound compound = new NBTTagCompound();
        for (Faction faction : FactionController.instance.factions.values()) {
            list.appendTag((NBTBase)faction.writeNBT(new NBTTagCompound()));
            if (list.tagCount() > 20) {
                compound = new NBTTagCompound();
                compound.setTag("Data", (NBTBase)list);
                Server.sendData(player, EnumPacketClient.SYNC_ADD, 1, compound);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", (NBTBase)list);
        Server.sendData(player, EnumPacketClient.SYNC_END, 1, compound);
        for (QuestCategory category : QuestController.instance.categories.values()) {
            Server.sendData(player, EnumPacketClient.SYNC_ADD, 3, category.writeNBT(new NBTTagCompound()));
        }
        Server.sendData(player, EnumPacketClient.SYNC_END, 3, new NBTTagCompound());
        for (DialogCategory category2 : DialogController.instance.categories.values()) {
            Server.sendData(player, EnumPacketClient.SYNC_ADD, 5, category2.writeNBT(new NBTTagCompound()));
        }
        Server.sendData(player, EnumPacketClient.SYNC_END, 5, new NBTTagCompound());
        list = new NBTTagList();
        for (RecipeCarpentry category3 : RecipeController.instance.globalRecipes.values()) {
            list.appendTag((NBTBase)category3.writeNBT());
            if (list.tagCount() > 10) {
                compound = new NBTTagCompound();
                compound.setTag("Data", (NBTBase)list);
                Server.sendData(player, EnumPacketClient.SYNC_ADD, 6, compound);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", (NBTBase)list);
        Server.sendData(player, EnumPacketClient.SYNC_END, 6, compound);
        list = new NBTTagList();
        for (RecipeCarpentry category3 : RecipeController.instance.anvilRecipes.values()) {
            list.appendTag((NBTBase)category3.writeNBT());
            if (list.tagCount() > 10) {
                compound = new NBTTagCompound();
                compound.setTag("Data", (NBTBase)list);
                Server.sendData(player, EnumPacketClient.SYNC_ADD, 7, compound);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", (NBTBase)list);
        Server.sendData(player, EnumPacketClient.SYNC_END, 7, compound);
        PlayerData data = PlayerData.get((EntityPlayer)player);
        Server.sendData(player, EnumPacketClient.SYNC_END, 8, data.getNBT());
        syncScriptItems(player);
    }
    
    public static void syncAllDialogs(MinecraftServer server) {
        for (DialogCategory category : DialogController.instance.categories.values()) {
            Server.sendToAll(server, EnumPacketClient.SYNC_ADD, 5, category.writeNBT(new NBTTagCompound()));
        }
        Server.sendToAll(server, EnumPacketClient.SYNC_END, 5, new NBTTagCompound());
    }
    
    public static void syncAllQuests(MinecraftServer server) {
        for (QuestCategory category : QuestController.instance.categories.values()) {
            Server.sendToAll(server, EnumPacketClient.SYNC_ADD, 3, category.writeNBT(new NBTTagCompound()));
        }
        Server.sendToAll(server, EnumPacketClient.SYNC_END, 3, new NBTTagCompound());
    }
    
    public static void syncScriptItems(EntityPlayerMP player) {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setTag("List", NBTTags.nbtIntegerStringMap(ItemScripted.Resources));
        Server.sendData(player, EnumPacketClient.SYNC_END, 9, comp);
    }
    
    public static void syncScriptItemsEverybody() {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setTag("List", NBTTags.nbtIntegerStringMap(ItemScripted.Resources));
        for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
            Server.sendData(player, EnumPacketClient.SYNC_END, 9, comp);
        }
    }
    
    public static void clientSync(int synctype, NBTTagCompound compound, boolean syncEnd) {
        if (synctype == 1) {
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); ++i) {
                Faction faction = new Faction();
                faction.readNBT(list.getCompoundTagAt(i));
                FactionController.instance.factionsSync.put(faction.id, faction);
            }
            if (syncEnd) {
                FactionController.instance.factions = FactionController.instance.factionsSync;
                FactionController.instance.factionsSync = new HashMap<Integer, Faction>();
            }
        }
        else if (synctype == 3) {
            if (compound.getKeySet().size()!=0) {
                QuestCategory category = new QuestCategory();
                category.readNBT(compound);
                QuestController.instance.categoriesSync.put(category.id, category);
            }
            if (syncEnd) {
                HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();
                for (QuestCategory category2 : QuestController.instance.categoriesSync.values()) {
                    for (Quest quest : category2.quests.values()) {
                        quests.put(quest.id, quest);
                    }
                }
                QuestController.instance.categories = QuestController.instance.categoriesSync;
                QuestController.instance.quests = quests;
                QuestController.instance.categoriesSync = new HashMap<Integer, QuestCategory>();
            }
        }
        else if (synctype == 5) {
            if (compound.getKeySet().size()!=0) {
                DialogCategory category3 = new DialogCategory();
                category3.readNBT(compound);
                DialogController.instance.categoriesSync.put(category3.id, category3);
            }
            if (syncEnd) {
                HashMap<Integer, Dialog> dialogs = new HashMap<Integer, Dialog>();
                for (DialogCategory category4 : DialogController.instance.categoriesSync.values()) {
                    for (Dialog dialog : category4.dialogs.values()) {
                        dialogs.put(dialog.id, dialog);
                    }
                }
                DialogController.instance.categories = DialogController.instance.categoriesSync;
                DialogController.instance.dialogs = dialogs;
                DialogController.instance.categoriesSync = new HashMap<Integer, DialogCategory>();
            }
        }
    }
    
    public static void clientSyncUpdate(int synctype, NBTTagCompound compound, ByteBuf buffer) {
        if (synctype == 1) {
            Faction faction = new Faction();
            faction.readNBT(compound);
            FactionController.instance.factions.put(faction.id, faction);
        }
        else if (synctype == 4) {
            DialogCategory category = DialogController.instance.categories.get(buffer.readInt());
            Dialog dialog = new Dialog(category);
            dialog.readNBT(compound);
            DialogController.instance.dialogs.put(dialog.id, dialog);
            category.dialogs.put(dialog.id, dialog);
        }
        else if (synctype == 5) {
            DialogCategory category = new DialogCategory();
            category.readNBT(compound);
            DialogController.instance.categories.put(category.id, category);
        }
        else if (synctype == 2) {
            QuestCategory category2 = QuestController.instance.categories.get(buffer.readInt());
            Quest quest = new Quest(category2);
            quest.readNBT(compound);
            QuestController.instance.quests.put(quest.id, quest);
            category2.quests.put(quest.id, quest);
        }
        else if (synctype == 3) {
            QuestCategory category2 = new QuestCategory();
            category2.readNBT(compound);
            QuestController.instance.categories.put(category2.id, category2);
        }
    }
    
    public static void clientSyncRemove(int synctype, int id) {
        if (synctype == 1) {
            FactionController.instance.factions.remove(id);
        }
        else if (synctype == 4) {
            Dialog dialog = DialogController.instance.dialogs.remove(id);
            if (dialog != null) {
                dialog.category.dialogs.remove(id);
            }
        }
        else if (synctype == 5) {
            DialogCategory category = DialogController.instance.categories.remove(id);
            if (category != null) {
                DialogController.instance.dialogs.keySet().removeAll(category.dialogs.keySet());
            }
        }
        else if (synctype == 2) {
            Quest quest = QuestController.instance.quests.remove(id);
            if (quest != null) {
                quest.category.quests.remove(id);
            }
        }
        else if (synctype == 3) {
            QuestCategory category2 = QuestController.instance.categories.remove(id);
            if (category2 != null) {
                QuestController.instance.quests.keySet().removeAll(category2.quests.keySet());
            }
        }
    }
}
