package noppes.npcs.entity;

import net.minecraft.world.World;

public class EntityNpcAlex extends EntityCustomNpc
{
    public EntityNpcAlex(World world) {
        super(world);
        this.display.setSkinTexture("customnpcs:textures/entity/alex_skins/alex_2.png");
    }
}
