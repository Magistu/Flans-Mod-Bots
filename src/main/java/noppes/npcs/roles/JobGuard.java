package noppes.npcs.roles;

import java.util.Iterator;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import java.util.List;

public class JobGuard extends JobInterface
{
    public List<String> targets;
    
    public JobGuard(EntityNPCInterface npc) {
        super(npc);
        this.targets = new ArrayList<String>();
    }
    
    public boolean isEntityApplicable(Entity entity) {
        return !(entity instanceof EntityPlayer) && !(entity instanceof EntityNPCInterface) && this.targets.contains("entity." + EntityList.getEntityString(entity) + ".name");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setTag("GuardTargets", (NBTBase)NBTTags.nbtStringList(this.targets));
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.targets = NBTTags.getStringList(nbttagcompound.getTagList("GuardTargets", 10));
        if (nbttagcompound.getBoolean("GuardAttackAnimals")) {
            for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
                Class<? extends Entity> cl = (Class<? extends Entity>)ent.getEntityClass();
                String name = "entity." + ent.getName() + ".name";
                if (EntityAnimal.class.isAssignableFrom(cl) && !this.targets.contains(name)) {
                    this.targets.add(name);
                }
            }
        }
        if (nbttagcompound.getBoolean("GuardAttackMobs")) {
            for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
                Class<? extends Entity> cl = (Class<? extends Entity>)ent.getEntityClass();
                String name = "entity." + ent.getName() + ".name";
                if (EntityMob.class.isAssignableFrom(cl) && !EntityCreeper.class.isAssignableFrom(cl) && !this.targets.contains(name)) {
                    this.targets.add(name);
                }
            }
        }
        if (nbttagcompound.getBoolean("GuardAttackCreepers")) {
            for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
                Class<? extends Entity> cl = (Class<? extends Entity>)ent.getEntityClass();
                String name = "entity." + ent.getName() + ".name";
                if (EntityCreeper.class.isAssignableFrom(cl) && !this.targets.contains(name)) {
                    this.targets.add(name);
                }
            }
        }
    }
}
