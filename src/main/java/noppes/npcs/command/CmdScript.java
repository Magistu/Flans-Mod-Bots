package noppes.npcs.command;

import noppes.npcs.api.IPos;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.WorldEvent;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.NpcAPI;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.controllers.ScriptController;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CommandNoppesBase;

public class CmdScript extends CommandNoppesBase
{
    @SubCommand(desc = "Reload scripts and saved data from disks script folder.")
    public Boolean reload(MinecraftServer server, ICommandSender sender, String[] args) {
        ScriptController.Instance.loadCategories();
        if (ScriptController.Instance.loadPlayerScripts()) {
            sender.sendMessage((ITextComponent)new TextComponentString("Reload player scripts succesfully"));
        }
        else {
            sender.sendMessage((ITextComponent)new TextComponentString("Failed reloading player scripts"));
        }
        if (ScriptController.Instance.loadForgeScripts()) {
            sender.sendMessage((ITextComponent)new TextComponentString("Reload forge scripts succesfully"));
        }
        else {
            sender.sendMessage((ITextComponent)new TextComponentString("Failed reloading forge scripts"));
        }
        if (ScriptController.Instance.loadStoredData()) {
            sender.sendMessage((ITextComponent)new TextComponentString("Reload stored data succesfully"));
        }
        else {
            sender.sendMessage((ITextComponent)new TextComponentString("Failed reloading stored data"));
        }
        return true;
    }
    
    @SubCommand(desc = "Runs scriptCommand in the players scripts", usage = "[args]")
    public Boolean run(MinecraftServer server, ICommandSender sender, String[] args) {
        IWorld world = NpcAPI.Instance().getIWorld((WorldServer)sender.getEntityWorld());
        BlockPos bpos = sender.getPosition();
        IPos pos = NpcAPI.Instance().getIPos(bpos.getX(), bpos.getY(), bpos.getZ());
        WorldEvent.ScriptCommandEvent event = new WorldEvent.ScriptCommandEvent(world, pos, args);
        EventHooks.onWorldScriptEvent(event);
        return true;
    }
    
    public String getName() {
        return "script";
    }
    
    @Override
    public String getDescription() {
        return "Commands for scripts";
    }
}
