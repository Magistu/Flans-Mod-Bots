package noppes.npcs.controllers;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.player.EntityPlayerMP;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.ICommandSender;
import noppes.npcs.controllers.data.PlayerMail;
import net.minecraft.server.MinecraftServer;
import java.util.Iterator;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerBankData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import java.io.File;
import noppes.npcs.LogWriter;
import noppes.npcs.util.NBTJsonUtil;
import java.util.HashMap;
import noppes.npcs.CustomNpcs;
import java.util.Map;

public class PlayerDataController
{
    public static PlayerDataController instance;
    public Map<String, String> nameUUIDs;
    
    public PlayerDataController() {
        PlayerDataController.instance = this;
        File dir = CustomNpcs.getWorldSaveDirectory("playerdata");
        Map<String, String> map = new HashMap<String, String>();
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                if (file.getName().endsWith(".json")) {
                    try {
                        NBTTagCompound compound = NBTJsonUtil.LoadFile(file);
                        if (compound.hasKey("PlayerName")) {
                            map.put(compound.getString("PlayerName"), file.getName().substring(0, file.getName().length() - 5));
                        }
                    }
                    catch (Exception e) {
                        LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                    }
                }
            }
        }
        this.nameUUIDs = map;
    }
    
    public PlayerBankData getBankData(EntityPlayer player, int bankId) {
        Bank bank = BankController.getInstance().getBank(bankId);
        PlayerBankData data = PlayerData.get(player).bankData;
        if (!data.hasBank(bank.id)) {
            data.loadNew(bank.id);
        }
        return data;
    }
    
    public String hasPlayer(String username) {
        for (String name : this.nameUUIDs.keySet()) {
            if (name.equalsIgnoreCase(username)) {
                return name;
            }
        }
        return "";
    }
    
    public PlayerData getDataFromUsername(MinecraftServer server, String username) {
        EntityPlayer player = (EntityPlayer)server.getPlayerList().getPlayerByUsername(username);
        PlayerData data = null;
        if (player == null) {
            for (String name : this.nameUUIDs.keySet()) {
                if (name.equalsIgnoreCase(username)) {
                    data = new PlayerData();
                    data.setNBT(PlayerData.loadPlayerData(this.nameUUIDs.get(name)));
                    break;
                }
            }
        }
        else {
            data = PlayerData.get(player);
        }
        return data;
    }
    
    public void addPlayerMessage(MinecraftServer server, String username, PlayerMail mail) {
        mail.time = System.currentTimeMillis();
        PlayerData data = this.getDataFromUsername(server, username);
        data.mailData.playermail.add(mail.copy());
        data.save(false);
    }
    
    public List<PlayerData> getPlayersData(ICommandSender sender, String username) throws CommandException {
        ArrayList<PlayerData> list = new ArrayList<PlayerData>();
        List<EntityPlayerMP> players = (List<EntityPlayerMP>)EntitySelector.matchEntities(sender, username, (Class)EntityPlayerMP.class);
        if (players.isEmpty()) {
            PlayerData data = this.getDataFromUsername(sender.getServer(), username);
            if (data != null) {
                list.add(data);
            }
        }
        else {
            for (EntityPlayer player : players) {
                list.add(PlayerData.get(player));
            }
        }
        return list;
    }
}
