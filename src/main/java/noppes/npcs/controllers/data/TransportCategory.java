package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;

public class TransportCategory
{
    public int id;
    public String title;
    public HashMap<Integer, TransportLocation> locations;
    
    public TransportCategory() {
        this.id = -1;
        this.title = "";
        this.locations = new HashMap<Integer, TransportLocation>();
    }
    
    public Vector<TransportLocation> getDefaultLocations() {
        Vector<TransportLocation> list = new Vector<TransportLocation>();
        for (TransportLocation loc : this.locations.values()) {
            if (loc.isDefault()) {
                list.add(loc);
            }
        }
        return list;
    }
    
    public void readNBT(NBTTagCompound compound) {
        this.id = compound.getInteger("CategoryId");
        this.title = compound.getString("CategoryTitle");
        NBTTagList locs = compound.getTagList("CategoryLocations", 10);
        if (locs == null || locs.tagCount() == 0) {
            return;
        }
        for (int ii = 0; ii < locs.tagCount(); ++ii) {
            TransportLocation location = new TransportLocation();
            location.readNBT(locs.getCompoundTagAt(ii));
            location.category = this;
            this.locations.put(location.id, location);
        }
    }
    
    public void writeNBT(NBTTagCompound compound) {
        compound.setInteger("CategoryId", this.id);
        compound.setString("CategoryTitle", this.title);
        NBTTagList locs = new NBTTagList();
        for (TransportLocation location : this.locations.values()) {
            locs.appendTag((NBTBase)location.writeNBT());
        }
        compound.setTag("CategoryLocations", (NBTBase)locs);
    }
}
