package com.magistumod.procedure;

import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import com.magistumod.Elements;
import com.magistumod.Main;
import com.magistumod.gui.GuiItemBot;

@Elements.ModElement.Tag
public class ProcedureCustomBotOpenMenu extends Elements.ModElement
{
	public ProcedureCustomBotOpenMenu(Elements instance)
	{
		super(instance, 6);
	}

	public static void executeProcedure(java.util.HashMap<String, Object> dependencies)
	{
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if (entity instanceof EntityPlayer)
			((EntityPlayer) entity).openGui(Main.instance, GuiItemBot.GUIID, world, x, y, z);
	}
}
