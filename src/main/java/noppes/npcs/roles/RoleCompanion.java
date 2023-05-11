package noppes.npcs.roles;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Items;
import noppes.npcs.api.NpcAPI;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import com.google.common.collect.HashMultimap;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import java.util.UUID;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.roles.companion.CompanionGuard;
import noppes.npcs.roles.companion.CompanionFarmer;
import noppes.npcs.roles.companion.CompanionTrader;
import java.util.Iterator;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.controllers.data.PlayerData;
import java.util.TreeMap;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.roles.companion.CompanionFoodStats;
import noppes.npcs.roles.companion.CompanionJobInterface;
import noppes.npcs.constants.EnumCompanionJobs;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.constants.EnumCompanionTalent;
import java.util.Map;
import noppes.npcs.NpcMiscInventory;

public class RoleCompanion extends RoleInterface
{
    public NpcMiscInventory inventory;
    public String uuid;
    public String ownerName;
    public Map<EnumCompanionTalent, Integer> talents;
    public boolean canAge;
    public long ticksActive;
    public EnumCompanionStage stage;
    public EntityPlayer owner;
    public int companionID;
    public EnumCompanionJobs job;
    public CompanionJobInterface jobInterface;
    public boolean hasInv;
    public boolean defendOwner;
    public CompanionFoodStats foodstats;
    private int eatingTicks;
    private IItemStack eating;
    private int eatingDelay;
    public int currentExp;
    
    public RoleCompanion(EntityNPCInterface npc) {
        super(npc);
        this.uuid = "";
        this.ownerName = "";
        this.talents = new TreeMap<EnumCompanionTalent, Integer>();
        this.canAge = true;
        this.ticksActive = 0L;
        this.stage = EnumCompanionStage.FULLGROWN;
        this.owner = null;
        this.job = EnumCompanionJobs.NONE;
        this.jobInterface = null;
        this.hasInv = true;
        this.defendOwner = true;
        this.foodstats = new CompanionFoodStats();
        this.eatingTicks = 20;
        this.eating = null;
        this.eatingDelay = 0;
        this.currentExp = 0;
        this.inventory = new NpcMiscInventory(12);
    }
    
    @Override
    public boolean aiShouldExecute() {
        EntityPlayer prev = this.owner;
        this.owner = this.getOwner();
        if (this.jobInterface != null && this.jobInterface.isSelfSufficient()) {
            return true;
        }
        if (this.owner == null && !this.uuid.isEmpty()) {
            this.npc.isDead = true;
        }
        else if (prev != this.owner && this.owner != null) {
            this.ownerName = this.owner.getDisplayNameString();
            PlayerData data = PlayerData.get(this.owner);
            if (data.companionID != this.companionID) {
                this.npc.isDead = true;
            }
        }
        return this.owner != null;
    }
    
    @Override
    public void aiUpdateTask() {
        if (this.owner != null && (this.jobInterface == null || !this.jobInterface.isSelfSufficient())) {
            this.foodstats.onUpdate(this.npc);
        }
        if (this.foodstats.getFoodLevel() >= 18) {
            this.npc.stats.healthRegen = 0;
            this.npc.stats.combatRegen = 0;
        }
        if (this.foodstats.needFood() && this.isSitting()) {
            if (this.eatingDelay > 0) {
                --this.eatingDelay;
                return;
            }
            IItemStack prev = this.eating;
            this.eating = this.getFood();
            if (prev != null && this.eating == null) {
                this.npc.setRoleData("");
            }
            if (prev == null && this.eating != null) {
                this.npc.setRoleData("eating");
                this.eatingTicks = 20;
            }
            if (this.isEating()) {
                this.doEating();
            }
        }
        else if (this.eating != null && !this.isSitting()) {
            this.eating = null;
            this.eatingDelay = 20;
            this.npc.setRoleData("");
        }
        ++this.ticksActive;
        if (this.canAge && this.stage != EnumCompanionStage.FULLGROWN) {
            if (this.stage == EnumCompanionStage.BABY && this.ticksActive > EnumCompanionStage.CHILD.matureAge) {
                this.matureTo(EnumCompanionStage.CHILD);
            }
            else if (this.stage == EnumCompanionStage.CHILD && this.ticksActive > EnumCompanionStage.TEEN.matureAge) {
                this.matureTo(EnumCompanionStage.TEEN);
            }
            else if (this.stage == EnumCompanionStage.TEEN && this.ticksActive > EnumCompanionStage.ADULT.matureAge) {
                this.matureTo(EnumCompanionStage.ADULT);
            }
            else if (this.stage == EnumCompanionStage.ADULT && this.ticksActive > EnumCompanionStage.FULLGROWN.matureAge) {
                this.matureTo(EnumCompanionStage.FULLGROWN);
            }
        }
    }
    
    @Override
    public void clientUpdate() {
        if (this.npc.getRoleData().equals("eating")) {
            this.eating = this.getFood();
            if (this.isEating()) {
                this.doEating();
            }
        }
        else if (this.eating != null) {
            this.eating = null;
        }
    }
    
    private void doEating() {
        if (this.eating == null || this.eating.isEmpty()) {
            return;
        }
        ItemStack eating = this.eating.getMCItemStack();
        if (this.npc.world.isRemote) {
            Random rand = this.npc.getRNG();
            for (int j = 0; j < 2; ++j) {
                Vec3d vec3 = new Vec3d((rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
                vec3.rotateYaw(-this.npc.rotationPitch * 3.1415927f / 180.0f);
                vec3.rotatePitch(-this.npc.renderYawOffset * 3.1415927f / 180.0f);
                Vec3d vec4 = new Vec3d((rand.nextFloat() - 0.5) * 0.3, -rand.nextFloat() * 0.6 - 0.3, this.npc.width / 2.0f + 0.1);
                vec4.rotateYaw(-this.npc.rotationPitch * 3.1415927f / 180.0f);
                vec4.rotatePitch(-this.npc.renderYawOffset * 3.1415927f / 180.0f);
                vec4 = vec4.add(this.npc.posX, this.npc.posY + this.npc.height + 0.1, this.npc.posZ);
                String s = "iconcrack_" + Item.getIdFromItem(eating.getItem());
                if (eating.getHasSubtypes()) {
                    this.npc.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec4.x, vec4.y, vec4.z, vec3.x, vec3.y + 0.05, vec3.z, new int[] { Item.getIdFromItem(eating.getItem()), eating.getMetadata() });
                }
                else {
                    this.npc.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec4.x, vec4.y, vec4.z, vec3.x, vec3.y + 0.05, vec3.z, new int[] { Item.getIdFromItem(eating.getItem()) });
                }
            }
        }
        else {
            --this.eatingTicks;
            if (this.eatingTicks <= 0) {
                if (this.inventory.decrStackSize(eating, 1)) {
                    ItemFood food = (ItemFood)eating.getItem();
                    this.foodstats.onFoodEaten(food, eating);
                    this.npc.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5f, this.npc.getRNG().nextFloat() * 0.1f + 0.9f);
                }
                this.eatingDelay = 20;
                this.npc.setRoleData("");
                eating = null;
            }
            else if (this.eatingTicks > 3 && this.eatingTicks % 2 == 0) {
                Random rand = this.npc.getRNG();
                this.npc.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f + 0.5f * rand.nextInt(2), (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f);
            }
        }
    }
    
    public void matureTo(EnumCompanionStage stage) {
        this.stage = stage;
        EntityCustomNpc npc = (EntityCustomNpc)this.npc;
        npc.ais.animationType = stage.animation;
        if (stage == EnumCompanionStage.BABY) {
            npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.5f, 0.5f, 0.5f);
            npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.5f, 0.5f, 0.5f);
            npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.5f, 0.5f, 0.5f);
            npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.7f, 0.7f, 0.7f);
            npc.ais.onAttack = 1;
            npc.ais.setWalkingSpeed(3);
            if (!this.talents.containsKey(EnumCompanionTalent.INVENTORY)) {
                this.talents.put(EnumCompanionTalent.INVENTORY, 0);
            }
        }
        if (stage == EnumCompanionStage.CHILD) {
            npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.6f, 0.6f, 0.6f);
            npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.6f, 0.6f, 0.6f);
            npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.6f, 0.6f, 0.6f);
            npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.8f, 0.8f, 0.8f);
            npc.ais.onAttack = 0;
            npc.ais.setWalkingSpeed(4);
            if (!this.talents.containsKey(EnumCompanionTalent.SWORD)) {
                this.talents.put(EnumCompanionTalent.SWORD, 0);
            }
        }
        if (stage == EnumCompanionStage.TEEN) {
            npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 0.8f, 0.8f);
            npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.8f, 0.8f, 0.8f);
            npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.8f, 0.8f, 0.8f);
            npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.9f, 0.9f, 0.9f);
            npc.ais.onAttack = 0;
            npc.ais.setWalkingSpeed(5);
            if (!this.talents.containsKey(EnumCompanionTalent.ARMOR)) {
                this.talents.put(EnumCompanionTalent.ARMOR, 0);
            }
        }
        if (stage == EnumCompanionStage.ADULT || stage == EnumCompanionStage.FULLGROWN) {
            npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(1.0f, 1.0f, 1.0f);
            npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(1.0f, 1.0f, 1.0f);
            npc.modelData.getPartConfig(EnumParts.BODY).setScale(1.0f, 1.0f, 1.0f);
            npc.modelData.getPartConfig(EnumParts.HEAD).setScale(1.0f, 1.0f, 1.0f);
            npc.ais.onAttack = 0;
            npc.ais.setWalkingSpeed(5);
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("CompanionInventory", (NBTBase)this.inventory.getToNBT());
        compound.setString("CompanionOwner", this.uuid);
        compound.setString("CompanionOwnerName", this.ownerName);
        compound.setInteger("CompanionID", this.companionID);
        compound.setInteger("CompanionStage", this.stage.ordinal());
        compound.setInteger("CompanionExp", this.currentExp);
        compound.setBoolean("CompanionCanAge", this.canAge);
        compound.setLong("CompanionAge", this.ticksActive);
        compound.setBoolean("CompanionHasInv", this.hasInv);
        compound.setBoolean("CompanionDefendOwner", this.defendOwner);
        this.foodstats.writeNBT(compound);
        compound.setInteger("CompanionJob", this.job.ordinal());
        if (this.jobInterface != null) {
            compound.setTag("CompanionJobData", (NBTBase)this.jobInterface.getNBT());
        }
        NBTTagList list = new NBTTagList();
        for (EnumCompanionTalent talent : this.talents.keySet()) {
            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("Talent", talent.ordinal());
            c.setInteger("Exp", (int)this.talents.get(talent));
            list.appendTag((NBTBase)c);
        }
        compound.setTag("CompanionTalents", (NBTBase)list);
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.inventory.setFromNBT(compound.getCompoundTag("CompanionInventory"));
        this.uuid = compound.getString("CompanionOwner");
        this.ownerName = compound.getString("CompanionOwnerName");
        this.companionID = compound.getInteger("CompanionID");
        this.stage = EnumCompanionStage.values()[compound.getInteger("CompanionStage")];
        this.currentExp = compound.getInteger("CompanionExp");
        this.canAge = compound.getBoolean("CompanionCanAge");
        this.ticksActive = compound.getLong("CompanionAge");
        this.hasInv = compound.getBoolean("CompanionHasInv");
        this.defendOwner = compound.getBoolean("CompanionDefendOwner");
        this.foodstats.readNBT(compound);
        NBTTagList list = compound.getTagList("CompanionTalents", 10);
        Map<EnumCompanionTalent, Integer> talents = new TreeMap<EnumCompanionTalent, Integer>();
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            EnumCompanionTalent talent = EnumCompanionTalent.values()[c.getInteger("Talent")];
            talents.put(talent, c.getInteger("Exp"));
        }
        this.talents = talents;
        this.setJob(compound.getInteger("CompanionJob"));
        if (this.jobInterface != null) {
            this.jobInterface.setNBT(compound.getCompoundTag("CompanionJobData"));
        }
        this.setStats();
    }
    
    private void setJob(int i) {
        this.job = EnumCompanionJobs.values()[i];
        if (this.job == EnumCompanionJobs.SHOP) {
            this.jobInterface = new CompanionTrader();
        }
        else if (this.job == EnumCompanionJobs.FARMER) {
            this.jobInterface = new CompanionFarmer();
        }
        else if (this.job == EnumCompanionJobs.GUARD) {
            this.jobInterface = new CompanionGuard();
        }
        else {
            this.jobInterface = null;
        }
        if (this.jobInterface != null) {
            this.jobInterface.npc = this.npc;
        }
    }
    
    @Override
    public void interact(EntityPlayer player) {
        this.interact(player, false);
    }
    
    public void interact(EntityPlayer player, boolean openGui) {
        if (player != null && this.job == EnumCompanionJobs.SHOP) {
            ((CompanionTrader)this.jobInterface).interact(player);
        }
        if (player != this.owner || !this.npc.isEntityAlive() || this.npc.isAttacking()) {
            return;
        }
        if (player.isSneaking() || openGui) {
            this.openGui(player);
        }
        else {
            this.setSitting(!this.isSitting());
        }
    }
    
    public int getTotalLevel() {
        int level = 0;
        for (EnumCompanionTalent talent : this.talents.keySet()) {
            level += this.getTalentLevel(talent);
        }
        return level;
    }
    
    public int getMaxExp() {
        return 500 + this.getTotalLevel() * 200;
    }
    
    public void addExp(int exp) {
        if (this.canAddExp(exp)) {
            this.currentExp += exp;
        }
    }
    
    public boolean canAddExp(int exp) {
        int newExp = this.currentExp + exp;
        return newExp >= 0 && newExp < this.getMaxExp();
    }
    
    public void gainExp(int chance) {
        if (this.npc.getRNG().nextInt(chance) == 0) {
            this.addExp(1);
        }
    }
    
    private void openGui(EntityPlayer player) {
        NoppesUtilServer.sendOpenGui(player, EnumGuiType.Companion, this.npc);
    }
    
    public EntityPlayer getOwner() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            return null;
        }
        try {
            UUID id = UUID.fromString(this.uuid);
            if (id != null) {
                return NoppesUtilServer.getPlayer(this.npc.getServer(), id);
            }
        }
        catch (IllegalArgumentException ex) {}
        return null;
    }
    
    public void setOwner(EntityPlayer player) {
        this.uuid = player.getUniqueID().toString();
    }
    
    public boolean hasTalent(EnumCompanionTalent talent) {
        return this.getTalentLevel(talent) > 0;
    }
    
    public int getTalentLevel(EnumCompanionTalent talent) {
        if (!this.talents.containsKey(talent)) {
            return 0;
        }
        int exp = this.talents.get(talent);
        if (exp >= 5000) {
            return 5;
        }
        if (exp >= 3000) {
            return 4;
        }
        if (exp >= 1700) {
            return 3;
        }
        if (exp >= 1000) {
            return 2;
        }
        if (exp >= 400) {
            return 1;
        }
        return 0;
    }
    
    public Integer getNextLevel(EnumCompanionTalent talent) {
        if (!this.talents.containsKey(talent)) {
            return 0;
        }
        int exp = this.talents.get(talent);
        if (exp < 400) {
            return 400;
        }
        if (exp < 1000) {
            return 700;
        }
        if (exp < 1700) {
            return 1700;
        }
        if (exp < 3000) {
            return 3000;
        }
        return 5000;
    }
    
    public void levelSword() {
        if (!this.talents.containsKey(EnumCompanionTalent.SWORD)) {
            return;
        }
    }
    
    public void levelTalent(EnumCompanionTalent talent, int exp) {
        if (!this.talents.containsKey(EnumCompanionTalent.SWORD)) {
            return;
        }
        this.talents.put(talent, exp + this.talents.get(talent));
    }
    
    public int getExp(EnumCompanionTalent talent) {
        if (this.talents.containsKey(talent)) {
            return this.talents.get(talent);
        }
        return -1;
    }
    
    public void setExp(EnumCompanionTalent talent, int exp) {
        this.talents.put(talent, exp);
    }
    
    private boolean isWeapon(ItemStack item) {
        return item != null && item.getItem() != null && (item.getItem() instanceof ItemSword || item.getItem() instanceof ItemBow || item.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE));
    }
    
    public boolean canWearWeapon(IItemStack stack) {
        if (stack == null || stack.getMCItemStack().getItem() == null) {
            return false;
        }
        Item item = stack.getMCItemStack().getItem();
        if (item instanceof ItemSword) {
            return this.canWearSword(stack);
        }
        if (item instanceof ItemBow) {
            return this.getTalentLevel(EnumCompanionTalent.RANGED) > 2;
        }
        return item == Item.getItemFromBlock(Blocks.COBBLESTONE) && this.getTalentLevel(EnumCompanionTalent.RANGED) > 1;
    }
    
    public boolean canWearArmor(ItemStack item) {
        int level = this.getTalentLevel(EnumCompanionTalent.ARMOR);
        if (item == null || !(item.getItem() instanceof ItemArmor) || level <= 0) {
            return false;
        }
        if (level >= 5) {
            return true;
        }
        ItemArmor armor = (ItemArmor)item.getItem();
        int reduction = (int)ObfuscationReflectionHelper.getPrivateValue((Class)ItemArmor.ArmorMaterial.class, (Object)armor.getArmorMaterial(), 6);
        return (reduction <= 5 && level >= 1) || (reduction <= 7 && level >= 2) || (reduction <= 15 && level >= 3) || (reduction <= 33 && level >= 4);
    }
    
    public boolean canWearSword(IItemStack item) {
        int level = this.getTalentLevel(EnumCompanionTalent.SWORD);
        return item != null && item.getMCItemStack().getItem() instanceof ItemSword && level > 0 && (level >= 5 || this.getSwordDamage(item) - level < 4.0);
    }
    
    private double getSwordDamage(IItemStack item) {
        if (item == null || !(item.getMCItemStack().getItem() instanceof ItemSword)) {
            return 0.0;
        }
        HashMultimap map = (HashMultimap)item.getMCItemStack().getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        for (Object entry : map.entries()) {
            if (((Map.Entry) entry).getKey().equals(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
                AttributeModifier mod = (AttributeModifier) ((Map.Entry) entry).getValue();
                return mod.getAmount();
            }
        }
        return 0.0;
    }
    
    public void setStats() {
        IItemStack weapon = this.npc.inventory.getRightHand();
        this.npc.stats.melee.setStrength((int)(1.0 + this.getSwordDamage(weapon)));
        this.npc.stats.healthRegen = 0;
        this.npc.stats.combatRegen = 0;
        int ranged = this.getTalentLevel(EnumCompanionTalent.RANGED);
        if (ranged > 0 && weapon != null) {
            Item item = weapon.getMCItemStack().getItem();
            if (ranged > 0 && item == Item.getItemFromBlock(Blocks.COBBLESTONE)) {
                this.npc.inventory.setProjectile(weapon);
            }
            if (ranged > 0 && item instanceof ItemBow) {
                this.npc.inventory.setProjectile(NpcAPI.Instance().getIItemStack(new ItemStack(Items.ARROW)));
            }
        }
        this.inventory.setSize(2 + this.getTalentLevel(EnumCompanionTalent.INVENTORY) * 2);
    }
    
    public void setSelfsuficient(boolean bo) {
        if (this.owner == null || (this.jobInterface != null && bo == this.jobInterface.isSelfSufficient())) {
            return;
        }
        PlayerData data = PlayerData.get(this.owner);
        if (!bo && data.hasCompanion()) {
            return;
        }
        data.setCompanion(bo ? null : this.npc);
        if (this.job == EnumCompanionJobs.GUARD) {
            ((CompanionGuard)this.jobInterface).isStanding = bo;
        }
        else if (this.job == EnumCompanionJobs.FARMER) {
            ((CompanionFarmer)this.jobInterface).isStanding = bo;
        }
    }
    
    public void setSitting(boolean sit) {
        if (sit) {
            this.npc.ais.animationType = 1;
            this.npc.ais.onAttack = 3;
            this.npc.ais.setStartPos(new BlockPos((Entity)this.npc));
            this.npc.getNavigator().clearPath();
            this.npc.setPositionAndUpdate((double)this.npc.getStartXPos(), this.npc.posY, (double)this.npc.getStartZPos());
        }
        else {
            this.npc.ais.animationType = this.stage.animation;
            this.npc.ais.onAttack = 0;
        }
        this.npc.updateAI = true;
    }
    
    public boolean isSitting() {
        return this.npc.ais.animationType == 1;
    }
    
    public float applyArmorCalculations(DamageSource source, float damage) {
        if (!this.hasInv || this.getTalentLevel(EnumCompanionTalent.ARMOR) <= 0) {
            return damage;
        }
        if (!source.isUnblockable()) {
            this.damageArmor(damage);
            int i = 25 - this.getTotalArmorValue();
            float f1 = damage * i;
            damage = f1 / 25.0f;
        }
        return damage;
    }
    
    private void damageArmor(float damage) {
        damage /= 4.0f;
        if (damage < 1.0f) {
            damage = 1.0f;
        }
        boolean hasArmor = false;
        Iterator<Map.Entry<Integer, IItemStack>> ita = this.npc.inventory.armor.entrySet().iterator();
        while (ita.hasNext()) {
            Map.Entry<Integer, IItemStack> entry = ita.next();
            IItemStack item = entry.getValue();
            if (item != null) {
                if (!(item.getMCItemStack().getItem() instanceof ItemArmor)) {
                    continue;
                }
                hasArmor = true;
                item.getMCItemStack().damageItem((int)damage, (EntityLivingBase)this.npc);
                if (item.getStackSize() > 0) {
                    continue;
                }
                ita.remove();
            }
        }
        this.gainExp(hasArmor ? 4 : 8);
    }
    
    public int getTotalArmorValue() {
        int armorValue = 0;
        for (IItemStack armor : this.npc.inventory.armor.values()) {
            if (armor != null && armor.getMCItemStack().getItem() instanceof ItemArmor) {
                armorValue += ((ItemArmor)armor.getMCItemStack().getItem()).damageReduceAmount;
            }
        }
        return armorValue;
    }
    
    @Override
    public boolean isFollowing() {
        return (this.jobInterface == null || !this.jobInterface.isSelfSufficient()) && this.owner != null && !this.isSitting();
    }
    
    @Override
    public boolean defendOwner() {
        return this.defendOwner && this.owner != null && this.stage != EnumCompanionStage.BABY && (this.jobInterface == null || !this.jobInterface.isSelfSufficient());
    }
    
    public boolean hasOwner() {
        return !this.uuid.isEmpty();
    }
    
    public void addMovementStat(double x, double y, double z) {
        int i = Math.round(MathHelper.sqrt(x * x + y * y + z * z) * 100.0f);
        if (this.npc.isAttacking()) {
            this.foodstats.addExhaustion(0.04f * i * 0.01f);
        }
        else {
            this.foodstats.addExhaustion(0.02f * i * 0.01f);
        }
    }
    
    private IItemStack getFood() {
        List<ItemStack> food = new ArrayList<ItemStack>((Collection<? extends ItemStack>)this.inventory.items);
        Iterator<ItemStack> ite = food.iterator();
        int i = -1;
        while (ite.hasNext()) {
            ItemStack is = ite.next();
            if (is.isEmpty() || !(is.getItem() instanceof ItemFood)) {
                ite.remove();
            }
            else {
                int amount = ((ItemFood)is.getItem()).getDamage(is);
                if (i != -1 && amount >= i) {
                    continue;
                }
                i = amount;
            }
        }
        for (ItemStack is2 : food) {
            if (((ItemFood)is2.getItem()).getDamage(is2) == i) {
                return NpcAPI.Instance().getIItemStack(is2);
            }
        }
        return null;
    }
    
    public IItemStack getHeldItem() {
        if (this.eating != null && !this.eating.isEmpty()) {
            return this.eating;
        }
        return this.npc.inventory.getRightHand();
    }
    
    public boolean isEating() {
        return this.eating != null && !this.eating.isEmpty();
    }
    
    public boolean hasInv() {
        return this.hasInv && (this.hasTalent(EnumCompanionTalent.INVENTORY) || this.hasTalent(EnumCompanionTalent.ARMOR) || this.hasTalent(EnumCompanionTalent.SWORD));
    }
    
    public void attackedEntity(Entity entity) {
        IItemStack weapon = this.npc.inventory.getRightHand();
        this.gainExp((weapon == null) ? 8 : 4);
        if (weapon == null) {
            return;
        }
        weapon.getMCItemStack().damageItem(1, (EntityLivingBase)this.npc);
        if (weapon.getMCItemStack().getCount() <= 0) {
            this.npc.inventory.setRightHand(null);
        }
    }
    
    public void addTalentExp(EnumCompanionTalent talent, int exp) {
        if (this.talents.containsKey(talent)) {
            exp += this.talents.get(talent);
        }
        this.talents.put(talent, exp);
    }
}
