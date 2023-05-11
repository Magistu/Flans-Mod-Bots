package noppes.npcs.items;

import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
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

public class ItemTeleporter extends Item implements IPermission
{
    public ItemTeleporter() {
        this.maxStackSize = 1;
        this.setCreativeTab((CreativeTabs)CustomItems.tab);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!world.isRemote) {
            return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
        }
        CustomNpcs.proxy.openGui((EntityNPCInterface)null, EnumGuiType.NpcDimensions);
        return (ActionResult<ItemStack>)new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
    }
    
    public boolean onEntitySwing(EntityLivingBase par3EntityPlayer, ItemStack stack) {
        if (par3EntityPlayer.world.isRemote) {
            return false;
        }
        float f = 1.0f;
        float f2 = par3EntityPlayer.prevRotationPitch + (par3EntityPlayer.rotationPitch - par3EntityPlayer.prevRotationPitch) * f;
        float f3 = par3EntityPlayer.prevRotationYaw + (par3EntityPlayer.rotationYaw - par3EntityPlayer.prevRotationYaw) * f;
        double d0 = par3EntityPlayer.prevPosX + (par3EntityPlayer.posX - par3EntityPlayer.prevPosX) * f;
        double d2 = par3EntityPlayer.prevPosY + (par3EntityPlayer.posY - par3EntityPlayer.prevPosY) * f + 1.62;
        double d3 = par3EntityPlayer.prevPosZ + (par3EntityPlayer.posZ - par3EntityPlayer.prevPosZ) * f;
        Vec3d vec3 = new Vec3d(d0, d2, d3);
        float f4 = MathHelper.cos(-f3 * 0.017453292f - 3.1415927f);
        float f5 = MathHelper.sin(-f3 * 0.017453292f - 3.1415927f);
        float f6 = -MathHelper.cos(-f2 * 0.017453292f);
        float f7 = MathHelper.sin(-f2 * 0.017453292f);
        float f8 = f5 * f6;
        float f9 = f4 * f6;
        double d4 = 80.0;
        Vec3d vec4 = vec3.add(f8 * d4, f7 * d4, f9 * d4);
        RayTraceResult movingobjectposition = par3EntityPlayer.world.rayTraceBlocks(vec3, vec4, true);
        if (movingobjectposition == null) {
            return false;
        }
        Vec3d vec5 = par3EntityPlayer.getLook(f);
        boolean flag = false;
        float f10 = 1.0f;
        List<Entity> list = par3EntityPlayer.world.getEntitiesWithinAABBExcludingEntity((Entity)par3EntityPlayer, par3EntityPlayer.getEntityBoundingBox().grow(vec5.x * d4, vec5.y * d4, vec5.z * d4).grow((double)f10, (double)f10, (double)f10));
        for (int i = 0; i < list.size(); ++i) {
        	Entity entity = list.get(i);
            if (entity.canBeCollidedWith()) {
                float f11 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow((double)f11, (double)f11, (double)f11);
                if (axisalignedbb.contains(vec3)) {
                    flag = true;
                }
            }
        }
        if (flag) {
            return false;
        }
        if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos;
            for (pos = movingobjectposition.getBlockPos(); par3EntityPlayer.world.getBlockState(pos).getBlock() != Blocks.AIR; pos = pos.up()) {}
            par3EntityPlayer.setPositionAndUpdate((double)(pos.getX() + 0.5f), (double)(pos.getY() + 1.0f), (double)(pos.getZ() + 0.5f));
        }
        return true;
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName("customnpcs", name);
    }
    
    public boolean isAllowed(EnumPacketServer e) {
        return e == EnumPacketServer.DimensionsGet || e == EnumPacketServer.DimensionTeleport;
    }
}
