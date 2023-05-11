package noppes.npcs.controllers.data;

import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.api.entity.data.IMark;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraft.util.EnumFacing;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class MarkData implements ICapabilityProvider
{
    @CapabilityInject(MarkData.class)
    public static Capability<MarkData> MARKDATA_CAPABILITY;
    private static String NBTKEY = "cnpcmarkdata";
    private static ResourceLocation CAPKEY;
    private EntityLivingBase entity;
    public List<Mark> marks;
    
    public MarkData() {
        this.marks = new ArrayList<Mark>();
    }
    
    public void setNBT(NBTTagCompound compound) {
        List<Mark> marks = new ArrayList<Mark>();
        NBTTagList list = compound.getTagList("marks", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound c = list.getCompoundTagAt(i);
            Mark m = new Mark();
            m.type = c.getInteger("type");
            m.color = c.getInteger("color");
            m.availability.readFromNBT(c.getCompoundTag("availability"));
            marks.add(m);
        }
        this.marks = marks;
    }
    
    public NBTTagCompound getNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (Mark m : this.marks) {
            NBTTagCompound c = new NBTTagCompound();
            c.setInteger("type", m.type);
            c.setInteger("color", m.color);
            c.setTag("availability", (NBTBase)m.availability.writeToNBT(new NBTTagCompound()));
            list.appendTag((NBTBase)c);
        }
        compound.setTag("marks", (NBTBase)list);
        return compound;
    }
    
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == MarkData.MARKDATA_CAPABILITY;
    }
    
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (this.hasCapability(capability, facing)) {
            return (T)this;
        }
        return null;
    }
    
    public static void register(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(MarkData.CAPKEY, (ICapabilityProvider)new MarkData());
    }
    
    public void save() {
        this.entity.getEntityData().setTag("cnpcmarkdata", (NBTBase)this.getNBT());
    }
    
    public IMark addMark(int type) {
        Mark m = new Mark();
        m.type = type;
        this.marks.add(m);
        if (!this.entity.world.isRemote) {
            this.syncClients();
        }
        return m;
    }
    
    public IMark addMark(int type, int color) {
        Mark m = new Mark();
        m.type = type;
        m.color = color;
        this.marks.add(m);
        if (!this.entity.world.isRemote) {
            this.syncClients();
        }
        return m;
    }
    
    public static MarkData get(EntityLivingBase entity) {
        MarkData data = (MarkData)entity.getCapability((Capability)MarkData.MARKDATA_CAPABILITY, (EnumFacing)null);
        if (data.entity == null) {
            data.entity = entity;
            data.setNBT(entity.getEntityData().getCompoundTag("cnpcmarkdata"));
        }
        return data;
    }
    
    public void syncClients() {
        Server.sendToAll(this.entity.getServer(), EnumPacketClient.MARK_DATA, this.entity.getEntityId(), this.getNBT());
    }
    
    static {
        MarkData.MARKDATA_CAPABILITY = null;
        CAPKEY = new ResourceLocation("customnpcs", "markdata");
    }
    
    public class Mark implements IMark
    {
        public int type;
        public Availability availability;
        public int color;
        
        public Mark() {
            this.type = 0;
            this.availability = new Availability();
            this.color = 16772433;
        }
        
        @Override
        public IAvailability getAvailability() {
            return this.availability;
        }
        
        @Override
        public int getColor() {
            return this.color;
        }
        
        @Override
        public void setColor(int color) {
            this.color = color;
        }
        
        @Override
        public int getType() {
            return this.type;
        }
        
        @Override
        public void setType(int type) {
            this.type = type;
        }
        
        @Override
        public void update() {
            MarkData.this.syncClients();
        }
    }
}
