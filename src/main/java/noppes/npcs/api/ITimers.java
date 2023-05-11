package noppes.npcs.api;

public interface ITimers
{
    void start(int p0, int p1, boolean p2);
    
    void forceStart(int p0, int p1, boolean p2);
    
    boolean has(int p0);
    
    boolean stop(int p0);
    
    void reset(int p0);
    
    void clear();
}
