package noppes.npcs.schematics;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

public interface ISchematic
{
    short getWidth();
    
    short getHeight();
    
    short getLength();
    
    int getTileEntitySize();
    
    NBTTagCompound getTileEntity(int p0);
    
    String getName();
    
    IBlockState getBlockState(int p0, int p1, int p2);
    
    IBlockState getBlockState(int p0);
    
    NBTTagCompound getNBT();
}
