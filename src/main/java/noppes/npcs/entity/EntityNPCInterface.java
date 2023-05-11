package noppes.npcs.entity;

import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.datasync.EntityDataManager;
import noppes.npcs.roles.JobFollower;
import noppes.npcs.roles.RoleFollower;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.NBTTags;
import java.io.IOException;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.MobEffects;
import noppes.npcs.controllers.FactionController;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.inventory.EntityEquipmentSlot;
import noppes.npcs.api.item.IItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraft.block.Block;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.block.material.Material;
import noppes.npcs.ai.EntityAITransform;
import noppes.npcs.ai.EntityAIRole;
import noppes.npcs.ai.EntityAIJob;
import noppes.npcs.ai.EntityAIWorldLines;
import noppes.npcs.ai.EntityAIWatchClosest;
import noppes.npcs.ai.EntityAIFollow;
import noppes.npcs.ai.EntityAIReturn;
import noppes.npcs.ai.EntityAIFindShade;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import noppes.npcs.ai.EntityAIMoveIndoors;
import noppes.npcs.ai.EntityAIBustDoor;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import noppes.npcs.ai.EntityAIMovingPath;
import noppes.npcs.ai.EntityAIWander;
import noppes.npcs.ai.EntityAIAttackTarget;
import noppes.npcs.ai.EntityAIDodgeShoot;
import noppes.npcs.ai.EntityAIStalkTarget;
import noppes.npcs.ai.EntityAIAmbushTarget;
import noppes.npcs.ai.EntityAIOrbitTarget;
import noppes.npcs.ai.EntityAIZigZagTarget;
import noppes.npcs.ai.EntityAIPounceTarget;
import noppes.npcs.ai.EntityAIAvoidTarget;
import noppes.npcs.ai.EntityAIPanic;
import noppes.npcs.ai.EntityAISprintToTarget;
import com.google.common.base.Predicate;
import noppes.npcs.ai.EntityAIWaterNav;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNavigateFlying;
import noppes.npcs.ai.FlyingMoveHelper;
import noppes.npcs.ai.target.EntityAIOwnerHurtTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtByTarget;
import noppes.npcs.ai.target.EntityAIClosestTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import noppes.npcs.ai.target.EntityAIClearTarget;
import noppes.npcs.ai.selector.NPCAttackSelector;
import java.util.Collection;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IProjectile;
import net.minecraft.entity.EntityLiving;
import noppes.npcs.items.ItemSoulstoneFilled;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraftforge.common.ForgeHooks;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.controllers.data.Dialog;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.data.PlayerData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import noppes.npcs.CustomItems;
import net.minecraft.util.EnumHand;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import java.util.Iterator;
import noppes.npcs.roles.JobBard;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.potion.PotionEffect;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.roles.RoleCompanion;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.client.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.api.event.NpcEvent;
import net.minecraft.entity.Entity;
import noppes.npcs.EventHooks;
import net.minecraft.entity.SharedMonsterAttributes;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.wrapper.NPCWrapper;
import net.minecraft.world.BossInfo;
import noppes.npcs.VersionCompatibility;
import java.util.ArrayList;
import net.minecraft.world.World;
import java.util.HashSet;
import net.minecraft.world.BossInfoServer;
import noppes.npcs.IChatMessages;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import java.util.List;
import noppes.npcs.ai.EntityAIAnimation;
import noppes.npcs.ai.EntityAILook;
import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.ai.EntityAIRangedAttack;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.DialogOption;
import java.util.HashMap;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.RoleInterface;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.ai.CombatHandler;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.controllers.data.DataTransform;
import noppes.npcs.entity.data.DataScript;
import noppes.npcs.entity.data.DataInventory;
import noppes.npcs.entity.data.DataAdvanced;
import noppes.npcs.entity.data.DataAI;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.entity.data.DataAbilities;
import noppes.npcs.api.entity.ICustomNpc;
import net.minecraftforge.common.util.FakePlayer;
import noppes.npcs.util.GameProfileAlt;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.EntityCreature;

public abstract class EntityNPCInterface extends EntityCreature implements IEntityAdditionalSpawnData, ICommandSender, IRangedAttackMob, IAnimals
{
    public static DataParameter<Boolean> Attacking;
    protected static DataParameter<Integer> Animation;
    private static DataParameter<String> RoleData;
    private static DataParameter<String> JobData;
    private static DataParameter<Integer> FactionData;
    private static DataParameter<Boolean> Walking;
    private static DataParameter<Boolean> Interacting;
    private static DataParameter<Boolean> IsDead;
    public static GameProfileAlt CommandProfile;
    public static GameProfileAlt ChatEventProfile;
    public static GameProfileAlt GenericProfile;
    public static FakePlayer ChatEventPlayer;
    public static FakePlayer CommandPlayer;
    public static FakePlayer GenericPlayer;
    public ICustomNpc wrappedNPC;
    public DataAbilities abilities;
    public DataDisplay display;
    public DataStats stats;
    public DataAI ais;
    public DataAdvanced advanced;
    public DataInventory inventory;
    public DataScript script;
    public DataTransform transform;
    public DataTimers timers;
    public CombatHandler combatHandler;
    public String linkedName;
    public long linkedLast;
    public LinkedNpcController.LinkedData linkedData;
    public float baseHeight;
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    private boolean wasKilled;
    public RoleInterface roleInterface;
    public JobInterface jobInterface;
    public HashMap<Integer, DialogOption> dialogs;
    public boolean hasDied;
    public long killedtime;
    public long totalTicksAlive;
    private int taskCount;
    public int lastInteract;
    public Faction faction;
    private EntityAIRangedAttack aiRange;
    private EntityAIBase aiAttackTarget;
    public EntityAILook lookAi;
    public EntityAIAnimation animateAi;
    public List<EntityLivingBase> interactingEntities;
    public ResourceLocation textureLocation;
    public ResourceLocation textureGlowLocation;
    public ResourceLocation textureCloakLocation;
    public int currentAnimation;
    public int animationStart;
    public int npcVersion;
    public IChatMessages messages;
    public boolean updateClient;
    public boolean updateAI;
    public BossInfoServer bossInfo;
    public HashSet<Integer> tracking;
    public double field_20066_r;
    public double field_20065_s;
    public double field_20064_t;
    public double field_20063_u;
    public double field_20062_v;
    public double field_20061_w;
    private double startYPos;
    
    public EntityNPCInterface(World world) {
        super(world);
        this.combatHandler = new CombatHandler(this);
        this.linkedName = "";
        this.linkedLast = 0L;
        this.baseHeight = 1.8f;
        this.wasKilled = false;
        this.hasDied = false;
        this.killedtime = 0L;
        this.totalTicksAlive = 0L;
        this.taskCount = 1;
        this.lastInteract = 0;
        this.interactingEntities = new ArrayList<EntityLivingBase>();
        this.textureLocation = null;
        this.textureGlowLocation = null;
        this.textureCloakLocation = null;
        this.currentAnimation = 0;
        this.animationStart = 0;
        this.npcVersion = VersionCompatibility.ModRev;
        this.updateClient = false;
        this.updateAI = false;
        this.bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
        this.tracking = new HashSet<Integer>();
        this.startYPos = -1.0;
        if (!this.isRemote()) {
            this.wrappedNPC = new NPCWrapper(this);
        }
        this.dialogs = new HashMap<Integer, DialogOption>();
        if (!CustomNpcs.DefaultInteractLine.isEmpty()) {
            this.advanced.interactLines.lines.put(0, new Line(CustomNpcs.DefaultInteractLine));
        }
        this.experienceValue = 0;
        float scaleX = 0.9375f;
        this.scaleZ = scaleX;
        this.scaleY = scaleX;
        this.scaleX = scaleX;
        this.faction = this.getFaction();
        this.setFaction(this.faction.id);
        this.setSize(1.0f, 1.0f);
        this.updateAI = true;
        this.bossInfo.setVisible(false);
    }
    
    public boolean canBreatheUnderwater() {
        return this.ais.movementType == 2;
    }
    
    public boolean isPushedByWater() {
        return this.ais.movementType != 2;
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.abilities = new DataAbilities(this);
        this.display = new DataDisplay(this);
        this.stats = new DataStats(this);
        this.ais = new DataAI(this);
        this.advanced = new DataAdvanced(this);
        this.inventory = new DataInventory(this);
        this.transform = new DataTransform(this);
        this.script = new DataScript(this);
        this.timers = new DataTimers(this);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.stats.maxHealth);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)CustomNpcs.NpcNavRange);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)this.getSpeed());
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)this.stats.melee.getStrength());
        this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((double)(this.getSpeed() * 2.0f));
    }
    
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register((DataParameter)EntityNPCInterface.RoleData, (Object)String.valueOf(""));
        this.dataManager.register((DataParameter)EntityNPCInterface.JobData, (Object)String.valueOf(""));
        this.dataManager.register((DataParameter)EntityNPCInterface.FactionData, (Object)0);
        this.dataManager.register((DataParameter)EntityNPCInterface.Animation, (Object)0);
        this.dataManager.register((DataParameter)EntityNPCInterface.Walking, (Object)false);
        this.dataManager.register((DataParameter)EntityNPCInterface.Interacting, (Object)false);
        this.dataManager.register((DataParameter)EntityNPCInterface.IsDead, (Object)false);
        this.dataManager.register((DataParameter)EntityNPCInterface.Attacking, (Object)false);
    }
    
    public boolean isEntityAlive() {
        return super.isEntityAlive() && !this.isKilled();
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted % 10 == 0) {
            this.startYPos = this.calculateStartYPos(this.ais.startPos()) + 1.0;
            if (this.startYPos < 0.0 && !this.isRemote()) {
                this.setDead();
            }
            EventHooks.onNPCTick(this);
        }
        this.timers.update();
        if (this.world.isRemote && this.wasKilled != this.isKilled()) {
            this.deathTime = 0;
            this.updateHitbox();
        }
        this.wasKilled = this.isKilled();
        if (this.currentAnimation == 14) {
            this.deathTime = 19;
        }
    }
    
    public boolean attackEntityAsMob(Entity par1Entity) {
        float f = (float)this.stats.melee.getStrength();
        if (this.stats.melee.getDelay() < 10) {
            par1Entity.hurtResistantTime = 0;
        }
        if (par1Entity instanceof EntityLivingBase) {
            NpcEvent.MeleeAttackEvent event = new NpcEvent.MeleeAttackEvent(this.wrappedNPC, (EntityLivingBase)par1Entity, f);
            if (EventHooks.onNPCAttacksMelee(this, event)) {
                return false;
            }
            f = event.damage;
        }
        boolean var4 = par1Entity.attackEntityFrom((DamageSource)new NpcDamageSource("mob", (Entity)this), f);
        if (var4) {
            /*if (this.getOwner() instanceof EntityPlayer) {
	            EntityUtil.setRecentlyHit((EntityLivingBase)par1Entity);
	        }*/
            if (this.stats.melee.getKnockback() > 0) {
                par1Entity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 3.1415927f / 180.0f) * this.stats.melee.getKnockback() * 0.5f), 0.1, (double)(MathHelper.cos(this.rotationYaw * 3.1415927f / 180.0f) * this.stats.melee.getKnockback() * 0.5f));
                this.motionX *= 0.6;
                this.motionZ *= 0.6;
            }
            if (this.advanced.role == 6) {
                ((RoleCompanion)this.roleInterface).attackedEntity(par1Entity);
            }
        }
        if (this.stats.melee.getEffectType() != 0) {
            if (this.stats.melee.getEffectType() != 1) {
                ((EntityLivingBase)par1Entity).addPotionEffect(new PotionEffect(PotionEffectType.getMCType(this.stats.melee.getEffectType()), this.stats.melee.getEffectTime() * 20, this.stats.melee.getEffectStrength()));
            }
            else {
                par1Entity.setFire(this.stats.melee.getEffectTime());
            }
        }
        return var4;
    }
    
    public void onLivingUpdate() {
        if (CustomNpcs.FreezeNPCs) {
            return;
        }
        if (this.isAIDisabled()) {
            super.onLivingUpdate();
            return;
        }
        ++this.totalTicksAlive;
        this.updateArmSwingProgress();
        if (this.ticksExisted % 20 == 0) {
            this.faction = this.getFaction();
        }
        if (!this.world.isRemote) {
            if (!this.isKilled() && this.ticksExisted % 20 == 0) {
                this.advanced.scenes.update();
                if (this.getHealth() < this.getMaxHealth()) {
                    if (this.stats.healthRegen > 0 && !this.isAttacking()) {
                        this.heal((float)this.stats.healthRegen);
                    }
                    if (this.stats.combatRegen > 0 && this.isAttacking()) {
                        this.heal((float)this.stats.combatRegen);
                    }
                }
                if (this.faction.getsAttacked && !this.isAttacking()) {
                    List<EntityMob> list = (List<EntityMob>)this.world.getEntitiesWithinAABB((Class)EntityMob.class, this.getEntityBoundingBox().grow(16.0, 16.0, 16.0));
                    for (EntityMob mob : list) {
                        if (mob.getAttackTarget() == null && this.canSee((Entity)mob)) {
                            mob.setAttackTarget((EntityLivingBase)this);
                        }
                    }
                }
                if (this.linkedData != null && this.linkedData.time > this.linkedLast) {
                    LinkedNpcController.Instance.loadNpcData(this);
                }
                if (this.updateClient) {
                    this.updateClient();
                }
                if (this.updateAI) {
                    this.updateTasks();
                    this.updateAI = false;
                }
            }
            if (this.getHealth() <= 0.0f && !this.isKilled()) {
                this.clearActivePotions();
                this.dataManager.set((DataParameter)EntityNPCInterface.IsDead, (Object)true);
                this.updateTasks();
                this.updateHitbox();
            }
            if (this.display.getBossbar() == 2) {
                this.bossInfo.setVisible(this.getAttackTarget() != null);
            }
            this.dataManager.set((DataParameter)EntityNPCInterface.Walking, (Object)!this.getNavigator().noPath());
            this.dataManager.set((DataParameter)EntityNPCInterface.Interacting, (Object)this.isInteracting());
            this.combatHandler.update();
            this.onCollide();
        }
        if (this.wasKilled != this.isKilled() && this.wasKilled) {
            this.reset();
        }
        if (this.world.isDaytime() && !this.world.isRemote && this.stats.burnInSun) {
            float f = this.getBrightness();
            if (f > 0.5f && this.rand.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && this.world.canBlockSeeSky(new BlockPos((Entity)this))) {
                this.setFire(8);
            }
        }
        super.onLivingUpdate();
        if (this.world.isRemote) {
            if (this.roleInterface != null) {
                this.roleInterface.clientUpdate();
            }
            if (this.textureCloakLocation != null) {
                this.cloakUpdate();
            }
            if (this.currentAnimation != (int)this.dataManager.get((DataParameter)EntityNPCInterface.Animation)) {
                this.currentAnimation = (int)this.dataManager.get((DataParameter)EntityNPCInterface.Animation);
                this.animationStart = this.ticksExisted;
                this.updateHitbox();
            }
            if (this.advanced.job == 1) {
                ((JobBard)this.jobInterface).onLivingUpdate();
            }
        }
        if (this.display.getBossbar() > 0) {
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }
    
    public void updateClient() {
        NBTTagCompound compound = this.writeSpawnData();
        compound.setInteger("EntityId", this.getEntityId());
        Server.sendAssociatedData((Entity)this, EnumPacketClient.UPDATE_NPC, compound);
        this.updateClient = false;
    }
    
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        super.damageEntity(damageSrc, damageAmount);
        this.combatHandler.damage(damageSrc, damageAmount);
    }
    
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (this.world.isRemote) {
            return !this.isAttacking();
        }
        if (hand != EnumHand.MAIN_HAND) {
            return true;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (stack != null) {
            Item item = stack.getItem();
            if (item == CustomItems.cloner || item == CustomItems.wand || item == CustomItems.mount || item == CustomItems.scripter) {
                this.setAttackTarget(null);
                this.setRevengeTarget((EntityLivingBase)null);
                return true;
            }
            if (item == CustomItems.moving) {
                this.setAttackTarget(null);
                stack.setTagInfo("NPCID", (NBTBase)new NBTTagInt(this.getEntityId()));
                player.sendMessage((ITextComponent)new TextComponentTranslation("Registered " + this.getName() + " to your NPC Pather", new Object[0]));
                return true;
            }
        }
        if (EventHooks.onNPCInteract(this, player)) {
            return false;
        }
        if (this.getFaction().isAggressiveToPlayer(player)) {
            return !this.isAttacking();
        }
        this.addInteract((EntityLivingBase)player);
        Dialog dialog = this.getDialog(player);
        QuestData data = PlayerData.get(player).questData.getQuestCompletion(player, this);
        if (data != null) {
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.QUEST_COMPLETION, data.quest.id);
        }
        else if (dialog != null) {
            NoppesUtilServer.openDialog(player, this, dialog);
        }
        else if (this.roleInterface != null) {
            this.roleInterface.interact(player);
        }
        else {
            this.say(player, this.advanced.getInteractLine());
        }
        return true;
    }
    
    public void addInteract(EntityLivingBase entity) {
        if (!this.ais.stopAndInteract || this.isAttacking() || !entity.isEntityAlive() || this.isAIDisabled()) {
            return;
        }
        if (this.ticksExisted - this.lastInteract < 180) {
            this.interactingEntities.clear();
        }
        this.getNavigator().clearPath();
        this.lastInteract = this.ticksExisted;
        if (!this.interactingEntities.contains(entity)) {
            this.interactingEntities.add(entity);
        }
    }
    
    public boolean isInteracting() {
        return this.ticksExisted - this.lastInteract < 40 || (this.isRemote() && (boolean)this.dataManager.get((DataParameter)EntityNPCInterface.Interacting)) || (this.ais.stopAndInteract && !this.interactingEntities.isEmpty() && this.ticksExisted - this.lastInteract < 180);
    }
    
    private Dialog getDialog(EntityPlayer player) {
        for (DialogOption option : this.dialogs.values()) {
            if (option == null) {
                continue;
            }
            if (!option.hasDialog()) {
                continue;
            }
            Dialog dialog = option.getDialog();
            if (dialog.availability.isAvailable(player)) {
                return dialog;
            }
        }
        return null;
    }
    
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (this.world.isRemote || CustomNpcs.FreezeNPCs || damagesource.damageType.equals("inWall")) {
            return false;
        }
        if (damagesource.damageType.equals("outOfWorld") && this.isKilled()) {
            this.reset();
        }
        i = this.stats.resistances.applyResistance(damagesource, i);
        if (this.hurtResistantTime > this.maxHurtResistantTime / 2.0f && i <= this.lastDamage) {
            return false;
        }
        Entity entity = NoppesUtilServer.GetDamageSourcee(damagesource);
        EntityLivingBase attackingEntity = null;
        if (entity instanceof EntityLivingBase) {
            attackingEntity = (EntityLivingBase)entity;
        }
        if (attackingEntity != null && attackingEntity == this.getOwner()) {
            return false;
        }
        if (attackingEntity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)attackingEntity;
            if (npc.faction.id == this.faction.id) {
                return false;
            }
            if (npc.getOwner() instanceof EntityPlayer) {
                this.recentlyHit = 100;
            }
        }
        else if (attackingEntity instanceof EntityPlayer && this.faction.isFriendlyToPlayer((EntityPlayer)attackingEntity)) {
            ForgeHooks.onLivingAttack((EntityLivingBase)this, damagesource, i);
            return false;
        }
        NpcEvent.DamagedEvent event = new NpcEvent.DamagedEvent(this.wrappedNPC, entity, i, damagesource);
        if (EventHooks.onNPCDamaged(this, event)) {
            ForgeHooks.onLivingAttack((EntityLivingBase)this, damagesource, i);
            return false;
        }
        i = event.damage;
        if (this.isKilled()) {
            return false;
        }
        if (attackingEntity == null) {
            return super.attackEntityFrom(damagesource, i);
        }
        try {
            if (this.isAttacking()) {
                if (this.getAttackTarget() != null && attackingEntity != null && this.getDistance((Entity)this.getAttackTarget()) > this.getDistance((Entity)attackingEntity)) {
                    this.setAttackTarget(attackingEntity);
                }
                return super.attackEntityFrom(damagesource, i);
            }
            if (i > 0.0f) {
                List<EntityNPCInterface> inRange = (List<EntityNPCInterface>)this.world.getEntitiesWithinAABB((Class)EntityNPCInterface.class, this.getEntityBoundingBox().grow(32.0, 16.0, 32.0));
                for (EntityNPCInterface npc2 : inRange) {
                    if (!npc2.isKilled() && npc2.advanced.defendFaction) {
                        if (npc2.faction.id != this.faction.id) {
                            continue;
                        }
                        if (!npc2.canSee((Entity)this) && !npc2.ais.directLOS && !npc2.canSee((Entity)attackingEntity)) {
                            continue;
                        }
                        npc2.onAttack(attackingEntity);
                    }
                }
                this.setAttackTarget(attackingEntity);
            }
            return super.attackEntityFrom(damagesource, i);
        }
        finally {
            if (event.clearTarget) {
                this.setAttackTarget(null);
                this.setRevengeTarget((EntityLivingBase)null);
            }
        }
    }
    
    public void onAttack(EntityLivingBase entity) {
        if (entity == null || entity == this || this.isAttacking() || this.ais.onAttack == 3 || entity == this.getOwner()) {
            return;
        }
        super.setAttackTarget(entity);
    }
    
    public void setAttackTarget(EntityLivingBase entity) {
        if ((entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.disableDamage) || (entity != null && entity == this.getOwner()) || this.getAttackTarget() == entity) {
            return;
        }
        if (entity != null) {
            NpcEvent.TargetEvent event = new NpcEvent.TargetEvent(this.wrappedNPC, entity);
            if (EventHooks.onNPCTarget(this, event)) {
                return;
            }
            if (event.entity == null) {
                entity = null;
            }
            else {
                entity = event.entity.getMCEntity();
            }
        }
        else {
            for (EntityAITasks.EntityAITaskEntry en : this.targetTasks.taskEntries) {
                if (en.using) {
                    en.using = false;
                    en.action.resetTask();
                }
            }
            if (EventHooks.onNPCTargetLost(this, this.getAttackTarget())) {
                return;
            }
        }
        if (entity != null && entity != this && this.ais.onAttack != 3 && !this.isAttacking() && !this.isRemote()) {
            Line line = this.advanced.getAttackLine();
            if (line != null) {
                this.saySurrounding(Line.formatTarget(line, entity));
            }
        }
        super.setAttackTarget(entity);
    }
    
    public void attackEntityWithRangedAttack(EntityLivingBase entity, float f) {
        ItemStack proj = ItemStackWrapper.MCItem(this.inventory.getProjectile());
        if (proj == null) {
            this.updateAI = true;
            return;
        }
        NpcEvent.RangedLaunchedEvent event = new NpcEvent.RangedLaunchedEvent(this.wrappedNPC, entity, (float)this.stats.ranged.getStrength());
        for (int i = 0; i < this.stats.ranged.getShotCount(); ++i) {
            EntityProjectile projectile2 = this.shoot(entity, this.stats.ranged.getAccuracy(), proj, f == 1.0f);
            projectile2.damage = event.damage;
            projectile2.callback = ((projectile1, pos, entity1) -> {
				IItemStack stack = event.projectiles.get(0).getItem();
				if (stack == CustomItems.soulstoneFull) {
                	Entity e = ItemSoulstoneFilled.Spawn(null, stack.getMCItemStack(), this.world, pos);
                    if (e instanceof EntityLivingBase && entity1 instanceof EntityLivingBase) {
                        if (e instanceof EntityLiving) {
                            ((EntityLiving)e).setAttackTarget((EntityLivingBase)entity1);
                        }
                        else {
                            ((EntityLivingBase)e).setRevengeTarget((EntityLivingBase)entity1);
                        }
                    }
                }
                projectile1.playSound(this.stats.ranged.getSoundEvent((entity1 != null) ? 1 : 2), 1.0f, 1.2f / (this.getRNG().nextFloat() * 0.2f + 0.9f));
                return false;
            });
            this.playSound(this.stats.ranged.getSoundEvent(0), 2.0f, 1.0f);
            event.projectiles.add((IProjectile)NpcAPI.Instance().getIEntity((Entity)projectile2));
        }
        EventHooks.onNPCRangedLaunched(this, event);
    }
    
    public EntityProjectile shoot(EntityLivingBase entity, int accuracy, ItemStack proj, boolean indirect) {
        return this.shoot(entity.posX, entity.getEntityBoundingBox().minY + entity.height / 2.0f, entity.posZ, accuracy, proj, indirect);
    }
    
    public EntityProjectile shoot(double x, double y, double z, int accuracy, ItemStack proj, boolean indirect) {
        EntityProjectile projectile = new EntityProjectile(this.world, (EntityLivingBase)this, proj.copy(), true);
        double varX = x - this.posX;
        double varY = y - (this.posY + this.getEyeHeight());
        double varZ = z - this.posZ;
        float varF = projectile.hasGravity() ? MathHelper.sqrt(varX * varX + varZ * varZ) : 0.0f;
        float angle = projectile.getAngleForXYZ(varX, varY, varZ, varF, indirect);
        float acc = 20.0f - MathHelper.floor(accuracy / 5.0f);
        projectile.shoot(varX, varY, varZ, angle, acc);
        this.world.spawnEntity((Entity)projectile);
        return projectile;
    }
    
    private void clearTasks(EntityAITasks tasks) {
        Iterator iterator = tasks.taskEntries.iterator();
        List<EntityAITasks.EntityAITaskEntry> list = new ArrayList<EntityAITasks.EntityAITaskEntry>(tasks.taskEntries);
        for (EntityAITasks.EntityAITaskEntry entityaitaskentry : list) {
            tasks.removeTask(entityaitaskentry.action);
        }
        tasks.taskEntries.clear();
    }
    
    private void updateTasks() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        this.clearTasks(this.tasks);
        this.clearTasks(this.targetTasks);
        if (this.isKilled()) {
            return;
        }
        Predicate attackEntitySelector = (Predicate)new NPCAttackSelector(this);
        this.targetTasks.addTask(0, (EntityAIBase)new EntityAIClearTarget(this));
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAIClosestTarget(this, EntityLivingBase.class, 4, this.ais.directLOS, false, attackEntitySelector));
        this.targetTasks.addTask(3, (EntityAIBase)new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(4, (EntityAIBase)new EntityAIOwnerHurtTarget(this));
        this.world.onEntityRemoved((Entity)this);
        if (this.ais.movementType == 1) {
            this.moveHelper = new FlyingMoveHelper(this);
            this.navigator = (PathNavigate)new PathNavigateFlying((EntityLiving)this, this.world);
        }
        else if (this.ais.movementType == 2) {
            this.moveHelper = new FlyingMoveHelper(this);
            this.navigator = (PathNavigate)new PathNavigateSwimmer((EntityLiving)this, this.world);
        }
        else {
            this.moveHelper = new EntityMoveHelper((EntityLiving)this);
            this.navigator = (PathNavigate)new PathNavigateGround((EntityLiving)this, this.world);
            this.tasks.addTask(0, (EntityAIBase)new EntityAIWaterNav(this));
        }
        this.world.onEntityAdded((Entity)this);
        this.taskCount = 1;
        this.addRegularEntries();
        this.doorInteractType();
        this.seekShelter();
        this.setResponse();
        this.setMoveType();
    }
    
    private void setResponse() {
        EntityAIRangedAttack entityAIRangedAttack = null;
        this.aiRange = entityAIRangedAttack;
        this.aiAttackTarget = entityAIRangedAttack;
        if (this.ais.canSprint) {
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAISprintToTarget(this));
        }
        if (this.ais.onAttack == 1) {
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIPanic(this, 1.2f));
        }
        else if (this.ais.onAttack == 2) {
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIAvoidTarget(this));
        }
        else if (this.ais.onAttack == 0) {
            if (this.ais.canLeap) {
                this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIPounceTarget(this));
            }
            if (this.inventory.getProjectile() == null) {
                switch (this.ais.tacticalVariant) {
                    case 1: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIZigZagTarget(this, 1.3));
                        break;
                    }
                    case 2: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIOrbitTarget(this, 1.3, true));
                        break;
                    }
                    case 3: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIAvoidTarget(this));
                        break;
                    }
                    case 4: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIAmbushTarget(this, 1.2));
                        break;
                    }
                    case 5: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIStalkTarget(this));
                        break;
                    }
                }
            }
            else {
                switch (this.ais.tacticalVariant) {
                    case 1: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIDodgeShoot(this));
                        break;
                    }
                    case 2: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIOrbitTarget(this, 1.3, false));
                        break;
                    }
                    case 3: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIAvoidTarget(this));
                        break;
                    }
                    case 4: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIAmbushTarget(this, 1.3));
                        break;
                    }
                    case 5: {
                        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIStalkTarget(this));
                        break;
                    }
                }
            }
            this.tasks.addTask(this.taskCount, this.aiAttackTarget = new EntityAIAttackTarget(this));
            ((EntityAIAttackTarget)this.aiAttackTarget).navOverride(this.ais.tacticalVariant == 6);
            if (this.inventory.getProjectile() != null) {
                this.tasks.addTask(this.taskCount++, (EntityAIBase)(this.aiRange = new EntityAIRangedAttack((IRangedAttackMob)this)));
                this.aiRange.navOverride(this.ais.tacticalVariant == 6);
            }
        }
        else if (this.ais.onAttack == 3) {}
    }
    
    public boolean canFly() {
        return false;
    }
    
    public void setMoveType() {
        if (this.ais.getMovingType() == 1) {
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIWander(this));
        }
        if (this.ais.getMovingType() == 2) {
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIMovingPath(this));
        }
    }
    
    public void doorInteractType() {
        if (this.canFly()) {
            return;
        }
        EntityAIBase aiDoor = null;
        if (this.ais.doorInteract == 1) {
            this.tasks.addTask(this.taskCount++, aiDoor = (EntityAIBase)new EntityAIOpenDoor((EntityLiving)this, true));
        }
        else if (this.ais.doorInteract == 0) {
            this.tasks.addTask(this.taskCount++, aiDoor = (EntityAIBase)new EntityAIBustDoor((EntityLiving)this));
        }
        if (this.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround)this.getNavigator()).setBreakDoors(aiDoor != null);
        }
    }
    
    public void seekShelter() {
        if (this.ais.findShelter == 0) {
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIMoveIndoors(this));
        }
        else if (this.ais.findShelter == 1) {
            if (!this.canFly()) {
                this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIRestrictSun((EntityCreature)this));
            }
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIFindShade(this));
        }
    }
    
    public void addRegularEntries() {
        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIReturn(this));
        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIFollow(this));
        if (this.ais.getStandingType() != 1 && this.ais.getStandingType() != 3) {
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIWatchClosest(this, EntityLivingBase.class, 5.0f));
        }
        this.tasks.addTask(this.taskCount++, (EntityAIBase)(this.lookAi = new EntityAILook(this)));
        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIWorldLines(this));
        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIJob(this));
        this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAIRole(this));
        this.tasks.addTask(this.taskCount++, (EntityAIBase)(this.animateAi = new EntityAIAnimation(this)));
        if (this.transform.isValid()) {
            this.tasks.addTask(this.taskCount++, (EntityAIBase)new EntityAITransform(this));
        }
    }
    
    public float getSpeed() {
        return this.ais.getWalkingSpeed() / 20.0f;
    }
    
    public float getBlockPathWeight(BlockPos pos) {
        if (this.ais.movementType == 2) {
            return (this.world.getBlockState(pos).getMaterial() == Material.WATER) ? 10.0f : 0.0f;
        }
        float weight = this.world.getLightBrightness(pos) - 0.5f;
        if (this.world.getBlockState(pos).isOpaqueCube()) {
            weight += 10.0f;
        }
        return weight;
    }
    
    protected int decreaseAirSupply(int par1) {
        if (!this.stats.canDrown) {
            return par1;
        }
        return super.decreaseAirSupply(par1);
    }
    
    public EnumCreatureAttribute getCreatureAttribute() {
        return (this.stats == null) ? null : this.stats.creatureType;
    }
    
    public int getTalkInterval() {
        return 160;
    }
    
    public void playLivingSound() {
        if (!this.isEntityAlive()) {
            return;
        }
        this.advanced.playSound((this.getAttackTarget() != null) ? 1 : 0, this.getSoundVolume(), this.getSoundPitch());
    }
    
    protected void playHurtSound(DamageSource source) {
        this.advanced.playSound(2, this.getSoundVolume(), this.getSoundPitch());
    }
    
    public SoundEvent getDeathSound() {
        return null;
    }
    
    protected float getSoundPitch() {
        if (this.advanced.disablePitch) {
            return 1.0f;
        }
        return super.getSoundPitch();
    }
    
    protected void playStepSound(BlockPos pos, Block block) {
        if (this.advanced.getSound(4) != null) {
            this.advanced.playSound(4, 0.15f, 1.0f);
        }
        else {
            super.playStepSound(pos, block);
        }
    }
    
    public EntityPlayerMP getFakeChatPlayer() {
        if (this.world.isRemote) {
            return null;
        }
        EntityUtil.Copy((EntityLivingBase)this, (EntityLivingBase)EntityNPCInterface.ChatEventPlayer);
        EntityNPCInterface.ChatEventProfile.npc = this;
        EntityNPCInterface.ChatEventPlayer.refreshDisplayName();
        EntityNPCInterface.ChatEventPlayer.setWorld(this.world);
        EntityNPCInterface.ChatEventPlayer.setPosition(this.posX, this.posY, this.posZ);
        return (EntityPlayerMP)EntityNPCInterface.ChatEventPlayer;
    }
    
    public void saySurrounding(Line line) {
        if (line == null) {
            return;
        }
        if (line.getShowText() && !line.getText().isEmpty()) {
            ServerChatEvent event = new ServerChatEvent(this.getFakeChatPlayer(), line.getText(), (ITextComponent)new TextComponentTranslation(line.getText().replace("%", "%%"), new Object[0]));
            if (CustomNpcs.NpcSpeachTriggersChatEvent && (MinecraftForge.EVENT_BUS.post((Event)event) || event.getComponent() == null)) {
                return;
            }
            line.setText(event.getComponent().getUnformattedText().replace("%%", "%"));
        }
        List<EntityPlayer> inRange = (List<EntityPlayer>)this.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.getEntityBoundingBox().grow(20.0, 20.0, 20.0));
        for (EntityPlayer player : inRange) {
            this.say(player, line);
        }
    }
    
    public void say(EntityPlayer player, Line line) {
        if (line == null || !this.canSee((Entity)player)) {
            return;
        }
        if (!line.getSound().isEmpty()) {
            BlockPos pos = this.getPosition();
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.PLAY_SOUND, line.getSound(), pos.getX(), pos.getY(), pos.getZ(), this.getSoundVolume(), this.getSoundPitch());
        }
        if (!line.getText().isEmpty()) {
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.CHATBUBBLE, this.getEntityId(), line.getText(), line.getShowText());
        }
    }
    
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }
    
    public void addVelocity(double d, double d1, double d2) {
        if (this.isWalking() && !this.isKilled()) {
            super.addVelocity(d, d1, d2);
        }
    }
    
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.npcVersion = compound.getInteger("ModRev");
        VersionCompatibility.CheckNpcCompatibility(this, compound);
        this.display.readToNBT(compound);
        this.stats.readToNBT(compound);
        this.ais.readToNBT(compound);
        this.script.readFromNBT(compound);
        this.timers.readFromNBT(compound);
        this.advanced.readToNBT(compound);
        if (this.advanced.role != 0 && this.roleInterface != null) {
            this.roleInterface.readFromNBT(compound);
        }
        if (this.advanced.job != 0 && this.jobInterface != null) {
            this.jobInterface.readFromNBT(compound);
        }
        this.inventory.readEntityFromNBT(compound);
        this.transform.readToNBT(compound);
        this.killedtime = compound.getLong("KilledTime");
        this.totalTicksAlive = compound.getLong("TotalTicksAlive");
        this.linkedName = compound.getString("LinkedNpcName");
        if (!this.isRemote()) {
            LinkedNpcController.Instance.loadNpcData(this);
        }
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)CustomNpcs.NpcNavRange);
        this.updateAI = true;
    }
    
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        this.display.writeToNBT(compound);
        this.stats.writeToNBT(compound);
        this.ais.writeToNBT(compound);
        this.script.writeToNBT(compound);
        this.timers.writeToNBT(compound);
        this.advanced.writeToNBT(compound);
        if (this.advanced.role != 0 && this.roleInterface != null) {
            this.roleInterface.writeToNBT(compound);
        }
        if (this.advanced.job != 0 && this.jobInterface != null) {
            this.jobInterface.writeToNBT(compound);
        }
        this.inventory.writeEntityToNBT(compound);
        this.transform.writeToNBT(compound);
        compound.setLong("KilledTime", this.killedtime);
        compound.setLong("TotalTicksAlive", this.totalTicksAlive);
        compound.setInteger("ModRev", this.npcVersion);
        compound.setString("LinkedNpcName", this.linkedName);
    }
    
    public void updateHitbox() {
        if (this.currentAnimation == 2 || this.currentAnimation == 7 || this.deathTime > 0) {
            this.width = 0.8f;
            this.height = 0.4f;
        }
        else if (this.isRiding()) {
            this.width = 0.6f;
            this.height = this.baseHeight * 0.77f;
        }
        else {
            this.width = 0.6f;
            this.height = this.baseHeight;
        }
        this.width = this.width / 5.0f * this.display.getSize();
        this.height = this.height / 5.0f * this.display.getSize();
        if (!this.display.getHasHitbox() || (this.isKilled() && this.stats.hideKilledBody)) {
            this.width = 1.0E-5f;
        }
        double n = this.width / 2.0f;
        World world = this.world;
        if (n > World.MAX_ENTITY_RADIUS) {
            World world2 = this.world;
            World.MAX_ENTITY_RADIUS = this.width / 2.0f;
        }
        this.setPosition(this.posX, this.posY, this.posZ);
    }
    
    public void onDeathUpdate() {
        if (this.stats.spawnCycle == 3 || this.stats.spawnCycle == 4) {
            super.onDeathUpdate();
            return;
        }
        ++this.deathTime;
        if (this.world.isRemote) {
            return;
        }
        if (!this.hasDied) {
            this.setDead();
        }
        if (this.killedtime < System.currentTimeMillis() && (this.stats.spawnCycle == 0 || (this.world.isDaytime() && this.stats.spawnCycle == 1) || (!this.world.isDaytime() && this.stats.spawnCycle == 2))) {
            this.reset();
        }
    }
    
    public void reset() {
        this.hasDied = false;
        this.isDead = false;
        this.setSprinting(this.wasKilled = false);
        this.setHealth(this.getMaxHealth());
        this.dataManager.set((DataParameter)EntityNPCInterface.Animation, (Object)0);
        this.dataManager.set((DataParameter)EntityNPCInterface.Walking, (Object)false);
        this.dataManager.set((DataParameter)EntityNPCInterface.IsDead, (Object)false);
        this.dataManager.set((DataParameter)EntityNPCInterface.Interacting, (Object)false);
        this.interactingEntities.clear();
        this.combatHandler.reset();
        this.setAttackTarget(null);
        this.setRevengeTarget((EntityLivingBase)null);
        this.deathTime = 0;
        if (this.ais.returnToStart && !this.hasOwner() && !this.isRemote() && !this.isRiding()) {
            this.setLocationAndAngles((double)this.getStartXPos(), this.getStartYPos(), (double)this.getStartZPos(), this.rotationYaw, this.rotationPitch);
        }
        this.killedtime = 0L;
        this.extinguish();
        this.clearActivePotions();
        this.travel(0.0f, 0.0f, 0.0f);
        this.distanceWalkedModified = 0.0f;
        this.getNavigator().clearPath();
        this.currentAnimation = 0;
        this.updateHitbox();
        this.updateAI = true;
        this.ais.movingPos = 0;
        if (this.getOwner() != null) {
            this.getOwner().setLastAttackedEntity((Entity)null);
        }
        this.bossInfo.setVisible(this.display.getBossbar() == 1);
        if (this.jobInterface != null) {
            this.jobInterface.reset();
        }
        EventHooks.onNPCInit(this);
    }
    
    public void onCollide() {
        if (!this.isEntityAlive() || this.ticksExisted % 4 != 0 || this.world.isRemote) {
            return;
        }
        AxisAlignedBB axisalignedbb = null;
        if (this.getRidingEntity() != null && this.getRidingEntity().isEntityAlive()) {
            axisalignedbb = this.getEntityBoundingBox().union(this.getRidingEntity().getEntityBoundingBox()).grow(1.0, 0.0, 1.0);
        }
        else {
            axisalignedbb = this.getEntityBoundingBox().grow(1.0, 0.5, 1.0);
        }
        List<Entity> list = this.world.getEntitiesWithinAABB((Class)EntityLivingBase.class, axisalignedbb);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity != this && entity.isEntityAlive()) {
                EventHooks.onNPCCollide(this, entity);
            }
        }
    }
    
    public void setPortal(BlockPos pos) {
    }
    
    public void cloakUpdate() {
        this.field_20066_r = this.field_20063_u;
        this.field_20065_s = this.field_20062_v;
        this.field_20064_t = this.field_20061_w;
        double d = this.posX - this.field_20063_u;
        double d2 = this.posY - this.field_20062_v;
        double d3 = this.posZ - this.field_20061_w;
        double d4 = 10.0;
        if (d > d4) {
            double posX = this.posX;
            this.field_20063_u = posX;
            this.field_20066_r = posX;
        }
        if (d3 > d4) {
            double posZ = this.posZ;
            this.field_20061_w = posZ;
            this.field_20064_t = posZ;
        }
        if (d2 > d4) {
            double posY = this.posY;
            this.field_20062_v = posY;
            this.field_20065_s = posY;
        }
        if (d < -d4) {
            double posX2 = this.posX;
            this.field_20063_u = posX2;
            this.field_20066_r = posX2;
        }
        if (d3 < -d4) {
            double posZ2 = this.posZ;
            this.field_20061_w = posZ2;
            this.field_20064_t = posZ2;
        }
        if (d2 < -d4) {
            double posY2 = this.posY;
            this.field_20062_v = posY2;
            this.field_20065_s = posY2;
        }
        this.field_20063_u += d * 0.25;
        this.field_20061_w += d3 * 0.25;
        this.field_20062_v += d2 * 0.25;
    }
    
    protected boolean canDespawn() {
        return this.stats.spawnCycle == 4;
    }
    
    public ItemStack getHeldItemMainhand() {
        IItemStack item = null;
        if (this.isAttacking()) {
            item = this.inventory.getRightHand();
        }
        else if (this.advanced.role == 6) {
            item = ((RoleCompanion)this.roleInterface).getHeldItem();
        }
        else if (this.jobInterface != null && this.jobInterface.overrideMainHand) {
            item = this.jobInterface.getMainhand();
        }
        else {
            item = this.inventory.getRightHand();
        }
        return ItemStackWrapper.MCItem(item);
    }
    
    public ItemStack getHeldItemOffhand() {
        IItemStack item = null;
        if (this.isAttacking()) {
            item = this.inventory.getLeftHand();
        }
        else if (this.jobInterface != null && this.jobInterface.overrideOffHand) {
            item = this.jobInterface.getOffhand();
        }
        else {
            item = this.inventory.getLeftHand();
        }
        return ItemStackWrapper.MCItem(item);
    }
    
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slot) {
        if (slot == EntityEquipmentSlot.MAINHAND) {
            return this.getHeldItemMainhand();
        }
        if (slot == EntityEquipmentSlot.OFFHAND) {
            return this.getHeldItemOffhand();
        }
        return ItemStackWrapper.MCItem(this.inventory.getArmor(3 - slot.getIndex()));
    }
    
    public void setItemStackToSlot(EntityEquipmentSlot slot, ItemStack item) {
        if (slot == EntityEquipmentSlot.MAINHAND) {
            this.inventory.weapons.put(0, NpcAPI.Instance().getIItemStack(item));
        }
        else if (slot == EntityEquipmentSlot.OFFHAND) {
            this.inventory.weapons.put(2, NpcAPI.Instance().getIItemStack(item));
        }
        else {
            this.inventory.armor.put(3 - slot.getIndex(), NpcAPI.Instance().getIItemStack(item));
        }
    }
    
    public Iterable<ItemStack> getArmorInventoryList() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (int i = 0; i < 4; ++i) {
            list.add(ItemStackWrapper.MCItem(this.inventory.armor.get(3 - i)));
        }
        return list;
    }
    
    public Iterable<ItemStack> getHeldEquipment() {
        ArrayList list = new ArrayList();
        list.add(ItemStackWrapper.MCItem(this.inventory.weapons.get(0)));
        list.add(ItemStackWrapper.MCItem(this.inventory.weapons.get(2)));
        return (Iterable<ItemStack>)list;
    }
    
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
    }
    
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
    }
    
    public void onDeath(DamageSource damagesource) {
        this.setSprinting(false);
        this.getNavigator().clearPath();
        this.extinguish();
        this.clearActivePotions();
        if (!this.isRemote()) {
            this.advanced.playSound(3, this.getSoundVolume(), this.getSoundPitch());
            Entity attackingEntity = NoppesUtilServer.GetDamageSourcee(damagesource);
            NpcEvent.DiedEvent event = new NpcEvent.DiedEvent(this.wrappedNPC, damagesource, attackingEntity);
            event.droppedItems = this.inventory.getItemsRNG();
            event.expDropped = this.inventory.getExpRNG();
            event.line = this.advanced.getKilledLine();
            EventHooks.onNPCDied(this, event);
            this.bossInfo.setVisible(false);
            this.inventory.dropStuff(event, attackingEntity, damagesource);
            if (event.line != null) {
            	EntityLivingBase elb = (attackingEntity instanceof EntityLivingBase) ? (EntityLivingBase) attackingEntity : null;
            	this.saySurrounding(Line.formatTarget((Line) event.line, elb));
            }
        }
        super.onDeath(damagesource);
    }
    
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }
    
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }
    
    public void setDead() {
        this.hasDied = true;
        this.removePassengers();
        this.dismountRidingEntity();
        if (this.world.isRemote || this.stats.spawnCycle == 3 || this.stats.spawnCycle == 4) {
            this.delete();
        }
        else {
            this.setHealth(-1.0f);
            this.setSprinting(false);
            this.getNavigator().clearPath();
            this.setCurrentAnimation(2);
            this.updateHitbox();
            if (this.killedtime <= 0L) {
                this.killedtime = this.stats.respawnTime * 1000 + System.currentTimeMillis();
            }
            if (this.advanced.role != 0 && this.roleInterface != null) {
                this.roleInterface.killed();
            }
            if (this.advanced.job != 0 && this.jobInterface != null) {
                this.jobInterface.killed();
            }
        }
    }
    
    public void delete() {
        if (this.advanced.role != 0 && this.roleInterface != null) {
            this.roleInterface.delete();
        }
        if (this.advanced.job != 0 && this.jobInterface != null) {
            this.jobInterface.delete();
        }
        super.setDead();
    }
    
    public float getStartXPos() {
        return this.ais.startPos().getX() + this.ais.bodyOffsetX / 10.0f;
    }
    
    public float getStartZPos() {
        return this.ais.startPos().getZ() + this.ais.bodyOffsetZ / 10.0f;
    }
    
    public boolean isVeryNearAssignedPlace() {
        double xx = this.posX - this.getStartXPos();
        double zz = this.posZ - this.getStartZPos();
        return xx >= -0.2 && xx <= 0.2 && zz >= -0.2 && zz <= 0.2;
    }
    
    public double getStartYPos() {
        if (this.startYPos < 0.0) {
            return this.calculateStartYPos(this.ais.startPos());
        }
        return this.startYPos;
    }
    
    private double calculateStartYPos(BlockPos pos) {
        BlockPos startPos = this.ais.startPos();
        while (pos.getY() > 0) {
            IBlockState state = this.world.getBlockState(pos);
            AxisAlignedBB bb = state.getBoundingBox((IBlockAccess)this.world, pos).offset(pos);
            if (bb != null) {
                if (this.ais.movementType != 2 || startPos.getY() > pos.getY() || state.getMaterial() != Material.WATER) {
                    return bb.maxY;
                }
                pos = pos.down();
            }
            else {
                pos = pos.down();
            }
        }
        return 0.0;
    }
    
    private BlockPos calculateTopPos(BlockPos pos) {
        for (BlockPos check = pos; check.getY() > 0; check = check.down()) {
            IBlockState state = this.world.getBlockState(pos);
            AxisAlignedBB bb = state.getBoundingBox((IBlockAccess)this.world, pos).offset(pos);
            if (bb != null) {
                return check;
            }
        }
        return pos;
    }
    
    public boolean isInRange(Entity entity, double range) {
        return this.isInRange(entity.posX, entity.posY, entity.posZ, range);
    }
    
    public boolean isInRange(double posX, double posY, double posZ, double range) {
        double y = Math.abs(this.posY - posY);
        if (posY >= 0.0 && y > range) {
            return false;
        }
        double x = Math.abs(this.posX - posX);
        double z = Math.abs(this.posZ - posZ);
        return x <= range && z <= range;
    }
    
    public void givePlayerItem(EntityPlayer player, ItemStack item) {
        if (this.world.isRemote) {
            return;
        }
        item = item.copy();
        float f = 0.7f;
        double d = this.world.rand.nextFloat() * f + (double)(1.0f - f);
        double d2 = this.world.rand.nextFloat() * f + (double)(1.0f - f);
        double d3 = this.world.rand.nextFloat() * f + (double)(1.0f - f);
        EntityItem entityitem = new EntityItem(this.world, this.posX + d, this.posY + d2, this.posZ + d3, item);
        entityitem.setPickupDelay(2);
        this.world.spawnEntity((Entity)entityitem);
        int i = item.getCount();
        if (player.inventory.addItemStackToInventory(item)) {
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.onItemPickup((Entity)entityitem, i);
            if (item.getCount() <= 0) {
                entityitem.setDead();
            }
        }
    }
    
    public boolean isPlayerSleeping() {
        return this.currentAnimation == 2 && !this.isAttacking();
    }
    
    public boolean isWalking() {
        return this.ais.getMovingType() != 0 || this.isAttacking() || this.isFollower() || (boolean)this.dataManager.get((DataParameter)EntityNPCInterface.Walking);
    }
    
    public boolean isSneaking() {
        return this.currentAnimation == 4;
    }
    
    public void knockBack(Entity par1Entity, float strength, double ratioX, double ratioZ) {
        super.knockBack(par1Entity, strength * (2.0f - this.stats.resistances.knockback), ratioX, ratioZ);
    }
    
    public Faction getFaction() {
        Faction fac = FactionController.instance.getFaction((int)this.dataManager.get((DataParameter)EntityNPCInterface.FactionData));
        if (fac == null) {
            return FactionController.instance.getFaction(FactionController.instance.getFirstFactionId());
        }
        return fac;
    }
    
    public boolean isRemote() {
        return this.world == null || this.world.isRemote;
    }
    
    public void setFaction(int id) {
        if (id < 0 || this.isRemote()) {
            return;
        }
        this.dataManager.set((DataParameter)EntityNPCInterface.FactionData, (Object)id);
    }
    
    public boolean isPotionApplicable(PotionEffect effect) {
        return !this.stats.potionImmune && (this.getCreatureAttribute() != EnumCreatureAttribute.ARTHROPOD || effect.getPotion() != MobEffects.POISON) && super.isPotionApplicable(effect);
    }
    
    public boolean isAttacking() {
        return (boolean)this.dataManager.get((DataParameter)EntityNPCInterface.Attacking);
    }
    
    public boolean isKilled() {
        return this.isDead || (boolean)this.dataManager.get((DataParameter)EntityNPCInterface.IsDead);
    }
    
    public void writeSpawnData(ByteBuf buffer) {
        try {
            Server.writeNBT(buffer, this.writeSpawnData());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public NBTTagCompound writeSpawnData() {
        NBTTagCompound compound = new NBTTagCompound();
        this.display.writeToNBT(compound);
        compound.setInteger("MaxHealth", this.stats.maxHealth);
        compound.setTag("Armor", (NBTBase)NBTTags.nbtIItemStackMap(this.inventory.armor));
        compound.setTag("Weapons", (NBTBase)NBTTags.nbtIItemStackMap(this.inventory.weapons));
        compound.setInteger("Speed", this.ais.getWalkingSpeed());
        compound.setBoolean("DeadBody", this.stats.hideKilledBody);
        compound.setInteger("StandingState", this.ais.getStandingType());
        compound.setInteger("MovingState", this.ais.getMovingType());
        compound.setInteger("Orientation", this.ais.orientation);
        compound.setFloat("PositionXOffset", this.ais.bodyOffsetX);
        compound.setFloat("PositionYOffset", this.ais.bodyOffsetY);
        compound.setFloat("PositionZOffset", this.ais.bodyOffsetZ);
        compound.setInteger("Role", this.advanced.role);
        compound.setInteger("Job", this.advanced.job);
        if (this.advanced.job == 1) {
            NBTTagCompound bard = new NBTTagCompound();
            this.jobInterface.writeToNBT(bard);
            compound.setTag("Bard", (NBTBase)bard);
        }
        if (this.advanced.job == 9) {
            NBTTagCompound bard = new NBTTagCompound();
            this.jobInterface.writeToNBT(bard);
            compound.setTag("Puppet", (NBTBase)bard);
        }
        if (this.advanced.role == 6) {
            NBTTagCompound bard = new NBTTagCompound();
            this.roleInterface.writeToNBT(bard);
            compound.setTag("Companion", (NBTBase)bard);
        }
        if (this instanceof EntityCustomNpc) {
            compound.setTag("ModelData", (NBTBase)((EntityCustomNpc)this).modelData.writeToNBT());
        }
        return compound;
    }
    
    public void readSpawnData(ByteBuf buf) {
        try {
            this.readSpawnData(Server.readNBT(buf));
        }
        catch (IOException ex) {}
    }
    
    public void readSpawnData(NBTTagCompound compound) {
        this.stats.setMaxHealth(compound.getInteger("MaxHealth"));
        this.ais.setWalkingSpeed(compound.getInteger("Speed"));
        this.stats.hideKilledBody = compound.getBoolean("DeadBody");
        this.ais.setStandingType(compound.getInteger("StandingState"));
        this.ais.setMovingType(compound.getInteger("MovingState"));
        this.ais.orientation = compound.getInteger("Orientation");
        this.ais.bodyOffsetX = compound.getFloat("PositionXOffset");
        this.ais.bodyOffsetY = compound.getFloat("PositionYOffset");
        this.ais.bodyOffsetZ = compound.getFloat("PositionZOffset");
        this.inventory.armor = NBTTags.getIItemStackMap(compound.getTagList("Armor", 10));
        this.inventory.weapons = NBTTags.getIItemStackMap(compound.getTagList("Weapons", 10));
        this.advanced.setRole(compound.getInteger("Role"));
        this.advanced.setJob(compound.getInteger("Job"));
        if (this.advanced.job == 1) {
            NBTTagCompound bard = compound.getCompoundTag("Bard");
            this.jobInterface.readFromNBT(bard);
        }
        if (this.advanced.job == 9) {
            NBTTagCompound puppet = compound.getCompoundTag("Puppet");
            this.jobInterface.readFromNBT(puppet);
        }
        if (this.advanced.role == 6) {
            NBTTagCompound puppet = compound.getCompoundTag("Companion");
            this.roleInterface.readFromNBT(puppet);
        }
        if (this instanceof EntityCustomNpc) {
            ((EntityCustomNpc)this).modelData.readFromNBT(compound.getCompoundTag("ModelData"));
        }
        this.display.readToNBT(compound);
    }
    
    public Entity getCommandSenderEntity() {
        if (this.world.isRemote) {
            return (Entity)this;
        }
        EntityUtil.Copy((EntityLivingBase)this, (EntityLivingBase)EntityNPCInterface.CommandPlayer);
        EntityNPCInterface.CommandPlayer.setWorld(this.world);
        EntityNPCInterface.CommandPlayer.setPosition(this.posX, this.posY, this.posZ);
        return (Entity)EntityNPCInterface.CommandPlayer;
    }
    
    public String getName() {
        return this.display.getName();
    }
    
    public BlockPos getPosition() {
        return new BlockPos(this.posX, this.posY, this.posZ);
    }
    
    public Vec3d getPositionVector() {
        return new Vec3d(this.posX, this.posY, this.posZ);
    }
    
    public boolean canAttackClass(Class par1Class) {
        return EntityBat.class != par1Class;
    }
    
    public void setImmuneToFire(boolean immuneToFire) {
        this.isImmuneToFire = immuneToFire;
        this.stats.immuneToFire = immuneToFire;
    }
    
    public void fall(float distance, float modifier) {
        if (!this.stats.noFallDamage) {
            super.fall(distance, modifier);
        }
    }
    
    public void setInWeb() {
        if (!this.stats.ignoreCobweb) {
            super.setInWeb();
        }
    }
    
    public boolean canBeCollidedWith() {
        return !this.isKilled() && this.display.getHasHitbox();
    }
    
    public boolean canBePushed() {
        return super.canBePushed() && this.display.getHasHitbox();
    }
    
    public EnumPushReaction getPushReaction() {
        return this.display.getHasHitbox() ? super.getPushReaction() : EnumPushReaction.IGNORE;
    }
    
    public EntityAIRangedAttack getRangedTask() {
        return this.aiRange;
    }
    
    public String getRoleData() {
        return (String)this.dataManager.get((DataParameter)EntityNPCInterface.RoleData);
    }
    
    public void setRoleData(String s) {
        this.dataManager.set((DataParameter)EntityNPCInterface.RoleData, (Object)s);
    }
    
    public String getJobData() {
        return (String)this.dataManager.get((DataParameter)EntityNPCInterface.RoleData);
    }
    
    public void setJobData(String s) {
        this.dataManager.set((DataParameter)EntityNPCInterface.RoleData, (Object)s);
    }
    
    public World getEntityWorld() {
        return this.world;
    }
    
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return this.display.getVisible() == 1 && player.getHeldItemMainhand().getItem() != CustomItems.wand && !this.display.availability.hasOptions();
    }
    
    public boolean isInvisible() {
        return this.display.getVisible() != 0 && !this.display.availability.hasOptions();
    }
    
    public void sendMessage(ITextComponent var1) {
    }
    
    public void setCurrentAnimation(int animation) {
        this.currentAnimation = animation;
        this.dataManager.set((DataParameter)EntityNPCInterface.Animation, (Object)animation);
    }
    
    public boolean canSee(Entity entity) {
        return this.getEntitySenses().canSee(entity);
    }
    
    public boolean isFollower() {
        return this.advanced.scenes.getOwner() != null || (this.roleInterface != null && this.roleInterface.isFollowing()) || (this.jobInterface != null && this.jobInterface.isFollowing());
    }
    
    public EntityLivingBase getOwner() {
        if (this.advanced.scenes.getOwner() != null) {
            return this.advanced.scenes.getOwner();
        }
        if (this.advanced.role == 2 && this.roleInterface instanceof RoleFollower) {
            return (EntityLivingBase)((RoleFollower)this.roleInterface).owner;
        }
        if (this.advanced.role == 6 && this.roleInterface instanceof RoleCompanion) {
            return (EntityLivingBase)((RoleCompanion)this.roleInterface).owner;
        }
        if (this.advanced.job == 5 && this.jobInterface instanceof JobFollower) {
            return (EntityLivingBase)((JobFollower)this.jobInterface).following;
        }
        return null;
    }
    
    public boolean hasOwner() {
        return this.advanced.scenes.getOwner() != null || (this.advanced.role == 2 && ((RoleFollower)this.roleInterface).hasOwner()) || (this.advanced.role == 6 && ((RoleCompanion)this.roleInterface).hasOwner()) || (this.advanced.job == 5 && ((JobFollower)this.jobInterface).hasOwner());
    }
    
    public int followRange() {
        if (this.advanced.scenes.getOwner() != null) {
            return 4;
        }
        if (this.advanced.role == 2 && this.roleInterface.isFollowing()) {
            return 6;
        }
        if (this.advanced.role == 6 && this.roleInterface.isFollowing()) {
            return 4;
        }
        if (this.advanced.job == 5 && this.jobInterface.isFollowing()) {
            return 4;
        }
        return 15;
    }
    
    public void setHomePosAndDistance(BlockPos pos, int range) {
        super.setHomePosAndDistance(pos, range);
        this.ais.setStartPos(pos);
    }
    
    protected float applyArmorCalculations(DamageSource source, float damage) {
        if (this.advanced.role == 6) {
            damage = ((RoleCompanion)this.roleInterface).applyArmorCalculations(source, damage);
        }
        return damage;
    }
    
    public boolean isOnSameTeam(Entity entity) {
        if (!this.isRemote()) {
            if (entity instanceof EntityPlayer && this.getFaction().isFriendlyToPlayer((EntityPlayer)entity)) {
                return true;
            }
            if (entity == this.getOwner()) {
                return true;
            }
            if (entity instanceof EntityNPCInterface && ((EntityNPCInterface)entity).faction.id == this.faction.id) {
                return true;
            }
        }
        return super.isOnSameTeam(entity);
    }
    
    public void setDataWatcher(EntityDataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    public void travel(float f1, float f2, float f3) {
        double d0 = this.posX;
        double d2 = this.posY;
        double d3 = this.posZ;
        super.travel(f1, f2, f3);
        if (this.advanced.role == 6 && !this.isRemote()) {
            ((RoleCompanion)this.roleInterface).addMovementStat(this.posX - d0, this.posY - d2, this.posZ - d3);
        }
    }
    
    public boolean canBeLeashedTo(EntityPlayer player) {
        return false;
    }
    
    public boolean getLeashed() {
        return false;
    }
    
    public boolean nearPosition(BlockPos pos) {
        BlockPos npcpos = this.getPosition();
        float x = (float)(npcpos.getX() - pos.getX());
        float z = (float)(npcpos.getZ() - pos.getZ());
        float y = (float)(npcpos.getY() - pos.getY());
        float height = (float)(MathHelper.ceil(this.height + 1.0f) * MathHelper.ceil(this.height + 1.0f));
        return x * x + z * z < 2.5 && y * y < height + 2.5;
    }
    
    public void tpTo(EntityLivingBase owner) {
        if (owner == null) {
            return;
        }
        EnumFacing facing = owner.getHorizontalFacing().getOpposite();
        BlockPos pos = new BlockPos(owner.posX, owner.getEntityBoundingBox().minY, owner.posZ);
        pos = pos.add(facing.getXOffset(), 0, facing.getZOffset());
        pos = this.calculateTopPos(pos);
        for (int i = -1; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                BlockPos check;
                if (facing.getXOffset() == 0) {
                    check = pos.add(i, 0, j * facing.getZOffset());
                }
                else {
                    check = pos.add(j * facing.getXOffset(), 0, i);
                }
                check = this.calculateTopPos(check);
                if (!this.world.getBlockState(check).isFullBlock() && !this.world.getBlockState(check.up()).isFullBlock()) {
                    this.setLocationAndAngles((double)(check.getX() + 0.5f), (double)check.getY(), (double)(check.getZ() + 0.5f), this.rotationYaw, this.rotationPitch);
                    this.getNavigator().clearPath();
                    break;
                }
            }
        }
    }
    
    public int getMaxSpawnedInChunk() {
        return 8;
    }
    
    public void setSwingingArms(boolean swingingArms) {
    }
    
    public boolean getCanSpawnHere() {
        return this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0f && this.world.getBlockState(new BlockPos((Entity)this).down()).canEntitySpawn((Entity)this);
    }
    
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }
    
    public void setInvisible(EntityPlayerMP playerMP) {
        if (this.tracking.contains(playerMP.getEntityId())) {
            this.tracking.remove(playerMP.getEntityId());
            Server.sendData(playerMP, EnumPacketClient.VISIBLE_FALSE, this.getEntityId());
        }
    }
    
    public void setVisible(EntityPlayerMP playerMP) {
        if (!this.tracking.contains(playerMP.getEntityId())) {
            this.tracking.add(playerMP.getEntityId());
            EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn((Class)this.getClass(), false);
            FMLMessage.EntitySpawnMessage message = new FMLMessage.EntitySpawnMessage(er, (Entity)this, er.getContainer());
            Server.sendData(playerMP, EnumPacketClient.VISIBLE_TRUE, this.getEntityId(), message);
        }
    }
    
    static {
        Attacking = EntityDataManager.createKey((Class)EntityNPCInterface.class, DataSerializers.BOOLEAN);
        Animation = EntityDataManager.createKey((Class)EntityNPCInterface.class, DataSerializers.VARINT);
        RoleData = EntityDataManager.createKey((Class)EntityNPCInterface.class, DataSerializers.STRING);
        JobData = EntityDataManager.createKey((Class)EntityNPCInterface.class, DataSerializers.STRING);
        FactionData = EntityDataManager.createKey((Class)EntityNPCInterface.class, DataSerializers.VARINT);
        Walking = EntityDataManager.createKey((Class)EntityNPCInterface.class, DataSerializers.BOOLEAN);
        Interacting = EntityDataManager.createKey((Class)EntityNPCInterface.class, DataSerializers.BOOLEAN);
        IsDead = EntityDataManager.createKey((Class)EntityNPCInterface.class, DataSerializers.BOOLEAN);
        CommandProfile = new GameProfileAlt();
        ChatEventProfile = new GameProfileAlt();
        GenericProfile = new GameProfileAlt();
    }
}
