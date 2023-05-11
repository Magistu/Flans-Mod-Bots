package noppes.npcs.util;

import noppes.npcs.entity.EntityNPCInterface;
import java.util.UUID;
import com.mojang.authlib.GameProfile;

public class GameProfileAlt extends GameProfile
{
    private static UUID id;
    public EntityNPCInterface npc;
    
    public GameProfileAlt() {
        super(GameProfileAlt.id, "[customnpcs]");
    }
    
    public String getName() {
        if (this.npc == null) {
            return super.getName();
        }
        return this.npc.getName();
    }
    
    public UUID getId() {
        if (this.npc == null) {
            return GameProfileAlt.id;
        }
        return this.npc.getPersistentID();
    }
    
    public boolean isComplete() {
        return false;
    }
    
    static {
        id = UUID.fromString("c9c843f8-4cb1-4c82-aa61-e264291b7bd6");
    }
}
