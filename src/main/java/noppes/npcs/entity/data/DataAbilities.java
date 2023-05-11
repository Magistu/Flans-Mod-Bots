package noppes.npcs.entity.data;

import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.constants.EnumAbilityType;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.ability.AbstractAbility;
import java.util.List;

public class DataAbilities
{
    public List<AbstractAbility> abilities;
    public EntityNPCInterface npc;
    
    public DataAbilities(EntityNPCInterface npc) {
        this.abilities = new ArrayList<AbstractAbility>();
        this.npc = npc;
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return compound;
    }
    
    public void readToNBT(NBTTagCompound compound) {
    }
    
    public AbstractAbility getAbility(EnumAbilityType type) {
        EntityLivingBase target = this.npc.getAttackTarget();
        for (AbstractAbility ability : this.abilities) {
            if (ability.isType(type) && ability.canRun(target)) {
                return ability;
            }
        }
        return null;
    }
}
