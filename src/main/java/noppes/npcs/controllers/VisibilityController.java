package noppes.npcs.controllers;

import noppes.npcs.CustomItems;
import java.util.Iterator;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import noppes.npcs.entity.EntityNPCInterface;
import java.util.Map;

public class VisibilityController
{
    public static VisibilityController instance;
    private Map<Integer, EntityNPCInterface> trackedEntityHashTable;
    
    public VisibilityController() {
        this.trackedEntityHashTable = new ConcurrentHashMap<Integer, EntityNPCInterface>();
        this.trackedEntityHashTable = new TreeMap<Integer, EntityNPCInterface>();
    }
    
    public void trackNpc(EntityNPCInterface npc) {
        boolean hasOptions = npc.display.availability.hasOptions();
        if ((hasOptions || npc.display.getVisible() != 0) && !this.trackedEntityHashTable.containsKey(npc.getEntityId())) {
            this.trackedEntityHashTable.put(npc.getEntityId(), npc);
        }
        if (!hasOptions && npc.display.getVisible() == 0 && this.trackedEntityHashTable.containsKey(npc.getEntityId())) {
            this.trackedEntityHashTable.remove(npc.getEntityId());
        }
    }
    
    public void onUpdate(EntityPlayerMP player) {
        WorldServer world = player.getServerWorld();
        for (Map.Entry<Integer, EntityNPCInterface> entry : this.trackedEntityHashTable.entrySet()) {
            checkIsVisible(entry.getValue(), player);
        }
    }
    
    public static void checkIsVisible(EntityNPCInterface npc, EntityPlayerMP playerMP) {
        if (npc.display.isVisibleTo(playerMP) || playerMP.isSpectator() || playerMP.getHeldItemMainhand().getItem() == CustomItems.wand) {
            npc.setVisible(playerMP);
        }
        else {
            npc.setInvisible(playerMP);
        }
    }
    
    static {
        VisibilityController.instance = new VisibilityController();
    }
}
