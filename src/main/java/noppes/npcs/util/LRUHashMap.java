package noppes.npcs.util;

import java.util.Map;
import java.util.LinkedHashMap;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V>
{
    private int maxSize;
    
    public LRUHashMap(int size) {
        super(size, 0.75f, true);
        this.maxSize = size;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.maxSize;
    }
}
