package noppes.npcs.api.wrapper;

import noppes.npcs.api.IPos;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.IRayTrace;

public class RayTraceWrapper implements IRayTrace
{
    private IBlock block;
    private int sideHit;
    private IPos pos;
    
    public RayTraceWrapper(IBlock block, int sideHit) {
        this.block = block;
        this.sideHit = sideHit;
        this.pos = block.getPos();
    }
    
    @Override
    public IPos getPos() {
        return this.block.getPos();
    }
    
    @Override
    public IBlock getBlock() {
        return this.block;
    }
    
    @Override
    public int getSideHit() {
        return this.sideHit;
    }
}
