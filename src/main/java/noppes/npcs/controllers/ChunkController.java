package noppes.npcs.controllers;

import java.util.Iterator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.List;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.Entity;
import java.util.HashMap;
import net.minecraftforge.common.ForgeChunkManager;

public class ChunkController implements ForgeChunkManager.LoadingCallback
{
    public static ChunkController instance;
    private HashMap<Entity, ForgeChunkManager.Ticket> tickets;
    
    public ChunkController() {
        this.tickets = new HashMap<Entity, ForgeChunkManager.Ticket>();
        ChunkController.instance = this;
    }
    
    public void clear() {
        this.tickets = new HashMap<Entity, ForgeChunkManager.Ticket>();
    }
    
    public ForgeChunkManager.Ticket getTicket(EntityNPCInterface npc) {
        ForgeChunkManager.Ticket ticket = this.tickets.get(npc);
        if (ticket != null) {
            return ticket;
        }
        if (this.size() >= CustomNpcs.ChuckLoaders) {
            return null;
        }
        ticket = ForgeChunkManager.requestTicket((Object)CustomNpcs.instance, npc.world, ForgeChunkManager.Type.ENTITY);
        if (ticket == null) {
            return null;
        }
        ticket.bindEntity((Entity)npc);
        ticket.setChunkListDepth(6);
        this.tickets.put((Entity)npc, ticket);
        return null;
    }
    
    public void deleteNPC(EntityNPCInterface npc) {
        ForgeChunkManager.Ticket ticket = this.tickets.get(npc);
        if (ticket != null) {
            this.tickets.remove(npc);
            ForgeChunkManager.releaseTicket(ticket);
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            if (!(ticket.getEntity() instanceof EntityNPCInterface)) {
                continue;
            }
            EntityNPCInterface npc = (EntityNPCInterface)ticket.getEntity();
            if (npc.advanced.job != 8 || tickets.contains(npc)) {
                continue;
            }
            this.tickets.put((Entity)npc, ticket);
            double x = npc.posX / 16.0;
            double z = npc.posZ / 16.0;
            ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.floor(x), MathHelper.floor(z)));
            ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.ceil(x), MathHelper.ceil(z)));
            ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.floor(x), MathHelper.ceil(z)));
            ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.ceil(x), MathHelper.floor(z)));
        }
    }
    
    public int size() {
        return this.tickets.size();
    }
    
    public void unload(int toRemove) {
        Iterator<Entity> ite = this.tickets.keySet().iterator();
        int i = 0;
        while (ite.hasNext()) {
            if (i >= toRemove) {
                return;
            }
            Entity entity = ite.next();
            ForgeChunkManager.releaseTicket((ForgeChunkManager.Ticket)this.tickets.get(entity));
            ite.remove();
            ++i;
        }
    }
}
