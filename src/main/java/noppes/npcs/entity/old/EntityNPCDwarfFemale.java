package noppes.npcs.entity.old;

import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNPCDwarfFemale extends EntityNPCInterface
{
    public EntityNPCDwarfFemale(World world) {
        super(world);
        float n = 0.75f;
        this.scaleZ = n;
        this.scaleX = n;
        this.scaleY = 0.6275f;
        this.display.setSkinTexture("customnpcs:textures/entity/dwarffemale/Simone.png");
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
            data.getOrCreatePart(EnumParts.BREASTS).type = 2;
            data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.9f, 0.65f);
            data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.9f, 0.65f);
            data.getPartConfig(EnumParts.BODY).setScale(1.0f, 0.65f, 1.1f);
            data.getPartConfig(EnumParts.HEAD).setScale(0.85f, 0.85f);
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
