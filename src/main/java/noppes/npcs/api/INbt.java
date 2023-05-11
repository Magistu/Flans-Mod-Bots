package noppes.npcs.api;

import net.minecraft.nbt.NBTTagCompound;

public interface INbt
{
    void remove(String p0);
    
    boolean has(String p0);
    
    boolean getBoolean(String p0);
    
    void setBoolean(String p0, boolean p1);
    
    short getShort(String p0);
    
    void setShort(String p0, short p1);
    
    int getInteger(String p0);
    
    void setInteger(String p0, int p1);
    
    byte getByte(String p0);
    
    void setByte(String p0, byte p1);
    
    long getLong(String p0);
    
    void setLong(String p0, long p1);
    
    double getDouble(String p0);
    
    void setDouble(String p0, double p1);
    
    float getFloat(String p0);
    
    void setFloat(String p0, float p1);
    
    String getString(String p0);
    
    void setString(String p0, String p1);
    
    byte[] getByteArray(String p0);
    
    void setByteArray(String p0, byte[] p1);
    
    int[] getIntegerArray(String p0);
    
    void setIntegerArray(String p0, int[] p1);
    
    Object[] getList(String p0, int p1);
    
    int getListType(String p0);
    
    void setList(String p0, Object[] p1);
    
    INbt getCompound(String p0);
    
    void setCompound(String p0, INbt p1);
    
    String[] getKeys();
    
    int getType(String p0);
    
    NBTTagCompound getMCNBT();
    
    String toJsonString();
    
    boolean isEqual(INbt p0);
    
    void clear();
    
    void merge(INbt p0);
}
