package noppes.npcs.api;

import net.minecraft.util.math.BlockPos;

public interface IPos
{
    int getX();
    
    int getY();
    
    int getZ();
    
    IPos up();
    
    IPos up(int p0);
    
    IPos down();
    
    IPos down(int p0);
    
    IPos north();
    
    IPos north(int p0);
    
    IPos east();
    
    IPos east(int p0);
    
    IPos south();
    
    IPos south(int p0);
    
    IPos west();
    
    IPos west(int p0);
    
    IPos add(int p0, int p1, int p2);
    
    IPos add(IPos p0);
    
    IPos subtract(int p0, int p1, int p2);
    
    IPos subtract(IPos p0);
    
    double[] normalize();
    
    BlockPos getMCBlockPos();
    
    IPos offset(int p0);
    
    IPos offset(int p0, int p1);
    
    double distanceTo(IPos p0);
}
