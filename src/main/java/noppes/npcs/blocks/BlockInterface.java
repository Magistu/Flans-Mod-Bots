package noppes.npcs.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockContainer;

public abstract class BlockInterface extends BlockContainer
{
    protected BlockInterface(Material materialIn) {
        super(materialIn);
    }
    
    public Block setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName("customnpcs", name);
    }
}
