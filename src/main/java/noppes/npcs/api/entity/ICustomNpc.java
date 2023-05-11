package noppes.npcs.api.entity;

import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.entity.data.INPCJob;
import noppes.npcs.api.entity.data.INPCRole;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.api.entity.data.INPCAdvanced;
import noppes.npcs.api.entity.data.INPCAi;
import noppes.npcs.api.entity.data.INPCStats;
import noppes.npcs.api.entity.data.INPCInventory;
import noppes.npcs.api.entity.data.INPCDisplay;
import net.minecraft.entity.EntityCreature;

public interface ICustomNpc<T extends EntityCreature> extends IEntityLiving<T>
{
    INPCDisplay getDisplay();
    
    INPCInventory getInventory();
    
    INPCStats getStats();
    
    INPCAi getAi();
    
    INPCAdvanced getAdvanced();
    
    IFaction getFaction();
    
    void setFaction(int p0);
    
    INPCRole getRole();
    
    INPCJob getJob();
    
    ITimers getTimers();
    
    int getHomeX();
    
    int getHomeY();
    
    int getHomeZ();
    
    IEntityLivingBase getOwner();
    
    void setHome(int p0, int p1, int p2);
    
    void reset();
    
    void say(String p0);
    
    void sayTo(IPlayer p0, String p1);
    
    IProjectile shootItem(IEntityLivingBase p0, IItemStack p1, int p2);
    
    IProjectile shootItem(double p0, double p1, double p2, IItemStack p3, int p4);
    
    void giveItem(IPlayer p0, IItemStack p1);
    
    void setDialog(int p0, IDialog p1);
    
    IDialog getDialog(int p0);
    
    void updateClient();
    
    String executeCommand(String p0);
}
