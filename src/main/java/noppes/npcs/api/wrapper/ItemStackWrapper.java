package noppes.npcs.api.wrapper;

import noppes.npcs.ItemStackEmptyWrapper;
import noppes.npcs.NoppesUtilPlayer;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.init.Items;
import noppes.npcs.items.ItemScripted;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.IPlantable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import com.google.common.collect.HashMultimap;
import net.minecraft.item.ItemFood;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.INbt;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import java.util.Iterator;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.CustomNPCsException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTPrimitive;
import java.util.HashMap;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.entity.data.IData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.Capability;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import noppes.npcs.api.item.IItemStack;

public class ItemStackWrapper implements IItemStack, ICapabilityProvider, ICapabilitySerializable
{
    private Map<String, Object> tempData;
    @CapabilityInject(ItemStackWrapper.class)
    public static Capability<ItemStackWrapper> ITEMSCRIPTEDDATA_CAPABILITY;
    private static EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS;
    public ItemStack item;
    private NBTTagCompound storedData;
    public static ItemStackWrapper AIR;
    private IData tempdata;
    private IData storeddata;
    private static ResourceLocation key;
    
    protected ItemStackWrapper(ItemStack item) {
        this.tempData = new HashMap<String, Object>();
        this.storedData = new NBTTagCompound();
        this.tempdata = new IData() {
            @Override
            public void put(String key, Object value) {
                ItemStackWrapper.this.tempData.put(key, value);
            }
            
            @Override
            public Object get(String key) {
                return ItemStackWrapper.this.tempData.get(key);
            }
            
            @Override
            public void remove(String key) {
                ItemStackWrapper.this.tempData.remove(key);
            }
            
            @Override
            public boolean has(String key) {
                return ItemStackWrapper.this.tempData.containsKey(key);
            }
            
            @Override
            public void clear() {
                ItemStackWrapper.this.tempData.clear();
            }
            
            @Override
            public String[] getKeys() {
                return (String[])ItemStackWrapper.this.tempData.keySet().toArray(new String[ItemStackWrapper.this.tempData.size()]);
            }
        };
        this.storeddata = new IData() {
            @Override
            public void put(String key, Object value) {
                if (value instanceof Number) {
                    ItemStackWrapper.this.storedData.setDouble(key, ((Number)value).doubleValue());
                }
                else if (value instanceof String) {
                    ItemStackWrapper.this.storedData.setString(key, (String)value);
                }
            }
            
            @Override
            public Object get(String key) {
                if (!ItemStackWrapper.this.storedData.hasKey(key)) {
                    return null;
                }
                NBTBase base = ItemStackWrapper.this.storedData.getTag(key);
                if (base instanceof NBTPrimitive) {
                    return ((NBTPrimitive)base).getDouble();
                }
                return ((NBTTagString)base).getString();
            }
            
            @Override
            public void remove(String key) {
                ItemStackWrapper.this.storedData.removeTag(key);
            }
            
            @Override
            public boolean has(String key) {
                return ItemStackWrapper.this.storedData.hasKey(key);
            }
            
            @Override
            public void clear() {
                ItemStackWrapper.this.storedData = new NBTTagCompound();
            }
            
            @Override
            public String[] getKeys() {
                return ItemStackWrapper.this.storedData.getKeySet().toArray(new String[ItemStackWrapper.this.storedData.getKeySet().size()]);
            }
        };
        this.item = item;
    }
    
    @Override
    public IData getTempdata() {
        return this.tempdata;
    }
    
    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }
    
    @Override
    public int getStackSize() {
        return this.item.getCount();
    }
    
    @Override
    public void setStackSize(int size) {
        if (size > this.getMaxStackSize()) {
            throw new CustomNPCsException("Can't set the stacksize bigger than MaxStacksize", new Object[0]);
        }
        this.item.setCount(size);
    }
    
    @Override
    public void setAttribute(String name, double value) {
        this.setAttribute(name, value, -1);
    }
    
    @Override
    public void setAttribute(String name, double value, int slot) {
        if (slot < -1 || slot > 5) {
            throw new CustomNPCsException("Slot has to be between -1 and 5, given was: " + slot, new Object[0]);
        }
        NBTTagCompound compound = this.item.getTagCompound();
        if (compound == null) {
            this.item.setTagCompound(compound = new NBTTagCompound());
        }
        NBTTagList nbttaglist = compound.getTagList("AttributeModifiers", 10);
        NBTTagList newList = new NBTTagList();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound c = nbttaglist.getCompoundTagAt(i);
            if (!c.getString("AttributeName").equals(name)) {
                newList.appendTag((NBTBase)c);
            }
        }
        if (value != 0.0) {
            NBTTagCompound nbttagcompound = SharedMonsterAttributes.writeAttributeModifierToNBT(new AttributeModifier(name, value, 0));
            nbttagcompound.setString("AttributeName", name);
            if (slot >= 0) {
                nbttagcompound.setString("Slot", EntityEquipmentSlot.values()[slot].getName());
            }
            newList.appendTag((NBTBase)nbttagcompound);
        }
        compound.setTag("AttributeModifiers", (NBTBase)newList);
    }
    
    @Override
    public double getAttribute(String name) {
        NBTTagCompound compound = this.item.getTagCompound();
        if (compound == null) {
            return 0.0;
        }
        Multimap<String, AttributeModifier> map = (Multimap<String, AttributeModifier>)this.item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        for (Map.Entry<String, AttributeModifier> entry : map.entries()) {
            if (entry.getKey().equals(name)) {
                AttributeModifier mod = entry.getValue();
                return mod.getAmount();
            }
        }
        return 0.0;
    }
    
    @Override
    public boolean hasAttribute(String name) {
        NBTTagCompound compound = this.item.getTagCompound();
        if (compound == null) {
            return false;
        }
        NBTTagList nbttaglist = compound.getTagList("AttributeModifiers", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound c = nbttaglist.getCompoundTagAt(i);
            if (c.getString("AttributeName").equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getItemDamage() {
        return this.item.getItemDamage();
    }
    
    @Override
    public void setItemDamage(int value) {
        this.item.setItemDamage(value);
    }
    
    @Override
    public void addEnchantment(String id, int strenght) {
        Enchantment ench = Enchantment.getEnchantmentByLocation(id);
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id, new Object[0]);
        }
        this.item.addEnchantment(ench, strenght);
    }
    
    @Override
    public boolean isEnchanted() {
        return this.item.isItemEnchanted();
    }
    
    @Override
    public boolean hasEnchant(String id) {
        Enchantment ench = Enchantment.getEnchantmentByLocation(id);
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id, new Object[0]);
        }
        if (!this.isEnchanted()) {
            return false;
        }
        int enchId = Enchantment.getEnchantmentID(ench);
        NBTTagList list = this.item.getEnchantmentTagList();
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            if (compound.getShort("id") == enchId) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean removeEnchant(String id) {
        Enchantment ench = Enchantment.getEnchantmentByLocation(id);
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id, new Object[0]);
        }
        if (!this.isEnchanted()) {
            return false;
        }
        int enchId = Enchantment.getEnchantmentID(ench);
        NBTTagList list = this.item.getEnchantmentTagList();
        NBTTagList newList = new NBTTagList();
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            if (compound.getShort("id") != enchId) {
                newList.appendTag((NBTBase)compound);
            }
        }
        if (list.tagCount() == newList.tagCount()) {
            return false;
        }
        this.item.getTagCompound().setTag("ench", (NBTBase)newList);
        return true;
    }
    
    @Override
    public boolean isBlock() {
        Block block = Block.getBlockFromItem(this.item.getItem());
        return block != null && block != Blocks.AIR;
    }
    
    @Override
    public boolean hasCustomName() {
        return this.item.hasDisplayName();
    }
    
    @Override
    public void setCustomName(String name) {
        this.item.setStackDisplayName(name);
    }
    
    @Override
    public String getDisplayName() {
        return this.item.getDisplayName();
    }
    
    @Override
    public String getItemName() {
        return this.item.getItem().getItemStackDisplayName(this.item);
    }
    
    @Override
    public String getName() {
        return Item.REGISTRY.getNameForObject(this.item.getItem()) + "";
    }
    
    @Override
    public INbt getNbt() {
        NBTTagCompound compound = this.item.getTagCompound();
        if (compound == null) {
            this.item.setTagCompound(compound = new NBTTagCompound());
        }
        return NpcAPI.Instance().getINbt(compound);
    }
    
    @Override
    public boolean hasNbt() {
        NBTTagCompound compound = this.item.getTagCompound();
        return compound != null && compound.getKeySet().size()!=0;
    }
    
    @Override
    public ItemStack getMCItemStack() {
        return this.item;
    }
    
    public static ItemStack MCItem(IItemStack item) {
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return item.getMCItemStack();
    }
    
    @Override
    public void damageItem(int damage, IEntityLiving living) {
        this.item.damageItem(damage, (EntityLivingBase)((living == null) ? null : living.getMCEntity()));
    }
    
    @Override
    public boolean isBook() {
        return false;
    }
    
    @Override
    public int getFoodLevel() {
        if (this.item.getItem() instanceof ItemFood) {
            return ((ItemFood)this.item.getItem()).getHealAmount(this.item);
        }
        return 0;
    }
    
    @Override
    public IItemStack copy() {
        return createNew(this.item.copy());
    }
    
    @Override
    public int getMaxStackSize() {
        return this.item.getMaxStackSize();
    }
    
    @Override
    public int getMaxItemDamage() {
        return this.item.getMaxDamage();
    }
    
    @Override
    public INbt getItemNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        this.item.writeToNBT(compound);
        return NpcAPI.Instance().getINbt(compound);
    }
    
    @Override
    public double getAttackDamage() {
        HashMultimap map = (HashMultimap)this.item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
        Iterator iterator = map.entries().iterator();
        double damage = 0.0;
        while (iterator.hasNext()) {
            Map.Entry entry = (Entry) iterator.next();
            if (entry.getKey().equals(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
                AttributeModifier mod = (AttributeModifier) entry.getValue();
                damage = mod.getAmount();
            }
        }
        damage += EnchantmentHelper.getModifierForCreature(this.item, EnumCreatureAttribute.UNDEFINED);
        return damage;
    }
    
    @Override
    public boolean isEmpty() {
        return this.item.isEmpty();
    }
    
    @Override
    public int getType() {
        if (this.item.getItem() instanceof IPlantable) {
            return 5;
        }
        if (this.item.getItem() instanceof ItemSword) {
            return 4;
        }
        return 0;
    }
    
    @Override
    public boolean isWearable() {
        for (EntityEquipmentSlot slot : ItemStackWrapper.VALID_EQUIPMENT_SLOTS) {
            if (this.item.getItem().isValidArmor(this.item, slot, (Entity)EntityNPCInterface.CommandPlayer)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY;
    }
    
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (this.hasCapability(capability, facing)) {
            return (T)this;
        }
        return null;
    }
    
    public static void register(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStackWrapper wrapper = createNew((ItemStack)event.getObject());
        event.addCapability(ItemStackWrapper.key, (ICapabilityProvider)wrapper);
    }
    
    private static ItemStackWrapper createNew(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return ItemStackWrapper.AIR;
        }
        if (item.getItem() instanceof ItemScripted) {
            return new ItemScriptedWrapper(item);
        }
        if (item.getItem() == Items.WRITTEN_BOOK || item.getItem() == Items.WRITABLE_BOOK || item.getItem() instanceof ItemWritableBook || item.getItem() instanceof ItemWrittenBook) {
            return new ItemBookWrapper(item);
        }
        if (item.getItem() instanceof ItemArmor) {
            return new ItemArmorWrapper(item);
        }
        Block block = Block.getBlockFromItem(item.getItem());
        if (block != Blocks.AIR) {
            return new ItemBlockWrapper(item);
        }
        return new ItemStackWrapper(item);
    }
    
    @Override
    public String[] getLore() {
        NBTTagCompound compound = this.item.getSubCompound("display");
        if (compound == null || compound.getTagId("Lore") != 9) {
            return new String[0];
        }
        NBTTagList nbttaglist = compound.getTagList("Lore", 8);
        if (nbttaglist.tagCount()==0) {
            return new String[0];
        }
        List<String> lore = new ArrayList<String>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            lore.add(nbttaglist.getStringTagAt(i));
        }
        return lore.toArray(new String[lore.size()]);
    }
    
    @Override
    public void setLore(String[] lore) {
        NBTTagCompound compound = this.item.getOrCreateSubCompound("display");
        if (lore == null || lore.length == 0) {
            compound.removeTag("Lore");
            return;
        }
        NBTTagList nbtlist = new NBTTagList();
        for (String s : lore) {
            nbtlist.appendTag((NBTBase)new NBTTagString(s));
        }
        compound.setTag("Lore", (NBTBase)nbtlist);
    }
    
    public NBTBase serializeNBT() {
        return (NBTBase)this.getMCNbt();
    }
    
    public void deserializeNBT(NBTBase nbt) {
        this.setMCNbt((NBTTagCompound)nbt);
    }
    
    public NBTTagCompound getMCNbt() {
        NBTTagCompound compound = new NBTTagCompound();
        if (this.storedData.getKeySet().size()!=0) {
            compound.setTag("StoredData", (NBTBase)this.storedData);
        }
        return compound;
    }
    
    public void setMCNbt(NBTTagCompound compound) {
        this.storedData = compound.getCompoundTag("StoredData");
    }
    
    @Override
    public void removeNbt() {
        this.item.setTagCompound((NBTTagCompound)null);
    }
    
    @Override
    public boolean compare(IItemStack item, boolean ignoreNBT) {
        if (item == null) {
            item = ItemStackWrapper.AIR;
        }
        return NoppesUtilPlayer.compareItems(this.getMCItemStack(), item.getMCItemStack(), false, ignoreNBT);
    }
    
    static {
        ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY = null;
        VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
        ItemStackWrapper.AIR = new ItemStackEmptyWrapper();
        key = new ResourceLocation("customnpcs", "itemscripteddata");
    }
}
