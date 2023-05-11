package noppes.npcs.command;

import net.minecraft.command.CommandBase;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.controllers.data.PlayerFactionData;
import java.util.Iterator;
import java.util.Arrays;
import noppes.npcs.controllers.FactionController;
import net.minecraft.command.CommandException;
import noppes.npcs.controllers.PlayerDataController;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.controllers.data.PlayerData;
import java.util.List;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.api.CommandNoppesBase;

public class CmdFaction extends CommandNoppesBase
{
    public Faction selectedFaction;
    public List<PlayerData> data;
    
    public String getName() {
        return "faction";
    }
    
    @Override
    public String getDescription() {
        return "Faction operations";
    }
    
    @Override
    public String getUsage() {
        return "<player> <faction> <command>";
    }
    
    @Override
    public boolean runSubCommands() {
        return false;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        String factionname = args[1];
        this.data = PlayerDataController.instance.getPlayersData(sender, playername);
        if (this.data.isEmpty()) {
            throw new CommandException("Unknow player '%s'", new Object[] { playername });
        }
        try {
            this.selectedFaction = FactionController.instance.getFaction(Integer.parseInt(factionname));
        }
        catch (NumberFormatException e) {
            this.selectedFaction = FactionController.instance.getFactionFromName(factionname);
        }
        if (this.selectedFaction == null) {
            throw new CommandException("Unknow facion '%s", new Object[] { factionname });
        }
        this.executeSub(server, sender, args[2], Arrays.copyOfRange(args, 3, args.length));
    }
    
    @SubCommand(desc = "Add points", usage = "<points>")
    public void add(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int points;
        try {
            points = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException ex) {
            throw new CommandException("Must be an integer", new Object[0]);
        }
        int factionid = this.selectedFaction.id;
        for (PlayerData playerdata : this.data) {
            PlayerFactionData playerfactiondata = playerdata.factionData;
            playerfactiondata.increasePoints(playerdata.player, factionid, points);
            playerdata.save(true);
        }
    }
    
    @SubCommand(desc = "Substract points", usage = "<points>")
    public void subtract(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int points;
        try {
            points = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException ex) {
            throw new CommandException("Must be an integer", new Object[0]);
        }
        int factionid = this.selectedFaction.id;
        for (PlayerData playerdata : this.data) {
            PlayerFactionData playerfactiondata = playerdata.factionData;
            playerfactiondata.increasePoints(playerdata.player, factionid, -points);
            playerdata.save(true);
        }
    }
    
    @SubCommand(desc = "Reset points to default")
    public void reset(MinecraftServer server, ICommandSender sender, String[] args) {
        for (PlayerData playerdata : this.data) {
            playerdata.factionData.factionData.put(this.selectedFaction.id, this.selectedFaction.defaultPoints);
            playerdata.save(true);
        }
    }
    
    @SubCommand(desc = "Set points", usage = "<points>")
    public void set(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int points;
        try {
            points = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException ex) {
            throw new CommandException("Must be an integer", new Object[0]);
        }
        for (PlayerData playerdata : this.data) {
            PlayerFactionData playerfactiondata = playerdata.factionData;
            playerfactiondata.factionData.put(this.selectedFaction.id, points);
            playerdata.save(true);
        }
    }
    
    @SubCommand(desc = "Drop relationship")
    public void drop(MinecraftServer server, ICommandSender sender, String[] args) {
        for (PlayerData playerdata : this.data) {
            playerdata.factionData.factionData.remove(this.selectedFaction.id);
            playerdata.save(true);
        }
    }
    
    public List getTabCompletions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
        if (args.length == 3) {
            return CommandBase.getListOfStringsMatchingLastWord(args, new String[] { "add", "subtract", "set", "reset", "drop", "create" });
        }
        return null;
    }
}
