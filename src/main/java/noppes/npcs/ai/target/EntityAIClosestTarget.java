package noppes.npcs.ai.target;

import net.minecraft.entity.monster.EntityMob;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.EntityLivingBase;
import com.google.common.base.Predicate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIClosestTarget extends EntityAITarget
{
    private Class targetClass;
    private int targetChance;
    private EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    private Predicate targetEntitySelector;
    private EntityLivingBase targetEntity;
    private EntityNPCInterface npc;
    
    public EntityAIClosestTarget(EntityNPCInterface npc, Class par2Class, int par3, boolean par4, boolean par5, Predicate par6IEntitySelector) {
        super((EntityCreature)npc, par4, par5);
        this.targetClass = par2Class;
        this.targetChance = par3;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter((Entity)npc);
        this.setMutexBits(1);
        this.targetEntitySelector = par6IEntitySelector;
        this.npc = npc;
    }
    
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        }
        double d0 = this.getTargetDistance();
        List<EntityLivingBase> list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().grow(d0, (double)MathHelper.ceil(d0 / 2.0), d0), this.targetEntitySelector);
        Collections.sort(list, this.theNearestAttackableTargetSorter);
        if (list.isEmpty()) {
            return false;
        }
        this.targetEntity = list.get(0);
        return true;
    }
    
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        if (this.targetEntity instanceof EntityMob && ((EntityMob)this.targetEntity).getAttackTarget() == null) {
            ((EntityMob)this.targetEntity).setAttackTarget((EntityLivingBase)this.taskOwner);
        }
        super.startExecuting();
    }
}
