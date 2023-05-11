package noppes.npcs.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;

public interface IWorld
{
    @Deprecated
    IEntity[] getNearbyEntities(int p0, int p1, int p2, int p3, int p4);
    
    IEntity[] getNearbyEntities(IPos p0, int p1, int p2);
    
    @Deprecated
    IEntity getClosestEntity(int p0, int p1, int p2, int p3, int p4);
    
    IEntity getClosestEntity(IPos p0, int p1, int p2);
    
    IEntity[] getAllEntities(int p0);
    
    long getTime();
    
    void setTime(long p0);
    
    long getTotalTime();
    
    IBlock getBlock(int p0, int p1, int p2);
    
    void setBlock(int p0, int p1, int p2, String p3, int p4);
    
    void removeBlock(int p0, int p1, int p2);
    
    float getLightValue(int p0, int p1, int p2);
    
    IPlayer getPlayer(String p0);
    
    boolean isDay();
    
    boolean isRaining();
    
    IDimension getDimension();
    
    void setRaining(boolean p0);
    
    void thunderStrike(double p0, double p1, double p2);
    
    void playSoundAt(IPos p0, String p1, float p2, float p3);
    
    void spawnParticle(String p0, double p1, double p2, double p3, double p4, double p5, double p6, double p7, int p8);
    
    void broadcast(String p0);
    
    IScoreboard getScoreboard();
    
    IData getTempdata();
    
    IData getStoreddata();
    
    IItemStack createItem(String p0, int p1, int p2);
    
    IItemStack createItemFromNbt(INbt p0);
    
    void explode(double p0, double p1, double p2, float p3, boolean p4, boolean p5);
    
    IPlayer[] getAllPlayers();
    
    String getBiomeName(int p0, int p1);
    
    void spawnEntity(IEntity p0);
    
    @Deprecated
    IEntity spawnClone(double p0, double p1, double p2, int p3, String p4);
    
    @Deprecated
    IEntity getClone(int p0, String p1);
    
    int getRedstonePower(int p0, int p1, int p2);
    
    WorldServer getMCWorld();
    
    BlockPos getMCBlockPos(int p0, int p1, int p2);
    
    IEntity getEntity(String p0);
    
    IEntity createEntityFromNBT(INbt p0);
    
    IEntity createEntity(String p0);
    
    IBlock getSpawnPoint();
    
    void setSpawnPoint(IBlock p0);
    
    String getName();
}
