package noppes.npcs.client.gui.script;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileScripted;

public class GuiScriptBlock extends GuiScriptInterface
{
    private TileScripted script;
    
    public GuiScriptBlock(int x, int y, int z) {
        TileScripted tileScripted = (TileScripted)this.player.world.getTileEntity(new BlockPos(x, y, z));
        this.script = tileScripted;
        this.handler = tileScripted;
        Client.sendData(EnumPacketServer.ScriptBlockDataGet, x, y, z);
    }
    
    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.script.setNBT(compound);
        super.setGuiData(compound);
    }
    
    @Override
    public void save() {
        super.save();
        BlockPos pos = this.script.getPos();
        Client.sendData(EnumPacketServer.ScriptBlockDataSave, pos.getX(), pos.getY(), pos.getZ(), this.script.getNBT(new NBTTagCompound()));
    }
}
