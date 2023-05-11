package noppes.npcs.entity;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityNpcCrystal extends EntityNPCInterface
{
    public EntityNpcCrystal(World world) {
        super(world);
        this.scaleX = 0.7f;
        this.scaleY = 0.7f;
        this.scaleZ = 0.7f;
        this.display.setSkinTexture("customnpcs:textures/entity/crystal/EnderCrystal.png");
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
            data.setEntityClass((Class<? extends EntityLivingBase>)EntityNpcCrystal.class);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
