package noppes.npcs.entity;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;

public class EntityNpcPony extends EntityNPCInterface
{
    public boolean isPegasus;
    public boolean isUnicorn;
    public boolean isFlying;
    public ResourceLocation checked;
    
    public EntityNpcPony(World world) {
        super(world);
        this.isPegasus = false;
        this.isUnicorn = false;
        this.isFlying = false;
        this.checked = null;
        this.display.setSkinTexture("customnpcs:textures/entity/ponies/MineLP Derpy Hooves.png");
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
            data.setEntityClass((Class<? extends EntityLivingBase>)EntityNpcPony.class);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
