package noppes.npcs.api.block;

public interface IBlockFluidContainer extends IBlock
{
    float getFluidPercentage();
    
    float getFuildDensity();
    
    float getFuildTemperature();
    
    float getFluidValue();
    
    String getFluidName();
}
