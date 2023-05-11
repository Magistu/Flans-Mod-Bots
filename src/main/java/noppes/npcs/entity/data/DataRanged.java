package noppes.npcs.entity.data;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import noppes.npcs.util.ValueUtil;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.data.INPCRanged;

public class DataRanged implements INPCRanged
{
    private EntityNPCInterface npc;
    private int burstCount;
    private int pDamage;
    private int pSpeed;
    private int pImpact;
    private int pSize;
    private int pArea;
    private int pTrail;
    private int minDelay;
    private int maxDelay;
    private int rangedRange;
    private int fireRate;
    private int shotCount;
    private int accuracy;
    private int meleeDistance;
    private int canFireIndirect;
    private boolean pRender3D;
    private boolean pSpin;
    private boolean pStick;
    private boolean pPhysics;
    private boolean pXlr8;
    private boolean pGlows;
    private boolean aimWhileShooting;
    private int pEffect;
    private int pDur;
    private int pEffAmp;
    private String fireSound;
    private String hitSound;
    private String groundSound;
    
    public DataRanged(EntityNPCInterface npc) {
        this.burstCount = 1;
        this.pDamage = 4;
        this.pSpeed = 10;
        this.pImpact = 0;
        this.pSize = 5;
        this.pArea = 0;
        this.pTrail = 0;
        this.minDelay = 20;
        this.maxDelay = 40;
        this.rangedRange = 15;
        this.fireRate = 5;
        this.shotCount = 1;
        this.accuracy = 60;
        this.meleeDistance = 0;
        this.canFireIndirect = 0;
        this.pRender3D = true;
        this.pSpin = false;
        this.pStick = false;
        this.pPhysics = true;
        this.pXlr8 = false;
        this.pGlows = false;
        this.aimWhileShooting = false;
        this.pEffect = 0;
        this.pDur = 5;
        this.pEffAmp = 0;
        this.fireSound = "minecraft:entity.arrow.shoot";
        this.hitSound = "minecraft:entity.arrow.hit";
        this.groundSound = "minecraft:block.stone.break";
        this.npc = npc;
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        this.pDamage = compound.getInteger("pDamage");
        this.pSpeed = compound.getInteger("pSpeed");
        this.burstCount = compound.getInteger("BurstCount");
        this.pImpact = compound.getInteger("pImpact");
        this.pSize = compound.getInteger("pSize");
        this.pArea = compound.getInteger("pArea");
        this.pTrail = compound.getInteger("pTrail");
        this.rangedRange = compound.getInteger("MaxFiringRange");
        this.fireRate = compound.getInteger("FireRate");
        this.minDelay = ValueUtil.CorrectInt(compound.getInteger("minDelay"), 1, 9999);
        this.maxDelay = ValueUtil.CorrectInt(compound.getInteger("maxDelay"), 1, 9999);
        this.shotCount = ValueUtil.CorrectInt(compound.getInteger("ShotCount"), 1, 10);
        this.accuracy = compound.getInteger("Accuracy");
        this.pRender3D = compound.getBoolean("pRender3D");
        this.pSpin = compound.getBoolean("pSpin");
        this.pStick = compound.getBoolean("pStick");
        this.pPhysics = compound.getBoolean("pPhysics");
        this.pXlr8 = compound.getBoolean("pXlr8");
        this.pGlows = compound.getBoolean("pGlows");
        this.aimWhileShooting = compound.getBoolean("AimWhileShooting");
        this.pEffect = compound.getInteger("pEffect");
        this.pDur = compound.getInteger("pDur");
        this.pEffAmp = compound.getInteger("pEffAmp");
        this.fireSound = compound.getString("FiringSound");
        this.hitSound = compound.getString("HitSound");
        this.groundSound = compound.getString("GroundSound");
        this.canFireIndirect = compound.getInteger("FireIndirect");
        this.meleeDistance = compound.getInteger("DistanceToMelee");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("BurstCount", this.burstCount);
        compound.setInteger("pSpeed", this.pSpeed);
        compound.setInteger("pDamage", this.pDamage);
        compound.setInteger("pImpact", this.pImpact);
        compound.setInteger("pSize", this.pSize);
        compound.setInteger("pArea", this.pArea);
        compound.setInteger("pTrail", this.pTrail);
        compound.setInteger("MaxFiringRange", this.rangedRange);
        compound.setInteger("FireRate", this.fireRate);
        compound.setInteger("minDelay", this.minDelay);
        compound.setInteger("maxDelay", this.maxDelay);
        compound.setInteger("ShotCount", this.shotCount);
        compound.setInteger("Accuracy", this.accuracy);
        compound.setBoolean("pRender3D", this.pRender3D);
        compound.setBoolean("pSpin", this.pSpin);
        compound.setBoolean("pStick", this.pStick);
        compound.setBoolean("pPhysics", this.pPhysics);
        compound.setBoolean("pXlr8", this.pXlr8);
        compound.setBoolean("pGlows", this.pGlows);
        compound.setBoolean("AimWhileShooting", this.aimWhileShooting);
        compound.setInteger("pEffect", this.pEffect);
        compound.setInteger("pDur", this.pDur);
        compound.setInteger("pEffAmp", this.pEffAmp);
        compound.setString("FiringSound", this.fireSound);
        compound.setString("HitSound", this.hitSound);
        compound.setString("GroundSound", this.groundSound);
        compound.setInteger("FireIndirect", this.canFireIndirect);
        compound.setInteger("DistanceToMelee", this.meleeDistance);
        return compound;
    }
    
    @Override
    public int getStrength() {
        return this.pDamage;
    }
    
    @Override
    public void setStrength(int strength) {
        this.pDamage = strength;
    }
    
    @Override
    public int getSpeed() {
        return this.pSpeed;
    }
    
    @Override
    public void setSpeed(int speed) {
        this.pSpeed = ValueUtil.CorrectInt(speed, 0, 100);
    }
    
    @Override
    public int getKnockback() {
        return this.pImpact;
    }
    
    @Override
    public void setKnockback(int punch) {
        this.pImpact = punch;
    }
    
    @Override
    public int getSize() {
        return this.pSize;
    }
    
    @Override
    public void setSize(int size) {
        this.pSize = size;
    }
    
    @Override
    public boolean getRender3D() {
        return this.pRender3D;
    }
    
    @Override
    public void setRender3D(boolean render3d) {
        this.pRender3D = render3d;
    }
    
    @Override
    public boolean getSpins() {
        return this.pSpin;
    }
    
    @Override
    public void setSpins(boolean spins) {
        this.pSpin = spins;
    }
    
    @Override
    public boolean getSticks() {
        return this.pStick;
    }
    
    @Override
    public void setSticks(boolean sticks) {
        this.pStick = sticks;
    }
    
    @Override
    public boolean getHasGravity() {
        return this.pPhysics;
    }
    
    @Override
    public void setHasGravity(boolean hasGravity) {
        this.pPhysics = hasGravity;
    }
    
    @Override
    public boolean getAccelerate() {
        return this.pXlr8;
    }
    
    @Override
    public void setAccelerate(boolean accelerate) {
        this.pXlr8 = accelerate;
    }
    
    @Override
    public int getExplodeSize() {
        return this.pArea;
    }
    
    @Override
    public void setExplodeSize(int size) {
        this.pArea = size;
    }
    
    @Override
    public int getEffectType() {
        return this.pEffect;
    }
    
    @Override
    public int getEffectTime() {
        return this.pDur;
    }
    
    @Override
    public int getEffectStrength() {
        return this.pEffAmp;
    }
    
    @Override
    public void setEffect(int type, int strength, int time) {
        this.pEffect = type;
        this.pDur = time;
        this.pEffAmp = strength;
    }
    
    @Override
    public boolean getGlows() {
        return this.pGlows;
    }
    
    @Override
    public void setGlows(boolean glows) {
        this.pGlows = glows;
    }
    
    @Override
    public int getParticle() {
        return this.pTrail;
    }
    
    @Override
    public void setParticle(int type) {
        this.pTrail = type;
    }
    
    @Override
    public int getAccuracy() {
        return this.accuracy;
    }
    
    @Override
    public void setAccuracy(int accuracy) {
        this.accuracy = ValueUtil.CorrectInt(accuracy, 1, 100);
    }
    
    @Override
    public int getRange() {
        return this.rangedRange;
    }
    
    @Override
    public void setRange(int range) {
        this.rangedRange = ValueUtil.CorrectInt(range, 1, 64);
    }
    
    @Override
    public int getDelayMin() {
        return this.minDelay;
    }
    
    @Override
    public int getDelayMax() {
        return this.maxDelay;
    }
    
    @Override
    public int getDelayRNG() {
        int delay = this.minDelay;
        if (this.maxDelay - this.minDelay > 0) {
            delay += this.npc.world.rand.nextInt(this.maxDelay - this.minDelay);
        }
        return delay;
    }
    
    @Override
    public void setDelay(int min, int max) {
        min = Math.min(min, max);
        this.minDelay = min;
        this.maxDelay = max;
    }
    
    @Override
    public int getBurst() {
        return this.burstCount;
    }
    
    @Override
    public void setBurst(int count) {
        this.burstCount = count;
    }
    
    @Override
    public int getBurstDelay() {
        return this.fireRate;
    }
    
    @Override
    public void setBurstDelay(int delay) {
        this.fireRate = delay;
    }
    
    @Override
    public String getSound(int type) {
        String sound = null;
        if (type == 0) {
            sound = this.fireSound;
        }
        if (type == 1) {
            sound = this.hitSound;
        }
        if (type == 2) {
            sound = this.groundSound;
        }
        if (sound == null || sound.isEmpty()) {
            return null;
        }
        return sound;
    }
    
    public SoundEvent getSoundEvent(int type) {
        String sound = this.getSound(type);
        if (sound == null) {
            return null;
        }
        ResourceLocation res = new ResourceLocation(sound);
        SoundEvent ev = (SoundEvent)SoundEvent.REGISTRY.getObject(res);
        if (ev != null) {
            return ev;
        }
        return new SoundEvent(res);
    }
    
    @Override
    public void setSound(int type, String sound) {
        if (sound == null) {
            sound = "";
        }
        if (type == 0) {
            this.fireSound = sound;
        }
        if (type == 1) {
            this.hitSound = sound;
        }
        if (type == 2) {
            this.groundSound = sound;
        }
        this.npc.updateClient = true;
    }
    
    @Override
    public int getShotCount() {
        return this.shotCount;
    }
    
    @Override
    public void setShotCount(int count) {
        this.shotCount = count;
    }
    
    @Override
    public boolean getHasAimAnimation() {
        return this.aimWhileShooting;
    }
    
    @Override
    public void setHasAimAnimation(boolean aim) {
        this.aimWhileShooting = aim;
    }
    
    @Override
    public int getFireType() {
        return this.canFireIndirect;
    }
    
    @Override
    public void setFireType(int type) {
        this.canFireIndirect = type;
    }
    
    @Override
    public int getMeleeRange() {
        return this.meleeDistance;
    }
    
    @Override
    public void setMeleeRange(int range) {
        this.meleeDistance = range;
        this.npc.updateAI = true;
    }
}
