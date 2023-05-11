package noppes.npcs.items;

import net.minecraft.entity.passive.EntityAnimal;
import noppes.npcs.CustomNpcs;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.entity.EntityLiving;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTBase;
import noppes.npcs.controllers.ServerCloneController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;

public class ItemSoulstoneEmpty extends Item
{
    public ItemSoulstoneEmpty() {
        this.setMaxStackSize(64);
    }
    
    public Item setTranslationKey(String name) {
		super.setTranslationKey(name);
        return this.setRegistryName(new ResourceLocation("customnpcs", name));
    }
    
    public boolean store(EntityLivingBase entity, ItemStack stack, EntityPlayer player) {
        if (!this.hasPermission(entity, player) || entity instanceof EntityPlayer) {
            return false;
        }
        ItemStack stone = new ItemStack(CustomItems.soulstoneFull);
        NBTTagCompound compound = new NBTTagCompound();
        if (!entity.writeToNBTAtomically(compound)) {
            return false;
        }
        ServerCloneController.Instance.cleanTags(compound);
        stone.setTagInfo("Entity", (NBTBase)compound);
        String name = EntityList.getEntityString((Entity)entity);
        if (name == null) {
            name = "generic";
        }
        stone.setTagInfo("Name", (NBTBase)new NBTTagString("entity." + name + ".name"));
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            stone.setTagInfo("DisplayName", (NBTBase)new NBTTagString(entity.getName()));
            if (npc.advanced.role == 6) {
                RoleCompanion role = (RoleCompanion)npc.roleInterface;
                stone.setTagInfo("ExtraText", (NBTBase)new NBTTagString("companion.stage,: ," + role.stage.name));
            }
        }
        else if (entity instanceof EntityLiving && ((EntityLiving)entity).hasCustomName()) {
            stone.setTagInfo("DisplayName", (NBTBase)new NBTTagString(((EntityLiving)entity).getCustomNameTag()));
        }
        NoppesUtilServer.GivePlayerItem((Entity)player, player, stone);
        if (!player.capabilities.isCreativeMode) {
            stack.splitStack(1);
            if (stack.getCount() <= 0) {
                player.inventory.deleteStack(stack);
            }
        }
        return entity.isDead = true;
    }
    
    public boolean hasPermission(EntityLivingBase entity, EntityPlayer player) {
        if (NoppesUtilServer.isOp(player)) {
            return true;
        }
        if (CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.SOULSTONE_ALL)) {
            return true;
        }
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            if (npc.advanced.role == 6) {
                RoleCompanion role = (RoleCompanion)npc.roleInterface;
                if (role.getOwner() == player) {
                    return true;
                }
            }
            if (npc.advanced.role == 2) {
                RoleFollower role2 = (RoleFollower)npc.roleInterface;
                if (role2.getOwner() == player) {
                    return !role2.refuseSoulStone;
                }
            }
            return CustomNpcs.SoulStoneNPCs;
        }
        return entity instanceof EntityAnimal && CustomNpcs.SoulStoneAnimals;
    }
}
