package noppes.npcs.entity;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;

public class EntityNPCGolem extends EntityNPCInterface
{
    public EntityNPCGolem(World world) {
        super(world);
        this.display.setSkinTexture("customnpcs:textures/entity/golem/Iron Golem.png");
        this.width = 1.4f;
        this.height = 2.5f;
    }
    
    @Override
    public void updateHitbox() {
        this.currentAnimation = (int)this.dataManager.get((DataParameter)EntityNPCGolem.Animation);
        if (this.currentAnimation == 2) {
            float n = 0.5f;
            this.height = n;
            this.width = n;
        }
        else if (this.currentAnimation == 1) {
            this.width = 1.4f;
            this.height = 2.0f;
        }
        else {
            this.width = 1.4f;
            this.height = 2.5f;
        }
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
            data.setEntityClass((Class<? extends EntityLivingBase>)EntityNPCGolem.class);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
