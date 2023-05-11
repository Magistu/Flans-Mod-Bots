package noppes.npcs.roles;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import noppes.npcs.CustomNpcs;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.controllers.MusicController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.data.role.IJobBard;

public class JobBard extends JobInterface implements IJobBard
{
    public int minRange;
    public int maxRange;
    public boolean isStreamer;
    public boolean hasOffRange;
    public String song;
    private long ticks;
    
    public JobBard(EntityNPCInterface npc) {
        super(npc);
        this.minRange = 2;
        this.maxRange = 64;
        this.isStreamer = true;
        this.hasOffRange = true;
        this.song = "";
        this.ticks = 0L;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("BardSong", this.song);
        nbttagcompound.setInteger("BardMinRange", this.minRange);
        nbttagcompound.setInteger("BardMaxRange", this.maxRange);
        nbttagcompound.setBoolean("BardStreamer", this.isStreamer);
        nbttagcompound.setBoolean("BardHasOff", this.hasOffRange);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.song = nbttagcompound.getString("BardSong");
        this.minRange = nbttagcompound.getInteger("BardMinRange");
        this.maxRange = nbttagcompound.getInteger("BardMaxRange");
        this.isStreamer = nbttagcompound.getBoolean("BardStreamer");
        this.hasOffRange = nbttagcompound.getBoolean("BardHasOff");
    }
    
    public void onLivingUpdate() {
        if (!this.npc.isRemote() || this.song.isEmpty()) {
            return;
        }
        if (!MusicController.Instance.isPlaying(this.song)) {
            List<EntityPlayer> list = (List<EntityPlayer>)this.npc.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.npc.getEntityBoundingBox().grow((double)this.minRange, (double)(this.minRange / 2), (double)this.minRange));
            if (!list.contains(CustomNpcs.proxy.getPlayer())) {
                return;
            }
            if (this.isStreamer) {
                MusicController.Instance.playStreaming(this.song, (Entity)this.npc);
            }
            else {
                MusicController.Instance.playMusic(this.song, (Entity)this.npc);
            }
        }
        else if (MusicController.Instance.playingEntity != this.npc) {
            EntityPlayer player = CustomNpcs.proxy.getPlayer();
            if (this.npc.getDistance((Entity)player) < MusicController.Instance.playingEntity.getDistance((Entity)player)) {
                MusicController.Instance.playingEntity = (Entity)this.npc;
            }
        }
        else if (this.hasOffRange) {
            List<EntityPlayer> list = (List<EntityPlayer>)this.npc.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.npc.getEntityBoundingBox().grow((double)this.maxRange, (double)(this.maxRange / 2), (double)this.maxRange));
            if (!list.contains(CustomNpcs.proxy.getPlayer())) {
                MusicController.Instance.stopMusic();
            }
        }
        if (MusicController.Instance.isPlaying(this.song)) {
        	Minecraft.getMinecraft().getMusicTicker().update();
        }
    }
    
    @Override
    public void killed() {
        this.delete();
    }
    
    @Override
    public void delete() {
        if (this.npc.world.isRemote && this.hasOffRange && MusicController.Instance.isPlaying(this.song)) {
            MusicController.Instance.stopMusic();
        }
    }
    
    @Override
    public String getSong() {
        return this.song;
    }
    
    @Override
    public void setSong(String song) {
        this.song = song;
        this.npc.updateClient = true;
    }
}
