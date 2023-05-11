package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.Teleporter;

public class CustomTeleporter extends Teleporter
{
    public CustomTeleporter(WorldServer par1WorldServer) {
        super(par1WorldServer);
    }
    
    public void placeInPortal(Entity entityIn, float rotationYaw) {
    }
}
