package noppes.npcs.items;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;

public class ItemSoulstoneFilled extends Item
{
    public ItemSoulstoneFilled() {
        this.setMaxStackSize(1);
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName(new ResourceLocation("customnpcs", name));
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null || !compound.hasKey("Entity", 10)) {
            list.add(TextFormatting.RED + "Error");
            return;
        }
        String name = I18n.translateToLocal(compound.getString("Name"));
        if (compound.hasKey("DisplayName")) {
            name = compound.getString("DisplayName") + " (" + name + ")";
        }
        list.add(TextFormatting.BLUE + name);
        if (stack.getTagCompound().hasKey("ExtraText")) {
            String text = "";
            String[] split2;
            String[] split = split2 = compound.getString("ExtraText").split(",");
            for (String s : split2) {
                text += I18n.translateToLocal(s);
            }
            list.add(text);
        }
    }
    
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (Spawn(player, stack, world, pos) == null) {
            return EnumActionResult.FAIL;
        }
        if (!player.capabilities.isCreativeMode) {
            stack.splitStack(1);
        }
        return EnumActionResult.SUCCESS;
    }
    
    public static Entity Spawn(EntityPlayer player, ItemStack stack, World world, BlockPos pos) {
        if (world.isRemote) {
            return null;
        }
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("Entity", 10)) {
            return null;
        }
        NBTTagCompound compound = stack.getTagCompound().getCompoundTag("Entity");
        Entity entity = EntityList.createEntityFromNBT(compound, world);
        if (entity == null) {
            return null;
        }
        entity.setPosition(pos.getX() + 0.5, (double)(pos.getY() + 1 + 0.2f), pos.getZ() + 0.5);
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            npc.ais.setStartPos(pos);
            npc.setHealth(npc.getMaxHealth());
            npc.setPosition((double)(pos.getX() + 0.5f), npc.getStartYPos(), (double)(pos.getZ() + 0.5f));
            if (npc.advanced.role == 6 && player != null) {
                PlayerData data = PlayerData.get(player);
                if (data.hasCompanion()) {
                    return null;
                }
                ((RoleCompanion)npc.roleInterface).setOwner(player);
                data.setCompanion(npc);
            }
            if (npc.advanced.role == 2 && player != null) {
                ((RoleFollower)npc.roleInterface).setOwner(player);
            }
        }
        if (!world.spawnEntity(entity)) {
            player.sendMessage((ITextComponent)new TextComponentTranslation("error.failedToSpawn", new Object[0]));
            return null;
        }
        return entity;
    }
}
