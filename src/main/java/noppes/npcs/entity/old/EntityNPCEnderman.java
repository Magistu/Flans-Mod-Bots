package noppes.npcs.entity.old;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityNPCEnderman extends EntityNpcEnderchibi
{
    public EntityNPCEnderman(World world) {
        super(world);
        this.display.setSkinTexture("customnpcs:textures/entity/enderman/enderman.png");
        this.display.setOverlayTexture("customnpcs:textures/overlays/ender_eyes.png");
        this.width = 0.6f;
        this.height = 2.9f;
    }
    
    @Override
    public void updateHitbox() {
        if (this.currentAnimation == 2) {
            float n = 0.2f;
            this.height = n;
            this.width = n;
        }
        else if (this.currentAnimation == 1) {
            this.width = 0.6f;
            this.height = 2.3f;
        }
        else {
            this.width = 0.6f;
            this.height = 2.9f;
        }
        this.width = this.width / 5.0f * this.display.getSize();
        this.height = this.height / 5.0f * this.display.getSize();
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
            data.setEntityClass((Class<? extends EntityLivingBase>)EntityEnderman.class);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
