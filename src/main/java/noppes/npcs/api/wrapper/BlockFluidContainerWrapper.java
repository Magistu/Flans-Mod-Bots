package noppes.npcs.api.wrapper;

import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import noppes.npcs.api.block.IBlockFluidContainer;

public class BlockFluidContainerWrapper extends BlockWrapper implements IBlockFluidContainer
{
    private BlockFluidBase block;
    
    public BlockFluidContainerWrapper(World world, Block block, BlockPos pos) {
        super(world, block, pos);
        this.block = (BlockFluidBase)block;
    }
    
    @Override
    public float getFluidPercentage() {
        return this.block.getFilledPercentage((World)this.world.getMCWorld(), this.pos);
    }
    
    @Override
    public float getFuildDensity() {
        BlockFluidBase block = this.block;
        return (float)BlockFluidBase.getDensity((IBlockAccess)this.world.getMCWorld(), this.pos);
    }
    
    @Override
    public float getFuildTemperature() {
        BlockFluidBase block = this.block;
        return (float)BlockFluidBase.getTemperature((IBlockAccess)this.world.getMCWorld(), this.pos);
    }
    
    @Override
    public float getFluidValue() {
        return (float)this.block.getQuantaValue((IBlockAccess)this.world.getMCWorld(), this.pos);
    }
    
    @Override
    public String getFluidName() {
        return this.block.getFluid().getName();
    }
}
