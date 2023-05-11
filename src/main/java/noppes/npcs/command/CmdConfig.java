package noppes.npcs.command;

import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.command.CommandException;
import noppes.npcs.controllers.ChunkController;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockVine;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.BlockLeaves;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import noppes.npcs.CustomNpcs;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CommandNoppesBase;

public class CmdConfig extends CommandNoppesBase
{
    public String getName() {
        return "config";
    }
    
    @Override
    public String getDescription() {
        return "Some config things you can set";
    }
    
    @SubCommand(desc = "Disable/Enable the natural leaves decay", usage = "[true/false]")
    public void leavesdecay(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendMessage(sender, "LeavesDecay: " + CustomNpcs.LeavesDecayEnabled, new Object[0]);
        }
        else {
            CustomNpcs.LeavesDecayEnabled = Boolean.parseBoolean(args[0]);
            CustomNpcs.Config.updateConfig();
            Set<ResourceLocation> names = (Set<ResourceLocation>)Block.REGISTRY.getKeys();
            for (ResourceLocation name : names) {
                Block block = (Block)Block.REGISTRY.getObject(name);
                if (block instanceof BlockLeaves) {
                    block.setTickRandomly(CustomNpcs.LeavesDecayEnabled);
                }
            }
            this.sendMessage(sender, "LeavesDecay is now " + CustomNpcs.LeavesDecayEnabled, new Object[0]);
        }
    }
    
    @SubCommand(desc = "Disable/Enable the vines growing", usage = "[true/false]")
    public void vinegrowth(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendMessage(sender, "VineGrowth: " + CustomNpcs.VineGrowthEnabled, new Object[0]);
        }
        else {
            CustomNpcs.VineGrowthEnabled = Boolean.parseBoolean(args[0]);
            CustomNpcs.Config.updateConfig();
            Set<ResourceLocation> names = (Set<ResourceLocation>)Block.REGISTRY.getKeys();
            for (ResourceLocation name : names) {
                Block block = (Block)Block.REGISTRY.getObject(name);
                if (block instanceof BlockVine) {
                    block.setTickRandomly(CustomNpcs.VineGrowthEnabled);
                }
            }
            this.sendMessage(sender, "VineGrowth is now " + CustomNpcs.VineGrowthEnabled, new Object[0]);
        }
    }
    
    @SubCommand(desc = "Disable/Enable the ice melting", usage = "[true/false]")
    public void icemelts(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendMessage(sender, "IceMelts: " + CustomNpcs.IceMeltsEnabled, new Object[0]);
        }
        else {
            CustomNpcs.IceMeltsEnabled = Boolean.parseBoolean(args[0]);
            CustomNpcs.Config.updateConfig();
            Set<ResourceLocation> names = (Set<ResourceLocation>)Block.REGISTRY.getKeys();
            for (ResourceLocation name : names) {
                Block block = (Block)Block.REGISTRY.getObject(name);
                if (block instanceof BlockIce) {
                    block.setTickRandomly(CustomNpcs.IceMeltsEnabled);
                }
            }
            this.sendMessage(sender, "IceMelts is now " + CustomNpcs.IceMeltsEnabled, new Object[0]);
        }
    }
    
    @SubCommand(desc = "Freezes/Unfreezes npcs", usage = "[true/false]")
    public void freezenpcs(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendMessage(sender, "Frozen NPCs: " + CustomNpcs.FreezeNPCs, new Object[0]);
        }
        else {
            CustomNpcs.FreezeNPCs = Boolean.parseBoolean(args[0]);
            this.sendMessage(sender, "FrozenNPCs is now " + CustomNpcs.FreezeNPCs, new Object[0]);
        }
    }
    
    @SubCommand(desc = "Add debug info to log", usage = "<true/false>")
    public void debug(MinecraftServer server, ICommandSender sender, String[] args) {
        CustomNpcs.VerboseDebug = Boolean.parseBoolean(args[0]);
        this.sendMessage(sender, "Verbose debug is now " + CustomNpcs.VerboseDebug, new Object[0]);
    }
    
    @SubCommand(desc = "Enables/Disables scripting", usage = "[true/false]")
    public void scripting(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendMessage(sender, "Scripting: " + CustomNpcs.EnableScripting, new Object[0]);
        }
        else {
            CustomNpcs.EnableScripting = Boolean.parseBoolean(args[0]);
            CustomNpcs.Config.updateConfig();
            this.sendMessage(sender, "Scripting is now " + CustomNpcs.EnableScripting, new Object[0]);
        }
    }
    
    @SubCommand(desc = "Set how many active chunkloaders you can have", usage = "<number>")
    public void chunkloaders(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            this.sendMessage(sender, "ChunkLoaders: " + ChunkController.instance.size() + "/" + CustomNpcs.ChuckLoaders, new Object[0]);
        }
        else {
            try {
                CustomNpcs.ChuckLoaders = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException ex) {
                throw new CommandException("Didnt get a number", new Object[0]);
            }
            CustomNpcs.Config.updateConfig();
            int size = ChunkController.instance.size();
            if (size > CustomNpcs.ChuckLoaders) {
                ChunkController.instance.unload(size - CustomNpcs.ChuckLoaders);
                this.sendMessage(sender, size - CustomNpcs.ChuckLoaders + " chunksloaders unloaded", new Object[0]);
            }
            this.sendMessage(sender, "ChunkLoaders: " + ChunkController.instance.size() + "/" + CustomNpcs.ChuckLoaders, new Object[0]);
        }
    }
    
    @SubCommand(desc = "Get/Set font", usage = "[type] [size]", permission = 2)
    public void font(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            return;
        }
        int size = 18;
        if (args.length > 1) {
            try {
                size = Integer.parseInt(args[args.length - 1]);
                args = Arrays.copyOfRange(args, 0, args.length - 1);
            }
            catch (Exception ex) {}
        }
        String font = "";
        for (int i = 0; i < args.length; ++i) {
            font = font + " " + args[i];
        }
        Server.sendData((EntityPlayerMP)sender, EnumPacketClient.CONFIG, 0, font.trim(), size);
    }
}
