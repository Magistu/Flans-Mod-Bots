package noppes.npcs.command;

import net.minecraft.command.CommandBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.command.CommandException;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import java.util.ArrayList;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.EntityLivingBase;
import java.util.LinkedHashMap;
import java.util.Map;
import noppes.npcs.api.CommandNoppesBase;

public class CmdSlay extends CommandNoppesBase
{
    public Map<String, Class<?>> SlayMap;
    
    public CmdSlay() {
        (this.SlayMap = new LinkedHashMap<String, Class<?>>()).clear();
        this.SlayMap.put("all", EntityLivingBase.class);
        this.SlayMap.put("mobs", EntityMob.class);
        this.SlayMap.put("animals", EntityAnimal.class);
        this.SlayMap.put("items", EntityItem.class);
        this.SlayMap.put("xporbs", EntityXPOrb.class);
        this.SlayMap.put("npcs", EntityNPCInterface.class);
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
            String name = ent.getName();
            Class<? extends Entity> cls = (Class<? extends Entity>)ent.getEntityClass();
            if (EntityNPCInterface.class.isAssignableFrom(cls)) {
                continue;
            }
            if (!EntityLivingBase.class.isAssignableFrom(cls)) {
                continue;
            }
            this.SlayMap.put(name.toLowerCase(), cls);
        }
        this.SlayMap.remove("monster");
        this.SlayMap.remove("mob");
    }
    
    public String getName() {
        return "slay";
    }
    
    @Override
    public String getDescription() {
        return "Kills given entity within range. Also has all, mobs, animal options. Can have multiple types";
    }
    
    @Override
    public String getUsage() {
        return "<type>.. [range]";
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ArrayList<Class<?>> toDelete = new ArrayList<Class<?>>();
        boolean deleteNPCs = false;
        for (String delete : args) {
            delete = delete.toLowerCase();
            Class<?> cls = this.SlayMap.get(delete);
            if (cls != null) {
                toDelete.add(cls);
            }
            if (delete.equals("mobs")) {
                toDelete.add(EntityGhast.class);
                toDelete.add(EntityDragon.class);
            }
            if (delete.equals("npcs")) {
                deleteNPCs = true;
            }
        }
        int count = 0;
        int range = 120;
        try {
            range = Integer.parseInt(args[args.length - 1]);
        }
        catch (NumberFormatException ex) {}
        AxisAlignedBB box = new AxisAlignedBB(sender.getPosition(), sender.getPosition().add(1, 1, 1)).grow((double)range, (double)range, (double)range);
        List<? extends Entity> list = (List<? extends Entity>)sender.getEntityWorld().getEntitiesWithinAABB((Class)EntityLivingBase.class, box);
        for (Entity entity : list) {
            if (entity instanceof EntityPlayer) {
                continue;
            }
            if (entity instanceof EntityTameable && ((EntityTameable)entity).isTamed()) {
                continue;
            }
            if (entity instanceof EntityNPCInterface && !deleteNPCs) {
                continue;
            }
            if (!this.delete(entity, toDelete)) {
                continue;
            }
            ++count;
        }
        if (toDelete.contains(EntityXPOrb.class)) {
            list = (List<? extends Entity>)sender.getEntityWorld().getEntitiesWithinAABB((Class)EntityXPOrb.class, box);
            for (Entity entity : list) {
                entity.isDead = true;
                ++count;
            }
        }
        if (toDelete.contains(EntityItem.class)) {
            list = (List<? extends Entity>)sender.getEntityWorld().getEntitiesWithinAABB((Class)EntityItem.class, box);
            for (Entity entity : list) {
                entity.isDead = true;
                ++count;
            }
        }
        sender.sendMessage((ITextComponent)new TextComponentTranslation(count + " entities deleted", new Object[0]));
    }
    
    private boolean delete(Entity entity, ArrayList<Class<?>> toDelete) {
        for (Class<?> delete : toDelete) {
            if (delete == EntityAnimal.class && entity instanceof EntityHorse) {
                continue;
            }
            if (delete.isAssignableFrom(entity.getClass())) {
                return entity.isDead = true;
            }
        }
        return false;
    }
    
    public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return CommandBase.getListOfStringsMatchingLastWord(args, (String[])this.SlayMap.keySet().toArray(new String[this.SlayMap.size()]));
    }
}
