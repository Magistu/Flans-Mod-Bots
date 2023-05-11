package noppes.npcs.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.util.Map;
import net.minecraft.command.CommandBase;

public abstract class CommandNoppesBase extends CommandBase
{
    public Map<String, Method> subcommands;
    
    public CommandNoppesBase() {
        this.subcommands = new HashMap<String, Method>();
        for (Method m : this.getClass().getDeclaredMethods()) {
            SubCommand sc = m.getAnnotation(SubCommand.class);
            if (sc != null) {
                String name = sc.name();
                if (name.equals("")) {
                    name = m.getName();
                }
                this.subcommands.put(name.toLowerCase(), m);
            }
        }
    }
    
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    }
    
    public String getUsage(ICommandSender sender) {
        return this.getDescription();
    }
    
    public abstract String getDescription();
    
    public String getUsage() {
        return "";
    }
    
    public boolean runSubCommands() {
        return !this.subcommands.isEmpty();
    }
    
    protected void sendMessage(ICommandSender sender, String message, Object... obs) {
        sender.sendMessage((ITextComponent)new TextComponentTranslation(message, obs));
    }
    
    public void executeSub(MinecraftServer server, ICommandSender sender, String command, String[] args) throws CommandException {
        Method m = this.subcommands.get(command.toLowerCase());
        if (m == null) {
            throw new CommandException("Unknown subcommand " + command, new Object[0]);
        }
        SubCommand sc = m.getAnnotation(SubCommand.class);
        if (!sender.canUseCommand(sc.permission(), "commands.noppes." + this.getName().toLowerCase() + "." + command.toLowerCase())) {
            throw new CommandException("You are not allowed to use this command", new Object[0]);
        }
        this.canRun(server, sender, sc.usage(), args);
        try {
            m.invoke(this, server, sender, args);
        }
        catch (Exception e) {
            if (e.getCause() instanceof CommandException) {
                throw (CommandException)e.getCause();
            }
            e.printStackTrace();
        }
    }
    
    public void canRun(MinecraftServer server, ICommandSender sender, String usage, String[] args) throws CommandException {
        String[] np = usage.split(" ");
        List<String> required = new ArrayList<String>();
        for (int i = 0; i < np.length; ++i) {
            String command = np[i];
            if (command.startsWith("<")) {
                required.add(command);
            }
            if (command.equals("<player>") && args.length > i) {
                CommandBase.getPlayer(server, sender, args[i]);
            }
        }
        if (args.length < required.size()) {
            throw new CommandException("Missing parameter: " + required.get(args.length), new Object[0]);
        }
    }
    
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface SubCommand {
        String name() default "";
        
        String usage() default "";
        
        String desc();
        
        int permission() default 4;
    }
}
