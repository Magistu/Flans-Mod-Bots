package noppes.npcs.entity.data;

import noppes.npcs.NoppesUtilServer;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.ForgeHooks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import noppes.npcs.api.event.NpcEvent;
import java.util.Iterator;
import java.util.ArrayList;
import noppes.npcs.util.ValueUtil;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.CustomNPCsException;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.NBTTags;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.api.item.IItemStack;
import java.util.Map;
import noppes.npcs.api.entity.data.INPCInventory;
import net.minecraft.inventory.IInventory;

public class DataInventory implements IInventory, INPCInventory
{
    public Map<Integer, IItemStack> drops;
    public Map<Integer, Integer> dropchance;
    public Map<Integer, IItemStack> weapons;
    public Map<Integer, IItemStack> armor;
    private int minExp;
    private int maxExp;
    public int lootMode;
    private EntityNPCInterface npc;
    
    public DataInventory(EntityNPCInterface npc) {
        this.drops = new HashMap<Integer, IItemStack>();
        this.dropchance = new HashMap<Integer, Integer>();
        this.weapons = new HashMap<Integer, IItemStack>();
        this.armor = new HashMap<Integer, IItemStack>();
        this.minExp = 0;
        this.maxExp = 0;
        this.lootMode = 0;
        this.npc = npc;
    }
    
    public NBTTagCompound writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("MinExp", this.minExp);
        nbttagcompound.setInteger("MaxExp", this.maxExp);
        nbttagcompound.setTag("NpcInv", (NBTBase)NBTTags.nbtIItemStackMap(this.drops));
        nbttagcompound.setTag("Armor", (NBTBase)NBTTags.nbtIItemStackMap(this.armor));
        nbttagcompound.setTag("Weapons", (NBTBase)NBTTags.nbtIItemStackMap(this.weapons));
        nbttagcompound.setTag("DropChance", (NBTBase)NBTTags.nbtIntegerIntegerMap(this.dropchance));
        nbttagcompound.setInteger("LootMode", this.lootMode);
        return nbttagcompound;
    }
    
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        this.minExp = nbttagcompound.getInteger("MinExp");
        this.maxExp = nbttagcompound.getInteger("MaxExp");
        this.drops = NBTTags.getIItemStackMap(nbttagcompound.getTagList("NpcInv", 10));
        this.armor = NBTTags.getIItemStackMap(nbttagcompound.getTagList("Armor", 10));
        this.weapons = NBTTags.getIItemStackMap(nbttagcompound.getTagList("Weapons", 10));
        this.dropchance = NBTTags.getIntegerIntegerMap(nbttagcompound.getTagList("DropChance", 10));
        this.lootMode = nbttagcompound.getInteger("LootMode");
    }
    
    public IItemStack getArmor(int slot) {
        return this.armor.get(slot);
    }
    
    public void setArmor(int slot, IItemStack item) {
        this.armor.put(slot, item);
        this.npc.updateClient = true;
    }
    
    public IItemStack getRightHand() {
        return this.weapons.get(0);
    }
    
    public void setRightHand(IItemStack item) {
        this.weapons.put(0, item);
        this.npc.updateClient = true;
    }
    
    public IItemStack getProjectile() {
        return this.weapons.get(1);
    }
    
    public void setProjectile(IItemStack item) {
        this.weapons.put(1, item);
        this.npc.updateAI = true;
    }
    
    public IItemStack getLeftHand() {
        return this.weapons.get(2);
    }
    
    public void setLeftHand(IItemStack item) {
        this.weapons.put(2, item);
        this.npc.updateClient = true;
    }
    
    public IItemStack getDropItem(int slot) {
        if (slot < 0 || slot > 8) {
            throw new CustomNPCsException("Bad slot number: " + slot, new Object[0]);
        }
        IItemStack item = this.npc.inventory.drops.get(slot);
        if (item == null) {
            return null;
        }
        return NpcAPI.Instance().getIItemStack(item.getMCItemStack());
    }
    
    public void setDropItem(int slot, IItemStack item, int chance) {
        if (slot < 0 || slot > 8) {
            throw new CustomNPCsException("Bad slot number: " + slot, new Object[0]);
        }
        chance = ValueUtil.CorrectInt(chance, 1, 100);
        if (item == null || item.isEmpty()) {
            this.dropchance.remove(slot);
            this.drops.remove(slot);
        }
        else {
            this.dropchance.put(slot, chance);
            this.drops.put(slot, item);
        }
    }
    
    public IItemStack[] getItemsRNG() {
        ArrayList<IItemStack> list = new ArrayList<IItemStack>();
        for (int i : this.drops.keySet()) {
            IItemStack item = this.drops.get(i);
            if (item != null) {
                if (item.isEmpty()) {
                    continue;
                }
                int dchance = 100;
                if (this.dropchance.containsKey(i)) {
                    dchance = this.dropchance.get(i);
                }
                int chance = this.npc.world.rand.nextInt(100) + dchance;
                if (chance < 100) {
                    continue;
                }
                list.add(item);
            }
        }
        return list.toArray(new IItemStack[list.size()]);
    }
    
    public void dropStuff(NpcEvent.DiedEvent event, Entity entity, DamageSource damagesource) {
        ArrayList<EntityItem> list = new ArrayList<EntityItem>();
        if (event.droppedItems != null) {
            for (IItemStack item : event.droppedItems) {
                EntityItem e = this.getEntityItem(item.getMCItemStack().copy());
                if (e != null) {
                    list.add(e);
                }
            }
        }
        int enchant = 0;
        if (damagesource.getTrueSource() instanceof EntityPlayer) {
            enchant = EnchantmentHelper.getLootingModifier((EntityLivingBase)damagesource.getTrueSource());
        }
        if (!ForgeHooks.onLivingDrops((EntityLivingBase)this.npc, damagesource, (ArrayList)list, enchant, true)) {
            for (EntityItem item2 : list) {
                if (this.lootMode == 1 && entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer)entity;
                    item2.setPickupDelay(2);
                    this.npc.world.spawnEntity((Entity)item2);
                    ItemStack stack = item2.getItem();
                    int i = stack.getCount();
                    if (!player.inventory.addItemStackToInventory(stack)) {
                        continue;
                    }
                    entity.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    player.onItemPickup((Entity)item2, i);
                    if (stack.getCount() > 0) {
                        continue;
                    }
                    item2.setDead();
                }
                else {
                    this.npc.world.spawnEntity((Entity)item2);
                }
            }
        }
        int exp = event.expDropped;
        while (exp > 0) {
            int var2 = EntityXPOrb.getXPSplit(exp);
            exp -= var2;
            if (this.lootMode == 1 && entity instanceof EntityPlayer) {
                this.npc.world.spawnEntity((Entity)new EntityXPOrb(entity.world, entity.posX, entity.posY, entity.posZ, var2));
            }
            else {
                this.npc.world.spawnEntity((Entity)new EntityXPOrb(this.npc.world, this.npc.posX, this.npc.posY, this.npc.posZ, var2));
            }
        }
    }
    
    public EntityItem getEntityItem(ItemStack itemstack) {
        if (itemstack == null || itemstack.isEmpty()) {
            return null;
        }
        EntityItem entityitem = new EntityItem(this.npc.world, this.npc.posX, this.npc.posY - 0.30000001192092896 + this.npc.getEyeHeight(), this.npc.posZ, itemstack);
        entityitem.setPickupDelay(40);
        float f2 = this.npc.getRNG().nextFloat() * 0.5f;
        float f3 = this.npc.getRNG().nextFloat() * 3.141593f * 2.0f;
        entityitem.motionX = -MathHelper.sin(f3) * f2;
        entityitem.motionZ = MathHelper.cos(f3) * f2;
        entityitem.motionY = 0.20000000298023224;
        return entityitem;
    }
    
    public int getSizeInventory() {
        return 15;
    }
    
    public ItemStack getStackInSlot(int i) {
        if (i < 4) {
            return ItemStackWrapper.MCItem(this.getArmor(i));
        }
        if (i < 7) {
            return ItemStackWrapper.MCItem(this.weapons.get(i - 4));
        }
        return ItemStackWrapper.MCItem(this.drops.get(i - 7));
    }
    
    public ItemStack decrStackSize(int par1, int par2) {
        int i = 0;
        Map<Integer, IItemStack> var3;
        if (par1 >= 7) {
            var3 = this.drops;
            par1 -= 7;
        }
        else if (par1 >= 4) {
            var3 = this.weapons;
            par1 -= 4;
            i = 1;
        }
        else {
            var3 = this.armor;
            i = 2;
        }
        ItemStack var4 = null;
        if (var3.get(par1) != null) {
            if (var3.get(par1).getMCItemStack().getCount() <= par2) {
                var4 = var3.get(par1).getMCItemStack();
                var3.put(par1, null);
            }
            else {
                var4 = var3.get(par1).getMCItemStack().splitStack(par2);
                if (var3.get(par1).getMCItemStack().getCount() == 0) {
                    var3.put(par1, null);
                }
            }
        }
        if (i == 1) {
            this.weapons = var3;
        }
        if (i == 2) {
            this.armor = var3;
        }
        if (var4 == null) {
            return ItemStack.EMPTY;
        }
        return var4;
    }
    
    public ItemStack removeStackFromSlot(int par1) {
        int i = 0;
        Map<Integer, IItemStack> var2;
        if (par1 >= 7) {
            var2 = this.drops;
            par1 -= 7;
        }
        else if (par1 >= 4) {
            var2 = this.weapons;
            par1 -= 4;
            i = 1;
        }
        else {
            var2 = this.armor;
            i = 2;
        }
        if (var2.get(par1) != null) {
            ItemStack var3 = var2.get(par1).getMCItemStack();
            var2.put(par1, null);
            if (i == 1) {
                this.weapons = var2;
            }
            if (i == 2) {
                this.armor = var2;
            }
            return var3;
        }
        return ItemStack.EMPTY;
    }
    
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        int i = 0;
        Map<Integer, IItemStack> var3;
        if (par1 >= 7) {
            var3 = this.drops;
            par1 -= 7;
        }
        else if (par1 >= 4) {
            var3 = this.weapons;
            par1 -= 4;
            i = 1;
        }
        else {
            var3 = this.armor;
            i = 2;
        }
        var3.put(par1, NpcAPI.Instance().getIItemStack(par2ItemStack));
        if (i == 1) {
            this.weapons = var3;
        }
        if (i == 2) {
            this.armor = var3;
        }
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return true;
    }
    
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }
    
    public String getName() {
        return "NPC Inventory";
    }
    
    public void markDirty() {
    }
    
    public boolean hasCustomName() {
        return true;
    }
    
    public ITextComponent getDisplayName() {
        return null;
    }
    
    public void openInventory(EntityPlayer player) {
    }
    
    public void closeInventory(EntityPlayer player) {
    }
    
    public int getField(int id) {
        return 0;
    }
    
    public void setField(int id, int value) {
    }
    
    public int getFieldCount() {
        return 0;
    }
    
    public void clear() {
    }
    
    public int getExpMin() {
        return this.npc.inventory.minExp;
    }
    
    public int getExpMax() {
        return this.npc.inventory.maxExp;
    }
    
    public int getExpRNG() {
        int exp = this.minExp;
        if (this.maxExp - this.minExp > 0) {
            exp += this.npc.world.rand.nextInt(this.maxExp - this.minExp);
        }
        return exp;
    }
    
    public void setExp(int min, int max) {
        min = Math.min(min, max);
        this.npc.inventory.minExp = min;
        this.npc.inventory.maxExp = max;
    }
    
    public boolean isEmpty() {
        for (int slot = 0; slot < this.getSizeInventory(); ++slot) {
            ItemStack item = this.getStackInSlot(slot);
            if (!NoppesUtilServer.IsItemStackNull(item) && !item.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
