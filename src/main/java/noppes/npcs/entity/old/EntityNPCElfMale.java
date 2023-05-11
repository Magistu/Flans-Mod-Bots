package noppes.npcs.entity.old;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNPCElfMale extends EntityNPCInterface
{
    public EntityNPCElfMale(World world) {
        super(world);
        this.scaleX = 0.85f;
        this.scaleY = 1.07f;
        this.scaleZ = 0.85f;
        this.display.setSkinTexture("customnpcs:textures/entity/elfmale/ElfMale.png");
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
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.85f, 1.15f);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.85f, 1.15f);
            data.getPartConfig(EnumParts.BODY).setScale(0.85f, 1.15f);
            data.getPartConfig(EnumParts.HEAD).setScale(0.85f, 0.95f);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
