package noppes.npcs.roles;

import noppes.npcs.constants.AiMutex;
import noppes.npcs.controllers.data.BlockData;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.BlockStem;
import net.minecraft.block.NpcBlockHelper;
import net.minecraft.block.BlockCrops;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NoppesUtilPlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.block.BlockChest;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import noppes.npcs.api.entity.data.role.IJobFarmer;
import noppes.npcs.controllers.MassBlockController;

public class JobFarmer extends JobInterface implements MassBlockController.IMassBlock, IJobFarmer
{
    public int chestMode;
    private List<BlockPos> trackedBlocks;
    private int ticks;
    private int walkTicks;
    private int blockTicks;
    private boolean waitingForBlocks;
    private BlockPos ripe;
    private BlockPos chest;
    private ItemStack holding;
    
    public JobFarmer(EntityNPCInterface npc) {
        super(npc);
        this.chestMode = 1;
        this.trackedBlocks = new ArrayList<BlockPos>();
        this.ticks = 0;
        this.walkTicks = 0;
        this.blockTicks = 800;
        this.waitingForBlocks = false;
        this.ripe = null;
        this.chest = null;
        this.holding = ItemStack.EMPTY;
        this.overrideMainHand = true;
    }
    
    @Override
    public IItemStack getMainhand() {
        String name = this.npc.getJobData();
        ItemStack item = this.stringToItem(name);
        if (item.isEmpty()) {
            return this.npc.inventory.weapons.get(0);
        }
        return NpcAPI.Instance().getIItemStack(item);
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("JobChestMode", this.chestMode);
        if (!this.holding.isEmpty()) {
            compound.setTag("JobHolding", (NBTBase)this.holding.writeToNBT(new NBTTagCompound()));
        }
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.chestMode = compound.getInteger("JobChestMode");
        this.holding = new ItemStack(compound.getCompoundTag("JobHolding"));
        this.blockTicks = 1100;
    }
    
    public void setHolding(ItemStack item) {
        this.holding = item;
        this.npc.setJobData(this.itemToString(this.holding));
    }
    
    @Override
    public boolean aiShouldExecute() {
        if (!this.holding.isEmpty()) {
            if (this.chestMode == 0) {
                this.setHolding(ItemStack.EMPTY);
            }
            else if (this.chestMode == 1) {
                if (this.chest == null) {
                    this.dropItem(this.holding);
                    this.setHolding(ItemStack.EMPTY);
                }
                else {
                    this.chest();
                }
            }
            else if (this.chestMode == 2) {
                this.dropItem(this.holding);
                this.setHolding(ItemStack.EMPTY);
            }
            return false;
        }
        if (this.ripe != null) {
            this.pluck();
            return false;
        }
        if (!this.waitingForBlocks && this.blockTicks++ > 1200) {
            this.blockTicks = 0;
            this.waitingForBlocks = true;
            MassBlockController.Queue(this);
        }
        if (this.ticks++ < 100) {
            return false;
        }
        this.ticks = 0;
        return true;
    }
    
    private void dropItem(ItemStack item) {
        EntityItem entityitem = new EntityItem(this.npc.world, this.npc.posX, this.npc.posY, this.npc.posZ, item);
        entityitem.setDefaultPickupDelay();
        this.npc.world.spawnEntity((Entity)entityitem);
    }
    
    private void chest() {
        BlockPos pos = this.chest;
        this.npc.getNavigator().tryMoveToXYZ((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 1.0);
        this.npc.getLookHelper().setLookPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 10.0f, (float)this.npc.getVerticalFaceSpeed());
        if (this.npc.nearPosition(pos) || this.walkTicks++ > 400) {
            if (this.walkTicks < 400) {
                this.npc.swingArm(EnumHand.MAIN_HAND);
            }
            this.npc.getNavigator().clearPath();
            this.ticks = 100;
            this.walkTicks = 0;
            IBlockState state = this.npc.world.getBlockState(pos);
            if (state.getBlock() instanceof BlockChest) {
                TileEntityChest tile = (TileEntityChest)this.npc.world.getTileEntity(pos);
                for (int i = 0; !this.holding.isEmpty() && i < tile.getSizeInventory(); ++i) {
                    this.holding = this.mergeStack((IInventory)tile, i, this.holding);
                }
                for (int i = 0; !this.holding.isEmpty() && i < tile.getSizeInventory(); ++i) {
                    ItemStack item = tile.getStackInSlot(i);
                    if (item.isEmpty()) {
                        tile.setInventorySlotContents(i, this.holding);
                        this.holding = ItemStack.EMPTY;
                    }
                }
                if (!this.holding.isEmpty()) {
                    this.dropItem(this.holding);
                    this.holding = ItemStack.EMPTY;
                }
            }
            else {
                this.chest = null;
            }
            this.setHolding(this.holding);
        }
    }
    
    private ItemStack mergeStack(IInventory inventory, int slot, ItemStack item) {
        ItemStack item2 = inventory.getStackInSlot(slot);
        if (!NoppesUtilPlayer.compareItems(item, item2, false, false)) {
            return item;
        }
        int size = item2.getMaxStackSize() - item2.getCount();
        if (size >= item.getCount()) {
            item2.setCount(item2.getCount() + item.getCount());
            return ItemStack.EMPTY;
        }
        item2.setCount(item2.getMaxStackSize());
        item.setCount(item.getCount() - size);
        if (item.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return item;
    }
    
    private void pluck() {
        BlockPos pos = this.ripe;
        this.npc.getNavigator().tryMoveToXYZ((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 1.0);
        this.npc.getLookHelper().setLookPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 10.0f, (float)this.npc.getVerticalFaceSpeed());
        if (this.npc.nearPosition(pos) || this.walkTicks++ > 400) {
            if (this.walkTicks > 400) {
                pos = NoppesUtilServer.GetClosePos(pos, this.npc.world);
                this.npc.setPositionAndUpdate(pos.getX() + 0.5, (double)pos.getY(), pos.getZ() + 0.5);
            }
            this.ripe = null;
            this.npc.getNavigator().clearPath();
            this.ticks = 90;
            this.walkTicks = 0;
            this.npc.swingArm(EnumHand.MAIN_HAND);
            IBlockState state = this.npc.world.getBlockState(pos);
            Block b = state.getBlock();
            if (b instanceof BlockCrops && ((BlockCrops)b).isMaxAge(state)) {
                BlockCrops crop = (BlockCrops)b;
                this.npc.world.setBlockState(pos, crop.withAge(0));
                this.holding = new ItemStack(NpcBlockHelper.getCrop((BlockCrops)b));
            }
            if (b instanceof BlockStem) {
                state = b.getActualState(state, (IBlockAccess)this.npc.world, pos);
                EnumFacing facing = (EnumFacing)state.getValue((IProperty)BlockStem.FACING);
                if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
                    return;
                }
                pos = pos.add(facing.getDirectionVec());
                b = this.npc.world.getBlockState(pos).getBlock();
                this.npc.world.setBlockToAir(pos);
                if (b != Blocks.AIR) {
                    this.holding = new ItemStack(b);
                }
            }
            this.setHolding(this.holding);
        }
    }
    
    @Override
    public boolean aiContinueExecute() {
        return false;
    }
    
    @Override
    public void aiUpdateTask() {
        Iterator<BlockPos> ite = this.trackedBlocks.iterator();
        while (ite.hasNext() && this.ripe == null) {
            BlockPos pos = ite.next();
            IBlockState state = this.npc.world.getBlockState(pos);
            Block b = state.getBlock();
            if (b instanceof BlockCrops) {
                if (!((BlockCrops)b).isMaxAge(state)) {
                    continue;
                }
                this.ripe = pos;
            }
            else if (b instanceof BlockStem) {
                state = b.getActualState(state, (IBlockAccess)this.npc.world, pos);
                EnumFacing facing = (EnumFacing)state.getValue((IProperty)BlockStem.FACING);
                if (facing == EnumFacing.UP) {
                    continue;
                }
                this.ripe = pos;
            }
            else {
                ite.remove();
            }
        }
        this.npc.ais.returnToStart = (this.ripe == null);
        if (this.ripe != null) {
            this.npc.getNavigator().clearPath();
            this.npc.getLookHelper().setLookPosition((double)this.ripe.getX(), (double)this.ripe.getY(), (double)this.ripe.getZ(), 10.0f, (float)this.npc.getVerticalFaceSpeed());
        }
    }
    
    @Override
    public boolean isPlucking() {
        return this.ripe != null || !this.holding.isEmpty();
    }
    
    @Override
    public EntityNPCInterface getNpc() {
        return this.npc;
    }
    
    @Override
    public int getRange() {
        return 16;
    }
    
    @Override
    public void processed(List<BlockData> list) {
        List<BlockPos> trackedBlocks = new ArrayList<BlockPos>();
        BlockPos chest = null;
        for (BlockData data : list) {
            Block b = data.state.getBlock();
            if (b instanceof BlockChest) {
                if (chest != null && this.npc.getDistanceSq(chest) <= this.npc.getDistanceSq(data.pos)) {
                    continue;
                }
                chest = data.pos;
            }
            else {
                if (!(b instanceof BlockCrops) && !(b instanceof BlockStem)) {
                    continue;
                }
                if (trackedBlocks.contains(data.pos)) {
                    continue;
                }
                trackedBlocks.add(data.pos);
            }
        }
        this.chest = chest;
        this.trackedBlocks = trackedBlocks;
        this.waitingForBlocks = false;
    }
    
    @Override
    public int getMutexBits() {
        return this.npc.getNavigator().noPath() ? 0 : AiMutex.LOOK;
    }
}
