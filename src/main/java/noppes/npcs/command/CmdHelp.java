package noppes.npcs.command;

import java.util.Iterator;
import java.lang.reflect.Method;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.command.CommandException;
import java.util.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CommandNoppesBase;

public class CmdHelp extends CommandNoppesBase
{
    private CommandNoppes parent;
    
    public CmdHelp(CommandNoppes parent) {
        this.parent = parent;
    }
    
    public String getName() {
        return "help";
    }
    
    @Override
    public String getDescription() {
        return "help [command]";
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            this.sendMessage(sender, "------Noppes Commands------", new Object[0]);
            for (Map.Entry<String, CommandNoppesBase> entry : this.parent.map.entrySet()) {
                this.sendMessage(sender, entry.getKey() + ": " + entry.getValue().getUsage(sender), new Object[0]);
            }
            return;
        }
        CommandNoppesBase command = this.parent.getCommand(args);
        if (command == null) {
            throw new CommandException("Unknown command " + args[0], new Object[0]);
        }
        if (command.subcommands.isEmpty()) {
            sender.sendMessage((ITextComponent)new TextComponentTranslation(command.getUsage(sender), new Object[0]));
            return;
        }
        Method m = null;
        if (args.length > 1) {
            m = command.subcommands.get(args[1].toLowerCase());
        }
        if (m == null) {
            this.sendMessage(sender, "------" + command.getName() + " SubCommands------", new Object[0]);
            for (Map.Entry<String, Method> entry2 : command.subcommands.entrySet()) {
                sender.sendMessage((ITextComponent)new TextComponentTranslation(entry2.getKey() + ": " + entry2.getValue().getAnnotation(SubCommand.class).desc(), new Object[0]));
            }
        }
        else {
            this.sendMessage(sender, "------" + command.getName() + "." + args[1].toLowerCase() + " Command------", new Object[0]);
            SubCommand sc = m.getAnnotation(SubCommand.class);
            sender.sendMessage((ITextComponent)new TextComponentTranslation(sc.desc(), new Object[0]));
            if (!sc.usage().isEmpty()) {
                sender.sendMessage((ITextComponent)new TextComponentTranslation("Usage: " + sc.usage(), new Object[0]));
            }
        }
    }
}
