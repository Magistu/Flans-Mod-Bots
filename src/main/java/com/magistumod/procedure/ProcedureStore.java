package com.magistumod.procedure;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import com.magistumod.Elements;

import java.util.function.Supplier;

import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.guns.ShootableType;
import com.magistumod.Main;

import java.util.Map;

@Elements.ModElement.Tag
public class ProcedureStore extends Elements.ModElement
{
	public ProcedureStore(Elements instance)
	{
		super(instance, 9);
	}

	public static void executeProcedure(java.util.HashMap<String, Object> dependencies)
	{
		Entity entity = (Entity)dependencies.get("entity");
		if (entity instanceof EntityPlayerMP)
		{
			Container _current = ((EntityPlayerMP)entity).openContainer;
			if (_current instanceof Supplier)
			{
				Object invobj = ((Supplier) _current).get();
				if (invobj instanceof Map && ((Slot)((Map)invobj).get(4)).getHasStack())
				{
					ItemStack _setstack = new ItemStack(Main.EQUIPED_BOT);
					
					NBTTagCompound info = new NBTTagCompound();
					String weaponID;
					String coalition;
					
			        //Equipment
			        info.setString("Helmet", ((Slot)((Map)invobj).get(0)).getStack().getItem().getRegistryName().toString());
			        info.setString("Chest", ((Slot)((Map)invobj).get(1)).getStack().getItem().getRegistryName().toString());
			        info.setString("Leggings", ((Slot)((Map)invobj).get(2)).getStack().getItem().getRegistryName().toString());
			        info.setString("Boots", ((Slot)((Map)invobj).get(3)).getStack().getItem().getRegistryName().toString());
					info.setString("Weapon", weaponID = ((Slot)((Map)invobj).get(4)).getStack().getItem().getRegistryName().toString());
			        info.setInteger("AmmoAmount", (int) dependencies.get("AmmoAmount"));
			        
			        //AI
			        int targetPriority = -1;
			        Item item = Item.getByNameOrId(weaponID);
			        if (item instanceof ItemGun && ((ItemGun) item).GetType().ammo.size() > 0)
			        {
			        	ShootableType bullet = ((ItemGun) item).GetType().ammo.get(0);
			        	if (bullet != null)
			        	{
			        		targetPriority = bullet.explodeOnImpact ?  1 : 0;
			        	}
			        }
			        info.setInteger("TargetPriority", targetPriority);
			        info.setString("Coalition", coalition = (String) dependencies.get("Coalition"));

			        _setstack.setTagInfo("Info", (NBTBase)info);
			        
					_setstack.setCount(1);
					_setstack.setStackDisplayName(coalition + " Soldier with " + (new ItemStack(item)).getDisplayName());
					((Slot)((Map)invobj).get(5)).putStack(_setstack);
					_current.detectAndSendChanges();
				}
			}
		}
	}
}
