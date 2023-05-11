package noppes.npcs.roles;

import noppes.npcs.CustomNpcs;
import java.io.File;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import java.util.Iterator;
import noppes.npcs.NoppesUtilPlayer;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.entity.data.role.IRoleTrader;

public class RoleTrader extends RoleInterface implements IRoleTrader
{
    public String marketName;
    public NpcMiscInventory inventoryCurrency;
    public NpcMiscInventory inventorySold;
    public boolean ignoreDamage;
    public boolean ignoreNBT;
    public boolean toSave;
    
    public RoleTrader(EntityNPCInterface npc) {
        super(npc);
        this.marketName = "";
        this.ignoreDamage = false;
        this.ignoreNBT = false;
        this.toSave = false;
        this.inventoryCurrency = new NpcMiscInventory(36);
        this.inventorySold = new NpcMiscInventory(18);
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("TraderMarket", this.marketName);
        this.writeNBT(nbttagcompound);
        if (this.toSave && !this.npc.isRemote()) {
            save(this, this.marketName);
        }
        this.toSave = false;
        return nbttagcompound;
    }
    
    public NBTTagCompound writeNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setTag("TraderCurrency", (NBTBase)this.inventoryCurrency.getToNBT());
        nbttagcompound.setTag("TraderSold", (NBTBase)this.inventorySold.getToNBT());
        nbttagcompound.setBoolean("TraderIgnoreDamage", this.ignoreDamage);
        nbttagcompound.setBoolean("TraderIgnoreNBT", this.ignoreNBT);
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.marketName = nbttagcompound.getString("TraderMarket");
        this.readNBT(nbttagcompound);
    }
    
    public void readNBT(NBTTagCompound nbttagcompound) {
        this.inventoryCurrency.setFromNBT(nbttagcompound.getCompoundTag("TraderCurrency"));
        this.inventorySold.setFromNBT(nbttagcompound.getCompoundTag("TraderSold"));
        this.ignoreDamage = nbttagcompound.getBoolean("TraderIgnoreDamage");
        this.ignoreNBT = nbttagcompound.getBoolean("TraderIgnoreNBT");
    }
    
    @Override
    public void interact(EntityPlayer player) {
        this.npc.say(player, this.npc.advanced.getInteractLine());
        try {
            load(this, this.marketName);
        }
        catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerTrader, this.npc);
    }
    
    public boolean hasCurrency(ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        for (ItemStack item : this.inventoryCurrency.items) {
            if (!item.isEmpty() && NoppesUtilPlayer.compareItems(item, itemstack, this.ignoreDamage, this.ignoreNBT)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public IItemStack getSold(int slot) {
        return NpcAPI.Instance().getIItemStack(this.inventorySold.getStackInSlot(slot));
    }
    
    @Override
    public IItemStack getCurrency1(int slot) {
        return NpcAPI.Instance().getIItemStack(this.inventoryCurrency.getStackInSlot(slot));
    }
    
    @Override
    public IItemStack getCurrency2(int slot) {
        return NpcAPI.Instance().getIItemStack(this.inventoryCurrency.getStackInSlot(slot + 18));
    }
    
    @Override
    public void set(int slot, IItemStack currency, IItemStack currency2, IItemStack sold) {
        if (sold == null) {
            throw new CustomNPCsException("Sold item was null", new Object[0]);
        }
        if (slot >= 18 || slot < 0) {
            throw new CustomNPCsException("Invalid slot: " + slot, new Object[0]);
        }
        if (currency == null) {
            currency = currency2;
            currency2 = null;
        }
        if (currency != null) {
            this.inventoryCurrency.items.set(slot, currency.getMCItemStack());
        }
        else {
            this.inventoryCurrency.items.set(slot, ItemStack.EMPTY);
        }
        if (currency2 != null) {
            this.inventoryCurrency.items.set(slot + 18, currency2.getMCItemStack());
        }
        else {
            this.inventoryCurrency.items.set(slot + 18, ItemStack.EMPTY);
        }
        this.inventorySold.items.set(slot, sold.getMCItemStack());
    }
    
    @Override
    public void remove(int slot) {
        if (slot >= 18 || slot < 0) {
            throw new CustomNPCsException("Invalid slot: " + slot, new Object[0]);
        }
        this.inventoryCurrency.items.set(slot, ItemStack.EMPTY);
        this.inventoryCurrency.items.set(slot + 18, ItemStack.EMPTY);
        this.inventorySold.items.set(slot, ItemStack.EMPTY);
    }
    
    @Override
    public void setMarket(String name) {
        load(this, this.marketName = name);
    }
    
    @Override
    public String getMarket() {
        return this.marketName;
    }
    
    public static void save(RoleTrader r, String name) {
        if (name.isEmpty()) {
            return;
        }
        File file = getFile(name + "_new");
        File file2 = getFile(name);
        try {
            NBTJsonUtil.SaveFile(file, r.writeNBT(new NBTTagCompound()));
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
        }
        catch (Exception ex) {}
    }
    
    public static void load(RoleTrader role, String name) {
        if (role.npc.world.isRemote) {
            return;
        }
        File file = getFile(name);
        if (!file.exists()) {
            return;
        }
        try {
            role.readNBT(NBTJsonUtil.LoadFile(file));
        }
        catch (Exception ex) {}
    }
    
    private static File getFile(String name) {
        File dir = new File(CustomNpcs.getWorldSaveDirectory(), "markets");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, name.toLowerCase() + ".json");
    }
    
    public static void setMarket(EntityNPCInterface npc, String marketName) {
        if (marketName.isEmpty()) {
            return;
        }
        if (!getFile(marketName).exists()) {
            save((RoleTrader)npc.roleInterface, marketName);
        }
        load((RoleTrader)npc.roleInterface, marketName);
    }
}
