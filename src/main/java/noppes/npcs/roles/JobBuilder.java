package noppes.npcs.roles;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.util.EnumHand;
import net.minecraft.init.Items;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.controllers.data.BlockData;
import java.util.Stack;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.api.entity.data.role.IJobBuilder;

public class JobBuilder extends JobInterface implements IJobBuilder
{
    public TileBuilder build;
    private BlockPos possibleBuildPos;
    private Stack<BlockData> placingList;
    private BlockData placing;
    private int tryTicks;
    private int ticks;
    
    public JobBuilder(EntityNPCInterface npc) {
        super(npc);
        this.build = null;
        this.possibleBuildPos = null;
        this.placingList = null;
        this.placing = null;
        this.tryTicks = 0;
        this.ticks = 0;
        this.overrideMainHand = true;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (this.build != null) {
            compound.setInteger("BuildX", this.build.getPos().getX());
            compound.setInteger("BuildY", this.build.getPos().getY());
            compound.setInteger("BuildZ", this.build.getPos().getZ());
            if (this.placingList != null && !this.placingList.isEmpty()) {
                NBTTagList list = new NBTTagList();
                for (BlockData data : this.placingList) {
                    list.appendTag((NBTBase)data.getNBT());
                }
                if (this.placing != null) {
                    list.appendTag((NBTBase)this.placing.getNBT());
                }
                compound.setTag("Placing", (NBTBase)list);
            }
        }
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("BuildX")) {
            this.possibleBuildPos = new BlockPos(compound.getInteger("BuildX"), compound.getInteger("BuildY"), compound.getInteger("BuildZ"));
        }
        if (this.possibleBuildPos != null && compound.hasKey("Placing")) {
            Stack<BlockData> placing = new Stack<BlockData>();
            NBTTagList list = compound.getTagList("Placing", 10);
            for (int i = 0; i < list.tagCount(); ++i) {
                BlockData data = BlockData.getData(list.getCompoundTagAt(i));
                if (data != null) {
                    placing.add(data);
                }
            }
            this.placingList = placing;
        }
        this.npc.ais.doorInteract = 1;
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
    public boolean aiShouldExecute() {
        if (this.possibleBuildPos != null) {
            TileEntity tile = this.npc.world.getTileEntity(this.possibleBuildPos);
            if (tile instanceof TileBuilder) {
                this.build = (TileBuilder)tile;
            }
            else {
                this.placingList.clear();
            }
            this.possibleBuildPos = null;
        }
        return this.build != null;
    }
    
    @Override
    public void aiUpdateTask() {
        if ((this.build.finished && this.placingList == null) || !this.build.enabled || this.build.isInvalid()) {
            this.build = null;
            this.npc.getNavigator().tryMoveToXYZ((double)this.npc.getStartXPos(), this.npc.getStartYPos(), (double)this.npc.getStartZPos(), 1.0);
            return;
        }
        if (this.ticks++ < 10) {
            return;
        }
        this.ticks = 0;
        if ((this.placingList == null || this.placingList.isEmpty()) && this.placing == null) {
            this.placingList = this.build.getBlock();
            this.npc.setJobData("");
            return;
        }
        if (this.placing == null) {
            this.placing = this.placingList.pop();
            if (this.placing.state.getBlock() == Blocks.STRUCTURE_VOID) {
                this.placing = null;
                return;
            }
            this.tryTicks = 0;
            this.npc.setJobData(this.blockToString(this.placing));
        }
        this.npc.getNavigator().tryMoveToXYZ((double)this.placing.pos.getX(), (double)(this.placing.pos.getY() + 1), (double)this.placing.pos.getZ(), 1.0);
        if (this.tryTicks++ > 40 || this.npc.nearPosition(this.placing.pos)) {
            BlockPos blockPos = this.placing.pos;
            this.placeBlock();
            if (this.tryTicks > 40) {
                blockPos = NoppesUtilServer.GetClosePos(blockPos, this.npc.world);
                this.npc.setPositionAndUpdate(blockPos.getX() + 0.5, (double)blockPos.getY(), blockPos.getZ() + 0.5);
            }
        }
    }
    
    private String blockToString(BlockData data) {
        if (data.state.getBlock() == Blocks.AIR) {
            return Items.IRON_PICKAXE.getRegistryName().toString();
        }
        return this.itemToString(data.getStack());
    }
    
    @Override
    public void resetTask() {
        this.reset();
    }
    
    @Override
    public void reset() {
        this.build = null;
        this.npc.setJobData("");
    }
    
    public void placeBlock() {
        if (this.placing == null) {
            return;
        }
        this.npc.getNavigator().clearPath();
        this.npc.swingArm(EnumHand.MAIN_HAND);
        this.npc.world.setBlockState(this.placing.pos, this.placing.state, 2);
        if (this.placing.state.getBlock() instanceof ITileEntityProvider && this.placing.tile != null) {
            TileEntity tile = this.npc.world.getTileEntity(this.placing.pos);
            if (tile != null) {
                try {
                    tile.readFromNBT(this.placing.tile);
                }
                catch (Exception ex) {}
            }
        }
        this.placing = null;
    }
    
    @Override
    public boolean isBuilding() {
        return this.build != null && this.build.enabled && !this.build.finished && this.build.started;
    }
}
