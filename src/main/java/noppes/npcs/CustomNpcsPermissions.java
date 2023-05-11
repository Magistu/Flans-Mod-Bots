package noppes.npcs;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Iterator;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import java.util.List;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;

public class CustomNpcsPermissions
{
    public static Permission NPC_DELETE;
    public static Permission NPC_CREATE;
    public static Permission NPC_GUI;
    public static Permission NPC_FREEZE;
    public static Permission NPC_RESET;
    public static Permission NPC_AI;
    public static Permission NPC_ADVANCED;
    public static Permission NPC_DISPLAY;
    public static Permission NPC_INVENTORY;
    public static Permission NPC_STATS;
    public static Permission NPC_CLONE;
    public static Permission GLOBAL_LINKED;
    public static Permission GLOBAL_PLAYERDATA;
    public static Permission GLOBAL_BANK;
    public static Permission GLOBAL_DIALOG;
    public static Permission GLOBAL_QUEST;
    public static Permission GLOBAL_FACTION;
    public static Permission GLOBAL_TRANSPORT;
    public static Permission GLOBAL_RECIPE;
    public static Permission GLOBAL_NATURALSPAWN;
    public static Permission SPAWNER_MOB;
    public static Permission SPAWNER_CREATE;
    public static Permission TOOL_MOUNTER;
    public static Permission TOOL_PATHER;
    public static Permission TOOL_SCRIPTER;
    public static Permission TOOL_NBTBOOK;
    public static Permission EDIT_VILLAGER;
    public static Permission EDIT_BLOCKS;
    public static Permission SOULSTONE_ALL;
    public static Permission SCENES;
    public static CustomNpcsPermissions Instance;
    
    public CustomNpcsPermissions() {
        CustomNpcsPermissions.Instance = this;
        if (!CustomNpcs.DisablePermissions) {
            LogManager.getLogger((Class)CustomNpcs.class).info("CustomNPC Permissions available:");
            Collections.sort(Permission.permissions, (o1, o2) -> o1.name.compareToIgnoreCase(o2.name));
            for (Permission p : Permission.permissions) {
                PermissionAPI.registerNode(p.name, p.defaultValue ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP, p.name);
                LogManager.getLogger((Class)CustomNpcs.class).info(p.name);
            }
        }
    }
    
    public static boolean hasPermission(EntityPlayer player, Permission permission) {
        if (CustomNpcs.DisablePermissions) {
            return permission.defaultValue;
        }
        return hasPermissionString(player, permission.name);
    }
    
    public static boolean hasPermissionString(EntityPlayer player, String permission) {
        return CustomNpcs.DisablePermissions || PermissionAPI.hasPermission(player, permission);
    }
    
    static {
        NPC_DELETE = new Permission("customnpcs.npc.delete");
        NPC_CREATE = new Permission("customnpcs.npc.create");
        NPC_GUI = new Permission("customnpcs.npc.gui");
        NPC_FREEZE = new Permission("customnpcs.npc.freeze");
        NPC_RESET = new Permission("customnpcs.npc.reset");
        NPC_AI = new Permission("customnpcs.npc.ai");
        NPC_ADVANCED = new Permission("customnpcs.npc.advanced");
        NPC_DISPLAY = new Permission("customnpcs.npc.display");
        NPC_INVENTORY = new Permission("customnpcs.npc.inventory");
        NPC_STATS = new Permission("customnpcs.npc.stats");
        NPC_CLONE = new Permission("customnpcs.npc.clone");
        GLOBAL_LINKED = new Permission("customnpcs.global.linked");
        GLOBAL_PLAYERDATA = new Permission("customnpcs.global.playerdata");
        GLOBAL_BANK = new Permission("customnpcs.global.bank");
        GLOBAL_DIALOG = new Permission("customnpcs.global.dialog");
        GLOBAL_QUEST = new Permission("customnpcs.global.quest");
        GLOBAL_FACTION = new Permission("customnpcs.global.faction");
        GLOBAL_TRANSPORT = new Permission("customnpcs.global.transport");
        GLOBAL_RECIPE = new Permission("customnpcs.global.recipe");
        GLOBAL_NATURALSPAWN = new Permission("customnpcs.global.naturalspawn");
        SPAWNER_MOB = new Permission("customnpcs.spawner.mob");
        SPAWNER_CREATE = new Permission("customnpcs.spawner.create");
        TOOL_MOUNTER = new Permission("customnpcs.tool.mounter");
        TOOL_PATHER = new Permission("customnpcs.tool.pather");
        TOOL_SCRIPTER = new Permission("customnpcs.tool.scripter");
        TOOL_NBTBOOK = new Permission("customnpcs.tool.nbtbook");
        EDIT_VILLAGER = new Permission("customnpcs.edit.villager");
        EDIT_BLOCKS = new Permission("customnpcs.edit.blocks");
        SOULSTONE_ALL = new Permission("customnpcs.soulstone.all", false);
        SCENES = new Permission("customnpcs.scenes");
    }
    
    public static class Permission
    {
        private static List<Permission> permissions;
        public String name;
        public boolean defaultValue;
        
        public Permission(String name) {
            this.defaultValue = true;
            this.name = name;
            Permission.permissions.add(this);
        }
        
        public Permission(String name, boolean defaultValue) {
            this.defaultValue = true;
            this.name = name;
            Permission.permissions.add(this);
            this.defaultValue = defaultValue;
        }
        
        static {
            permissions = new ArrayList<Permission>();
        }
    }
}
