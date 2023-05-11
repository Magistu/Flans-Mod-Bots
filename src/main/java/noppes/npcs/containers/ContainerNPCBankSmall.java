package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerNPCBankSmall extends ContainerNPCBankInterface
{
    public ContainerNPCBankSmall(EntityPlayer player, int slot, int bankid) {
        super(player, slot, bankid);
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public int getRowNumber() {
        return 3;
    }
}
