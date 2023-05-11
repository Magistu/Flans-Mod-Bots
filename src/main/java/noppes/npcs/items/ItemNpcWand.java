package noppes.npcs.items;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.CustomNpcsPermissions;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomNpcs;
import net.minecraft.util.EnumActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.CustomItems;
import noppes.npcs.util.IPermission;
import net.minecraft.item.Item;

public class ItemNpcWand extends Item implements IPermission
{
    public ItemNpcWand() {
        this.maxStackSize = 1;
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!world.isRemote) {
            return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
        }
        CustomNpcs.proxy.openGui(0, 0, 0, EnumGuiType.NpcRemote, player);
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
    }
    
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }
    
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos bpos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        if (CustomNpcs.OpsOnly && !player.getServer().getPlayerList().canSendCommands(player.getGameProfile())) {
            player.sendMessage((ITextComponent)new TextComponentTranslation("availability.permission", new Object[0]));
        }
        else if (CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.NPC_CREATE)) {
            EntityCustomNpc npc = new EntityCustomNpc(world);
            npc.ais.setStartPos(bpos.up());
            npc.setLocationAndAngles((double)(bpos.getX() + 0.5f), npc.getStartYPos(), (double)(bpos.getZ() + 0.5f), player.rotationYaw, player.rotationPitch);
            world.spawnEntity((Entity)npc);
            npc.setHealth(npc.getMaxHealth());
            CustomNPCsScheduler.runTack(() -> NoppesUtilServer.sendOpenGui(player, EnumGuiType.MainMenuDisplay, npc), 100);
        }
        else {
            player.sendMessage((ITextComponent)new TextComponentTranslation("availability.permission", new Object[0]));
        }
        return EnumActionResult.SUCCESS;
    }
    
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase playerIn) {
        return stack;
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
		return this.setRegistryName("customnpcs", name);
    }
    
    public boolean isAllowed(EnumPacketServer e) {
        return true;
    }
}
