package noppes.npcs.roles;

import java.util.Iterator;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.util.ValueUtil;
import net.minecraft.nbt.NBTBase;
import java.util.Map;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.EntityLivingBase;
import java.util.List;
import java.util.HashMap;

public class JobHealer extends JobInterface
{
    private int healTicks;
    public int range;
    public byte type;
    public int speed;
    public HashMap<Integer, Integer> effects;
    private List<EntityLivingBase> affected;
    
    public JobHealer(EntityNPCInterface npc) {
        super(npc);
        this.healTicks = 0;
        this.range = 8;
        this.type = 2;
        this.speed = 20;
        this.effects = new HashMap<Integer, Integer>();
        this.affected = new ArrayList<EntityLivingBase>();
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("HealerRange", this.range);
        nbttagcompound.setByte("HealerType", this.type);
        nbttagcompound.setTag("BeaconEffects", (NBTBase)NBTTags.nbtIntegerIntegerMap(this.effects));
        nbttagcompound.setInteger("HealerSpeed", this.speed);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.range = nbttagcompound.getInteger("HealerRange");
        this.type = nbttagcompound.getByte("HealerType");
        this.effects = NBTTags.getIntegerIntegerMap(nbttagcompound.getTagList("BeaconEffects", 10));
        this.speed = ValueUtil.CorrectInt(nbttagcompound.getInteger("HealerSpeed"), 10, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean aiShouldExecute() {
        ++this.healTicks;
        if (this.healTicks < this.speed) {
            return false;
        }
        this.healTicks = 0;
        this.affected = (List<EntityLivingBase>)this.npc.world.getEntitiesWithinAABB((Class)EntityLivingBase.class, this.npc.getEntityBoundingBox().grow((double)this.range, this.range / 2.0, (double)this.range));
        return !this.affected.isEmpty();
    }
    
    @Override
    public boolean aiContinueExecute() {
        return false;
    }
    
    @Override
    public void aiStartExecuting() {
        for (EntityLivingBase entity : this.affected) {
            boolean isEnemy = false;
            if (entity instanceof EntityPlayer) {
                isEnemy = this.npc.faction.isAggressiveToPlayer((EntityPlayer)entity);
            }
            else if (entity instanceof EntityNPCInterface) {
                isEnemy = this.npc.faction.isAggressiveToNpc((EntityNPCInterface)entity);
            }
            else {
                isEnemy = (entity instanceof EntityMob);
            }
            if (entity != this.npc && (this.type != 0 || !isEnemy)) {
                if (this.type == 1 && !isEnemy) {
                    continue;
                }
                for (Integer potionEffect : this.effects.keySet()) {
                    Potion p = Potion.getPotionById((int)potionEffect);
                    if (p != null) {
                        entity.addPotionEffect(new PotionEffect(p, 100, (int)this.effects.get(potionEffect)));
                    }
                }
            }
        }
        this.affected.clear();
    }
}
