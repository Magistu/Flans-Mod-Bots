package noppes.npcs.client.controllers;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundCategory;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.audio.PositionedSoundRecord;

public class MusicController
{
    public static MusicController Instance;
    public PositionedSoundRecord playing;
    public ResourceLocation playingResource;
    public Entity playingEntity;
    
    public MusicController() {
        MusicController.Instance = this;
    }
    
    public void stopMusic() {
        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        if (this.playing != null) {
            handler.stopSound((ISound)this.playing);
        }
        handler.stop("", SoundCategory.MUSIC);
        handler.stop("", SoundCategory.AMBIENT);
        handler.stop("", SoundCategory.RECORDS);
        this.playingResource = null;
        this.playingEntity = null;
        this.playing = null;
    }
    
    public void playStreaming(String music, Entity entity) {
        if (this.isPlaying(music)) {
            return;
        }
        this.stopMusic();
        this.playingEntity = entity;
        this.playingResource = new ResourceLocation(music);
        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        handler.playSound((ISound)(this.playing = new PositionedSoundRecord(this.playingResource, SoundCategory.RECORDS, 4.0f, 1.0f, false, 0, ISound.AttenuationType.LINEAR, (float)entity.posX, (float)entity.posY, (float)entity.posZ)));
    }
    
    public void playMusic(String music, Entity entity) {
        if (this.isPlaying(music)) {
            return;
        }
        this.stopMusic();
        this.playingResource = new ResourceLocation(music);
        this.playingEntity = entity;
        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        handler.playSound((ISound)(this.playing = new PositionedSoundRecord(this.playingResource, SoundCategory.MUSIC, 1.0f, 1.0f, false, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f)));
    }
    
    public boolean isPlaying(String music) {
        ResourceLocation resource = new ResourceLocation(music);
        return this.playingResource != null && this.playingResource.equals((Object)resource) && Minecraft.getMinecraft().getSoundHandler().isSoundPlaying((ISound)this.playing);
    }
    
    public void playSound(SoundCategory cat, String music, int x, int y, int z, float volumne, float pitch) {
        PositionedSoundRecord rec = new PositionedSoundRecord(new ResourceLocation(music), cat, volumne, pitch, false, 0, ISound.AttenuationType.LINEAR, x + 0.5f, (float)y, z + 0.5f);
        Minecraft.getMinecraft().getSoundHandler().playSound((ISound)rec);
    }
}
