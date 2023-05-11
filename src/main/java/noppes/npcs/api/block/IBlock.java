package noppes.npcs.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.IPos;

public interface IBlock
{
    int getX();
    
    int getY();
    
    int getZ();
    
    IPos getPos();
    
    int getMetadata();
    
    void setMetadata(int p0);
    
    String getName();
    
    void remove();
    
    boolean isRemoved();
    
    boolean isAir();
    
    IBlock setBlock(String p0);
    
    IBlock setBlock(IBlock p0);
    
    boolean hasTileEntity();
    
    boolean isContainer();
    
    IContainer getContainer();
    
    IData getTempdata();
    
    IData getStoreddata();
    
    IWorld getWorld();
    
    INbt getTileEntityNBT();
    
    void setTileEntityNBT(INbt p0);
    
    TileEntity getMCTileEntity();
    
    Block getMCBlock();
    
    void blockEvent(int p0, int p1);
    
    String getDisplayName();
    
    IBlockState getMCBlockState();
    
    void interact(int p0);
}
