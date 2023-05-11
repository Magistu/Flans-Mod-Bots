package noppes.npcs.entity;

import net.minecraft.world.World;

public class EntityNPC64x32 extends EntityCustomNpc
{
    public EntityNPC64x32(World world) {
        super(world);
        this.display.setSkinTexture("customnpcs:textures/entity/humanmale/Steve64x32.png");
    }
}
