package noppes.npcs.entity;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityNpcSlime extends EntityNPCInterface
{
    public EntityNpcSlime(World world) {
        super(world);
        this.scaleX = 2.0f;
        this.scaleY = 2.0f;
        this.scaleZ = 2.0f;
        this.display.setSkinTexture("customnpcs:textures/entity/slime/Slime.png");
        this.width = 0.8f;
        this.height = 0.8f;
    }
    
    @Override
    public void updateHitbox() {
        this.width = 0.8f;
        this.height = 0.8f;
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
            data.setEntityClass((Class<? extends EntityLivingBase>)EntityNpcSlime.class);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
