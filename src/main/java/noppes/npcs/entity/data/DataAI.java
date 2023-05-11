package noppes.npcs.entity.data;

import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.entity.SharedMonsterAttributes;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.roles.JobFarmer;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.api.IPos;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.data.INPCAi;

public class DataAI implements INPCAi
{
    private EntityNPCInterface npc;
    public int onAttack;
    public int doorInteract;
    public int findShelter;
    public boolean canSwim;
    public boolean reactsToFire;
    public boolean avoidsWater;
    public boolean avoidsSun;
    public boolean returnToStart;
    public boolean directLOS;
    public boolean canLeap;
    public boolean canSprint;
    public boolean stopAndInteract;
    public boolean attackInvisible;
    public int tacticalVariant;
    private int tacticalRadius;
    public int movementType;
    public int animationType;
    private int standingType;
    private int movingType;
    public boolean npcInteracting;
    public int orientation;
    public float bodyOffsetX;
    public float bodyOffsetY;
    public float bodyOffsetZ;
    public int walkingRange;
    private int moveSpeed;
    private List<int[]> movingPath;
    private BlockPos startPos;
    public int movingPos;
    public int movingPattern;
    public boolean movingPause;
    
    public DataAI(EntityNPCInterface npc) {
        this.onAttack = 0;
        this.doorInteract = 2;
        this.findShelter = 2;
        this.canSwim = true;
        this.reactsToFire = false;
        this.avoidsWater = false;
        this.avoidsSun = false;
        this.returnToStart = true;
        this.directLOS = true;
        this.canLeap = false;
        this.canSprint = false;
        this.stopAndInteract = true;
        this.attackInvisible = false;
        this.tacticalVariant = 0;
        this.tacticalRadius = 8;
        this.movementType = 0;
        this.animationType = 0;
        this.standingType = 0;
        this.movingType = 0;
        this.npcInteracting = true;
        this.orientation = 0;
        this.bodyOffsetX = 5.0f;
        this.bodyOffsetY = 5.0f;
        this.bodyOffsetZ = 5.0f;
        this.walkingRange = 10;
        this.moveSpeed = 5;
        this.movingPath = new ArrayList<int[]>();
        this.startPos = null;
        this.movingPos = 0;
        this.movingPattern = 0;
        this.movingPause = true;
        this.npc = npc;
    }
    
    public void readToNBT(NBTTagCompound compound) {
        this.canSwim = compound.getBoolean("CanSwim");
        this.reactsToFire = compound.getBoolean("ReactsToFire");
        this.setAvoidsWater(compound.getBoolean("AvoidsWater"));
        this.avoidsSun = compound.getBoolean("AvoidsSun");
        this.returnToStart = compound.getBoolean("ReturnToStart");
        this.onAttack = compound.getInteger("OnAttack");
        this.doorInteract = compound.getInteger("DoorInteract");
        this.findShelter = compound.getInteger("FindShelter");
        this.directLOS = compound.getBoolean("DirectLOS");
        this.canLeap = compound.getBoolean("CanLeap");
        this.canSprint = compound.getBoolean("CanSprint");
        this.tacticalRadius = compound.getInteger("TacticalRadius");
        this.movingPause = compound.getBoolean("MovingPause");
        this.npcInteracting = compound.getBoolean("npcInteracting");
        this.stopAndInteract = compound.getBoolean("stopAndInteract");
        this.movementType = compound.getInteger("MovementType");
        this.animationType = compound.getInteger("MoveState");
        this.standingType = compound.getInteger("StandingState");
        this.movingType = compound.getInteger("MovingState");
        this.tacticalVariant = compound.getInteger("TacticalVariant");
        this.orientation = compound.getInteger("Orientation");
        this.bodyOffsetY = compound.getFloat("PositionOffsetY");
        this.bodyOffsetZ = compound.getFloat("PositionOffsetZ");
        this.bodyOffsetX = compound.getFloat("PositionOffsetX");
        this.walkingRange = compound.getInteger("WalkingRange");
        this.setWalkingSpeed(compound.getInteger("MoveSpeed"));
        this.setMovingPath(NBTTags.getIntegerArraySet(compound.getTagList("MovingPathNew", 10)));
        this.movingPos = compound.getInteger("MovingPos");
        this.movingPattern = compound.getInteger("MovingPatern");
        this.attackInvisible = compound.getBoolean("AttackInvisible");
        if (compound.hasKey("StartPosNew")) {
            int[] startPos = compound.getIntArray("StartPosNew");
            this.startPos = new BlockPos(startPos[0], startPos[1], startPos[2]);
        }
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("CanSwim", this.canSwim);
        compound.setBoolean("ReactsToFire", this.reactsToFire);
        compound.setBoolean("AvoidsWater", this.avoidsWater);
        compound.setBoolean("AvoidsSun", this.avoidsSun);
        compound.setBoolean("ReturnToStart", this.returnToStart);
        compound.setInteger("OnAttack", this.onAttack);
        compound.setInteger("DoorInteract", this.doorInteract);
        compound.setInteger("FindShelter", this.findShelter);
        compound.setBoolean("DirectLOS", this.directLOS);
        compound.setBoolean("CanLeap", this.canLeap);
        compound.setBoolean("CanSprint", this.canSprint);
        compound.setInteger("TacticalRadius", this.tacticalRadius);
        compound.setBoolean("MovingPause", this.movingPause);
        compound.setBoolean("npcInteracting", this.npcInteracting);
        compound.setBoolean("stopAndInteract", this.stopAndInteract);
        compound.setInteger("MoveState", this.animationType);
        compound.setInteger("StandingState", this.standingType);
        compound.setInteger("MovingState", this.movingType);
        compound.setInteger("TacticalVariant", this.tacticalVariant);
        compound.setInteger("MovementType", this.movementType);
        compound.setInteger("Orientation", this.orientation);
        compound.setFloat("PositionOffsetX", this.bodyOffsetX);
        compound.setFloat("PositionOffsetY", this.bodyOffsetY);
        compound.setFloat("PositionOffsetZ", this.bodyOffsetZ);
        compound.setInteger("WalkingRange", this.walkingRange);
        compound.setInteger("MoveSpeed", this.moveSpeed);
        compound.setTag("MovingPathNew", (NBTBase)NBTTags.nbtIntegerArraySet(this.movingPath));
        compound.setInteger("MovingPos", this.movingPos);
        compound.setInteger("MovingPatern", this.movingPattern);
        this.setAvoidsWater(this.avoidsWater);
        compound.setIntArray("StartPosNew", this.getStartArray());
        compound.setBoolean("AttackInvisible", this.attackInvisible);
        return compound;
    }
    
    public List<int[]> getMovingPath() {
        if (this.movingPath.isEmpty() && this.startPos != null) {
            this.movingPath.add(this.getStartArray());
        }
        return this.movingPath;
    }
    
    public void setMovingPath(List<int[]> list) {
        this.movingPath = list;
        if (!this.movingPath.isEmpty()) {
            int[] startPos = this.movingPath.get(0);
            this.startPos = new BlockPos(startPos[0], startPos[1], startPos[2]);
        }
    }
    
    public BlockPos startPos() {
        if (this.startPos == null) {
            this.startPos = new BlockPos((Entity)this.npc);
        }
        return this.startPos;
    }
    
    public int[] getStartArray() {
        BlockPos pos = this.startPos();
        return new int[] { pos.getX(), pos.getY(), pos.getZ() };
    }
    
    public int[] getCurrentMovingPath() {
        List<int[]> list = this.getMovingPath();
        int size = list.size();
        if (size == 1) {
            return list.get(0);
        }
        int pos = this.movingPos;
        if (this.movingPattern == 0 && pos >= size) {
            boolean movingPos = false;
            this.movingPos = (movingPos ? 1 : 0);
            pos = (movingPos ? 1 : 0);
        }
        if (this.movingPattern == 1) {
            int size2 = size * 2 - 1;
            if (pos >= size2) {
                boolean movingPos2 = false;
                this.movingPos = (movingPos2 ? 1 : 0);
                pos = (movingPos2 ? 1 : 0);
            }
            else if (pos >= size) {
                pos = size2 - pos;
            }
        }
        return list.get(pos);
    }
    
    public void clearMovingPath() {
        this.movingPath.clear();
        this.movingPos = 0;
    }
    
    public void setMovingPathPos(int m_pos, int[] pos) {
        if (m_pos < 0) {
            m_pos = 0;
        }
        this.movingPath.set(m_pos, pos);
    }
    
    public int[] getMovingPathPos(int m_pos) {
        return this.movingPath.get(m_pos);
    }
    
    public void appendMovingPath(int[] pos) {
        this.movingPath.add(pos);
    }
    
    public int getMovingPos() {
        return this.movingPos;
    }
    
    public void setMovingPos(int pos) {
        this.movingPos = pos;
    }
    
    public int getMovingPathSize() {
        return this.movingPath.size();
    }
    
    public void incrementMovingPath() {
        List<int[]> list = this.getMovingPath();
        if (list.size() == 1) {
            this.movingPos = 0;
            return;
        }
        ++this.movingPos;
        if (this.movingPattern == 0) {
            this.movingPos %= list.size();
        }
        else if (this.movingPattern == 1) {
            int size = list.size() * 2 - 1;
            this.movingPos %= size;
        }
    }
    
    public void decreaseMovingPath() {
        List<int[]> list = this.getMovingPath();
        if (list.size() == 1) {
            this.movingPos = 0;
            return;
        }
        --this.movingPos;
        if (this.movingPos < 0) {
            if (this.movingPattern == 0) {
                this.movingPos = list.size() - 1;
            }
            else if (this.movingPattern == 1) {
                this.movingPos = list.size() * 2 - 2;
            }
        }
    }
    
    public double getDistanceSqToPathPoint() {
        int[] pos = this.getCurrentMovingPath();
        return this.npc.getDistanceSq(pos[0] + 0.5, (double)pos[1], pos[2] + 0.5);
    }
    
    public IPos getStartPos() {
        return new BlockPosWrapper(this.startPos());
    }
    
    public void setStartPos(BlockPos pos) {
        this.startPos = pos;
    }
    
    public void setStartPos(IPos pos) {
        this.startPos = pos.getMCBlockPos();
    }
    
    public void setStartPos(double x, double y, double z) {
        this.startPos = new BlockPos(x, y, z);
    }
    
    @Override
    public void setReturnsHome(boolean bo) {
        this.returnToStart = bo;
    }
    
    @Override
    public boolean getReturnsHome() {
        return this.returnToStart;
    }
    
    public boolean shouldReturnHome() {
        return (this.npc.advanced.job != 10 || !((JobBuilder)this.npc.jobInterface).isBuilding()) && (this.npc.advanced.job != 11 || !((JobFarmer)this.npc.jobInterface).isPlucking()) && this.returnToStart;
    }
    
    @Override
    public int getAnimation() {
        return this.animationType;
    }
    
    @Override
    public int getCurrentAnimation() {
        return this.npc.currentAnimation;
    }
    
    @Override
    public void setAnimation(int type) {
        this.animationType = type;
    }
    
    @Override
    public int getRetaliateType() {
        return this.onAttack;
    }
    
    @Override
    public void setRetaliateType(int type) {
        if (type < 0 || type > 3) {
            throw new CustomNPCsException("Unknown retaliation type: " + type, new Object[0]);
        }
        this.onAttack = type;
        this.npc.updateAI = true;
    }
    
    @Override
    public int getMovingType() {
        return this.movingType;
    }
    
    @Override
    public void setMovingType(int type) {
        if (type < 0 || type > 2) {
            throw new CustomNPCsException("Unknown moving type: " + type, new Object[0]);
        }
        this.movingType = type;
        this.npc.updateAI = true;
    }
    
    @Override
    public int getStandingType() {
        return this.standingType;
    }
    
    @Override
    public void setStandingType(int type) {
        if (type < 0 || type > 3) {
            throw new CustomNPCsException("Unknown standing type: " + type, new Object[0]);
        }
        this.standingType = type;
        this.npc.updateAI = true;
    }
    
    @Override
    public boolean getAttackInvisible() {
        return this.attackInvisible;
    }
    
    @Override
    public void setAttackInvisible(boolean attack) {
        this.attackInvisible = attack;
    }
    
    @Override
    public int getWanderingRange() {
        return this.walkingRange;
    }
    
    @Override
    public void setWanderingRange(int range) {
        if (range < 1 || range > 50) {
            throw new CustomNPCsException("Bad wandering range: " + range, new Object[0]);
        }
        this.walkingRange = range;
    }
    
    @Override
    public boolean getInteractWithNPCs() {
        return this.npcInteracting;
    }
    
    @Override
    public void setInteractWithNPCs(boolean interact) {
        this.npcInteracting = interact;
    }
    
    @Override
    public boolean getStopOnInteract() {
        return this.stopAndInteract;
    }
    
    @Override
    public void setStopOnInteract(boolean stopOnInteract) {
        this.stopAndInteract = stopOnInteract;
    }
    
    @Override
    public int getWalkingSpeed() {
        return this.moveSpeed;
    }
    
    @Override
    public void setWalkingSpeed(int speed) {
        if (speed < 0 || speed > 10) {
            throw new CustomNPCsException("Wrong speed: " + speed, new Object[0]);
        }
        this.moveSpeed = speed;
        this.npc.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)this.npc.getSpeed());
        this.npc.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((double)(this.npc.getSpeed() * 2.0f));
    }
    
    @Override
    public int getMovingPathType() {
        return this.movingPattern;
    }
    
    @Override
    public boolean getMovingPathPauses() {
        return this.movingPause;
    }
    
    @Override
    public void setMovingPathType(int type, boolean pauses) {
        if (type < 0 && type > 1) {
            throw new CustomNPCsException("Moving path type: " + type, new Object[0]);
        }
        this.movingPattern = type;
        this.movingPause = pauses;
    }
    
    @Override
    public int getDoorInteract() {
        return this.doorInteract;
    }
    
    @Override
    public void setDoorInteract(int type) {
        this.doorInteract = type;
        this.npc.updateAI = true;
    }
    
    @Override
    public boolean getCanSwim() {
        return this.canSwim;
    }
    
    @Override
    public void setCanSwim(boolean canSwim) {
        this.canSwim = canSwim;
    }
    
    @Override
    public int getSheltersFrom() {
        return this.findShelter;
    }
    
    @Override
    public void setSheltersFrom(int type) {
        this.findShelter = type;
        this.npc.updateAI = true;
    }
    
    @Override
    public boolean getAttackLOS() {
        return this.directLOS;
    }
    
    @Override
    public void setAttackLOS(boolean enabled) {
        this.directLOS = enabled;
        this.npc.updateAI = true;
    }
    
    @Override
    public boolean getAvoidsWater() {
        return this.avoidsWater;
    }
    
    @Override
    public void setAvoidsWater(boolean enabled) {
        if (this.npc.getNavigator() instanceof PathNavigateGround) {
            this.npc.setPathPriority(PathNodeType.WATER, enabled ? PathNodeType.WATER.getPriority() : 0.0f);
        }
        this.avoidsWater = enabled;
    }
    
    @Override
    public boolean getLeapAtTarget() {
        return this.canLeap;
    }
    
    @Override
    public void setLeapAtTarget(boolean leap) {
        this.canLeap = leap;
        this.npc.updateAI = true;
    }
    
    @Override
    public int getTacticalType() {
        return this.tacticalVariant;
    }
    
    @Override
    public void setTacticalType(int type) {
        this.tacticalVariant = type;
        this.npc.updateAI = true;
    }
    
    @Override
    public int getTacticalRange() {
        return this.tacticalRadius;
    }
    
    @Override
    public void setTacticalRange(int range) {
        this.tacticalRadius = range;
    }
    
    @Override
    public int getNavigationType() {
        return this.movementType;
    }
    
    @Override
    public void setNavigationType(int type) {
        this.movementType = type;
    }
}
