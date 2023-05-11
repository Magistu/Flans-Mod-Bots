package noppes.npcs;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.common.util.FakePlayer;
import java.util.List;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.PlayerQuestController;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import java.util.ArrayList;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.event.QuestEvent;
import noppes.npcs.controllers.data.QuestData;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.command.ICommandSender;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.roles.RoleDialog;
import noppes.npcs.controllers.data.PlayerData;
import java.io.IOException;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.BankController;
import noppes.npcs.containers.ContainerNPCBankInterface;
import java.util.Iterator;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.api.event.RoleEvent;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import net.minecraft.world.WorldServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.containers.ContainerNPCFollower;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.player.EntityPlayerMP;

public class NoppesUtilPlayer
{
    public static void changeFollowerState(EntityPlayerMP player, EntityNPCInterface npc) {
        if (npc.advanced.role != 2) {
            return;
        }
        RoleFollower role = (RoleFollower)npc.roleInterface;
        EntityPlayer owner = role.owner;
        if (owner == null || !owner.getName().equals(player.getName())) {
            return;
        }
        role.isFollowing = !role.isFollowing;
    }
    
    public static void hireFollower(EntityPlayerMP player, EntityNPCInterface npc) {
        if (npc.advanced.role != 2) {
            return;
        }
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerNPCFollowerHire)) {
            return;
        }
        ContainerNPCFollowerHire container = (ContainerNPCFollowerHire)con;
        RoleFollower role = (RoleFollower)npc.roleInterface;
        followerBuy(role, (IInventory)container.currencyMatrix, player, npc);
    }
    
    public static void extendFollower(EntityPlayerMP player, EntityNPCInterface npc) {
        if (npc.advanced.role != 2) {
            return;
        }
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerNPCFollower)) {
            return;
        }
        ContainerNPCFollower container = (ContainerNPCFollower)con;
        RoleFollower role = (RoleFollower)npc.roleInterface;
        followerBuy(role, (IInventory)container.currencyMatrix, player, npc);
    }
    
    public static void teleportPlayer(EntityPlayerMP player, double x, double y, double z, int dimension) {
        if (player.dimension != dimension) {
            int dim = player.dimension;
            MinecraftServer server = player.getServer();
            WorldServer wor = server.getWorld(dimension);
            if (wor == null) {
                player.sendMessage((ITextComponent)new TextComponentString("Broken transporter. Dimenion does not exist"));
                return;
            }
            player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
            server.getPlayerList().transferPlayerToDimension(player, dimension, (Teleporter)new CustomTeleporter(wor));
            player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
            if (!wor.playerEntities.contains(player)) {
                wor.spawnEntity((Entity)player);
            }
        }
        else {
            player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
        }
        player.world.updateEntityWithOptionalForce((Entity)player, false);
    }
    
    private static void followerBuy(RoleFollower role, IInventory currencyInv, EntityPlayerMP player, EntityNPCInterface npc) {
        ItemStack currency = currencyInv.getStackInSlot(0);
        if (currency == null || currency.isEmpty()) {
            return;
        }
        HashMap<ItemStack, Integer> cd = new HashMap<ItemStack, Integer>();
        for (int slot = 0; slot < role.inventory.items.size(); ++slot) {
            ItemStack is = (ItemStack)role.inventory.items.get(slot);
            if (!is.isEmpty() && is.getItem() == currency.getItem()) {
                if (!is.getHasSubtypes() || is.getItemDamage() == currency.getItemDamage()) {
                    int days = 1;
                    if (role.rates.containsKey(slot)) {
                        days = role.rates.get(slot);
                    }
                    cd.put(is, days);
                }
            }
        }
        if (cd.size() == 0) {
            return;
        }
        int stackSize = currency.getCount();
        int days2 = 0;
        int possibleDays = 0;
        int possibleSize = stackSize;
        while (true) {
            for (ItemStack item : cd.keySet()) {
                int rDays = cd.get(item);
                int rValue = item.getCount();
                if (rValue > stackSize) {
                    continue;
                }
                int newStackSize = stackSize % rValue;
                int size = stackSize - newStackSize;
                int posDays = size / rValue * rDays;
                if (possibleDays > posDays) {
                    continue;
                }
                possibleDays = posDays;
                possibleSize = newStackSize;
            }
            if (stackSize == possibleSize) {
                break;
            }
            stackSize = possibleSize;
            days2 += possibleDays;
            possibleDays = 0;
        }
        RoleEvent.FollowerHireEvent event = new RoleEvent.FollowerHireEvent((EntityPlayer)player, npc.wrappedNPC, days2);
        if (EventHooks.onNPCRole(npc, event)) {
            return;
        }
        if (event.days == 0) {
            return;
        }
        if (stackSize <= 0) {
            currencyInv.setInventorySlotContents(0, ItemStack.EMPTY);
        }
        else {
            currencyInv.setInventorySlotContents(0, currency.splitStack(stackSize));
        }
        npc.say((EntityPlayer)player, new Line(NoppesStringUtils.formatText(role.dialogHire.replace("{days}", days2 + ""), player, npc)));
        role.setOwner((EntityPlayer)player);
        role.addDays(days2);
    }
    
    public static void bankUpgrade(EntityPlayerMP player, EntityNPCInterface npc) {
        if (npc.advanced.role != 3) {
            return;
        }
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerNPCBankInterface)) {
            return;
        }
        ContainerNPCBankInterface container = (ContainerNPCBankInterface)con;
        Bank bank = BankController.getInstance().getBank(container.bankid);
        ItemStack item = bank.upgradeInventory.getStackInSlot(container.slot);
        if (item == null || item.isEmpty()) {
            return;
        }
        int price = item.getCount();
        ItemStack currency = container.currencyMatrix.getStackInSlot(0);
        if (currency == null || currency.isEmpty() || price > currency.getCount()) {
            return;
        }
        if (currency.getCount() - price == 0) {
            container.currencyMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
        }
        else {
            currency = currency.splitStack(price);
        }
        player.closeContainer();
        PlayerBankData data = PlayerDataController.instance.getBankData((EntityPlayer)player, bank.id);
        BankData bankData = data.getBank(bank.id);
        bankData.upgradedSlots.put(container.slot, true);
        RoleEvent.BankUpgradedEvent event = new RoleEvent.BankUpgradedEvent((EntityPlayer)player, npc.wrappedNPC, container.slot);
        EventHooks.onNPCRole(npc, event);
        bankData.openBankGui((EntityPlayer)player, npc, bank.id, container.slot);
    }
    
    public static void bankUnlock(EntityPlayerMP player, EntityNPCInterface npc) {
        if (npc.advanced.role != 3) {
            return;
        }
        Container con = player.openContainer;
        if (con == null || !(con instanceof ContainerNPCBankInterface)) {
            return;
        }
        ContainerNPCBankInterface container = (ContainerNPCBankInterface)con;
        Bank bank = BankController.getInstance().getBank(container.bankid);
        ItemStack item = bank.currencyInventory.getStackInSlot(container.slot);
        if (item == null || item.isEmpty()) {
            return;
        }
        int price = item.getCount();
        ItemStack currency = container.currencyMatrix.getStackInSlot(0);
        if (currency == null || currency.isEmpty() || price > currency.getCount()) {
            return;
        }
        if (currency.getCount() - price == 0) {
            container.currencyMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
        }
        else {
            currency = currency.splitStack(price);
        }
        player.closeContainer();
        PlayerBankData data = PlayerDataController.instance.getBankData((EntityPlayer)player, bank.id);
        BankData bankData = data.getBank(bank.id);
        if (bankData.unlockedSlots + 1 <= bank.maxSlots) {
            BankData bankData2 = bankData;
            ++bankData2.unlockedSlots;
        }
        RoleEvent.BankUnlockedEvent event = new RoleEvent.BankUnlockedEvent((EntityPlayer)player, npc.wrappedNPC, container.slot);
        EventHooks.onNPCRole(npc, event);
        bankData.openBankGui((EntityPlayer)player, npc, bank.id, container.slot);
    }
    
    public static void sendData(EnumPlayerPacket enu, Object... obs) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        try {
            if (!Server.fillBuffer((ByteBuf)buffer, enu, obs)) {
                return;
            }
            CustomNpcs.ChannelPlayer.sendToServer(new FMLProxyPacket(buffer, "CustomNPCsPlayer"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void dialogSelected(int diaId, int optionId, EntityPlayerMP player, EntityNPCInterface npc) {
        PlayerData data = PlayerData.get((EntityPlayer)player);
        if (data.dialogId != diaId) {
            return;
        }
        if (data.dialogId < 0 && npc.advanced.role == 7) {
            String text = ((RoleDialog)npc.roleInterface).optionsTexts.get(optionId);
            if (text != null && !text.isEmpty()) {
                Dialog d = new Dialog(null);
                d.text = text;
                NoppesUtilServer.openDialog((EntityPlayer)player, npc, d);
            }
            return;
        }
        Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
        if (dialog == null) {
            return;
        }
        if (!dialog.hasDialogs((EntityPlayer)player) && !dialog.hasOtherOptions()) {
            closeDialog(player, npc, true);
            return;
        }
        DialogOption option = dialog.options.get(optionId);
        if (option == null || EventHooks.onNPCDialogOption(npc, player, dialog, option) || (option.optionType == 1 && (!option.isAvailable((EntityPlayer)player) || !option.hasDialog())) || option.optionType == 2 || option.optionType == 0) {
            closeDialog(player, npc, true);
            return;
        }
        if (option.optionType == 3) {
            closeDialog(player, npc, true);
            if (npc.roleInterface != null) {
                if (npc.advanced.role == 6) {
                    ((RoleCompanion)npc.roleInterface).interact((EntityPlayer)player, true);
                }
                else {
                    npc.roleInterface.interact((EntityPlayer)player);
                }
            }
        }
        else if (option.optionType == 1) {
            closeDialog(player, npc, false);
            NoppesUtilServer.openDialog((EntityPlayer)player, npc, option.getDialog());
        }
        else if (option.optionType == 4) {
            closeDialog(player, npc, true);
            NoppesUtilServer.runCommand((ICommandSender)npc, npc.getName(), option.command, (EntityPlayer)player);
        }
        else {
            closeDialog(player, npc, true);
        }
    }
    
    public static void closeDialog(EntityPlayerMP player, EntityNPCInterface npc, boolean notifyClient) {
        PlayerData data = PlayerData.get((EntityPlayer)player);
        Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
        EventHooks.onNPCDialogClose(npc, player, dialog);
        if (notifyClient) {
            Server.sendData(player, EnumPacketClient.GUI_CLOSE, -1, new NBTTagCompound());
        }
        data.dialogId = -1;
    }
    
    public static void questCompletion(EntityPlayerMP player, int questId) {
        PlayerData data = PlayerData.get((EntityPlayer)player);
        PlayerQuestData playerdata = data.questData;
        QuestData questdata = playerdata.activeQuests.get(questId);
        if (questdata == null) {
            return;
        }
        Quest quest = questdata.quest;
        if (!quest.questInterface.isCompleted((EntityPlayer)player)) {
            return;
        }
        QuestEvent.QuestTurnedInEvent event = new QuestEvent.QuestTurnedInEvent(data.scriptData.getPlayer(), quest);
        event.expReward = quest.rewardExp;
        List<IItemStack> list = new ArrayList<IItemStack>();
        for (ItemStack item : quest.rewardItems.items) {
            if (!item.isEmpty()) {
                list.add(NpcAPI.Instance().getIItemStack(item));
            }
        }
        if (!quest.randomReward) {
            event.itemRewards = list.toArray(new IItemStack[list.size()]);
        }
        else if (!list.isEmpty()) {
            event.itemRewards = new IItemStack[] { list.get(player.getRNG().nextInt(list.size())) };
        }
        EventHooks.onQuestTurnedIn(data.scriptData, event);
        for (IItemStack item2 : event.itemRewards) {
            if (item2 != null) {
                NoppesUtilServer.GivePlayerItem((Entity)player, (EntityPlayer)player, item2.getMCItemStack());
            }
        }
        quest.questInterface.handleComplete((EntityPlayer)player);
        if (event.expReward > 0) {
            NoppesUtilServer.playSound((EntityLivingBase)player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 0.5f * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7f + 1.8f));
            player.addExperience(event.expReward);
        }
        quest.factionOptions.addPoints((EntityPlayer)player);
        if (quest.mail.isValid()) {
            PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName(), quest.mail);
        }
        if (!quest.command.isEmpty()) {
            FakePlayer cplayer = EntityNPCInterface.CommandPlayer;
            cplayer.setWorld(player.world);
            cplayer.setPosition(player.posX, player.posY, player.posZ);
            NoppesUtilServer.runCommand((ICommandSender)cplayer, "QuestCompletion", quest.command, (EntityPlayer)player);
        }
        PlayerQuestController.setQuestFinished(quest, (EntityPlayer)player);
        if (quest.hasNewQuest()) {
            PlayerQuestController.addActiveQuest(quest.getNextQuest(), (EntityPlayer)player);
        }
    }
    
    public static boolean compareItems(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
        if (NoppesUtilServer.IsItemStackNull(item) || NoppesUtilServer.IsItemStackNull(item2)) {
            return false;
        }
        boolean oreMatched = false;
        OreDictionary.itemMatches(item, item2, false);
        int[] ids = OreDictionary.getOreIDs(item);
        if (ids.length > 0) {
            for (int id : ids) {
                boolean match1 = false;
                boolean match2 = false;
                for (ItemStack is : OreDictionary.getOres(OreDictionary.getOreName(id))) {
                    if (compareItemDetails(item, is, ignoreDamage, ignoreNBT)) {
                        match1 = true;
                    }
                    if (compareItemDetails(item2, is, ignoreDamage, ignoreNBT)) {
                        match2 = true;
                    }
                }
                if (match1 && match2) {
                    return true;
                }
            }
        }
        return compareItemDetails(item, item2, ignoreDamage, ignoreNBT);
    }
    
    private static boolean compareItemDetails(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
        return item.getItem() == item2.getItem() && (ignoreDamage || item.getItemDamage() == -1 || item.getItemDamage() == item2.getItemDamage()) && (ignoreNBT || item.getTagCompound() == null || (item2.getTagCompound() != null && item.getTagCompound().equals((Object)item2.getTagCompound()))) && (ignoreNBT || item2.getTagCompound() == null || item.getTagCompound() != null);
    }
    
    public static boolean compareItems(EntityPlayer player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
        int size = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack is = player.inventory.getStackInSlot(i);
            if (!NoppesUtilServer.IsItemStackNull(is) && compareItems(item, is, ignoreDamage, ignoreNBT)) {
                size += is.getCount();
            }
        }
        return size >= item.getCount();
    }
    
    public static void consumeItem(EntityPlayer player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
        if (NoppesUtilServer.IsItemStackNull(item)) {
            return;
        }
        int size = item.getCount();
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack is = player.inventory.getStackInSlot(i);
            if (!NoppesUtilServer.IsItemStackNull(is)) {
                if (compareItems(item, is, ignoreDamage, ignoreNBT)) {
                    if (size < is.getCount()) {
                        is.splitStack(size);
                        break;
                    }
                    size -= is.getCount();
                    player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }
    }
    
    public static List<ItemStack> countStacks(IInventory inv, boolean ignoreDamage, boolean ignoreNBT) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            if (!NoppesUtilServer.IsItemStackNull(item)) {
                boolean found = false;
                for (ItemStack is : list) {
                    if (compareItems(item, is, ignoreDamage, ignoreNBT)) {
                        is.setCount(is.getCount() + item.getCount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    list.add(item.copy());
                }
            }
        }
        return list;
    }
}
