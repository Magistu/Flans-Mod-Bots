package noppes.npcs;

import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import java.util.Map;

public class ModelPartData
{
    private static Map<String, ResourceLocation> resources;
    public int color;
    public int colorPattern;
    public byte type;
    public byte pattern;
    public boolean playerTexture;
    public String name;
    private ResourceLocation location;
    
    public ModelPartData(String name) {
        this.color = 16777215;
        this.colorPattern = 16777215;
        this.type = 0;
        this.pattern = 0;
        this.playerTexture = false;
        this.name = name;
    }
    
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("Type", this.type);
        compound.setInteger("Color", this.color);
        compound.setBoolean("PlayerTexture", this.playerTexture);
        compound.setByte("Pattern", this.pattern);
        return compound;
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey("Type")) {
            this.type = -1;
            return;
        }
        this.type = compound.getByte("Type");
        this.color = compound.getInteger("Color");
        this.playerTexture = compound.getBoolean("PlayerTexture");
        this.pattern = compound.getByte("Pattern");
        this.location = null;
    }
    
    public ResourceLocation getResource() {
        if (this.location != null) {
            return this.location;
        }
        String texture = this.name + "/" + this.type;
        if ((this.location = ModelPartData.resources.get(texture)) != null) {
            return this.location;
        }
        this.location = new ResourceLocation("moreplayermodels:textures/" + texture + ".png");
        ModelPartData.resources.put(texture, this.location);
        return this.location;
    }
    
    public void setType(int type) {
        this.type = (byte)type;
        this.location = null;
    }
    
    @Override
    public String toString() {
        return "Color: " + this.color + " Type: " + this.type;
    }
    
    public String getColor() {
        String str;
        for (str = Integer.toHexString(this.color); str.length() < 6; str = "0" + str) {}
        return str;
    }
    
    static {
        ModelPartData.resources = new HashMap<String, ResourceLocation>();
    }
}
