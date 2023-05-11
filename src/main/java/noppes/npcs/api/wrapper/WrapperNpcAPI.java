package noppes.npcs.api.wrapper;

import noppes.npcs.util.LRUHashMap;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.api.handler.ICloneHandler;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.api.handler.IDialogHandler;
import noppes.npcs.api.IDamageSource;
import net.minecraft.util.DamageSource;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.api.INbt;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.CommandNoppesBase;
import java.io.File;
import noppes.npcs.api.IPos;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.api.handler.IFactionHandler;
import noppes.npcs.containers.ContainerNpcInterface;
import net.minecraft.inventory.Container;
import noppes.npcs.api.IContainer;
import net.minecraft.inventory.IInventory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IWorld;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import noppes.npcs.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.block.IBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.api.entity.ICustomNpc;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.IEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.Map;
import noppes.npcs.api.NpcAPI;

public class WrapperNpcAPI extends NpcAPI
{
    private static Map<Integer, WorldWrapper> worldCache;
    public static EventBus EVENT_BUS;
    private static NpcAPI instance;
    
    public static void clearCache() {
        WrapperNpcAPI.worldCache.clear();
        BlockWrapper.clearCache();
    }
    
    @Override
    public IEntity getIEntity(Entity entity) {
        if (entity == null || entity.world.isRemote) {
            return null;
        }
        if (entity instanceof EntityNPCInterface) {
            return ((EntityNPCInterface)entity).wrappedNPC;
        }
        return WrapperEntityData.get(entity);
    }
    
    @Override
    public ICustomNpc createNPC(World world) {
        if (world.isRemote) {
            return null;
        }
        EntityCustomNpc npc = new EntityCustomNpc(world);
        return npc.wrappedNPC;
    }
    
    @Override
    public void registerPermissionNode(String permission, int defaultType) {
        if (defaultType < 0 || defaultType > 2) {
            throw new CustomNPCsException("Default type cant be smaller than 0 or larger than 2", new Object[0]);
        }
        if (this.hasPermissionNode(permission)) {
            throw new CustomNPCsException("Permission already exists", new Object[0]);
        }
        DefaultPermissionLevel level = DefaultPermissionLevel.values()[defaultType];
        PermissionAPI.registerNode(permission, level, permission);
    }
    
    @Override
    public boolean hasPermissionNode(String permission) {
        return PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(permission);
    }
    
    @Override
    public ICustomNpc spawnNPC(World world, int x, int y, int z) {
        if (world.isRemote) {
            return null;
        }
        EntityCustomNpc npc = new EntityCustomNpc(world);
        npc.setPositionAndRotation(x + 0.5, (double)y, z + 0.5, 0.0f, 0.0f);
        npc.ais.setStartPos(x, y, z);
        npc.setHealth(npc.getMaxHealth());
        world.spawnEntity((Entity)npc);
        return npc.wrappedNPC;
    }
    
    public static NpcAPI Instance() {
        if (WrapperNpcAPI.instance == null) {
            WrapperNpcAPI.instance = new WrapperNpcAPI();
        }
        return WrapperNpcAPI.instance;
    }
    
    @Override
    public EventBus events() {
        return WrapperNpcAPI.EVENT_BUS;
    }
    
    @Override
    public IBlock getIBlock(World world, BlockPos pos) {
        return BlockWrapper.createNew(world, pos, world.getBlockState(pos));
    }
    
    @Override
    public IItemStack getIItemStack(ItemStack itemstack) {
        if (itemstack == null || itemstack.isEmpty()) {
            return ItemStackWrapper.AIR;
        }
        return (IItemStack)itemstack.getCapability((Capability)ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY, (EnumFacing)null);
    }
    
    @Override
    public IWorld getIWorld(WorldServer world) {
        WorldWrapper w = WrapperNpcAPI.worldCache.get(world.provider.getDimension());
        if (w != null) {
            w.world = world;
            return w;
        }
        WrapperNpcAPI.worldCache.put(world.provider.getDimension(), w = WorldWrapper.createNew(world));
        return w;
    }
    
    @Override
    public IWorld getIWorld(int dimensionId) {
        for (WorldServer world : CustomNpcs.Server.worlds) {
            if (world.provider.getDimension() == dimensionId) {
                return this.getIWorld(world);
            }
        }
        throw new CustomNPCsException("Unknown dimension id: " + dimensionId, new Object[0]);
    }
    
    @Override
    public IContainer getIContainer(IInventory inventory) {
        return new ContainerWrapper(inventory);
    }
    
    @Override
    public IContainer getIContainer(Container container) {
        if (container instanceof ContainerNpcInterface) {
            return ContainerNpcInterface.getOrCreateIContainer((ContainerNpcInterface)container);
        }
        return new ContainerWrapper(container);
    }
    
    @Override
    public IFactionHandler getFactions() {
        this.checkWorld();
        return FactionController.instance;
    }
    
    private void checkWorld() {
        if (CustomNpcs.Server == null || CustomNpcs.Server.isServerStopped()) {
            throw new CustomNPCsException("No world is loaded right now", new Object[0]);
        }
    }
    
    @Override
    public IRecipeHandler getRecipes() {
        this.checkWorld();
        return RecipeController.instance;
    }
    
    @Override
    public IQuestHandler getQuests() {
        this.checkWorld();
        return QuestController.instance;
    }
    
    @Override
    public IWorld[] getIWorlds() {
        this.checkWorld();
        IWorld[] worlds = new IWorld[CustomNpcs.Server.worlds.length];
        for (int i = 0; i < CustomNpcs.Server.worlds.length; ++i) {
            worlds[i] = this.getIWorld(CustomNpcs.Server.worlds[i]);
        }
        return worlds;
    }
    
    @Override
    public IPos getIPos(double x, double y, double z) {
        return new BlockPosWrapper(new BlockPos(x, y, z));
    }
    
    @Override
    public File getGlobalDir() {
        return CustomNpcs.Dir;
    }
    
    @Override
    public File getWorldDir() {
        return CustomNpcs.getWorldSaveDirectory();
    }
    
    @Override
    public void registerCommand(CommandNoppesBase command) {
        CustomNpcs.NoppesCommand.registerCommand(command);
    }
    
    @Override
    public INbt getINbt(NBTTagCompound compound) {
        if (compound == null) {
            return new NBTWrapper(new NBTTagCompound());
        }
        return new NBTWrapper(compound);
    }
    
    @Override
    public INbt stringToNbt(String str) {
        if (str == null || str.isEmpty()) {
            throw new CustomNPCsException("Cant cast empty string to nbt", new Object[0]);
        }
        try {
            return this.getINbt(NBTJsonUtil.Convert(str));
        }
        catch (NBTJsonUtil.JsonException e) {
            throw new CustomNPCsException(e, "Failed converting " + str, new Object[0]);
        }
    }
    
    @Override
    public IDamageSource getIDamageSource(DamageSource damagesource) {
        return new DamageSourceWrapper(damagesource);
    }
    
    @Override
    public IDialogHandler getDialogs() {
        return DialogController.instance;
    }
    
    @Override
    public ICloneHandler getClones() {
        return ServerCloneController.Instance;
    }
    
    @Override
    public String executeCommand(IWorld world, String command) {
        FakePlayer player = EntityNPCInterface.CommandPlayer;
        player.setWorld((World)world.getMCWorld());
        player.setPosition(0.0, 0.0, 0.0);
        return NoppesUtilServer.runCommand((World)world.getMCWorld(), BlockPos.ORIGIN, "API", command, null, (ICommandSender)player);
    }
    
    @Override
    public INbt getRawPlayerData(String uuid) {
        return this.getINbt(PlayerData.loadPlayerData(uuid));
    }
    
    @Override
    public IPlayerMail createMail(String sender, String subject) {
        PlayerMail mail = new PlayerMail();
        mail.sender = sender;
        mail.subject = subject;
        return mail;
    }
    
    @Override
    public ICustomGui createCustomGui(int id, int width, int height, boolean pauseGame) {
        return new CustomGuiWrapper(id, width, height, pauseGame);
    }
    
    @Override
    public String getRandomName(int dictionary, int gender) {
        return CustomNpcs.MARKOV_GENERATOR[dictionary].fetch(gender);
    }
    
    static {
        worldCache = new LRUHashMap<Integer, WorldWrapper>(10);
        EVENT_BUS = new EventBus();
        WrapperNpcAPI.instance = null;
    }
}
