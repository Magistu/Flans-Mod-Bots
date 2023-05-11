package noppes.npcs.blocks.tiles;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.NetworkManager;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.block.properties.IProperty;
import noppes.npcs.blocks.BlockBorder;
import noppes.npcs.CustomItems;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import noppes.npcs.controllers.data.Availability;
import net.minecraft.util.ITickable;
import com.google.common.base.Predicate;

public class TileBorder extends TileNpcEntity implements Predicate, ITickable
{
    public Availability availability;
    public AxisAlignedBB boundingbox;
    public int rotation;
    public int height;
    public String message;
    
    public TileBorder() {
        this.availability = new Availability();
        this.rotation = 0;
        this.height = 10;
        this.message = "availability.areaNotAvailble";
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.readExtraNBT(compound);
        if (this.getWorld() != null) {
            this.getWorld().setBlockState(this.getPos(), CustomItems.border.getDefaultState().withProperty((IProperty)BlockBorder.ROTATION, (Comparable)this.rotation));
        }
    }
    
    public void readExtraNBT(NBTTagCompound compound) {
        this.availability.readFromNBT(compound.getCompoundTag("BorderAvailability"));
        this.rotation = compound.getInteger("BorderRotation");
        this.height = compound.getInteger("BorderHeight");
        this.message = compound.getString("BorderMessage");
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        this.writeExtraNBT(compound);
        return super.writeToNBT(compound);
    }
    
    public void writeExtraNBT(NBTTagCompound compound) {
        compound.setTag("BorderAvailability", (NBTBase)this.availability.writeToNBT(new NBTTagCompound()));
        compound.setInteger("BorderRotation", this.rotation);
        compound.setInteger("BorderHeight", this.height);
        compound.setString("BorderMessage", this.message);
    }
    
    public void update() {
        if (this.world.isRemote) {
            return;
        }
        AxisAlignedBB box = new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + this.height + 1), (double)(this.pos.getZ() + 1));
        List<Entity> list = (List<Entity>)this.world.getEntitiesWithinAABB((Class)Entity.class, box, (Predicate)this);
        for (Entity entity : list) {
            if (entity instanceof EntityEnderPearl) {
                EntityEnderPearl pearl = (EntityEnderPearl)entity;
                if (!(pearl.getThrower() instanceof EntityPlayer) || this.availability.isAvailable((EntityPlayer)pearl.getThrower())) {
                    continue;
                }
                entity.isDead = true;
            }
            else {
                EntityPlayer player = (EntityPlayer)entity;
                if (this.availability.isAvailable(player)) {
                    continue;
                }
                BlockPos pos2 = new BlockPos((Vec3i)this.pos);
                if (this.rotation == 2) {
                    pos2 = pos2.south();
                }
                else if (this.rotation == 0) {
                    pos2 = pos2.north();
                }
                else if (this.rotation == 1) {
                    pos2 = pos2.east();
                }
                else if (this.rotation == 3) {
                    pos2 = pos2.west();
                }
                while (!this.world.isAirBlock(pos2)) {
                    pos2 = pos2.up();
                }
                player.setPositionAndUpdate(pos2.getX() + 0.5, (double)pos2.getY(), pos2.getZ() + 0.5);
                if (this.message.isEmpty()) {
                    continue;
                }
                player.sendStatusMessage((ITextComponent)new TextComponentTranslation(this.message, new Object[0]), true);
            }
        }
    }
    
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.handleUpdateTag(pkt.getNbtCompound());
    }
    
    public void handleUpdateTag(NBTTagCompound compound) {
        this.rotation = compound.getInteger("Rotation");
    }
    
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }
    
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("x", this.pos.getX());
        compound.setInteger("y", this.pos.getY());
        compound.setInteger("z", this.pos.getZ());
        compound.setInteger("Rotation", this.rotation);
        return compound;
    }
    
    public boolean isEntityApplicable(Entity var1) {
        return var1 instanceof EntityPlayerMP || var1 instanceof EntityEnderPearl;
    }
    
    public boolean apply(Object ob) {
        return this.isEntityApplicable((Entity)ob);
    }
}
