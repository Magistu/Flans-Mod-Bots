package noppes.npcs.entity.old;

import noppes.npcs.ModelPartData;
import noppes.npcs.ModelData;
import net.minecraft.entity.Entity;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNpcNagaMale extends EntityNPCInterface
{
    public EntityNpcNagaMale(World world) {
        super(world);
        this.display.setSkinTexture("customnpcs:textures/entity/nagamale/Cobra.png");
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
            ModelPartData legs = data.getOrCreatePart(EnumParts.LEGS);
            legs.playerTexture = true;
            legs.type = 1;
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
