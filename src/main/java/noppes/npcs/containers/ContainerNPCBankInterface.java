package noppes.npcs.containers;

import noppes.npcs.NoppesUtilServer;
import net.minecraft.item.ItemStack;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.NpcMiscInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Container;
import noppes.npcs.controllers.data.PlayerBankData;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerNPCBankInterface extends ContainerNpcInterface
{
    public InventoryNPC currencyMatrix;
    private EntityPlayer player;
    public SlotNpcBankCurrency currency;
    public int slot;
    public int bankid;
    private PlayerBankData data;
    
    public ContainerNPCBankInterface(EntityPlayer player, int slot, int bankid) {
        super(player);
        this.slot = 0;
        this.bankid = bankid;
        this.slot = slot;
        this.player = player;
        this.currencyMatrix = new InventoryNPC("currency", 1, this);
        if (!this.isAvailable() || this.canBeUpgraded()) {
            this.addSlotToContainer((Slot)(this.currency = new SlotNpcBankCurrency(this, (IInventory)this.currencyMatrix, 0, 80, 29)));
        }
        NpcMiscInventory items = new NpcMiscInventory(54);
        if (!player.world.isRemote) {
            this.data = PlayerDataController.instance.getBankData(player, bankid);
            items = this.data.getBankOrDefault(bankid).itemSlots.get(slot);
        }
        int xOffset = this.xOffset();
        for (int j = 0; j < this.getRowNumber(); ++j) {
            for (int i1 = 0; i1 < 9; ++i1) {
                int id = i1 + j * 9;
                this.addSlotToContainer(new Slot((IInventory)items, id, 8 + i1 * 18, 17 + xOffset + j * 18));
            }
        }
        if (this.isUpgraded()) {
            xOffset += 54;
        }
        for (int k = 0; k < 3; ++k) {
            for (int j2 = 0; j2 < 9; ++j2) {
                this.addSlotToContainer(new Slot((IInventory)player.inventory, j2 + k * 9 + 9, 8 + j2 * 18, 86 + xOffset + k * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot((IInventory)player.inventory, l, 8 + l * 18, 144 + xOffset));
        }
    }
    
    public synchronized void setCurrency(ItemStack item) {
        this.currency.item = item;
    }
    
    public int getRowNumber() {
        return 0;
    }
    
    public int xOffset() {
        return 0;
    }
    
    public void onCraftMatrixChanged(IInventory inv) {
    }
    
    public boolean isAvailable() {
        return false;
    }
    
    public boolean isUpgraded() {
        return false;
    }
    
    public boolean canBeUpgraded() {
        return false;
    }
    
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i) {
        return ItemStack.EMPTY;
    }
    
    public void onContainerClosed(EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        if (!entityplayer.world.isRemote) {
            ItemStack var3 = this.currencyMatrix.getStackInSlot(0);
            this.currencyMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
            if (!NoppesUtilServer.IsItemStackNull(var3)) {
                entityplayer.dropItem(var3, false);
            }
        }
    }
}
