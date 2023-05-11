package noppes.npcs.items;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.CustomItems;
import noppes.npcs.util.IPermission;
import net.minecraft.item.Item;

public class ItemNbtBook extends Item implements IPermission
{
    public ItemNbtBook() {
        this.maxStackSize = 1;
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName(new ResourceLocation("customnpcs", name));
    }
    
    public void blockEvent(PlayerInteractEvent.RightClickBlock event) {
        Server.sendData((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.GUI, EnumGuiType.NbtBook, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        NBTTagCompound data = new NBTTagCompound();
        TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (tile != null) {
            tile.writeToNBT(data);
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", (NBTBase)data);
        Server.sendData((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.GUI_DATA, compound);
    }
    
    public void entityEvent(PlayerInteractEvent.EntityInteract event) {
        Server.sendData((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.GUI, EnumGuiType.NbtBook, 0, 0, 0);
        NBTTagCompound data = new NBTTagCompound();
        event.getTarget().writeToNBTAtomically(data);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("EntityId", event.getTarget().getEntityId());
        compound.setTag("Data", (NBTBase)data);
        Server.sendData((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.GUI_DATA, compound);
    }
    
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.NbtBookSaveEntity || e == EnumPacketServer.NbtBookSaveBlock;
    }
}
