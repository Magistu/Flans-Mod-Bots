package noppes.npcs.entity.old;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNPCOrcMale extends EntityNPCInterface
{
    public EntityNPCOrcMale(World world) {
        super(world);
        this.scaleY = 1.0f;
        float n = 1.2f;
        this.scaleZ = n;
        this.scaleX = n;
        this.display.setSkinTexture("customnpcs:textures/entity/orcmale/StrandedOrc.png");
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
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.2f, 1.05f);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(1.2f, 1.05f);
            data.getPartConfig(EnumParts.BODY).setScale(1.4f, 1.1f, 1.5f);
            data.getPartConfig(EnumParts.HEAD).setScale(1.2f, 1.1f);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
