package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.entity.data.role.IRoleTransporter;

public class TransportLocation implements IRoleTransporter.ITransportLocation
{
    public int id;
    public String name;
    public BlockPos pos;
    public int type;
    public int dimension;
    public TransportCategory category;
    
    public TransportLocation() {
        this.id = -1;
        this.name = "default name";
        this.type = 0;
        this.dimension = 0;
    }
    
    public void readNBT(NBTTagCompound compound) {
        if (compound == null) {
            return;
        }
        this.id = compound.getInteger("Id");
        this.pos = new BlockPos(compound.getDouble("PosX"), compound.getDouble("PosY"), compound.getDouble("PosZ"));
        this.type = compound.getInteger("Type");
        this.dimension = compound.getInteger("Dimension");
        this.name = compound.getString("Name");
    }
    
    public NBTTagCompound writeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Id", this.id);
        compound.setDouble("PosX", (double)this.pos.getX());
        compound.setDouble("PosY", (double)this.pos.getY());
        compound.setDouble("PosZ", (double)this.pos.getZ());
        compound.setInteger("Type", this.type);
        compound.setInteger("Dimension", this.dimension);
        compound.setString("Name", this.name);
        return compound;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public int getDimension() {
        return this.dimension;
    }
    
    @Override
    public int getX() {
        return this.pos.getX();
    }
    
    @Override
    public int getY() {
        return this.pos.getY();
    }
    
    @Override
    public int getZ() {
        return this.pos.getZ();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public int getType() {
        return this.type;
    }
    
    public boolean isDefault() {
        return this.type == 1;
    }
}
