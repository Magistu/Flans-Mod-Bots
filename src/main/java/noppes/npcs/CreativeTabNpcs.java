package noppes.npcs;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.creativetab.CreativeTabs;

public class CreativeTabNpcs extends CreativeTabs
{
    public Item item;
    public int meta;
    
    public CreativeTabNpcs(String label) {
        super(label);
        this.item = Items.BOWL;
        this.meta = 0;
    }
    
    public ItemStack createIcon() {
        return new ItemStack(this.item, 1, this.meta);
    }

	@Override
	public ItemStack getIcon() {
		return new ItemStack(this.item, 1, this.meta);
	}
}
