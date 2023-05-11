package noppes.npcs.api.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IEntityItem;
import net.minecraft.entity.item.EntityItem;

public class EntityItemWrapper<T extends EntityItem> extends EntityWrapper<T> implements IEntityItem
{
    public EntityItemWrapper(T entity) {
        super(entity);
    }
    
    @Override
    public String getOwner() {
        return this.entity.getOwner();
    }
    
    @Override
    public void setOwner(String name) {
        this.entity.setOwner(name);
    }
    
    @Override
    public int getPickupDelay() {
    	NBTTagCompound compound = new NBTTagCompound();
		this.entity.writeEntityToNBT(compound);
        return (int) compound.getShort("PickupDelay");
    }
    
    @Override
    public void setPickupDelay(int delay) {
        this.entity.setPickupDelay(delay);
    }
    
    @Override
    public int getType() {
        return 6;
    }
    
    @Override
    public long getAge() {
        return this.entity.getAge();
    }
    
    @Override
    public void setAge(long age) {
        age = Math.max(Math.min(age, 2147483647L), -2147483648L);
        NBTTagCompound compound = new NBTTagCompound();
		this.entity.writeEntityToNBT(compound);
		compound.setShort("PickupDelay", (short)age);
		this.entity.readEntityFromNBT(compound);
    }
    
    @Override
    public int getLifeSpawn() {
        return this.entity.lifespan;
    }
    
    @Override
    public void setLifeSpawn(int age) {
        this.entity.lifespan = age;
    }
    
    @Override
    public IItemStack getItem() {
        return NpcAPI.Instance().getIItemStack(this.entity.getItem());
    }
    
    @Override
    public void setItem(IItemStack item) {
        ItemStack stack = (item == null) ? ItemStack.EMPTY : item.getMCItemStack();
        this.entity.setItem(stack);
    }
}
