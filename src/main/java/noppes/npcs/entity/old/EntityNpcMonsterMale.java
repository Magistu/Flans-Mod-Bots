package noppes.npcs.entity.old;

import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNpcMonsterMale extends EntityNPCInterface
{
    public EntityNpcMonsterMale(World world) {
        super(world);
        this.display.setSkinTexture("customnpcs:textures/entity/monstermale/ZombieSteve.png");
    }
    
    @Override
    public void onUpdate() {
        this.setNoAI(this.isDead = true);
        if (!this.world.isRemote) {
            NBTTagCompound compound = new NBTTagCompound();
            this.writeToNBT(compound);
            EntityCustomNpc npc = new EntityCustomNpc(this.world);
            npc.readFromNBT(compound);
            npc.ais.animationType = 3;
            this.world.spawnEntity((Entity)npc);
        }
        super.onUpdate();
    }
}
