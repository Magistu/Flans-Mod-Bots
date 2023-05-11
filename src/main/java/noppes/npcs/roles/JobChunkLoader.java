package noppes.npcs.roles;

import java.util.Iterator;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.controllers.ChunkController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.util.math.ChunkPos;
import java.util.List;

public class JobChunkLoader extends JobInterface
{
    private List<ChunkPos> chunks;
    private int ticks;
    private long playerLastSeen;
    
    public JobChunkLoader(EntityNPCInterface npc) {
        super(npc);
        this.chunks = new ArrayList<ChunkPos>();
        this.ticks = 20;
        this.playerLastSeen = 0L;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setLong("ChunkPlayerLastSeen", this.playerLastSeen);
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.playerLastSeen = compound.getLong("ChunkPlayerLastSeen");
    }
    
    @Override
    public boolean aiShouldExecute() {
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.ticks = 20;
        List players = this.npc.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.npc.getEntityBoundingBox().grow(48.0, 48.0, 48.0));
        if (!players.isEmpty()) {
            this.playerLastSeen = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() > this.playerLastSeen + 600000L) {
            ChunkController.instance.deleteNPC(this.npc);
            this.chunks.clear();
            return false;
        }
        ForgeChunkManager.Ticket ticket = ChunkController.instance.getTicket(this.npc);
        if (ticket == null) {
            return false;
        }
        double x = this.npc.posX / 16.0;
        double z = this.npc.posZ / 16.0;
        List<ChunkPos> list = new ArrayList<ChunkPos>();
        list.add(new ChunkPos(MathHelper.floor(x), MathHelper.floor(z)));
        list.add(new ChunkPos(MathHelper.ceil(x), MathHelper.ceil(z)));
        list.add(new ChunkPos(MathHelper.floor(x), MathHelper.ceil(z)));
        list.add(new ChunkPos(MathHelper.ceil(x), MathHelper.floor(z)));
        for (ChunkPos chunk : list) {
            if (!this.chunks.contains(chunk)) {
                ForgeChunkManager.forceChunk(ticket, chunk);
            }
            else {
                this.chunks.remove(chunk);
            }
        }
        for (ChunkPos chunk : this.chunks) {
            ForgeChunkManager.unforceChunk(ticket, chunk);
        }
        this.chunks = list;
        return false;
    }
    
    @Override
    public boolean aiContinueExecute() {
        return false;
    }
    
    @Override
    public void reset() {
        ChunkController.instance.deleteNPC(this.npc);
        this.chunks.clear();
        this.playerLastSeen = 0L;
    }
    
    @Override
    public void delete() {
    }
}
