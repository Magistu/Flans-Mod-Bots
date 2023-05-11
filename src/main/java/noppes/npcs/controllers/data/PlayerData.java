package noppes.npcs.controllers.data;

import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.LogWriter;
import java.io.InputStream;
import net.minecraft.nbt.CompressedStreamTools;
import java.io.FileInputStream;
import java.io.File;
import noppes.npcs.CustomNpcs;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTBase;
import net.minecraft.entity.Entity;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataTimers;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PlayerData implements ICapabilityProvider
{
    @CapabilityInject(PlayerData.class)
    public static Capability<PlayerData> PLAYERDATA_CAPABILITY;
    public PlayerDialogData dialogData;
    public PlayerBankData bankData;
    public PlayerQuestData questData;
    public PlayerTransportData transportData;
    public PlayerFactionData factionData;
    public PlayerItemGiverData itemgiverData;
    public PlayerMailData mailData;
    public PlayerScriptData scriptData;
    public DataTimers timers;
    public EntityNPCInterface editingNpc;
    public NBTTagCompound cloned;
    public NBTTagCompound scriptStoreddata;
    public EntityPlayer player;
    public String playername;
    public String uuid;
    private EntityNPCInterface activeCompanion;
    public int companionID;
    public int playerLevel;
    public boolean updateClient;
    public int dialogId;
    public ItemStack prevHeldItem;
    private static ResourceLocation key;
    
    public PlayerData() {
        this.dialogData = new PlayerDialogData();
        this.bankData = new PlayerBankData();
        this.questData = new PlayerQuestData();
        this.transportData = new PlayerTransportData();
        this.factionData = new PlayerFactionData();
        this.itemgiverData = new PlayerItemGiverData();
        this.mailData = new PlayerMailData();
        this.timers = new DataTimers(this);
        this.scriptStoreddata = new NBTTagCompound();
        this.playername = "";
        this.uuid = "";
        this.activeCompanion = null;
        this.companionID = 0;
        this.playerLevel = 0;
        this.updateClient = false;
        this.dialogId = -1;
        this.prevHeldItem = ItemStack.EMPTY;
    }
    
    public void setNBT(NBTTagCompound data) {
        this.dialogData.loadNBTData(data);
        this.bankData.loadNBTData(data);
        this.questData.loadNBTData(data);
        this.transportData.loadNBTData(data);
        this.factionData.loadNBTData(data);
        this.itemgiverData.loadNBTData(data);
        this.mailData.loadNBTData(data);
        this.timers.readFromNBT(data);
        if (this.player != null) {
            this.playername = this.player.getName();
            this.uuid = this.player.getPersistentID().toString();
        }
        else {
            this.playername = data.getString("PlayerName");
            this.uuid = data.getString("UUID");
        }
        this.companionID = data.getInteger("PlayerCompanionId");
        if (data.hasKey("PlayerCompanion") && !this.hasCompanion()) {
            EntityCustomNpc npc = new EntityCustomNpc(this.player.world);
            npc.readEntityFromNBT(data.getCompoundTag("PlayerCompanion"));
            npc.setPosition(this.player.posX, this.player.posY, this.player.posZ);
            if (npc.advanced.role == 6) {
                this.setCompanion(npc);
                ((RoleCompanion)npc.roleInterface).setSitting(false);
                this.player.world.spawnEntity((Entity)npc);
            }
        }
        this.scriptStoreddata = data.getCompoundTag("ScriptStoreddata");
    }
    
    public NBTTagCompound getSyncNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        this.dialogData.saveNBTData(compound);
        this.questData.saveNBTData(compound);
        this.factionData.saveNBTData(compound);
        return compound;
    }
    
    public NBTTagCompound getNBT() {
        if (this.player != null) {
            this.playername = this.player.getName();
            this.uuid = this.player.getPersistentID().toString();
        }
        NBTTagCompound compound = new NBTTagCompound();
        this.dialogData.saveNBTData(compound);
        this.bankData.saveNBTData(compound);
        this.questData.saveNBTData(compound);
        this.transportData.saveNBTData(compound);
        this.factionData.saveNBTData(compound);
        this.itemgiverData.saveNBTData(compound);
        this.mailData.saveNBTData(compound);
        this.timers.writeToNBT(compound);
        compound.setString("PlayerName", this.playername);
        compound.setString("UUID", this.uuid);
        compound.setInteger("PlayerCompanionId", this.companionID);
        compound.setTag("ScriptStoreddata", (NBTBase)this.scriptStoreddata);
        if (this.hasCompanion()) {
            NBTTagCompound nbt = new NBTTagCompound();
            if (this.activeCompanion.writeToNBTAtomically(nbt)) {
                compound.setTag("PlayerCompanion", (NBTBase)nbt);
            }
        }
        return compound;
    }
    
    public boolean hasCompanion() {
        return this.activeCompanion != null && !this.activeCompanion.isDead;
    }
    
    public void setCompanion(EntityNPCInterface npc) {
        if (npc != null && npc.advanced.role != 6) {
            return;
        }
        ++this.companionID;
        if ((this.activeCompanion = npc) != null) {
            ((RoleCompanion)npc.roleInterface).companionID = this.companionID;
        }
        this.save(false);
    }
    
    public void updateCompanion(World world) {
        if (!this.hasCompanion() || world == this.activeCompanion.world) {
            return;
        }
        RoleCompanion role = (RoleCompanion)this.activeCompanion.roleInterface;
        role.owner = this.player;
        if (!role.isFollowing()) {
            return;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        this.activeCompanion.writeToNBTAtomically(nbt);
        this.activeCompanion.isDead = true;
        EntityCustomNpc npc = new EntityCustomNpc(world);
        npc.readEntityFromNBT(nbt);
        npc.setPosition(this.player.posX, this.player.posY, this.player.posZ);
        this.setCompanion(npc);
        ((RoleCompanion)npc.roleInterface).setSitting(false);
        world.spawnEntity((Entity)npc);
    }
    
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == PlayerData.PLAYERDATA_CAPABILITY;
    }
    
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (this.hasCapability(capability, facing)) {
            return (T)this;
        }
        return null;
    }
    
    public static void register(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(PlayerData.key, (ICapabilityProvider)new PlayerData());
        }
    }

    public synchronized void save(final boolean update) {
    	NBTTagCompound compound = this.getNBT();
        String filename = this.uuid + ".json";
        CustomNPCsScheduler.runTack(() -> {
            try {
                File saveDir = CustomNpcs.getWorldSaveDirectory("playerdata");
                File file = new File(saveDir, filename + "_new");
                File file1 = new File(saveDir, filename);
                NBTJsonUtil.SaveFile(file, compound);
                if (file1.exists()) {
                    file1.delete();
                }
                file.renameTo(file1);
            }
            catch (Exception e) {
                LogWriter.except(e);
            }
        });
        if (update) {
            this.updateClient = true;
        }
    }
    
    public static NBTTagCompound loadPlayerDataOld(String player) {
        File saveDir = CustomNpcs.getWorldSaveDirectory("playerdata");
        String filename = player;
        if (filename.isEmpty()) {
            filename = "noplayername";
        }
        filename += ".dat";
        try {
            File file = new File(saveDir, filename);
            if (file.exists()) {
                NBTTagCompound comp = CompressedStreamTools.readCompressed((InputStream)new FileInputStream(file));
                file.delete();
                file = new File(saveDir, filename + "_old");
                if (file.exists()) {
                    file.delete();
                }
                return comp;
            }
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
        try {
            File file = new File(saveDir, filename + "_old");
            if (file.exists()) {
                return CompressedStreamTools.readCompressed((InputStream)new FileInputStream(file));
            }
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
        return new NBTTagCompound();
    }
    
    public static NBTTagCompound loadPlayerData(String player) {
        File saveDir = CustomNpcs.getWorldSaveDirectory("playerdata");
        String filename = player;
        if (filename.isEmpty()) {
            filename = "noplayername";
        }
        filename += ".json";
        File file = null;
        try {
            file = new File(saveDir, filename);
            if (file.exists()) {
                return NBTJsonUtil.LoadFile(file);
            }
        }
        catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
        }
        return new NBTTagCompound();
    }
    
    public static PlayerData get(EntityPlayer player) {
        if (player.world.isRemote) {
            return CustomNpcs.proxy.getPlayerData(player);
        }
        PlayerData data = (PlayerData)player.getCapability((Capability)PlayerData.PLAYERDATA_CAPABILITY, (EnumFacing)null);
        if (data.player == null) {
            data.player = player;
            data.playerLevel = player.experienceLevel;
            data.scriptData = new PlayerScriptData(player);
            NBTTagCompound compound = loadPlayerData(player.getPersistentID().toString());
            if (compound.getKeySet().size()==0) {
                compound = loadPlayerDataOld(player.getName());
            }
            data.setNBT(compound);
        }
        return data;
    }
    
    static {
        PlayerData.PLAYERDATA_CAPABILITY = null;
        key = new ResourceLocation("customnpcs", "playerdata");
    }
}
