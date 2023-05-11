package noppes.npcs.items;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import java.util.List;
import noppes.npcs.CustomNpcs;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.util.EnumActionResult;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomNpcsPermissions;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.CustomItems;
import noppes.npcs.util.IPermission;
import net.minecraft.item.Item;

public class ItemNpcMovingPath extends Item implements IPermission
{
    public ItemNpcMovingPath() {
        this.maxStackSize = 1;
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!world.isRemote) {
            CustomNpcsPermissions instance = CustomNpcsPermissions.Instance;
            if (CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.TOOL_MOUNTER)) {
                EntityNPCInterface npc = this.getNpc(itemstack, world);
                if (npc != null) {
                    NoppesUtilServer.sendOpenGui(player, EnumGuiType.MovingPath, npc);
                }
                return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
            }
        }
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.PASS, (Object)itemstack);
    }
    
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos bpos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            CustomNpcsPermissions instance = CustomNpcsPermissions.Instance;
            if (CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.TOOL_MOUNTER)) {
                ItemStack stack = player.getHeldItem(hand);
                EntityNPCInterface npc = this.getNpc(stack, world);
                if (npc == null) {
                    return EnumActionResult.PASS;
                }
                List<int[]> list = npc.ais.getMovingPath();
                int[] pos = list.get(list.size() - 1);
                int x = bpos.getX();
                int y = bpos.getY();
                int z = bpos.getZ();
                list.add(new int[] { x, y, z });
                double d3 = x - pos[0];
                double d4 = y - pos[1];
                double d5 = z - pos[2];
                double distance = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                player.sendMessage((ITextComponent)new TextComponentString("Added point x:" + x + " y:" + y + " z:" + z + " to npc " + npc.getName()));
                if (distance > CustomNpcs.NpcNavRange) {
                    player.sendMessage((ITextComponent)new TextComponentString("Warning: point is too far away from previous point. Max block walk distance = " + CustomNpcs.NpcNavRange));
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }
    
    private EntityNPCInterface getNpc(ItemStack item, World world) {
        if (world.isRemote || item.getTagCompound() == null) {
            return null;
        }
        Entity entity = world.getEntityByID(item.getTagCompound().getInteger("NPCID"));
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return null;
        }
        return (EntityNPCInterface)entity;
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName("customnpcs", name);
    }
    
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.MovingPathGet || e == EnumPacketServer.MovingPathSave;
    }
}
