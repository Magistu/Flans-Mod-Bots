package noppes.npcs.items;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.CustomItems;
import noppes.npcs.util.IPermission;
import net.minecraft.item.Item;

public class ItemMounter extends Item implements IPermission
{
    public ItemMounter() {
        this.maxStackSize = 1;
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName("customnpcs", name);
    }
    
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.SpawnRider || e == EnumPacketServer.PlayerRider || e == EnumPacketServer.CloneList;
    }
}
