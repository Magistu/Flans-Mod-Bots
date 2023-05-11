package noppes.npcs.api.wrapper;

import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.containers.ContainerNpcInterface;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.api.IContainerCustomChest;

public class ContainerCustomChestWrapper extends ContainerWrapper implements IContainerCustomChest
{
    public ScriptContainer script;
    public String name;
    
    public ContainerCustomChestWrapper(IInventory inventory) {
        super(inventory);
        this.script = null;
        this.name = "";
    }
    
    public ContainerCustomChestWrapper(Container container) {
        super(container);
        this.script = null;
        this.name = "";
    }
    
    @Override
    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        if (this.name.equals(name)) {
            return;
        }
        this.name = name;
        Server.sendDataDelayed((EntityPlayerMP)((ContainerNpcInterface)this.getMCContainer()).player, EnumPacketClient.CHEST_NAME, 10, name);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
