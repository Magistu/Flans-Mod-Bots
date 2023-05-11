package noppes.npcs.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemNpcBlock extends ItemBlock
{
    public ItemNpcBlock(Block block) {
        super(block);
        String name = block.getRegistryName().getPath();
        this.setTranslationKey(name);
        this.setRegistryName("customnpcs", name);
    }
}
