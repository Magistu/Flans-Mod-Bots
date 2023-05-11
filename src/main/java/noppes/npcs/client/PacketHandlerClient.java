package noppes.npcs.client;

import noppes.npcs.api.handler.data.IQuest;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.ModelData;
import noppes.npcs.ServerEventsHandler;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.IGuiClose;
import noppes.npcs.client.gui.util.IGuiError;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.GuiNpcMobSpawnerAdd;
import noppes.npcs.client.gui.util.IScrollData;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.Entity;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.client.gui.player.GuiQuestCompletion;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.MarkData;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.gui.player.GuiCustomChest;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraft.item.Item;
import noppes.npcs.CustomItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import java.util.Map;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.SyncController;
import net.minecraftforge.fml.common.network.internal.EntitySpawnMessageHelper;
import net.minecraft.client.multiplayer.WorldClient;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import net.minecraft.client.gui.toasts.IToast;
import noppes.npcs.client.gui.GuiAchievement;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.Server;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import noppes.npcs.PacketHandlerServer;

public class PacketHandlerClient extends PacketHandlerServer
{
    @SubscribeEvent
    public void onPacketData(FMLNetworkEvent.ClientCustomPacketEvent event) {
        EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }
        ByteBuf buffer = event.getPacket().payload();
        Minecraft.getMinecraft().addScheduledTask(() -> {
        	EnumPacketClient type = null;
            try {
                type = EnumPacketClient.values()[buffer.readInt()];
                LogWriter.debug("Received: " + type);
                this.client(buffer, player, type);
            }
            catch (Exception e) {
                LogWriter.error("Error with EnumPacketClient." + type, e);
            }
            finally {
                buffer.release();
            }
        });
    }
    
    private void client(ByteBuf buffer, EntityPlayer player, EnumPacketClient type) throws Exception {
        if (type == EnumPacketClient.CHATBUBBLE) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                return;
            }
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            if (npc.messages == null) {
                npc.messages = new RenderChatMessages();
            }
            String text = NoppesStringUtils.formatText(Server.readString(buffer), player, npc);
            npc.messages.addMessage(text, npc);
            if (buffer.readBoolean()) {
                player.sendMessage((ITextComponent)new TextComponentTranslation(npc.getName() + ": " + text, new Object[0]));
            }
        }
        else if (type == EnumPacketClient.CHAT) {
            String message = "";
            String str;
            while ((str = Server.readString(buffer)) != null && !str.isEmpty()) {
                message += I18n.translateToLocal(str);
            }
            player.sendMessage((ITextComponent)new TextComponentTranslation(message, new Object[0]));
        }
        else if (type == EnumPacketClient.EYE_BLINK) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                return;
            }
            ModelData data = ((EntityCustomNpc)entity).modelData;
            data.eyes.blinkStart = System.currentTimeMillis();
        }
        else if (type == EnumPacketClient.MESSAGE) {
            TextComponentTranslation title = new TextComponentTranslation(Server.readString(buffer), new Object[0]);
            TextComponentTranslation message2 = new TextComponentTranslation(Server.readString(buffer), new Object[0]);
            int btype = buffer.readInt();
            Minecraft.getMinecraft().getToastGui().add((IToast)new GuiAchievement((ITextComponent)title, (ITextComponent)message2, btype));
        }
        else if (type == EnumPacketClient.UPDATE_ITEM) {
            int id = buffer.readInt();
            NBTTagCompound compound = Server.readNBT(buffer);
            ItemStack stack = player.inventory.getStackInSlot(id);
            if (!stack.isEmpty()) {
                ((ItemStackWrapper)NpcAPI.Instance().getIItemStack(stack)).setMCNbt(compound);
            }
        }
        else if (type == EnumPacketClient.VISIBLE_FALSE) {
            WorldClient w = (WorldClient)player.world;
            int id2 = buffer.readInt();
            Entity entity2 = w.getEntityByID(id2);
            if (entity2 == null || !(entity2 instanceof EntityNPCInterface)) {
                return;
            }
            w.removeEntityFromWorld(id2);
        }
        else if (type == EnumPacketClient.VISIBLE_TRUE) {
            WorldClient w = (WorldClient)player.world;
            int id2 = buffer.readInt();
            Entity entity2 = w.getEntityByID(id2);
            if (entity2 == null) {
                EntitySpawnMessageHelper.spawn(buffer);
            }
        }
        else if (type == EnumPacketClient.SYNC_ADD || type == EnumPacketClient.SYNC_END) {
            int synctype = buffer.readInt();
            NBTTagCompound compound = Server.readNBT(buffer);
            SyncController.clientSync(synctype, compound, type == EnumPacketClient.SYNC_END);
            if (synctype == 8) {
                ClientProxy.playerData.setNBT(compound);
            }
            else if (synctype == 9) {
                if (player.getServer() == null) {
                    ItemScripted.Resources = NBTTags.getIntegerStringMap(compound.getTagList("List", 10));
                }
                for (Map.Entry<Integer, String> entry : ItemScripted.Resources.entrySet()) {
                    ModelResourceLocation mrl = new ModelResourceLocation((String)entry.getValue(), "inventory");
                    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register((Item)CustomItems.scripted_item, (int)entry.getKey(), mrl);
                    ModelLoader.setCustomModelResourceLocation((Item)CustomItems.scripted_item, (int)entry.getKey(), mrl);
                }
            }
        }
        else if (type == EnumPacketClient.SYNC_UPDATE) {
            int synctype = buffer.readInt();
            NBTTagCompound compound = Server.readNBT(buffer);
            SyncController.clientSyncUpdate(synctype, compound, buffer);
        }
        else if (type == EnumPacketClient.CHEST_NAME) {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof GuiCustomChest) {
                ((GuiCustomChest)screen).title = I18n.translateToLocal(Server.readString(buffer));
            }
        }
        else if (type == EnumPacketClient.SYNC_REMOVE) {
            int synctype = buffer.readInt();
            int id2 = buffer.readInt();
            SyncController.clientSyncRemove(synctype, id2);
        }
        else if (type == EnumPacketClient.MARK_DATA) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityLivingBase)) {
                return;
            }
            MarkData data2 = MarkData.get((EntityLivingBase)entity);
            data2.setNBT(Server.readNBT(buffer));
        }
        else if (type == EnumPacketClient.DIALOG) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                return;
            }
            Dialog dialog = DialogController.instance.dialogs.get(buffer.readInt());
            NoppesUtil.openDialog(dialog, (EntityNPCInterface)entity, player);
        }
        else if (type == EnumPacketClient.DIALOG_DUMMY) {
            EntityDialogNpc npc2 = new EntityDialogNpc(player.world);
            npc2.display.setName(Server.readString(buffer));
            EntityUtil.Copy((EntityLivingBase)player, (EntityLivingBase)npc2);
            Dialog dialog = new Dialog(null);
            dialog.readNBT(Server.readNBT(buffer));
            NoppesUtil.openDialog(dialog, npc2, player);
        }
        else if (type == EnumPacketClient.QUEST_COMPLETION) {
            int id = buffer.readInt();
            IQuest quest = QuestController.instance.get(id);
            if (!quest.getCompleteText().isEmpty()) {
                NoppesUtil.openGUI(player, new GuiQuestCompletion(quest));
            }
            else {
                NoppesUtilPlayer.sendData(EnumPlayerPacket.QuestCompletion, id);
            }
        }
        else if (type == EnumPacketClient.EDIT_NPC) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                NoppesUtil.setLastNpc(null);
            }
            else {
                NoppesUtil.setLastNpc((EntityNPCInterface)entity);
            }
        }
        else if (type == EnumPacketClient.PLAY_MUSIC) {
            MusicController.Instance.playMusic(Server.readString(buffer), (Entity)player);
        }
        else if (type == EnumPacketClient.PLAY_SOUND) {
            MusicController.Instance.playSound(SoundCategory.VOICE, Server.readString(buffer), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readFloat());
        }
        else if (type == EnumPacketClient.UPDATE_NPC) {
            NBTTagCompound compound2 = Server.readNBT(buffer);
            Entity entity3 = Minecraft.getMinecraft().world.getEntityByID(compound2.getInteger("EntityId"));
            if (entity3 == null || !(entity3 instanceof EntityNPCInterface)) {
                return;
            }
            ((EntityNPCInterface)entity3).readSpawnData(compound2);
        }
        else if (type == EnumPacketClient.ROLE) {
            NBTTagCompound compound2 = Server.readNBT(buffer);
            Entity entity3 = Minecraft.getMinecraft().world.getEntityByID(compound2.getInteger("EntityId"));
            if (entity3 == null || !(entity3 instanceof EntityNPCInterface)) {
                return;
            }
            ((EntityNPCInterface)entity3).advanced.setRole(compound2.getInteger("Role"));
            ((EntityNPCInterface)entity3).roleInterface.readFromNBT(compound2);
            NoppesUtil.setLastNpc((EntityNPCInterface)entity3);
        }
        else if (type == EnumPacketClient.GUI) {
            EnumGuiType gui = EnumGuiType.values()[buffer.readInt()];
            CustomNpcs.proxy.openGui(NoppesUtil.getLastNpc(), gui, buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
        else if (type == EnumPacketClient.PARTICLE) {
            NoppesUtil.spawnParticle(buffer);
        }
        else if (type == EnumPacketClient.DELETE_NPC) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(buffer.readInt());
            if (entity == null || !(entity instanceof EntityNPCInterface)) {
                return;
            }
            ((EntityNPCInterface)entity).delete();
        }
        else if (type == EnumPacketClient.SCROLL_LIST) {
            NoppesUtil.setScrollList(buffer);
        }
        else if (type == EnumPacketClient.SCROLL_DATA) {
            NoppesUtil.setScrollData(buffer);
        }
        else if (type == EnumPacketClient.SCROLL_DATA_PART) {
            NoppesUtil.addScrollData(buffer);
        }
        else if (type == EnumPacketClient.SCROLL_SELECTED) {
            GuiScreen gui2 = Minecraft.getMinecraft().currentScreen;
            if (gui2 == null || !(gui2 instanceof IScrollData)) {
                return;
            }
            String selected = Server.readString(buffer);
            ((IScrollData)gui2).setSelected(selected);
        }
        else if (type == EnumPacketClient.CLONE) {
            NBTTagCompound compound2 = Server.readNBT(buffer);
            NoppesUtil.openGUI(player, new GuiNpcMobSpawnerAdd(compound2));
        }
        else if (type == EnumPacketClient.GUI_DATA) {
            GuiScreen gui2 = Minecraft.getMinecraft().currentScreen;
            if (gui2 == null) {
                return;
            }
            if (gui2 instanceof GuiNPCInterface && ((GuiNPCInterface)gui2).hasSubGui()) {
                gui2 = ((GuiNPCInterface)gui2).getSubGui();
            }
            else if (gui2 instanceof GuiContainerNPCInterface && ((GuiContainerNPCInterface)gui2).hasSubGui()) {
                gui2 = ((GuiContainerNPCInterface)gui2).getSubGui();
            }
            if (gui2 instanceof IGuiData) {
                ((IGuiData)gui2).setGuiData(Server.readNBT(buffer));
            }
        }
        else if (type == EnumPacketClient.GUI_UPDATE) {
            GuiScreen gui2 = Minecraft.getMinecraft().currentScreen;
            if (gui2 == null) {
                return;
            }
            gui2.initGui();
        }
        else if (type == EnumPacketClient.GUI_ERROR) {
            GuiScreen gui2 = Minecraft.getMinecraft().currentScreen;
            if (gui2 == null || !(gui2 instanceof IGuiError)) {
                return;
            }
            int i = buffer.readInt();
            NBTTagCompound compound3 = Server.readNBT(buffer);
            ((IGuiError)gui2).setError(i, compound3);
        }
        else if (type == EnumPacketClient.GUI_CLOSE) {
            GuiScreen gui2 = Minecraft.getMinecraft().currentScreen;
            if (gui2 == null) {
                return;
            }
            if (gui2 instanceof IGuiClose) {
                int i = buffer.readInt();
                NBTTagCompound compound3 = Server.readNBT(buffer);
                ((IGuiClose)gui2).setClose(i, compound3);
            }
            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen((GuiScreen)null);
            mc.setIngameFocus();
        }
        else if (type == EnumPacketClient.VILLAGER_LIST) {
            MerchantRecipeList merchantrecipelist = MerchantRecipeList.readFromBuf(new PacketBuffer(buffer));
            ServerEventsHandler.Merchant.setRecipes(merchantrecipelist);
        }
        else if (type == EnumPacketClient.CONFIG) {
            int config = buffer.readInt();
            if (config == 0) {
                String font = Server.readString(buffer);
                int size = buffer.readInt();
                Runnable run = () -> {
                    if (!font.isEmpty()) {
                        CustomNpcs.FontType = font;
                        CustomNpcs.FontSize = size;
                        ClientProxy.Font.clear();
                        ClientProxy.Font = new ClientProxy.FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
                        CustomNpcs.Config.updateConfig();
                        TextComponentTranslation textComponentTranslation = new TextComponentTranslation("Font set to %s", new Object[] { ClientProxy.Font.getName() });
                        player.sendMessage((ITextComponent)textComponentTranslation);
                    }
                    else {
                    	TextComponentTranslation textComponentTranslation = new TextComponentTranslation("Current font is %s", new Object[] { ClientProxy.Font.getName() });
                        player.sendMessage((ITextComponent)textComponentTranslation);
                    }
                    return;
                };
                Minecraft.getMinecraft().addScheduledTask(run);
            }
        }
    }
}
