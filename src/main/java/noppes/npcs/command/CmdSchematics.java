package noppes.npcs.command;

import net.minecraft.world.WorldServer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.schematics.SchematicWrapper;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import noppes.npcs.controllers.SchematicController;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CommandNoppesBase;

public class CmdSchematics extends CommandNoppesBase
{
    public String getName() {
        return "schema";
    }
    
    @Override
    public String getDescription() {
        return "Schematic operation";
    }
    
    @SubCommand(desc = "Build the schematic", usage = "<name> [rotation] [[world:]x,y,z]]", permission = 4)
    public void build(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String name = args[0];
        SchematicWrapper schem = SchematicController.Instance.load(name);
        if (schem == null) {
            throw new CommandException("Unknown schematic: " + name, new Object[0]);
        }
        this.sendMessage(sender, "width: " + schem.schema.getWidth() + ", length: " + schem.schema.getLength() + ", height: " + schem.schema.getHeight(), new Object[0]);
        BlockPos pos = sender.getPosition();
        World world = sender.getEntityWorld();
        int rotation = 0;
        if (args.length > 1) {
            try {
                rotation = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException ex) {}
        }
        if (args.length > 2) {
            String location = args[2];
            if (location.contains(":")) {
                String[] par = location.split(":");
                location = par[1];
                world = this.getWorld(server, par[0]);
                if (world == null) {
                    throw new CommandException("'%s' is an unknown world", new Object[] { par[0] });
                }
            }
            if (location.contains(",")) {
                String[] par = location.split(",");
                if (par.length != 3) {
                    throw new CommandException("Location should be x,y,z", new Object[0]);
                }
                try {
                    pos = CommandBase.parseBlockPos(sender, par, 0, false);
                }
                catch (NumberInvalidException e) {
                    throw new CommandException("Location should be in numbers", new Object[0]);
                }
            }
        }
        if (pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0) {
            throw new CommandException("Location needed", new Object[0]);
        }
        schem.init(pos, world, rotation);
        SchematicController.Instance.build(schem, sender);
    }
    
    @SubCommand(desc = "Stops the current build", permission = 4)
    public void stop(MinecraftServer server, ICommandSender sender, String[] args) {
        SchematicController.Instance.stop(sender);
    }
    
    @SubCommand(desc = "Gives info about the current build", permission = 4)
    public void info(MinecraftServer server, ICommandSender sender, String[] args) {
        SchematicController.Instance.info(sender);
    }
    
    @SubCommand(desc = "Lists available schematics", permission = 4)
    public void list(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<String> list = SchematicController.Instance.list();
        if (list.isEmpty()) {
            throw new CommandException("No available schematics", new Object[0]);
        }
        String s = "";
        for (String file : list) {
            s = s + file + ", ";
        }
        this.sendMessage(sender, s, new Object[0]);
    }
    
    public List getTabCompletions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
        if (args[0].equalsIgnoreCase("build") && args.length == 2) {
            List<String> list = SchematicController.Instance.list();
            return CommandBase.getListOfStringsMatchingLastWord(args, (String[])list.toArray(new String[list.size()]));
        }
        return null;
    }
    
    public World getWorld(MinecraftServer server, String t) {
        WorldServer[] worlds;
        WorldServer[] ws = worlds = server.worlds;
        for (WorldServer w : worlds) {
            if (w != null && (w.provider.getDimension() + "").equalsIgnoreCase(t)) {
                return (World)w;
            }
        }
        return null;
    }
}
