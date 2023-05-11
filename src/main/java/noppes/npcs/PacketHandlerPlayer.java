package noppes.npcs;

import java.util.Iterator;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.controllers.data.PlayerFactionData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.controllers.data.PlayerScriptData;
import net.minecraft.item.ItemStack;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.roles.RoleTransporter;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.controllers.CustomGuiController;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import net.minecraft.entity.Entity;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.event.ItemEvent;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.api.event.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.MarkData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.constants.EnumPlayerPacket;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class PacketHandlerPlayer
{
    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).player;
        ByteBuf buffer = event.getPacket().payload();
        player.getServer().addScheduledTask(() -> {
        	EnumPlayerPacket type = null;
            try {
                type = EnumPlayerPacket.values()[buffer.readInt()];
                LogWriter.debug("Received: " + type);
                this.player(buffer, player, type);
            }
            catch (Exception e) {
                LogWriter.error("Error with EnumPlayerPacket." + type, e);
            }
            finally {
                buffer.release();
            }
        });
    }
    
    private void player(ByteBuf buffer, EntityPlayerMP player, EnumPlayerPacket type) throws Exception {
        if (type == EnumPlayerPacket.MarkData) {
            Entity entity = player.getServer().getEntityFromUuid(Server.readUUID(buffer));
            if (entity == null || !(entity instanceof EntityLivingBase)) {
                return;
            }
            MarkData.get((EntityLivingBase)entity);
        }
        else if (type == EnumPlayerPacket.KeyPressed) {
            if (!CustomNpcs.EnableScripting || ScriptController.Instance.languages.isEmpty()) {
                return;
            }
            EventHooks.onPlayerKeyPressed(player, buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
        }
        else if (type == EnumPlayerPacket.LeftClick) {
            if (!CustomNpcs.EnableScripting || ScriptController.Instance.languages.isEmpty()) {
                return;
            }
            ItemStack item = player.getHeldItemMainhand();
            PlayerScriptData handler = PlayerData.get((EntityPlayer)player).scriptData;
            PlayerEvent.AttackEvent ev = new PlayerEvent.AttackEvent(handler.getPlayer(), 0, null);
            EventHooks.onPlayerAttack(handler, ev);
            if (item.getItem() == CustomItems.scripted_item) {
                ItemScriptedWrapper isw = ItemScripted.GetWrapper(item);
                ItemEvent.AttackEvent eve = new ItemEvent.AttackEvent(isw, handler.getPlayer(), 0, null);
                EventHooks.onScriptItemAttack(isw, eve);
            }
        }
        else if (type == EnumPlayerPacket.CustomGuiClose) {
            EventHooks.onCustomGuiClose((PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player), new CustomGuiWrapper().fromNBT(Server.readNBT(buffer)));
        }
        else if (type == EnumPlayerPacket.CustomGuiButton) {
            if (player.openContainer instanceof ContainerCustomGui) {
                ((ContainerCustomGui)player.openContainer).customGui.fromNBT(Server.readNBT(buffer));
                EventHooks.onCustomGuiButton((PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player), ((ContainerCustomGui)player.openContainer).customGui, buffer.readInt());
            }
        }
        else if (type == EnumPlayerPacket.CustomGuiScrollClick) {
            if (player.openContainer instanceof ContainerCustomGui) {
                ((ContainerCustomGui)player.openContainer).customGui.fromNBT(Server.readNBT(buffer));
                EventHooks.onCustomGuiScrollClick((PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player), ((ContainerCustomGui)player.openContainer).customGui, buffer.readInt(), buffer.readInt(), CustomGuiController.readScrollSelection(buffer), buffer.readBoolean());
            }
        }
        else if (type == EnumPlayerPacket.CloseGui) {
            player.closeContainer();
        }
        else if (type == EnumPlayerPacket.CompanionTalentExp) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 6 || player != npc.getOwner()) {
                return;
            }
            int id = buffer.readInt();
            int exp = buffer.readInt();
            RoleCompanion role = (RoleCompanion)npc.roleInterface;
            if (exp <= 0 || !role.canAddExp(-exp) || id < 0 || id >= EnumCompanionTalent.values().length) {
                return;
            }
            EnumCompanionTalent talent = EnumCompanionTalent.values()[id];
            role.addExp(-exp);
            role.addTalentExp(talent, exp);
        }
        else if (type == EnumPlayerPacket.CompanionOpenInv) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 6 || player != npc.getOwner()) {
                return;
            }
            NoppesUtilServer.sendOpenGui((EntityPlayer)player, EnumGuiType.CompanionInv, npc);
        }
        else if (type == EnumPlayerPacket.FollowerHire) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 2) {
                return;
            }
            NoppesUtilPlayer.hireFollower(player, npc);
        }
        else if (type == EnumPlayerPacket.FollowerExtend) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 2) {
                return;
            }
            NoppesUtilPlayer.extendFollower(player, npc);
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.roleInterface.writeToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPlayerPacket.FollowerState) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 2) {
                return;
            }
            NoppesUtilPlayer.changeFollowerState(player, npc);
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.roleInterface.writeToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPlayerPacket.RoleGet) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role == 0) {
                return;
            }
            Server.sendData(player, EnumPacketClient.GUI_DATA, npc.roleInterface.writeToNBT(new NBTTagCompound()));
        }
        else if (type == EnumPlayerPacket.Transport) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 4) {
                return;
            }
            ((RoleTransporter)npc.roleInterface).transport(player, Server.readString(buffer));
        }
        else if (type == EnumPlayerPacket.BankUpgrade) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 3) {
                return;
            }
            NoppesUtilPlayer.bankUpgrade(player, npc);
        }
        else if (type == EnumPlayerPacket.BankUnlock) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 3) {
                return;
            }
            NoppesUtilPlayer.bankUnlock(player, npc);
        }
        else if (type == EnumPlayerPacket.BankSlotOpen) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc == null || npc.advanced.role != 3) {
                return;
            }
            int slot = buffer.readInt();
            int bankId = buffer.readInt();
            BankData data = PlayerDataController.instance.getBankData((EntityPlayer)player, bankId).getBankOrDefault(bankId);
            data.openBankGui((EntityPlayer)player, npc, bankId, slot);
        }
        else if (type == EnumPlayerPacket.Dialog) {
            EntityNPCInterface npc = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            LogWriter.debug("Dialog npc: " + npc);
            if (npc == null) {
                return;
            }
            NoppesUtilPlayer.dialogSelected(buffer.readInt(), buffer.readInt(), player, npc);
        }
        else if (type == EnumPlayerPacket.CheckQuestCompletion) {
            PlayerQuestData playerdata = PlayerData.get((EntityPlayer)player).questData;
            playerdata.checkQuestCompletion((EntityPlayer)player, -1);
        }
        else if (type == EnumPlayerPacket.QuestCompletion) {
            NoppesUtilPlayer.questCompletion(player, buffer.readInt());
        }
        else if (type == EnumPlayerPacket.FactionsGet) {
            PlayerFactionData data2 = PlayerData.get((EntityPlayer)player).factionData;
            Server.sendData(player, EnumPacketClient.GUI_DATA, data2.getPlayerGuiData());
        }
        else if (type == EnumPlayerPacket.MailGet) {
            PlayerMailData data3 = PlayerData.get((EntityPlayer)player).mailData;
            Server.sendData(player, EnumPacketClient.GUI_DATA, data3.saveNBTData(new NBTTagCompound()));
        }
        else if (type == EnumPlayerPacket.MailDelete) {
            long time = buffer.readLong();
            String username = Server.readString(buffer);
            PlayerMailData data4 = PlayerData.get((EntityPlayer)player).mailData;
            Iterator<PlayerMail> it = data4.playermail.iterator();
            while (it.hasNext()) {
                PlayerMail mail = it.next();
                if (mail.time == time && mail.sender.equals(username)) {
                    it.remove();
                }
            }
            Server.sendData(player, EnumPacketClient.GUI_DATA, data4.saveNBTData(new NBTTagCompound()));
        }
        else if (type == EnumPlayerPacket.MailSend) {
            String username2 = PlayerDataController.instance.hasPlayer(Server.readString(buffer));
            if (username2.isEmpty()) {
                NoppesUtilServer.sendGuiError((EntityPlayer)player, 0);
                return;
            }
            PlayerMail mail2 = new PlayerMail();
            String s = player.getDisplayNameString();
            if (!s.equals(player.getName())) {
                s = s + "(" + player.getName() + ")";
            }
            mail2.readNBT(Server.readNBT(buffer));
            mail2.sender = s;
            mail2.items = ((ContainerMail)player.openContainer).mail.items;
            if (mail2.subject.isEmpty()) {
                NoppesUtilServer.sendGuiError((EntityPlayer)player, 1);
                return;
            }
            NBTTagCompound comp = new NBTTagCompound();
            comp.setString("username", username2);
            NoppesUtilServer.sendGuiClose(player, 1, comp);
            EntityNPCInterface npc2 = NoppesUtilServer.getEditingNpc((EntityPlayer)player);
            if (npc2 != null && EventHooks.onNPCRole(npc2, new RoleEvent.MailmanEvent((EntityPlayer)player, npc2.wrappedNPC, mail2))) {
                return;
            }
            PlayerDataController.instance.addPlayerMessage(player.getServer(), username2, mail2);
        }
        else if (type == EnumPlayerPacket.MailboxOpenMail) {
            long time = buffer.readLong();
            String username = Server.readString(buffer);
            player.closeContainer();
            PlayerMailData data4 = PlayerData.get((EntityPlayer)player).mailData;
            for (PlayerMail mail : data4.playermail) {
                if (mail.time == time && mail.sender.equals(username)) {
                    ContainerMail.staticmail = mail;
                    player.openGui((Object)CustomNpcs.instance, EnumGuiType.PlayerMailman.ordinal(), player.world, 0, 0, 0);
                    break;
                }
            }
        }
        else if (type == EnumPlayerPacket.MailRead) {
            long time = buffer.readLong();
            String username = Server.readString(buffer);
            PlayerMailData data4 = PlayerData.get((EntityPlayer)player).mailData;
            for (PlayerMail mail : data4.playermail) {
                if (!mail.beenRead && mail.time == time && mail.sender.equals(username)) {
                    if (mail.hasQuest()) {
                        PlayerQuestController.addActiveQuest(mail.getQuest(), (EntityPlayer)player);
                    }
                    mail.beenRead = true;
                }
            }
        }
    }
}
