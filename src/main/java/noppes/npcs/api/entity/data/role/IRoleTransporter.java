package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.entity.data.INPCRole;

public interface IRoleTransporter extends INPCRole
{
    ITransportLocation getLocation();
    
    public interface ITransportLocation
    {
        int getId();
        
        int getDimension();
        
        int getX();
        
        int getY();
        
        int getZ();
        
        String getName();
        
        int getType();
    }
}
