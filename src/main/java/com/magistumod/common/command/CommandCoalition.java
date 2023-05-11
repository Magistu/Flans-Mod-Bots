package com.magistumod.common.command;


import com.google.common.base.Predicate;
import com.magistumod.ConfigHandler;
import com.magistumod.Main;
import com.magistumod.common.network.NetworkHelper;
import com.magistumod.common.network.messages.AbstractMessage;
import com.magistumod.common.network.messages.MessageSaveData;
import com.magistumod.common.storage.StorageEvents;
import com.magistumod.common.storage.StorageHelper;
import com.magistumod.entity.EnumCoalitions;
import com.magistumod.entity.FlansModShooter;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandCoalition extends CommandBase 
{
	private final List<String> aliases;
	private final String help = "Flan's Mod WW2 Bots Commands:\n"
			+ "to join AXIS:\n"
			+ "/ñ join AXIS\n"
			+ "to join ALLIES:\n"
			+ "/c join ALLIES\n"
			+ "to order squad 20 meters away to follow you:\n"
			+ "/c lead 20\n"
			+ "to order squad 20 meters away to stop following you:\n"
			+ "/c leave 20\n"
			+ "to order squad to get off from their vehicles\n"
			+ "/c dismount 20\n";

	public CommandCoalition() 
	{
		this.aliases = new ArrayList<>();
		this.aliases.add("c");
		this.aliases.add("coalitions");
	}

	public String getName() 
	{
		return "coalitions";
	}

	public String getUsage(ICommandSender sender) 
	{
		return "Flan's Mod WW2 Bots Commands:\n"
				+ "to join AXIS:\n"
				+ "/ñ join AXIS\n"
				+ "to join ALLIES:\n"
				+ "/c join ALLIES\n"
				+ "to order squad 20 meters away to follow you:\n"
				+ "/c lead 20\n"
				+ "to order squad 20 meters away to stop following you:\n"
				+ "/c leave 20\n"
				+ "to order squad to get off from their vehicles\n"
				+ "/c dismount 20\n";
	}

	public List<String> getAliases() 
	{
		return this.aliases;
	}

	public boolean checkPermission(MinecraftServer server, ICommandSender sender) 
	{
		return true;
	}

	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer() && args.length > 0) 
		{
			switch (args[0]) 
			{
				case "info":
					checkLength(args, 2);
					coalitionInfo(server, sender, args[1]);
					break;
				case "player":
					checkLength(args, 2);
					coalitionPlayer(server, sender, args[1]);
					break;
				case "join":
					checkLength(args, 2);
					coalitionJoin(server, sender, args[1]);
					break;
				case "lead":
					checkLength(args, 2);
					formationLead(server, sender, args[1]);
					break;
				case "leave":
					checkLength(args, 2);
					formationLeave(server, sender, args[1]);
					break;
				case "dismount":
					checkLength(args, 2);
					dismountVehicle(server, sender, args[1]);
					break;
			}
		}
		else 
		{
			sender.sendMessage((ITextComponent)new TextComponentString(getUsage(sender)));
		}
	}

	private void dismountVehicle(MinecraftServer server, ICommandSender sender, String radius)
	{
		if (sender instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)sender;
			AxisAlignedBB area = player.getEntityBoundingBox().grow(Float.valueOf(radius), Float.valueOf(radius), Float.valueOf(radius));
			List<FlansModShooter> list = player.world.<FlansModShooter>getEntitiesWithinAABB(FlansModShooter.class, area, new Predicate<FlansModShooter>()
	        {
	            public boolean apply(@Nullable FlansModShooter p_apply_1_)
	            {
	                return p_apply_1_.getCoalition() == EnumCoalitions.getCoalition(StorageHelper.getCoalition(player.getUniqueID()));
	            }
	        });
			if (!list.isEmpty())
			{
				for (FlansModShooter soldier : list)
				{
					soldier.dismountRidingEntity();
					sender.sendMessage((ITextComponent)new TextComponentTranslation("dismount.success", new Object[0]));
				}
			}
		}
	}

	private void formationLeave(MinecraftServer server, ICommandSender sender, String radius) throws CommandException {
		if (sender instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)sender;
			AxisAlignedBB area = player.getEntityBoundingBox().grow(Float.valueOf(radius), Float.valueOf(radius), Float.valueOf(radius));
			List<FlansModShooter> list = player.world.<FlansModShooter>getEntitiesWithinAABB(FlansModShooter.class, area, new Predicate<FlansModShooter>()
	        {
	            public boolean apply(@Nullable FlansModShooter p_apply_1_)
	            {
	                return p_apply_1_.getCoalition() == EnumCoalitions.getCoalition(StorageHelper.getCoalition(player.getUniqueID()));
	            }
	        });
			if (!list.isEmpty())
			{
				for (FlansModShooter soldier : list)
				{
					soldier.removeFollowTask();
					soldier.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ConfigHandler.INFANTRY_FIRING_RANGE);
					soldier.addAvoidTask();
					sender.sendMessage((ITextComponent)new TextComponentTranslation("leave.success", new Object[0]));
				}
			}
		}
	}

	private <T> void formationLead(MinecraftServer server, ICommandSender sender, String radius) throws CommandException
	{
		if (sender instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)sender;
			AxisAlignedBB area = player.getEntityBoundingBox().grow(Float.valueOf(radius), Float.valueOf(radius), Float.valueOf(radius));
			List<FlansModShooter> list = player.world.<FlansModShooter>getEntitiesWithinAABB(FlansModShooter.class, area, new Predicate<FlansModShooter>()
	        {
	            public boolean apply(@Nullable FlansModShooter p_apply_1_)
	            {
	                return p_apply_1_.getCoalition() == EnumCoalitions.getCoalition(StorageHelper.getCoalition(player.getUniqueID()));
	            }
	        });
			if (!list.isEmpty())
			{
				for (FlansModShooter soldier : list)
				{
					soldier.addFollowTask(soldier, player, Float.valueOf(radius), Float.valueOf(radius) / 2, 1.5D);
					soldier.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ConfigHandler.INFANTRY_FIRING_RANGE - 10.0D);
					soldier.removeAvoidTask();
					sender.sendMessage((ITextComponent)new TextComponentTranslation("lead.success", new Object[0]));
				}
			}
		}
	}

	private void coalitionJoin(MinecraftServer server, ICommandSender sender, String coalitionName) throws CommandException
	{
		if (sender instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)sender;
			UUID uid = player.getUniqueID();
			if (!StorageHelper.doesCoalitionExist(coalitionName))
			{
				throw new CommandException((new TextComponentTranslation("info.invalidcoalition", new Object[0])).getFormattedText(), new Object[0]);
			}
			if (StorageHelper.isPlayerInCoalition(uid)) {
				StorageEvents.data.removePlayer(StorageHelper.getCoalition(uid), uid);
			}
			StorageEvents.data.addPlayer(coalitionName, uid);
			NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());
			sender.sendMessage((ITextComponent)new TextComponentTranslation("join.success", new Object[0]));
		}
	}

//	private void coalitionCreate(MinecraftServer server, ICommandSender sender, String coalitionName) throws CommandException
//	{
//		if (sender instanceof EntityPlayer) 
//		{
//			EntityPlayer player = (EntityPlayer)sender;
//			if (StorageHelper.doesCoalitionExist(coalitionName))
//				throw new CommandException((new TextComponentTranslation("magistumod.create.nametaken", new Object[0])).getFormattedText(), new Object[0]); 
//			if (StorageHelper.isPlayerInCoalition(player.getUniqueID())) {
//				throw new CommandException((new TextComponentTranslation("magistumod.incoalition", new Object[0])).getFormattedText(), new Object[0]);
//			}
//			sender.sendMessage((new TextComponentTranslation("magistumod.create.success", new Object[0])).appendText(coalitionName));
//			StorageEvents.data.addCoalition(coalitionName, player);
//			NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());
//		} 
//	}
//
//	private void coalitionList(ICommandSender sender)
//	{
//		sender.sendMessage((ITextComponent)new TextComponentTranslation("magistumod.list.success", new Object[0]));
//		for (String s : StorageHelper.getCoalitionSet())
//		{
//			sender.sendMessage((ITextComponent)new TextComponentString(s));
//		}
//	}

	private void coalitionInfo(MinecraftServer server, ICommandSender sender, String coalitionName) throws CommandException
	{
		if (!StorageHelper.doesCoalitionExist(coalitionName))
		{
			throw new CommandException((new TextComponentTranslation("info.invalidcoalition", new Object[0])).getFormattedText(), new Object[0]);
		}
		sender.sendMessage((ITextComponent)new TextComponentTranslation("info.success", new Object[0]));
		for (UUID id : StorageHelper.getCoalitionPlayers(coalitionName))
		{
			GameProfile profile = server.getPlayerProfileCache().getProfileByUUID(id);
			if (profile != null)
			{
				sender.sendMessage((ITextComponent)new TextComponentString(profile.getName()));
			}
		} 
	}

	private void coalitionPlayer(MinecraftServer server, ICommandSender sender, String playerName) throws CommandException
	{
		GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
		if (profile == null)
			throw new CommandException((new TextComponentTranslation("nosuchplayer", new Object[] { playerName })).getFormattedText(), new Object[0]); 
		if (StorageHelper.isPlayerInCoalition(profile.getId()))
		{
			String playerCoalition = StorageHelper.getCoalition(profile.getId());
			sender.sendMessage((ITextComponent)new TextComponentTranslation("player.success", new Object[] { playerName, playerCoalition }));
		}
		else
		{
			throw new CommandException((new TextComponentTranslation("player.coalitionless", new Object[] { playerName })).getFormattedText(), new Object[0]);
		} 
	}

//	private void coalitionInvite(ICommandSender sender, String playerName) throws CommandException {
//		if (sender instanceof EntityPlayer) {
//			EntityPlayerMP newPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
//			EntityPlayer oldPlayer = (EntityPlayer)sender;
//			String coalitionName = StorageHelper.getCoalition(oldPlayer.getUniqueID());
//			if (coalitionName != null) {
//				if (newPlayer == null)
//					throw new CommandException((new TextComponentTranslation("magistumod.invite.nosuchplayer", new Object[0])).getFormattedText(), new Object[0]); 
//				if (StorageHelper.getCoalitionPlayers(coalitionName).contains(newPlayer.getUniqueID())) {
//					throw new CommandException((new TextComponentTranslation("magistumod.invite.alreadyincoalition", new Object[0])).getFormattedText(), new Object[0]);
//				}
//				newPlayer.getEntityData().setString("invitedto", coalitionName);
//				newPlayer.getEntityData().setUniqueId("invitedby", oldPlayer.getUniqueID());
//				oldPlayer.sendMessage((ITextComponent)new TextComponentTranslation("magistumod.invite.success", new Object[] { newPlayer.getGameProfile().getName() }));
//				NetworkHelper.sendToPlayer(newPlayer, (AbstractMessage)new MessageInvite(coalitionName));
//				newPlayer.sendMessage((new TextComponentTranslation("magistumod.invitedtocoalition", new Object[0])).appendText(coalitionName));
//			} else {
//				throw new CommandException((new TextComponentTranslation("magistumod.notincoalition", new Object[0])).getFormattedText(), new Object[0]);
//			} 
//		} 
//	}

//	private void coalitionAccept(MinecraftServer server, ICommandSender sender) throws CommandException {
//		if (sender instanceof EntityPlayer) {
//			EntityPlayerMP invitee = (EntityPlayerMP)sender;
//			String coalitionName = invitee.getEntityData().getString("invitedto");
//			UUID uid = invitee.getUniqueID();
//			if (coalitionName.equals(""))
//				throw new CommandException((new TextComponentTranslation("magistumod.accept.notinvited", new Object[0])).getFormattedText(), new Object[0]); 
//			if (StorageHelper.isPlayerInCoalition(uid)) {
//				throw new CommandException((new TextComponentTranslation("magistumod.incoalition", new Object[0])).getFormattedText(), new Object[0]);
//			}
//			StorageEvents.data.addPlayer(coalitionName, uid);
//			StorageHandler.syncPlayers(coalitionName, invitee);
//			NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());
//			sender.sendMessage((ITextComponent)new TextComponentTranslation("magistumod.accept.success", new Object[] { coalitionName }));
//			EntityPlayerMP inviter = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(invitee.getEntityData().getUniqueId("invitedby"));
//			if (inviter != null) {
//				inviter.sendMessage((ITextComponent)new TextComponentTranslation("magistumod.accept.joined", new Object[] { invitee.getGameProfile().getName() }));
//			}
//		} 
//	}

//	private void coalitionKick(MinecraftServer server, ICommandSender sender, String playerName) throws CommandException {
//		if (sender instanceof EntityPlayer) {
//			GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
//			if (profile == null) {
//				throw new CommandException((new TextComponentTranslation("magistumod.nosuchplayer", new Object[0])).getFormattedText(), new Object[0]);
//			}
//			UUID kickID = profile.getId();
//			UUID senderID = ((EntityPlayer)sender).getUniqueID();
//			if (!StorageHelper.isPlayerInCoalition(senderID))
//				throw new CommandException((new TextComponentTranslation("magistumod.notincoalition", new Object[0])).getFormattedText(), new Object[0]); 
//			if (!StorageHelper.isPlayerInCoalition(kickID) || !StorageHelper.getCoalition(senderID).equals(StorageHelper.getCoalition(kickID))) {
//				throw new CommandException((new TextComponentTranslation("magistumod.playernotincoalition", new Object[] { profile.getName() })).getFormattedText(), new Object[0]);
//			}
//			String myCoalition = StorageHelper.getCoalition(senderID);
//			if (!StorageHelper.getCoalitionOwner(myCoalition).equals(senderID)) {
//				throw new CommandException((new TextComponentTranslation("magistumod.notowner", new Object[] { profile.getName() })).getFormattedText(), new Object[0]);
//			}
//			StorageEvents.data.removePlayer(myCoalition, kickID);
//			sender.sendMessage((ITextComponent)new TextComponentTranslation("magistumod.kick.success", new Object[] { profile.getName() }));
//			if (server.getPlayerList().getPlayerByUUID(kickID) != null) {
//				server.getPlayerList().getPlayerByUUID(kickID).sendMessage((ITextComponent)new TextComponentTranslation("magistumod.kicked", new Object[0]));
//			}
//			NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());
//		} 
//	}

//	private void coalitionRemove(MinecraftServer server, ICommandSender sender, String coalitionName) throws CommandException {
//		if (sender.canUseCommand(2, getName()) || FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
//			if (!StorageHelper.doesCoalitionExist(coalitionName)) {
//				throw new CommandException((new TextComponentTranslation("magistumod.remove.nosuchcoalition", new Object[] { coalitionName })).getFormattedText(), new Object[0]);
//			}
//			sender.sendMessage((ITextComponent)new TextComponentTranslation("magistumod.remove.success", new Object[] { coalitionName }));
//			StorageEvents.data.removeCoalition(coalitionName);
//			NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());
//		} else {
//			throw new CommandException("Missing permissions", new Object[0]);
//		} 
//	}

//	private void coalitionConfig(MinecraftServer server, ICommandSender sender, String configOption, boolean configValue) throws CommandException {
//		if (sender instanceof EntityPlayer) {
//			String coalitionName = StorageHelper.getCoalition(((EntityPlayer)sender).getUniqueID());
//			if (coalitionName == null) {
//				throw new CommandException((new TextComponentTranslation("magistumod.notincoalition", new Object[0])).getFormattedText(), new Object[0]);
//			}
//			if (!configOption.equals("disableAdvancementSync") && !configOption.equals("enableFriendlyFire"))
//				throw new CommandException((new TextComponentTranslation("magistumod.config.invalid", new Object[0])).getFormattedText(), new Object[0]); 
//			if (!((EntityPlayer)sender).getUniqueID().equals(StorageHelper.getCoalitionOwner(coalitionName))) {
//				throw new CommandException((new TextComponentTranslation("magistumod.notowner", new Object[0])).getFormattedText(), new Object[0]);
//			}
//			StorageHelper.setCoalitionSetting(coalitionName, configOption, configValue);
//			NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());
//			sender.sendMessage((ITextComponent)new TextComponentTranslation("magistumod.config.success", new Object[0]));
//		} 
//	}
	
	private void checkLength(String[] args, int length) throws CommandException
	{
		if (args.length != length)
		{
			throw new CommandException((new TextComponentTranslation("badarguments", new Object[0])).getFormattedText(), new Object[0]);
		}
	}
}
