package noppes.npcs.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.CustomNpcs;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIReturn extends EntityAIBase
{
    public static int MaxTotalTicks = 600;
    private EntityNPCInterface npc;
    private int stuckTicks;
    private int totalTicks;
    private double endPosX;
    private double endPosY;
    private double endPosZ;
    private boolean wasAttacked;
    private double[] preAttackPos;
    private int stuckCount;
    
    public EntityAIReturn(EntityNPCInterface npc) {
        this.stuckTicks = 0;
        this.totalTicks = 0;
        this.wasAttacked = false;
        this.stuckCount = 0;
        this.npc = npc;
        this.setMutexBits((int)AiMutex.PASSIVE);
    }
    
    public boolean shouldExecute() {
        if (this.npc.hasOwner() || this.npc.isRiding() || !this.npc.ais.shouldReturnHome() || this.npc.isKilled() || !this.npc.getNavigator().noPath() || this.npc.isInteracting()) {
            return false;
        }
        if (this.npc.ais.findShelter == 0 && (!this.npc.world.isDaytime() || this.npc.world.isRaining()) && !this.npc.world.provider.hasSkyLight()) {
            BlockPos pos = new BlockPos((double)this.npc.getStartXPos(), this.npc.getStartYPos(), (double)this.npc.getStartZPos());
            if (this.npc.world.canSeeSky(pos) || this.npc.world.getLight(pos) <= 8) {
                return false;
            }
        }
        else if (this.npc.ais.findShelter == 1 && this.npc.world.isDaytime()) {
            BlockPos pos = new BlockPos((double)this.npc.getStartXPos(), this.npc.getStartYPos(), (double)this.npc.getStartZPos());
            if (this.npc.world.canSeeSky(pos)) {
                return false;
            }
        }
        if (this.npc.isAttacking()) {
            if (!this.wasAttacked) {
                this.wasAttacked = true;
                this.preAttackPos = new double[] { this.npc.posX, this.npc.posY, this.npc.posZ };
            }
            return false;
        }
        if (!this.npc.isAttacking() && this.wasAttacked) {
            return true;
        }
        if (this.npc.ais.getMovingType() == 2 && this.npc.ais.getDistanceSqToPathPoint() < CustomNpcs.NpcNavRange * CustomNpcs.NpcNavRange) {
            return false;
        }
        if (this.npc.ais.getMovingType() == 1) {
            double x = this.npc.posX - this.npc.getStartXPos();
            double z = this.npc.posZ - this.npc.getStartZPos();
            return !this.npc.isInRange(this.npc.getStartXPos(), -1.0, this.npc.getStartZPos(), this.npc.ais.walkingRange);
        }
        return this.npc.ais.getMovingType() == 0 && !this.npc.isVeryNearAssignedPlace();
    }
    
    public boolean shouldContinueExecuting() {
        return !this.npc.isFollower() && !this.npc.isKilled() && !this.npc.isAttacking() && !this.npc.isVeryNearAssignedPlace() && !this.npc.isInteracting() && !this.npc.isRiding() && (!this.npc.getNavigator().noPath() || !this.wasAttacked || this.isTooFar()) && this.totalTicks <= 600;
    }
    
    public void updateTask() {
        ++this.totalTicks;
        if (this.totalTicks > 600) {
            this.npc.setPosition(this.endPosX, this.endPosY, this.endPosZ);
            this.npc.getNavigator().clearPath();
            return;
        }
        if (this.stuckTicks > 0) {
            --this.stuckTicks;
        }
        else if (this.npc.getNavigator().noPath()) {
            ++this.stuckCount;
            this.stuckTicks = 10;
            if ((this.totalTicks > 30 && this.wasAttacked && this.isTooFar()) || this.stuckCount > 5) {
                this.npc.setPosition(this.endPosX, this.endPosY, this.endPosZ);
                this.npc.getNavigator().clearPath();
            }
            else {
                this.navigate(this.stuckCount % 2 == 1);
            }
        }
        else {
            this.stuckCount = 0;
        }
    }
    
    private boolean isTooFar() {
        int allowedDistance = this.npc.stats.aggroRange * 2;
        if (this.npc.ais.getMovingType() == 1) {
            allowedDistance += this.npc.ais.walkingRange;
        }
        double x = this.npc.posX - this.endPosX;
        double z = this.npc.posZ - this.endPosZ;
        return x * x + z * z > allowedDistance * allowedDistance;
    }
    
    public void startExecuting() {
        this.stuckTicks = 0;
        this.totalTicks = 0;
        this.stuckCount = 0;
        this.navigate(false);
    }
    
    private void navigate(boolean towards) {
        if (!this.wasAttacked) {
            this.endPosX = this.npc.getStartXPos();
            this.endPosY = this.npc.getStartYPos();
            this.endPosZ = this.npc.getStartZPos();
        }
        else {
            this.endPosX = this.preAttackPos[0];
            this.endPosY = this.preAttackPos[1];
            this.endPosZ = this.preAttackPos[2];
        }
        double posX = this.endPosX;
        double posY = this.endPosY;
        double posZ = this.endPosZ;
        double range = this.npc.getDistance(posX, posY, posZ);
        if (range > CustomNpcs.NpcNavRange || towards) {
            int distance = (int)range;
            if (distance > CustomNpcs.NpcNavRange) {
                distance = CustomNpcs.NpcNavRange / 2;
            }
            else {
                distance /= 2;
            }
            if (distance > 2) {
                Vec3d start = new Vec3d(posX, posY, posZ);
                Vec3d pos = RandomPositionGenerator.findRandomTargetBlockTowards((EntityCreature)this.npc, distance, (distance / 2 > 7) ? 7 : (distance / 2), start);
                if (pos != null) {
                    posX = pos.x;
                    posY = pos.y;
                    posZ = pos.z;
                }
            }
        }
        this.npc.getNavigator().clearPath();
        this.npc.getNavigator().tryMoveToXYZ(posX, posY, posZ, 1.0);
    }
    
    public void resetTask() {
        this.wasAttacked = false;
        this.npc.getNavigator().clearPath();
    }
}
