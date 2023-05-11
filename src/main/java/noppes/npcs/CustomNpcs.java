package noppes.npcs;

import net.minecraft.client.Minecraft;
import java.util.List;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.data.Availability;
import net.minecraft.scoreboard.ServerScoreboard;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.common.util.FakePlayer;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import noppes.npcs.items.ItemScripted;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.BankController;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockLeaves;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.GlobalDataController;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.FactionController;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import nikedemos.markovnames.generators.MarkovSpanish;
import nikedemos.markovnames.generators.MarkovCustomNPCsClassic;
import nikedemos.markovnames.generators.MarkovAztec;
import nikedemos.markovnames.generators.MarkovAncientGreek;
import nikedemos.markovnames.generators.MarkovOldNorse;
import nikedemos.markovnames.generators.MarkovSaami;
import nikedemos.markovnames.generators.MarkovWelsh;
import nikedemos.markovnames.generators.MarkovSlavic;
import nikedemos.markovnames.generators.MarkovJapanese;
import nikedemos.markovnames.generators.MarkovRoman;
import noppes.npcs.controllers.RecipeController;
import net.minecraftforge.common.ForgeModContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.PixelmonHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.ForgeChunkManager;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.api.NpcAPI;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.api.wrapper.WrapperEntityData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nikedemos.markovnames.generators.MarkovGenerator;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.command.CommandNoppes;
import noppes.npcs.config.ConfigLoader;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import java.io.File;
import net.minecraftforge.fml.common.SidedProxy;
import noppes.npcs.config.ConfigProp;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "customnpcs", name = "CustomNpcs", version = "1.12", acceptedMinecraftVersions = "1.12, 1.12.1, 1.12.2")
public class CustomNpcs
{
    public static String MODID = "customnpcs";
    @ConfigProp(info = "Whether scripting is enabled or not")
    public static boolean EnableScripting;
    @ConfigProp(info = "Arguments given to the Nashorn scripting library")
    public static String NashorArguments;
    @ConfigProp(info = "Disable Chat Bubbles")
    public static boolean EnableChatBubbles;
    @ConfigProp(info = "Navigation search range for NPCs. Not recommended to increase if you have a slow pc or on a server")
    public static int NpcNavRange;
    @ConfigProp(info = "Set to true if you want the dialog command option to be able to use op commands like tp etc")
    public static boolean NpcUseOpCommands;
    @ConfigProp
    public static boolean InventoryGuiEnabled;
    @ConfigProp
    public static boolean FixUpdateFromPre_1_12;
    @ConfigProp(info = "If you are running sponge and you want to disable the permissions set this to true")
    public static boolean DisablePermissions;
    @ConfigProp
    public static boolean SceneButtonsEnabled;
    @ConfigProp
    public static boolean EnableDefaultEyes;
    public static long ticks;
    @SidedProxy(clientSide = "noppes.npcs.client.ClientProxy", serverSide = "noppes.npcs.CommonProxy")
    public static CommonProxy proxy;
    @ConfigProp(info = "Enables CustomNpcs startup update message")
    public static boolean EnableUpdateChecker;
    public static CustomNpcs instance;
    public static boolean FreezeNPCs;
    @ConfigProp(info = "Only ops can create and edit npcs")
    public static boolean OpsOnly;
    @ConfigProp(info = "Default interact line. Leave empty to not have one")
    public static String DefaultInteractLine;
    @ConfigProp(info = "Number of chunk loading npcs that can be active at the same time")
    public static int ChuckLoaders;
    public static File Dir;
    @ConfigProp(info = "Enables leaves decay")
    public static boolean LeavesDecayEnabled;
    @ConfigProp(info = "Enables Vine Growth")
    public static boolean VineGrowthEnabled;
    @ConfigProp(info = "Enables Ice Melting")
    public static boolean IceMeltsEnabled;
    @ConfigProp(info = "Normal players can use soulstone on animals")
    public static boolean SoulStoneAnimals;
    @ConfigProp(info = "Normal players can use soulstone on all npcs")
    public static boolean SoulStoneNPCs;
    @ConfigProp(info = "Type 0 = Normal, Type 1 = Solid")
    public static int HeadWearType;
    @ConfigProp(info = "When set to Minecraft it will use minecrafts font, when Default it will use OpenSans. Can only use fonts installed on your PC")
    public static String FontType;
    @ConfigProp(info = "Font size for custom fonts (doesn't work with minecrafts font)")
    public static int FontSize;
    @ConfigProp
    public static boolean NpcSpeachTriggersChatEvent;
    public static FMLEventChannel Channel;
    public static FMLEventChannel ChannelPlayer;
    public static ConfigLoader Config;
    public static CommandNoppes NoppesCommand;
    public static boolean VerboseDebug;
    public static MinecraftServer Server;
    public static MarkovGenerator[] MARKOV_GENERATOR;
    
    public CustomNpcs() {
        CustomNpcs.instance = this;
    }
    
    @Mod.EventHandler
    public void load(FMLPreInitializationEvent ev) {
        CustomNpcs.Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("CustomNPCs");
        CustomNpcs.ChannelPlayer = NetworkRegistry.INSTANCE.newEventDrivenChannel("CustomNPCsPlayer");
        (CustomNpcs.Dir = new File(new File(ev.getModConfigurationDirectory(), ".."), "customnpcs")).mkdir();
        (CustomNpcs.Config = new ConfigLoader(this.getClass(), ev.getModConfigurationDirectory(), "CustomNpcs")).loadConfig();
        if (CustomNpcs.NpcNavRange < 16) {
            CustomNpcs.NpcNavRange = 16;
        }
        CustomItems.load();
        CapabilityManager.INSTANCE.register((Class)PlayerData.class, (Capability.IStorage)new Capability.IStorage() {
            public NBTBase writeNBT(Capability capability, Object instance, EnumFacing side) {
                return null;
            }
            
            public void readNBT(Capability capability, Object instance, EnumFacing side, NBTBase nbt) {
            }
        }, (Class)PlayerData.class);
        CapabilityManager.INSTANCE.register((Class)WrapperEntityData.class, (Capability.IStorage)new Capability.IStorage() {
            public NBTBase writeNBT(Capability capability, Object instance, EnumFacing side) {
                return null;
            }
            
            public void readNBT(Capability capability, Object instance, EnumFacing side, NBTBase nbt) {
            }
        }, (Class)WrapperEntityData.class);
        CapabilityManager.INSTANCE.register((Class)MarkData.class, (Capability.IStorage)new Capability.IStorage() {
            public NBTBase writeNBT(Capability capability, Object instance, EnumFacing side) {
                return null;
            }
            
            public void readNBT(Capability capability, Object instance, EnumFacing side, NBTBase nbt) {
            }
        }, (Class)MarkData.class);
        CapabilityManager.INSTANCE.register((Class)ItemStackWrapper.class, (Capability.IStorage)new Capability.IStorage<ItemStackWrapper>() {
            public NBTBase writeNBT(Capability capability, ItemStackWrapper instance, EnumFacing side) {
                return null;
            }
            
            public void readNBT(Capability capability, ItemStackWrapper instance, EnumFacing side, NBTBase nbt) {
            }
        }, () -> null);
        NetworkRegistry.INSTANCE.registerGuiHandler((Object)this, (IGuiHandler)CustomNpcs.proxy);
        MinecraftForge.EVENT_BUS.register((Object)new ServerEventsHandler());
        MinecraftForge.EVENT_BUS.register((Object)new ServerTickHandler());
        MinecraftForge.EVENT_BUS.register((Object)new CustomEntities());
        MinecraftForge.EVENT_BUS.register((Object)CustomNpcs.proxy);
        NpcAPI.Instance().events().register((Object)new AbilityEventHandler());
        ForgeChunkManager.setForcedChunkLoadingCallback((Object)this, (ForgeChunkManager.LoadingCallback)new ChunkController());
        CustomNpcs.proxy.load();
        ObfuscationReflectionHelper.setPrivateValue((Class)RangedAttribute.class, (Object)SharedMonsterAttributes.MAX_HEALTH, (Object)Double.MAX_VALUE, 1);
    }
    
    @Mod.EventHandler
    public void load(FMLInitializationEvent ev) {
        PixelmonHelper.load();
        ScriptController controller = new ScriptController();
        if (CustomNpcs.EnableScripting && controller.languages.size() > 0) {
            MinecraftForge.EVENT_BUS.register((Object)controller);
            MinecraftForge.EVENT_BUS.register((Object)new ScriptPlayerEventHandler().registerForgeEvents());
            MinecraftForge.EVENT_BUS.register((Object)new ScriptItemEventHandler());
        }
        ForgeModContainer.fullBoundingBoxLadders = true;
        new RecipeController();
        CustomNpcs.proxy.postload();
        new CustomNpcsPermissions();
        CustomNpcs.MARKOV_GENERATOR[0] = new MarkovRoman(3);
        CustomNpcs.MARKOV_GENERATOR[1] = new MarkovJapanese(4);
        CustomNpcs.MARKOV_GENERATOR[2] = new MarkovSlavic(3);
        CustomNpcs.MARKOV_GENERATOR[3] = new MarkovWelsh(3);
        CustomNpcs.MARKOV_GENERATOR[4] = new MarkovSaami(3);
        CustomNpcs.MARKOV_GENERATOR[5] = new MarkovOldNorse(4);
        CustomNpcs.MARKOV_GENERATOR[6] = new MarkovAncientGreek(3);
        CustomNpcs.MARKOV_GENERATOR[7] = new MarkovAztec(3);
        CustomNpcs.MARKOV_GENERATOR[8] = new MarkovCustomNPCsClassic(3);
        CustomNpcs.MARKOV_GENERATOR[9] = new MarkovSpanish(3);
    }
    
    @Mod.EventHandler
    public void setAboutToStart(FMLServerAboutToStartEvent event) {
        CustomNpcs.Server = event.getServer();
        ChunkController.instance.clear();
        FactionController.instance.load();
        VisibilityController.instance = new VisibilityController();
        new PlayerDataController();
        new TransportController();
        new GlobalDataController();
        new SpawnController();
        new LinkedNpcController();
        new MassBlockController();
        VisibilityController.instance = new VisibilityController();
        ScriptController.Instance.loadCategories();
        ScriptController.Instance.loadStoredData();
        ScriptController.Instance.loadPlayerScripts();
        ScriptController.Instance.loadForgeScripts();
        ScriptController.HasStart = false;
        WrapperNpcAPI.clearCache();
        Set<ResourceLocation> names = (Set<ResourceLocation>)Block.REGISTRY.getKeys();
        for (ResourceLocation name : names) {
            Block block = (Block)Block.REGISTRY.getObject(name);
            if (block instanceof BlockLeaves) {
                block.setTickRandomly(CustomNpcs.LeavesDecayEnabled);
            }
            if (block instanceof BlockVine) {
                block.setTickRandomly(CustomNpcs.VineGrowthEnabled);
            }
            if (block instanceof BlockIce) {
                block.setTickRandomly(CustomNpcs.IceMeltsEnabled);
            }
        }
    }
    
    @Mod.EventHandler
    public void started(FMLServerStartedEvent event) {
        RecipeController.instance.load();
        new BankController();
        DialogController.instance.load();
        QuestController.instance.load();
        ScriptController.HasStart = true;
        ServerCloneController.Instance = new ServerCloneController();
    }
    
    @Mod.EventHandler
    public void stopped(FMLServerStoppedEvent event) {
        ServerCloneController.Instance = null;
        CustomNpcs.Server = null;
        ItemScripted.Resources.clear();
    }
    
    @Mod.EventHandler
    public void serverstart(FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)CustomNpcs.NoppesCommand);
        EntityNPCInterface.ChatEventPlayer = new FakePlayer(event.getServer().getWorld(0), (GameProfile)EntityNPCInterface.ChatEventProfile);
        EntityNPCInterface.CommandPlayer = new FakePlayer(event.getServer().getWorld(0), (GameProfile)EntityNPCInterface.CommandProfile);
        EntityNPCInterface.GenericPlayer = new FakePlayer(event.getServer().getWorld(0), (GameProfile)EntityNPCInterface.GenericProfile);
        for (WorldServer world : CustomNpcs.Server.worlds) {
            ServerScoreboard board = (ServerScoreboard) world.getScoreboard();
            board.addDirtyRunnable(() -> {
            	Iterator<String> iterator = Availability.scores.iterator();
                while (iterator.hasNext()) {
                	String objective = iterator.next();
                    ScoreObjective so = board.getObjective(objective);
                    if (so != null) {
                        Iterator<EntityPlayerMP> iterator2 = CustomNpcs.Server.getPlayerList().getPlayers().iterator();
                        while (iterator2.hasNext()) {
                        	EntityPlayerMP player = iterator2.next();
                            if (!board.entityHasObjective(player.getName(), so) && board.getObjectiveDisplaySlotCount(so) == 0) {
                                player.connection.sendPacket((Packet)new SPacketScoreboardObjective(so, 0));
                            }
                            Score sco = board.getOrCreateScore(player.getName(), so);
                            player.connection.sendPacket((Packet)new SPacketUpdateScore(sco));
                        }
                    }
                }
                return;
            });
            board.addDirtyRunnable(() -> {
            	List players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
            	Iterator<EntityPlayerMP> iterator3 = players.iterator();
                while (iterator3.hasNext()) {
                	EntityPlayerMP playerMP = iterator3.next();
                    VisibilityController.instance.onUpdate(playerMP);
                }
                return;
            });
        }
    }
    
    public static File getWorldSaveDirectory() {
        return getWorldSaveDirectory(null);
    }
    
    public static File getWorldSaveDirectory(String s) {
        try {
            File dir = new File(".");
            if (CustomNpcs.Server != null) {
                if (!CustomNpcs.Server.isDedicatedServer()) {
                    dir = new File(Minecraft.getMinecraft().gameDir, "saves");
                }
                dir = new File(new File(dir, CustomNpcs.Server.getFolderName()), "customnpcs");
            }
            if (s != null) {
                dir = new File(dir, s);
            }
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        }
        catch (Exception e) {
            LogWriter.error("Error getting worldsave", e);
            return null;
        }
    }
    
    static {
        CustomNpcs.EnableScripting = true;
        CustomNpcs.NashorArguments = "-strict";
        CustomNpcs.EnableChatBubbles = true;
        CustomNpcs.NpcNavRange = 32;
        CustomNpcs.NpcUseOpCommands = false;
        CustomNpcs.InventoryGuiEnabled = true;
        CustomNpcs.FixUpdateFromPre_1_12 = false;
        CustomNpcs.DisablePermissions = false;
        CustomNpcs.SceneButtonsEnabled = true;
        CustomNpcs.EnableDefaultEyes = true;
        CustomNpcs.EnableUpdateChecker = true;
        CustomNpcs.FreezeNPCs = false;
        CustomNpcs.OpsOnly = false;
        CustomNpcs.DefaultInteractLine = "Hello @p";
        CustomNpcs.ChuckLoaders = 20;
        CustomNpcs.LeavesDecayEnabled = true;
        CustomNpcs.VineGrowthEnabled = true;
        CustomNpcs.IceMeltsEnabled = true;
        CustomNpcs.SoulStoneAnimals = true;
        CustomNpcs.SoulStoneNPCs = false;
        CustomNpcs.HeadWearType = 1;
        CustomNpcs.FontType = "Default";
        CustomNpcs.FontSize = 18;
        CustomNpcs.NpcSpeachTriggersChatEvent = false;
        CustomNpcs.NoppesCommand = new CommandNoppes();
        CustomNpcs.VerboseDebug = false;
        MARKOV_GENERATOR = new MarkovGenerator[10];
    }
}
