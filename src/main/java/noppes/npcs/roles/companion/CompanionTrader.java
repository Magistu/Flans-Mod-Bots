package noppes.npcs.roles.companion;

import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class CompanionTrader extends CompanionJobInterface
{
    @Override
    public NBTTagCompound getNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        return compound;
    }
    
    @Override
    public void setNBT(NBTTagCompound compound) {
    }
    
    public void interact(EntityPlayer player) {
        NoppesUtilServer.sendOpenGui(player, EnumGuiType.CompanionTrader, this.npc);
    }
}
