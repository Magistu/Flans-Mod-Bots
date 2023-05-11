package noppes.npcs.command;

import noppes.npcs.controllers.SyncController;
import noppes.npcs.api.handler.data.IQuestObjective;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.util.ValueUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.command.CommandBase;
import java.util.Iterator;
import java.util.List;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.PlayerDataController;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CommandNoppesBase;

public class CmdQuest extends CommandNoppesBase
{
    public String getName() {
        return "quest";
    }
    
    @Override
    public String getDescription() {
        return "Quest operations";
    }
    
    @SubCommand(desc = "Start a quest", usage = "<player> <quest>", permission = 2)
    public void start(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        int questid;
        try {
            questid = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException ex) {
            throw new CommandException("QuestID must be an integer", new Object[0]);
        }
        List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            throw new CommandException("Unknow player '%s'", new Object[] { playername });
        }
        Quest quest = QuestController.instance.quests.get(questid);
        if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
        }
        for (PlayerData playerdata : data) {
            QuestData questdata = new QuestData(quest);
            playerdata.questData.activeQuests.put(questid, questdata);
            playerdata.save(true);
            Server.sendData((EntityPlayerMP)playerdata.player, EnumPacketClient.MESSAGE, "quest.newquest", quest.title, 2);
            Server.sendData((EntityPlayerMP)playerdata.player, EnumPacketClient.CHAT, "quest.newquest", ": ", quest.title);
        }
    }
    
    @SubCommand(desc = "Finish a quest", usage = "<player> <quest>")
    public void finish(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        int questid;
        try {
            questid = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException ex) {
            throw new CommandException("QuestID must be an integer", new Object[0]);
        }
        List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            throw new CommandException(String.format("Unknow player '%s'", playername), new Object[0]);
        }
        Quest quest = QuestController.instance.quests.get(questid);
        if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
        }
        for (PlayerData playerdata : data) {
            playerdata.questData.finishedQuests.put(questid, System.currentTimeMillis());
            playerdata.save(true);
        }
    }
    
    @SubCommand(desc = "Stop a started quest", usage = "<player> <quest>", permission = 2)
    public void stop(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        int questid;
        try {
            questid = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException ex) {
            throw new CommandException("QuestID must be an integer", new Object[0]);
        }
        List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            throw new CommandException(String.format("Unknow player '%s'", playername), new Object[0]);
        }
        Quest quest = QuestController.instance.quests.get(questid);
        if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
        }
        for (PlayerData playerdata : data) {
            playerdata.questData.activeQuests.remove(questid);
            playerdata.save(true);
        }
    }
    
    @SubCommand(desc = "Removes a quest from finished and active quests", usage = "<player> <quest>", permission = 2)
    public void remove(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        int questid;
        try {
            questid = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException ex) {
            throw new CommandException("QuestID must be an integer", new Object[0]);
        }
        List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            throw new CommandException(String.format("Unknow player '%s'", playername), new Object[0]);
        }
        Quest quest = QuestController.instance.quests.get(questid);
        if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
        }
        for (PlayerData playerdata : data) {
            playerdata.questData.activeQuests.remove(questid);
            playerdata.questData.finishedQuests.remove(questid);
            playerdata.save(true);
        }
    }
    
    @SubCommand(desc = "get/set objectives for quests progress", usage = "<player> <quest> [objective] [value]", permission = 2)
    public void objective(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer)CommandBase.getPlayer(server, sender, args[0]);
        int questid;
        try {
            questid = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException ex) {
            throw new CommandException("QuestID must be an integer", new Object[0]);
        }
        Quest quest = QuestController.instance.quests.get(questid);
        if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
        }
        PlayerData data = PlayerData.get(player);
        if (!data.questData.activeQuests.containsKey(quest.id)) {
            throw new CommandException("Player doesnt have quest active", new Object[0]);
        }
        IQuestObjective[] objectives = quest.questInterface.getObjectives(player);
        if (args.length <= 2) {
            for (IQuestObjective ob : objectives) {
                sender.sendMessage((ITextComponent)new TextComponentString(ob.getText()));
            }
            return;
        }
        int objective;
        try {
            objective = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException ex2) {
            throw new CommandException("Objective must be an integer. Most often 0, 1 or 2", new Object[0]);
        }
        if (objective < 0 || objective >= objectives.length) {
            throw new CommandException("Invalid objective number was given", new Object[0]);
        }
        if (args.length <= 3) {
            sender.sendMessage((ITextComponent)new TextComponentString(objectives[objective].getText()));
            return;
        }
        IQuestObjective object = objectives[objective];
        String s = args[3];
        int value;
        try {
            value = Integer.parseInt(args[3]);
        }
        catch (NumberFormatException ex3) {
            throw new CommandException("Value must be an integer.", new Object[0]);
        }
        if (s.startsWith("-") || s.startsWith("+")) {
            value = ValueUtil.CorrectInt(object.getProgress() + value, 0, object.getMaxProgress());
        }
        object.setProgress(value);
    }
    
    @SubCommand(desc = "reload quests from disk", permission = 4)
    public void reload(MinecraftServer server, ICommandSender sender, String[] args) {
        new QuestController().load();
        SyncController.syncAllQuests(server);
    }
}
