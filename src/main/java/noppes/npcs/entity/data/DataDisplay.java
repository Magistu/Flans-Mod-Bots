package noppes.npcs.entity.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.ModelData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.api.CustomNPCsException;
import com.google.common.collect.Iterables;
import com.mojang.authlib.properties.Property;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.util.ValueUtil;
import java.util.UUID;
import net.minecraft.util.StringUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import java.util.Random;
import net.minecraft.world.BossInfo;
import noppes.npcs.controllers.data.Availability;
import com.mojang.authlib.GameProfile;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.data.INPCDisplay;

public class DataDisplay implements INPCDisplay
{
    EntityNPCInterface npc;
    private String name;
    private String title;
    private int markovGeneratorId;
    private int markovGender;
    public byte skinType;
    private String url;
    public GameProfile playerProfile;
    private String texture;
    private String cloakTexture;
    private String glowTexture;
    private int visible;
    public Availability availability;
    private int modelSize;
    private int showName;
    private int skinColor;
    private boolean disableLivingAnimation;
    private boolean noHitbox;
    private byte showBossBar;
    private BossInfo.Color bossColor;
    
    public DataDisplay(EntityNPCInterface npc) {
        this.title = "";
        this.markovGeneratorId = 8;
        this.markovGender = 0;
        this.skinType = 0;
        this.url = "";
        this.texture = "customnpcs:textures/entity/humanmale/steve.png";
        this.cloakTexture = "";
        this.glowTexture = "";
        this.visible = 0;
        this.availability = new Availability();
        this.modelSize = 5;
        this.showName = 0;
        this.skinColor = 16777215;
        this.disableLivingAnimation = false;
        this.noHitbox = false;
        this.showBossBar = 0;
        this.bossColor = BossInfo.Color.PINK;
        this.npc = npc;
        this.markovGeneratorId = new Random().nextInt(CustomNpcs.MARKOV_GENERATOR.length - 1);
        this.name = this.getRandomName();
    }
    
    public String getRandomName() {
        return CustomNpcs.MARKOV_GENERATOR[this.markovGeneratorId].fetch(this.markovGender);
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Name", this.name);
        nbttagcompound.setInteger("MarkovGeneratorId", this.markovGeneratorId);
        nbttagcompound.setInteger("MarkovGender", this.markovGender);
        nbttagcompound.setString("Title", this.title);
        nbttagcompound.setString("SkinUrl", this.url);
        nbttagcompound.setString("Texture", this.texture);
        nbttagcompound.setString("CloakTexture", this.cloakTexture);
        nbttagcompound.setString("GlowTexture", this.glowTexture);
        nbttagcompound.setByte("UsingSkinUrl", this.skinType);
        if (this.playerProfile != null) {
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            NBTUtil.writeGameProfile(nbttagcompound2, this.playerProfile);
            nbttagcompound.setTag("SkinUsername", (NBTBase)nbttagcompound2);
        }
        nbttagcompound.setInteger("Size", this.modelSize);
        nbttagcompound.setInteger("ShowName", this.showName);
        nbttagcompound.setInteger("SkinColor", this.skinColor);
        nbttagcompound.setInteger("NpcVisible", this.visible);
        nbttagcompound.setTag("VisibleAvailability", (NBTBase)this.availability.writeToNBT(new NBTTagCompound()));
        nbttagcompound.setBoolean("NoLivingAnimation", this.disableLivingAnimation);
        nbttagcompound.setBoolean("IsStatue", this.noHitbox);
        nbttagcompound.setByte("BossBar", this.showBossBar);
        nbttagcompound.setInteger("BossColor", this.bossColor.ordinal());
        return nbttagcompound;
    }
    
    public void readToNBT(NBTTagCompound nbttagcompound) {
        this.setName(nbttagcompound.getString("Name"));
        this.setMarkovGeneratorId(nbttagcompound.getInteger("MarkovGeneratorId"));
        this.setMarkovGender(nbttagcompound.getInteger("MarkovGender"));
        this.title = nbttagcompound.getString("Title");
        int prevSkinType = this.skinType;
        String prevTexture = this.texture;
        String prevUrl = this.url;
        String prevPlayer = this.getSkinPlayer();
        this.url = nbttagcompound.getString("SkinUrl");
        this.skinType = nbttagcompound.getByte("UsingSkinUrl");
        this.texture = nbttagcompound.getString("Texture");
        this.cloakTexture = nbttagcompound.getString("CloakTexture");
        this.glowTexture = nbttagcompound.getString("GlowTexture");
        this.playerProfile = null;
        if (this.skinType == 1) {
            if (nbttagcompound.hasKey("SkinUsername", 10)) {
                this.playerProfile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkinUsername"));
            }
            else if (nbttagcompound.hasKey("SkinUsername", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkinUsername"))) {
                this.playerProfile = new GameProfile((UUID)null, nbttagcompound.getString("SkinUsername"));
            }
            this.loadProfile();
        }
        this.modelSize = ValueUtil.CorrectInt(nbttagcompound.getInteger("Size"), 1, 30);
        this.showName = nbttagcompound.getInteger("ShowName");
        if (nbttagcompound.hasKey("SkinColor")) {
            this.skinColor = nbttagcompound.getInteger("SkinColor");
        }
        this.visible = nbttagcompound.getInteger("NpcVisible");
        this.availability.readFromNBT(nbttagcompound.getCompoundTag("VisibleAvailability"));
        VisibilityController.instance.trackNpc(this.npc);
        this.disableLivingAnimation = nbttagcompound.getBoolean("NoLivingAnimation");
        this.noHitbox = nbttagcompound.getBoolean("IsStatue");
        this.setBossbar(nbttagcompound.getByte("BossBar"));
        this.setBossColor(nbttagcompound.getInteger("BossColor"));
        if (prevSkinType != this.skinType || !this.texture.equals(prevTexture) || !this.url.equals(prevUrl) || !this.getSkinPlayer().equals(prevPlayer)) {
            this.npc.textureLocation = null;
        }
        this.npc.textureGlowLocation = null;
        this.npc.textureCloakLocation = null;
        this.npc.updateHitbox();
    }
    
    public void loadProfile() {
        if (this.playerProfile != null && !StringUtils.isNullOrEmpty(this.playerProfile.getName()) && this.npc.getServer() != null && (!this.playerProfile.isComplete() || !this.playerProfile.getProperties().containsKey((Object)"textures"))) {
            GameProfile gameprofile = this.npc.getServer().getPlayerProfileCache().getGameProfileForUsername(this.playerProfile.getName());
            if (gameprofile != null) {
                Property property = (Property)Iterables.getFirst((Iterable)gameprofile.getProperties().get("textures"), (Object)null);
                if (property == null) {
                    gameprofile = this.npc.getServer().getMinecraftSessionService().fillProfileProperties(gameprofile, true);
                }
                this.playerProfile = gameprofile;
            }
        }
    }
    
    public boolean showName() {
        return !this.npc.isKilled() && (this.showName == 0 || (this.showName == 2 && this.npc.isAttacking()));
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void setName(String name) {
        if (this.name.equals(name)) {
            return;
        }
        this.name = name;
        this.npc.bossInfo.setName(this.npc.getDisplayName());
        this.npc.updateClient = true;
    }
    
    @Override
    public int getShowName() {
        return this.showName;
    }
    
    @Override
    public void setShowName(int type) {
        if (type == this.showName) {
            return;
        }
        this.showName = ValueUtil.CorrectInt(type, 0, 2);
        this.npc.updateClient = true;
    }
    
    public int getMarkovGender() {
        return this.markovGender;
    }
    
    public void setMarkovGender(int gender) {
        if (this.markovGender == gender) {
            return;
        }
        this.markovGender = ValueUtil.CorrectInt(gender, 0, 2);
    }
    
    public int getMarkovGeneratorId() {
        return this.markovGeneratorId;
    }
    
    public void setMarkovGeneratorId(int id) {
        if (this.markovGeneratorId == id) {
            return;
        }
        this.markovGeneratorId = ValueUtil.CorrectInt(id, 0, CustomNpcs.MARKOV_GENERATOR.length - 1);
    }
    
    @Override
    public String getTitle() {
        return this.title;
    }
    
    @Override
    public void setTitle(String title) {
        if (this.title.equals(title)) {
            return;
        }
        this.title = title;
        this.npc.updateClient = true;
    }
    
    @Override
    public String getSkinUrl() {
        return this.url;
    }
    
    @Override
    public void setSkinUrl(String url) {
        if (this.url.equals(url)) {
            return;
        }
        this.url = url;
        if (url.isEmpty()) {
            this.skinType = 0;
        }
        else {
            this.skinType = 2;
        }
        this.npc.updateClient = true;
    }
    
    @Override
    public String getSkinPlayer() {
        return (this.playerProfile == null) ? "" : this.playerProfile.getName();
    }
    
    @Override
    public void setSkinPlayer(String name) {
        if (name == null || name.isEmpty()) {
            this.playerProfile = null;
            this.skinType = 0;
        }
        else {
            this.playerProfile = new GameProfile((UUID)null, name);
            this.skinType = 1;
        }
        this.npc.updateClient = true;
    }
    
    @Override
    public String getSkinTexture() {
        return this.texture;
    }
    
    @Override
    public void setSkinTexture(String texture) {
        if (texture == null || this.texture.equals(texture)) {
            return;
        }
        this.texture = texture.toLowerCase();
        this.npc.textureLocation = null;
        this.skinType = 0;
        this.npc.updateClient = true;
    }
    
    @Override
    public String getOverlayTexture() {
        return this.glowTexture;
    }
    
    @Override
    public void setOverlayTexture(String texture) {
        if (this.glowTexture.equals(texture)) {
            return;
        }
        this.glowTexture = texture;
        this.npc.textureGlowLocation = null;
        this.npc.updateClient = true;
    }
    
    @Override
    public String getCapeTexture() {
        return this.cloakTexture;
    }
    
    @Override
    public void setCapeTexture(String texture) {
        if (this.cloakTexture.equals(texture)) {
            return;
        }
        this.cloakTexture = texture.toLowerCase();
        this.npc.textureCloakLocation = null;
        this.npc.updateClient = true;
    }
    
    @Override
    public boolean getHasLivingAnimation() {
        return !this.disableLivingAnimation;
    }
    
    @Override
    public void setHasLivingAnimation(boolean enabled) {
        this.disableLivingAnimation = !enabled;
        this.npc.updateClient = true;
    }
    
    @Override
    public int getBossbar() {
        return this.showBossBar;
    }
    
    @Override
    public void setBossbar(int type) {
        if (type == this.showBossBar) {
            return;
        }
        this.showBossBar = (byte)ValueUtil.CorrectInt(type, 0, 2);
        this.npc.bossInfo.setVisible(this.showBossBar == 1);
        this.npc.updateClient = true;
    }
    
    @Override
    public int getBossColor() {
        return this.bossColor.ordinal();
    }
    
    @Override
    public void setBossColor(int color) {
        if (color < 0 || color >= BossInfo.Color.values().length) {
            throw new CustomNPCsException("Invalid Boss Color: " + color, new Object[0]);
        }
        this.bossColor = BossInfo.Color.values()[color];
        this.npc.bossInfo.setColor(this.bossColor);
    }
    
    @Override
    public int getVisible() {
        return this.visible;
    }
    
    @Override
    public void setVisible(int type) {
        if (type == this.visible) {
            return;
        }
        this.visible = ValueUtil.CorrectInt(type, 0, 2);
        this.npc.updateClient = true;
    }
    
    @Override
    public int getSize() {
        return this.modelSize;
    }
    
    @Override
    public void setSize(int size) {
        if (this.modelSize == size) {
            return;
        }
        this.modelSize = ValueUtil.CorrectInt(size, 1, 30);
        this.npc.updateClient = true;
    }
    
    @Override
    public void setModelScale(int part, float x, float y, float z) {
        ModelData modeldata = ((EntityCustomNpc)this.npc).modelData;
        ModelPartConfig model = null;
        if (part == 0) {
            model = modeldata.getPartConfig(EnumParts.HEAD);
        }
        else if (part == 1) {
            model = modeldata.getPartConfig(EnumParts.BODY);
        }
        else if (part == 2) {
            model = modeldata.getPartConfig(EnumParts.ARM_LEFT);
        }
        else if (part == 3) {
            model = modeldata.getPartConfig(EnumParts.ARM_RIGHT);
        }
        else if (part == 4) {
            model = modeldata.getPartConfig(EnumParts.LEG_LEFT);
        }
        else if (part == 5) {
            model = modeldata.getPartConfig(EnumParts.LEG_RIGHT);
        }
        if (model == null) {
            throw new CustomNPCsException("Unknown part: " + part, new Object[0]);
        }
        model.setScale(x, y, z);
        this.npc.updateClient = true;
    }
    
    @Override
    public float[] getModelScale(int part) {
        ModelData modeldata = ((EntityCustomNpc)this.npc).modelData;
        ModelPartConfig model = null;
        if (part == 0) {
            model = modeldata.getPartConfig(EnumParts.HEAD);
        }
        else if (part == 1) {
            model = modeldata.getPartConfig(EnumParts.BODY);
        }
        else if (part == 2) {
            model = modeldata.getPartConfig(EnumParts.ARM_LEFT);
        }
        else if (part == 3) {
            model = modeldata.getPartConfig(EnumParts.ARM_RIGHT);
        }
        else if (part == 4) {
            model = modeldata.getPartConfig(EnumParts.LEG_LEFT);
        }
        else if (part == 5) {
            model = modeldata.getPartConfig(EnumParts.LEG_RIGHT);
        }
        if (model == null) {
            throw new CustomNPCsException("Unknown part: " + part, new Object[0]);
        }
        return new float[] { model.scaleX, model.scaleY, model.scaleZ };
    }
    
    @Override
    public int getTint() {
        return this.skinColor;
    }
    
    @Override
    public void setTint(int color) {
        if (color == this.skinColor) {
            return;
        }
        this.skinColor = color;
        this.npc.updateClient = true;
    }
    
    @Override
    public void setModel(String id) {
        ModelData modeldata = ((EntityCustomNpc)this.npc).modelData;
        if (id == null) {
            if (modeldata.entityClass == null) {
                return;
            }
            modeldata.entityClass = null;
            this.npc.updateClient = true;
        }
        else {
            ResourceLocation resource = new ResourceLocation(id);
            Entity entity = EntityList.createEntityByIDFromName(resource, this.npc.world);
            if (entity == null) {
                throw new CustomNPCsException("Failed to create an entity from given id: " + id, new Object[0]);
            }
            modeldata.setEntityName(entity.getClass().getCanonicalName());
            this.npc.updateClient = true;
        }
    }
    
    @Override
    public String getModel() {
        ModelData modeldata = ((EntityCustomNpc)this.npc).modelData;
        if (modeldata.entityClass == null) {
            return null;
        }
        String name = modeldata.entityClass.getCanonicalName();
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
            Class<? extends Entity> c = (Class<? extends Entity>)ent.getEntityClass();
            if (c.getCanonicalName().equals(name) && EntityLivingBase.class.isAssignableFrom(c)) {
                return ent.getRegistryName().toString();
            }
        }
        return null;
    }
    
    @Override
    public boolean getHasHitbox() {
        return !this.noHitbox;
    }
    
    @Override
    public void setHasHitbox(boolean bo) {
        if (this.noHitbox != bo) {
            return;
        }
        this.noHitbox = !bo;
        this.npc.updateClient = true;
    }
    
    @Override
    public boolean isVisibleTo(IPlayer player) {
        return this.isVisibleTo(player);
    }
    
    public boolean isVisibleTo(EntityPlayerMP player) {
        if (this.visible == 1) {
            return !this.availability.isAvailable((EntityPlayer)player);
        }
        return this.availability.isAvailable((EntityPlayer)player);
    }
}
