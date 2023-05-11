package noppes.npcs.blocks.tiles;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileCopy extends TileEntity
{
    public short length;
    public short width;
    public short height;
    public String name;
    
    public TileCopy() {
        this.length = 10;
        this.width = 10;
        this.height = 10;
        this.name = "";
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.length = compound.getShort("Length");
        this.width = compound.getShort("Width");
        this.height = compound.getShort("Height");
        this.name = compound.getString("Name");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setShort("Length", this.length);
        compound.setShort("Width", this.width);
        compound.setShort("Height", this.height);
        compound.setString("Name", this.name);
        return super.writeToNBT(compound);
    }
    
    public void handleUpdateTag(NBTTagCompound compound) {
        this.length = compound.getShort("Length");
        this.width = compound.getShort("Width");
        this.height = compound.getShort("Height");
    }
    
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.handleUpdateTag(pkt.getNbtCompound());
    }
    
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }
    
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("x", this.pos.getX());
        compound.setInteger("y", this.pos.getY());
        compound.setInteger("z", this.pos.getZ());
        compound.setShort("Length", this.length);
        compound.setShort("Width", this.width);
        compound.setShort("Height", this.height);
        return compound;
    }
    
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + this.width + 1), (double)(this.pos.getY() + this.height + 1), (double)(this.pos.getZ() + this.length + 1));
    }
}
