package noppes.npcs.entity.old;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNpcSkeleton extends EntityNPCInterface
{
    public EntityNpcSkeleton(World world) {
        super(world);
        this.display.setSkinTexture("customnpcs:textures/entity/skeleton/Skeleton.png");
    }
    
    @Override
    public void onUpdate() {
        this.setNoAI(this.isDead = true);
        if (!this.world.isRemote) {
            NBTTagCompound compound = new NBTTagCompound();
            this.writeToNBT(compound);
            EntityCustomNpc npc = new EntityCustomNpc(this.world);
            npc.readFromNBT(compound);
            ModelData data = npc.modelData;
            data.setEntityClass((Class<? extends EntityLivingBase>)EntitySkeleton.class);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
