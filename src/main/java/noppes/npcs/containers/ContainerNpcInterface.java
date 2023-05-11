package noppes.npcs.containers;

import noppes.npcs.api.wrapper.ContainerWrapper;
import noppes.npcs.api.wrapper.ContainerCustomChestWrapper;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.IContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerNpcInterface extends Container
{
    private int posX;
    private int posZ;
    public EntityPlayer player;
    public IContainer scriptContainer;
    
    public ContainerNpcInterface(EntityPlayer player) {
        this.player = player;
        this.posX = MathHelper.floor(player.posX);
        this.posZ = MathHelper.floor(player.posZ);
        player.motionX = 0.0;
        player.motionZ = 0.0;
    }
    
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isDead && this.posX == MathHelper.floor(player.posX) && this.posZ == MathHelper.floor(player.posZ);
    }
    
    public static IContainer getOrCreateIContainer(ContainerNpcInterface container) {
        if (container.scriptContainer != null) {
            return container.scriptContainer;
        }
        if (container instanceof ContainerCustomChest) {
            return container.scriptContainer = new ContainerCustomChestWrapper(container);
        }
        return container.scriptContainer = new ContainerWrapper(container);
    }
}
