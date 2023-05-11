package noppes.npcs.client;

import noppes.npcs.LogWriter;
import java.awt.Font;
import noppes.npcs.config.TrueTypeFont;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.NpcAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ReportedException;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import java.util.Random;
import net.minecraft.client.particle.Particle;
import noppes.npcs.client.fx.EntityEnderFX;
import noppes.npcs.ModelPartData;
import noppes.npcs.ModelData;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.client.gui.GuiNbtBook;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionInv;
import noppes.npcs.containers.ContainerNPCCompanion;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionStats;
import noppes.npcs.client.gui.GuiNpcWaypoint;
import noppes.npcs.client.gui.GuiNpcMobSpawnerMounter;
import noppes.npcs.client.gui.GuiBlockCopy;
import noppes.npcs.client.gui.GuiNpcMobSpawner;
import noppes.npcs.client.gui.GuiNpcRedstoneBlock;
import noppes.npcs.client.gui.GuiBorderBlock;
import noppes.npcs.client.gui.GuiNpcDimension;
import noppes.npcs.client.gui.GuiMerchantAdd;
import noppes.npcs.client.gui.player.GuiMailbox;
import noppes.npcs.client.gui.player.GuiMailmanWrite;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.client.gui.GuiNpcRemoteEditor;
import noppes.npcs.client.gui.roles.GuiNpcBankSetup;
import noppes.npcs.client.gui.roles.GuiNpcTransporter;
import noppes.npcs.client.gui.roles.GuiNpcTraderSetup;
import noppes.npcs.containers.ContainerNPCTraderSetup;
import noppes.npcs.client.gui.roles.GuiNpcItemGiver;
import noppes.npcs.containers.ContainerNpcItemGiver;
import noppes.npcs.client.gui.roles.GuiNpcFollowerSetup;
import noppes.npcs.containers.ContainerNPCFollowerSetup;
import noppes.npcs.client.gui.script.GuiScriptGlobal;
import noppes.npcs.client.gui.script.GuiScriptDoor;
import noppes.npcs.client.gui.script.GuiScriptItem;
import noppes.npcs.client.gui.script.GuiScriptBlock;
import noppes.npcs.client.gui.script.GuiScript;
import noppes.npcs.client.gui.player.GuiTransportSelection;
import noppes.npcs.client.gui.player.GuiNPCBankChest;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.client.gui.player.GuiNPCTrader;
import noppes.npcs.containers.ContainerNPCTrader;
import noppes.npcs.client.gui.player.GuiNpcFollower;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.client.gui.player.GuiNpcFollowerHire;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.client.gui.player.GuiNpcCarpentryBench;
import noppes.npcs.containers.ContainerCarpentryBench;
import noppes.npcs.client.gui.mainmenu.GuiNpcAI;
import noppes.npcs.client.gui.mainmenu.GuiNPCGlobalMainMenu;
import noppes.npcs.client.gui.global.GuiNPCManageBanks;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.client.gui.global.GuiNPCManageQuest;
import noppes.npcs.client.gui.global.GuiNPCManageDialogs;
import noppes.npcs.client.gui.global.GuiNpcManageRecipes;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.client.gui.global.GuiNPCManageTransporters;
import noppes.npcs.client.gui.GuiBlockBuilder;
import noppes.npcs.client.gui.global.GuiNPCManageLinkedNpc;
import noppes.npcs.client.gui.global.GuiNPCManageFactions;
import noppes.npcs.client.gui.GuiNpcPather;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeItem;
import noppes.npcs.containers.ContainerNpcQuestTypeItem;
import noppes.npcs.client.gui.global.GuiNpcQuestReward;
import noppes.npcs.containers.ContainerNpcQuestReward;
import noppes.npcs.client.gui.mainmenu.GuiNpcAdvanced;
import noppes.npcs.client.gui.mainmenu.GuiNPCInv;
import noppes.npcs.containers.ContainerNPCInv;
import noppes.npcs.client.gui.mainmenu.GuiNpcStats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.client.gui.mainmenu.GuiNpcDisplay;
import noppes.npcs.client.gui.player.GuiCustomChest;
import noppes.npcs.containers.ContainerCustomChest;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.constants.EnumGuiType;
import net.minecraft.world.World;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import noppes.npcs.CustomItems;
import net.minecraft.item.Item;
import noppes.npcs.client.model.ModelClassicPlayer;
import noppes.npcs.entity.EntityNpcClassicPlayer;
import noppes.npcs.entity.EntityNpcAlex;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.client.model.ModelNPCGolem;
import noppes.npcs.entity.EntityNPCGolem;
import noppes.npcs.client.model.ModelBipedAlt;
import noppes.npcs.entity.EntityNPC64x32;
import net.minecraft.client.model.ModelBiped;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.client.model.ModelPlayerAlt;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.client.renderer.RenderProjectile;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.client.renderer.RenderNpcSlime;
import noppes.npcs.client.model.ModelNpcSlime;
import noppes.npcs.entity.EntityNpcSlime;
import net.minecraft.client.model.ModelBase;
import noppes.npcs.client.renderer.RenderNpcDragon;
import noppes.npcs.client.model.ModelNpcDragon;
import noppes.npcs.entity.EntityNpcDragon;
import noppes.npcs.client.renderer.RenderNpcCrystal;
import noppes.npcs.client.model.ModelNpcCrystal;
import noppes.npcs.entity.EntityNpcCrystal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import noppes.npcs.client.renderer.RenderNPCPony;
import noppes.npcs.entity.EntityNpcPony;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabQuests;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabFactions;
import micdoodle8.mods.galacticraft.api.client.tabs.AbstractTab;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabVanilla;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.client.controllers.PresetController;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.PacketHandlerPlayer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import noppes.npcs.CustomNpcs;
import net.minecraft.client.settings.KeyBinding;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.CommonProxy;

public class ClientProxy extends CommonProxy
{
    public static PlayerData playerData;
    public static KeyBinding QuestLog;
    public static KeyBinding Scene1;
    public static KeyBinding SceneReset;
    public static KeyBinding Scene2;
    public static KeyBinding Scene3;
    public static FontContainer Font;
    
    @Override
    public void load() {
        ClientProxy.Font = new FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
        this.createFolders();
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener((IResourceManagerReloadListener)new CustomNpcResourceListener());
        CustomNpcs.Channel.register((Object)new PacketHandlerClient());
        CustomNpcs.ChannelPlayer.register((Object)new PacketHandlerPlayer());
        new MusicController();
        MinecraftForge.EVENT_BUS.register((Object)new ClientTickHandler());
        Minecraft mc = Minecraft.getMinecraft();
        ClientProxy.QuestLog = new KeyBinding("Quest Log", 38, "key.categories.gameplay");
        if (CustomNpcs.SceneButtonsEnabled) {
            ClientProxy.Scene1 = new KeyBinding("Scene1 start/pause", 79, "key.categories.gameplay");
            ClientProxy.Scene2 = new KeyBinding("Scene2 start/pause", 80, "key.categories.gameplay");
            ClientProxy.Scene3 = new KeyBinding("Scene3 start/pause", 81, "key.categories.gameplay");
            ClientProxy.SceneReset = new KeyBinding("Scene reset", 82, "key.categories.gameplay");
            ClientRegistry.registerKeyBinding(ClientProxy.Scene1);
            ClientRegistry.registerKeyBinding(ClientProxy.Scene2);
            ClientRegistry.registerKeyBinding(ClientProxy.Scene3);
            ClientRegistry.registerKeyBinding(ClientProxy.SceneReset);
        }
        ClientRegistry.registerKeyBinding(ClientProxy.QuestLog);
        mc.gameSettings.loadOptions();
        new PresetController(CustomNpcs.Dir);
        if (CustomNpcs.EnableUpdateChecker) {
            VersionChecker checker = new VersionChecker();
            checker.start();
        }
        PixelmonHelper.loadClient();
    }
    
    @Override
    public PlayerData getPlayerData(EntityPlayer player) {
        if (player.getUniqueID() == Minecraft.getMinecraft().player.getUniqueID()) {
            if (ClientProxy.playerData.player != player) {
                ClientProxy.playerData.player = player;
            }
            return ClientProxy.playerData;
        }
        return null;
    }
    
    @Override
    public void postload() {
        MinecraftForge.EVENT_BUS.register((Object)new ClientEventHandler());
        if (CustomNpcs.InventoryGuiEnabled) {
            MinecraftForge.EVENT_BUS.register((Object)new TabRegistry());
            if (TabRegistry.getTabList().isEmpty()) {
                TabRegistry.registerTab(new InventoryTabVanilla());
            }
            TabRegistry.registerTab(new InventoryTabFactions());
            TabRegistry.registerTab(new InventoryTabQuests());
        }
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityNpcPony.class, (Render)new RenderNPCPony());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityNpcCrystal.class, (Render)new RenderNpcCrystal(new ModelNpcCrystal(0.5f)));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityNpcDragon.class, (Render)new RenderNpcDragon(new ModelNpcDragon(0.0f), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityNpcSlime.class, (Render)new RenderNpcSlime(new ModelNpcSlime(16), new ModelNpcSlime(0), 0.25f));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityProjectile.class, (Render)new RenderProjectile());
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityCustomNpc.class, (Render)new RenderCustomNpc((ModelBiped)new ModelPlayerAlt(0.0f, false)));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityNPC64x32.class, (Render)new RenderCustomNpc(new ModelBipedAlt(0.0f)));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityNPCGolem.class, (Render)new RenderNPCInterface((ModelBase)new ModelNPCGolem(0.0f), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityNpcAlex.class, (Render)new RenderCustomNpc((ModelBiped)new ModelPlayerAlt(0.0f, true)));
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityNpcClassicPlayer.class, (Render)new RenderCustomNpc((ModelBiped)new ModelClassicPlayer(0.0f)));
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> 9127187, new Item[] { CustomItems.mount, CustomItems.cloner, CustomItems.moving, CustomItems.scripter, CustomItems.wand, CustomItems.teleporter });
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            IItemStack item = NpcAPI.Instance().getIItemStack(stack);
            if (stack.getItem() == CustomItems.scripted_item) {
                return ((IItemScripted)item).getColor();
            }
            return -1;
        }, new Item[] { CustomItems.scripted_item });
    }
    
    private void createFolders() {
        File file = new File(CustomNpcs.Dir, "assets/customnpcs");
        if (!file.exists()) {
            file.mkdirs();
        }
        File check = new File(file, "sounds");
        if (!check.exists()) {
            check.mkdir();
        }
        File json = new File(file, "sounds.json");
        if (!json.exists()) {
            try {
                json.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(json));
                writer.write("{\n\n}");
                writer.close();
            }
            catch (IOException ex) {}
        }
        check = new File(file, "textures");
        if (!check.exists()) {
            check.mkdir();
        }
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID > EnumGuiType.values().length) {
            return null;
        }
        EnumGuiType gui = EnumGuiType.values()[ID];
        EntityNPCInterface npc = NoppesUtil.getLastNpc();
        Container container = this.getContainer(gui, player, x, y, z, npc);
        return this.getGui(npc, gui, container, x, y, z);
    }
    
    private GuiScreen getGui(EntityNPCInterface npc, EnumGuiType gui, Container container, int x, int y, int z) {
        if (gui == EnumGuiType.CustomChest) {
            return (GuiScreen)new GuiCustomChest((ContainerCustomChest)container);
        }
        if (gui == EnumGuiType.MainMenuDisplay) {
            if (npc != null) {
                return new GuiNpcDisplay(npc);
            }
            Minecraft.getMinecraft().player.sendMessage((ITextComponent)new TextComponentString("Unable to find npc"));
        }
        else {
            if (gui == EnumGuiType.MainMenuStats) {
                return new GuiNpcStats(npc);
            }
            if (gui == EnumGuiType.MainMenuInv) {
                return (GuiScreen)new GuiNPCInv(npc, (ContainerNPCInv)container);
            }
            if (gui == EnumGuiType.MainMenuAdvanced) {
                return new GuiNpcAdvanced(npc);
            }
            if (gui == EnumGuiType.QuestReward) {
                return (GuiScreen)new GuiNpcQuestReward(npc, (ContainerNpcQuestReward)container);
            }
            if (gui == EnumGuiType.QuestItem) {
                return (GuiScreen)new GuiNpcQuestTypeItem(npc, (ContainerNpcQuestTypeItem)container);
            }
            if (gui == EnumGuiType.MovingPath) {
                return new GuiNpcPather(npc);
            }
            if (gui == EnumGuiType.ManageFactions) {
                return new GuiNPCManageFactions(npc);
            }
            if (gui == EnumGuiType.ManageLinked) {
                return new GuiNPCManageLinkedNpc(npc);
            }
            if (gui == EnumGuiType.BuilderBlock) {
                return new GuiBlockBuilder(x, y, z);
            }
            if (gui == EnumGuiType.ManageTransport) {
                return new GuiNPCManageTransporters(npc);
            }
            if (gui == EnumGuiType.ManageRecipes) {
                return (GuiScreen)new GuiNpcManageRecipes(npc, (ContainerManageRecipes)container);
            }
            if (gui == EnumGuiType.ManageDialogs) {
                return new GuiNPCManageDialogs(npc);
            }
            if (gui == EnumGuiType.ManageQuests) {
                return new GuiNPCManageQuest(npc);
            }
            if (gui == EnumGuiType.ManageBanks) {
                return (GuiScreen)new GuiNPCManageBanks(npc, (ContainerManageBanks)container);
            }
            if (gui == EnumGuiType.MainMenuGlobal) {
                return new GuiNPCGlobalMainMenu(npc);
            }
            if (gui == EnumGuiType.MainMenuAI) {
                return new GuiNpcAI(npc);
            }
            if (gui == EnumGuiType.PlayerAnvil) {
                return (GuiScreen)new GuiNpcCarpentryBench((ContainerCarpentryBench)container);
            }
            if (gui == EnumGuiType.PlayerFollowerHire) {
                return (GuiScreen)new GuiNpcFollowerHire(npc, (ContainerNPCFollowerHire)container);
            }
            if (gui == EnumGuiType.PlayerFollower) {
                return (GuiScreen)new GuiNpcFollower(npc, (ContainerNPCFollower)container);
            }
            if (gui == EnumGuiType.PlayerTrader) {
                return (GuiScreen)new GuiNPCTrader(npc, (ContainerNPCTrader)container);
            }
            if (gui == EnumGuiType.PlayerBankSmall || gui == EnumGuiType.PlayerBankUnlock || gui == EnumGuiType.PlayerBankUprade || gui == EnumGuiType.PlayerBankLarge) {
                return (GuiScreen)new GuiNPCBankChest(npc, (ContainerNPCBankInterface)container);
            }
            if (gui == EnumGuiType.PlayerTransporter) {
                return new GuiTransportSelection(npc);
            }
            if (gui == EnumGuiType.Script) {
                return new GuiScript(npc);
            }
            if (gui == EnumGuiType.ScriptBlock) {
                return new GuiScriptBlock(x, y, z);
            }
            if (gui == EnumGuiType.ScriptItem) {
                return new GuiScriptItem((EntityPlayer)Minecraft.getMinecraft().player);
            }
            if (gui == EnumGuiType.ScriptDoor) {
                return new GuiScriptDoor(x, y, z);
            }
            if (gui == EnumGuiType.ScriptPlayers) {
                return new GuiScriptGlobal();
            }
            if (gui == EnumGuiType.SetupFollower) {
                return (GuiScreen)new GuiNpcFollowerSetup(npc, (ContainerNPCFollowerSetup)container);
            }
            if (gui == EnumGuiType.SetupItemGiver) {
                return (GuiScreen)new GuiNpcItemGiver(npc, (ContainerNpcItemGiver)container);
            }
            if (gui == EnumGuiType.SetupTrader) {
                return (GuiScreen)new GuiNpcTraderSetup(npc, (ContainerNPCTraderSetup)container);
            }
            if (gui == EnumGuiType.SetupTransporter) {
                return new GuiNpcTransporter(npc);
            }
            if (gui == EnumGuiType.SetupBank) {
                return new GuiNpcBankSetup(npc);
            }
            if (gui == EnumGuiType.NpcRemote && Minecraft.getMinecraft().currentScreen == null) {
                return new GuiNpcRemoteEditor();
            }
            if (gui == EnumGuiType.PlayerMailman) {
                return (GuiScreen)new GuiMailmanWrite((ContainerMail)container, x == 1, y == 1);
            }
            if (gui == EnumGuiType.PlayerMailbox) {
                return new GuiMailbox();
            }
            if (gui == EnumGuiType.MerchantAdd) {
                return (GuiScreen)new GuiMerchantAdd();
            }
            if (gui == EnumGuiType.NpcDimensions) {
                return new GuiNpcDimension();
            }
            if (gui == EnumGuiType.Border) {
                return new GuiBorderBlock(x, y, z);
            }
            if (gui == EnumGuiType.RedstoneBlock) {
                return new GuiNpcRedstoneBlock(x, y, z);
            }
            if (gui == EnumGuiType.MobSpawner) {
                return new GuiNpcMobSpawner(x, y, z);
            }
            if (gui == EnumGuiType.CopyBlock) {
                return new GuiBlockCopy(x, y, z);
            }
            if (gui == EnumGuiType.MobSpawnerMounter) {
                return new GuiNpcMobSpawnerMounter(x, y, z);
            }
            if (gui == EnumGuiType.Waypoint) {
                return new GuiNpcWaypoint(x, y, z);
            }
            if (gui == EnumGuiType.Companion) {
                return new GuiNpcCompanionStats(npc);
            }
            if (gui == EnumGuiType.CompanionTalent) {
                return new GuiNpcCompanionTalents(npc);
            }
            if (gui == EnumGuiType.CompanionInv) {
                return (GuiScreen)new GuiNpcCompanionInv(npc, (ContainerNPCCompanion)container);
            }
            if (gui == EnumGuiType.NbtBook) {
                return new GuiNbtBook(x, y, z);
            }
            if (gui == EnumGuiType.CustomGui) {
                return (GuiScreen)new GuiCustom((ContainerCustomGui)container);
            }
        }
        return null;
    }
    
    @Override
    public void openGui(int i, int j, int k, EnumGuiType gui, EntityPlayer player) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player != player) {
            return;
        }
        GuiScreen guiscreen = this.getGui(null, gui, null, i, j, k);
        if (guiscreen != null) {
            minecraft.displayGuiScreen(guiscreen);
        }
    }
    
    @Override
    public void openGui(EntityNPCInterface npc, EnumGuiType gui) {
        this.openGui(npc, gui, 0, 0, 0);
    }
    
    @Override
    public void openGui(EntityNPCInterface npc, EnumGuiType gui, int x, int y, int z) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Container container = this.getContainer(gui, (EntityPlayer)minecraft.player, x, y, z, npc);
        GuiScreen guiscreen = this.getGui(npc, gui, container, x, y, z);
        if (guiscreen != null) {
            minecraft.displayGuiScreen(guiscreen);
        }
    }
    
    @Override
    public void openGui(EntityPlayer player, Object guiscreen) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!player.world.isRemote || !(guiscreen instanceof GuiScreen)) {
            return;
        }
        if (guiscreen != null) {
            minecraft.displayGuiScreen((GuiScreen)guiscreen);
        }
    }
    
    @Override
    public void spawnParticle(EntityLivingBase player, String string, Object... ob) {
        if (string.equals("Block")) {
            BlockPos pos = (BlockPos)ob[0];
            int id = (int)ob[1];
            Block block = Block.getBlockById(id & 0xFFF);
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, block.getStateFromMeta(id >> 12 & 0xFF));
        }
        else if (string.equals("ModelData")) {
            ModelData data = (ModelData)ob[0];
            ModelPartData particles = (ModelPartData)ob[1];
            EntityCustomNpc npc = (EntityCustomNpc)player;
            Minecraft minecraft = Minecraft.getMinecraft();
            double height = npc.getYOffset() + data.getBodyY();
            Random rand = npc.getRNG();
            for (int i = 0; i < 2; ++i) {
                EntityEnderFX fx = new EntityEnderFX(npc, (rand.nextDouble() - 0.5) * player.width, rand.nextDouble() * player.height - height - 0.25, (rand.nextDouble() - 0.5) * player.width, (rand.nextDouble() - 0.5) * 2.0, -rand.nextDouble(), (rand.nextDouble() - 0.5) * 2.0, particles);
                minecraft.effectRenderer.addEffect((Particle)fx);
            }
        }
    }
    
    @Override
    public boolean hasClient() {
        return true;
    }
    
    @Override
    public EntityPlayer getPlayer() {
        return (EntityPlayer)Minecraft.getMinecraft().player;
    }
    
    public static void bindTexture(ResourceLocation location) {
        try {
            if (location == null) {
                return;
            }
            TextureManager manager = Minecraft.getMinecraft().getTextureManager();
            ITextureObject ob = manager.getTexture(location);
            if (ob == null) {
                ob = (ITextureObject)new SimpleTexture(location);
                manager.loadTexture(location, ob);
            }
            GlStateManager.bindTexture(ob.getGlTextureId());
        }
        catch (NullPointerException ex) {}
        catch (ReportedException ex2) {}
    }
    
    @Override
    public void spawnParticle(EnumParticleTypes particle, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
        Minecraft mc = Minecraft.getMinecraft();
        double xx = mc.getRenderViewEntity().posX - x;
        double yy = mc.getRenderViewEntity().posY - y;
        double zz = mc.getRenderViewEntity().posZ - z;
        if (xx * xx + yy * yy + zz * zz > 256.0) {
            return;
        }
        Particle fx = mc.effectRenderer.spawnEffectParticle(particle.getParticleID(), x, y, z, motionX, motionY, motionZ, new int[0]);
        if (fx == null) {
            return;
        }
        if (particle == EnumParticleTypes.FLAME) {
            ObfuscationReflectionHelper.setPrivateValue((Class)ParticleFlame.class, (Object)fx, (Object)scale, 0);
        }
        else if (particle == EnumParticleTypes.SMOKE_NORMAL) {
            ObfuscationReflectionHelper.setPrivateValue((Class)ParticleSmokeNormal.class, (Object)fx, (Object)scale, 0);
        }
    }
    
    static {
        ClientProxy.playerData = new PlayerData();
    }
    
    public static class FontContainer
    {
        private TrueTypeFont textFont;
        public boolean useCustomFont;
        
        private FontContainer() {
            this.textFont = null;
            this.useCustomFont = true;
        }
        
        public FontContainer(String fontType, int fontSize) {
            this.textFont = null;
            this.useCustomFont = true;
            this.textFont = new TrueTypeFont(new Font(fontType, 0, fontSize), 1.0f);
            this.useCustomFont = !fontType.equalsIgnoreCase("minecraft");
            try {
                if (!this.useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default")) {
                    this.textFont = new TrueTypeFont(new ResourceLocation("customnpcs", "opensans.ttf"), fontSize, 1.0f);
                }
            }
            catch (Exception e) {
                LogWriter.info("Failed loading font so using Arial");
            }
        }
        
        public int height(String text) {
            if (this.useCustomFont) {
                return this.textFont.height(text);
            }
            return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
        }
        
        public int width(String text) {
            if (this.useCustomFont) {
                return this.textFont.width(text);
            }
            return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        }
        
        public FontContainer copy() {
            FontContainer font = new FontContainer();
            font.textFont = this.textFont;
            font.useCustomFont = this.useCustomFont;
            return font;
        }
        
        public void drawString(String text, int x, int y, int color) {
            if (this.useCustomFont) {
                this.textFont.draw(text, (float)x, (float)y, color);
            }
            else {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float)x, (float)y, color);
            }
        }
        
        public String getName() {
            if (!this.useCustomFont) {
                return "Minecraft";
            }
            return this.textFont.getFontName();
        }
        
        public void clear() {
            if (this.textFont != null) {
                this.textFont.dispose();
            }
        }
    }
}
