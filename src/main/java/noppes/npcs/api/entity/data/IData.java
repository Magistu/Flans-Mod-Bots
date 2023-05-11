package noppes.npcs.api.entity.data;

public interface IData
{
    void put(String p0, Object p1);
    
    Object get(String p0);
    
    void remove(String p0);
    
    boolean has(String p0);
    
    String[] getKeys();
    
    void clear();
}
