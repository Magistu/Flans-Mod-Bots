package noppes.npcs.roles;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.TransportController;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.TransportLocation;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.entity.data.role.IRoleTransporter;

public class RoleTransporter extends RoleInterface implements IRoleTransporter
{
    public int transportId;
    public String name;
    private int ticks;
    
    public RoleTransporter(EntityNPCInterface npc) {
        super(npc);
        this.transportId = -1;
        this.ticks = 10;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("TransporterId", this.transportId);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.transportId = nbttagcompound.getInteger("TransporterId");
        TransportLocation loc = this.getLocation();
        if (loc != null) {
            this.name = loc.name;
        }
    }
    
    @Override
    public boolean aiShouldExecute() {
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.ticks = 10;
        if (!this.hasTransport()) {
            return false;
        }
        TransportLocation loc = this.getLocation();
        if (loc.type != 0) {
            return false;
        }
        List<EntityPlayer> inRange = (List<EntityPlayer>)this.npc.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.npc.getEntityBoundingBox().grow(6.0, 6.0, 6.0));
        for (EntityPlayer player : inRange) {
            if (!this.npc.canSee((Entity)player)) {
                continue;
            }
            this.unlock(player, loc);
        }
        return false;
    }
    
    @Override
    public void interact(EntityPlayer player) {
        if (this.hasTransport()) {
            TransportLocation loc = this.getLocation();
            if (loc.type == 2) {
                this.unlock(player, loc);
            }
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerTransporter, this.npc);
        }
    }
    
    public void transport(EntityPlayerMP player, String location) {
        TransportLocation loc = TransportController.getInstance().getTransport(location);
        PlayerTransportData playerdata = PlayerData.get((EntityPlayer)player).transportData;
        if (loc == null || (!loc.isDefault() && !playerdata.transports.contains(loc.id))) {
            return;
        }
        RoleEvent.TransporterUseEvent event = new RoleEvent.TransporterUseEvent((EntityPlayer)player, this.npc.wrappedNPC, loc);
        if (EventHooks.onNPCRole(this.npc, event)) {
            return;
        }
        NoppesUtilPlayer.teleportPlayer(player, loc.pos.getX(), loc.pos.getY(), loc.pos.getZ(), loc.dimension);
    }
    
    private void unlock(EntityPlayer player, TransportLocation loc) {
        PlayerTransportData data = PlayerData.get(player).transportData;
        if (data.transports.contains(this.transportId)) {
            return;
        }
        RoleEvent.TransporterUnlockedEvent event = new RoleEvent.TransporterUnlockedEvent(player, this.npc.wrappedNPC);
        if (EventHooks.onNPCRole(this.npc, event)) {
            return;
        }
        data.transports.add(this.transportId);
        player.sendMessage((ITextComponent)new TextComponentTranslation("transporter.unlock", new Object[] { loc.name }));
    }
    
    @Override
    public TransportLocation getLocation() {
        if (this.npc.isRemote()) {
            return null;
        }
        return TransportController.getInstance().getTransport(this.transportId);
    }
    
    public boolean hasTransport() {
        TransportLocation loc = this.getLocation();
        return loc != null && loc.id == this.transportId;
    }
    
    public void setTransport(TransportLocation location) {
        this.transportId = location.id;
        this.name = location.name;
    }
}
