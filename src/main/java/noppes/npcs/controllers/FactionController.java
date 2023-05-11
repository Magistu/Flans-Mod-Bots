package noppes.npcs.controllers;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.LogWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import noppes.npcs.EventHooks;
import java.io.File;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.Faction;
import java.util.HashMap;
import noppes.npcs.api.handler.IFactionHandler;

public class FactionController implements IFactionHandler
{
    public HashMap<Integer, Faction> factionsSync;
    public HashMap<Integer, Faction> factions;
    public static FactionController instance;
    private int lastUsedID;
    
    public FactionController() {
        this.factionsSync = new HashMap<Integer, Faction>();
        this.factions = new HashMap<Integer, Faction>();
        this.lastUsedID = 0;
        FactionController.instance = this;
        this.factions.put(0, new Faction(0, "Friendly", 56576, 2000));
        this.factions.put(1, new Faction(1, "Neutral", 15916288, 1000));
        this.factions.put(2, new Faction(2, "Aggressive", 14483456, 0));
    }
    
    public void load() {
        this.factions = new HashMap<Integer, Faction>();
        this.lastUsedID = 0;
        try {
            File saveDir = CustomNpcs.getWorldSaveDirectory();
            if (saveDir == null) {
                return;
            }
            try {
                File file = new File(saveDir, "factions.dat");
                if (file.exists()) {
                    this.loadFactionsFile(file);
                }
            }
            catch (Exception e) {
                try {
                    File file2 = new File(saveDir, "factions.dat_old");
                    if (file2.exists()) {
                        this.loadFactionsFile(file2);
                    }
                }
                catch (Exception ex) {}
            }
        }
        finally {
            EventHooks.onGlobalFactionsLoaded(this);
            if (this.factions.isEmpty()) {
                this.factions.put(0, new Faction(0, "Friendly", 56576, 2000));
                this.factions.put(1, new Faction(1, "Neutral", 15916288, 1000));
                this.factions.put(2, new Faction(2, "Aggressive", 14483456, 0));
            }
        }
    }
    
    private void loadFactionsFile(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        this.loadFactions(var1);
        var1.close();
    }
    
    public void loadFactions(DataInputStream stream) throws IOException {
        HashMap<Integer, Faction> factions = new HashMap<Integer, Faction>();
        NBTTagCompound nbttagcompound1 = CompressedStreamTools.read(stream);
        this.lastUsedID = nbttagcompound1.getInteger("lastID");
        NBTTagList list = nbttagcompound1.getTagList("NPCFactions", 10);
        if (list != null) {
            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound nbttagcompound2 = list.getCompoundTagAt(i);
                Faction faction = new Faction();
                faction.readNBT(nbttagcompound2);
                factions.put(faction.id, faction);
            }
        }
        this.factions = factions;
    }
    
    public NBTTagCompound getNBT() {
        NBTTagList list = new NBTTagList();
        for (int slot : this.factions.keySet()) {
            Faction faction = this.factions.get(slot);
            NBTTagCompound nbtfactions = new NBTTagCompound();
            faction.writeNBT(nbtfactions);
            list.appendTag((NBTBase)nbtfactions);
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setInteger("lastID", this.lastUsedID);
        nbttagcompound.setTag("NPCFactions", (NBTBase)list);
        return nbttagcompound;
    }
    
    public void saveFactions() {
        try {
            File saveDir = CustomNpcs.getWorldSaveDirectory();
            File file = new File(saveDir, "factions.dat_new");
            File file2 = new File(saveDir, "factions.dat_old");
            File file3 = new File(saveDir, "factions.dat");
            CompressedStreamTools.writeCompressed(this.getNBT(), (OutputStream)new FileOutputStream(file));
            if (file2.exists()) {
                file2.delete();
            }
            file3.renameTo(file2);
            if (file3.exists()) {
                file3.delete();
            }
            file.renameTo(file3);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
    }
    
    public Faction getFaction(int faction) {
        return this.factions.get(faction);
    }
    
    public void saveFaction(Faction faction) {
        if (faction.id < 0) {
            faction.id = this.getUnusedId();
            while (this.hasName(faction.name)) {
                faction.name += "_";
            }
        }
        else {
            Faction existing = this.factions.get(faction.id);
            if (existing != null && !existing.name.equals(faction.name)) {
                while (this.hasName(faction.name)) {
                    faction.name += "_";
                }
            }
        }
        this.factions.remove(faction.id);
        this.factions.put(faction.id, faction);
        Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, 1, faction.writeNBT(new NBTTagCompound()));
        this.saveFactions();
    }
    
    public int getUnusedId() {
        if (this.lastUsedID == 0) {
            for (int catid : this.factions.keySet()) {
                if (catid > this.lastUsedID) {
                    this.lastUsedID = catid;
                }
            }
        }
        return ++this.lastUsedID;
    }
    
    @Override
    public IFaction delete(int id) {
        if (id < 0 || this.factions.size() <= 1) {
            return null;
        }
        Faction faction = this.factions.remove(id);
        if (faction == null) {
            return null;
        }
        this.saveFactions();
        faction.id = -1;
        Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, 1, id);
        return faction;
    }
    
    public int getFirstFactionId() {
        return this.factions.keySet().iterator().next();
    }
    
    public Faction getFirstFaction() {
        return this.factions.values().iterator().next();
    }
    
    public boolean hasName(String newName) {
        if (newName.trim().isEmpty()) {
            return true;
        }
        for (Faction faction : this.factions.values()) {
            if (faction.name.equals(newName)) {
                return true;
            }
        }
        return false;
    }
    
    public Faction getFactionFromName(String factioname) {
        for (Map.Entry<Integer, Faction> entryfaction : this.factions.entrySet()) {
            if (entryfaction.getValue().name.equalsIgnoreCase(factioname)) {
                return entryfaction.getValue();
            }
        }
        return null;
    }
    
    public String[] getNames() {
        String[] names = new String[this.factions.size()];
        int i = 0;
        for (Faction faction : this.factions.values()) {
            names[i] = faction.name.toLowerCase();
            ++i;
        }
        return names;
    }
    
    @Override
    public List<IFaction> list() {
        return new ArrayList<IFaction>(this.factions.values());
    }
    
    @Override
    public IFaction create(String name, int color) {
        Faction faction = new Faction();
        while (this.hasName(name)) {
            name += "_";
        }
        faction.name = name;
        faction.color = color;
        this.saveFaction(faction);
        return faction;
    }
    
    @Override
    public IFaction get(int id) {
        return this.factions.get(id);
    }
    
    static {
        FactionController.instance = new FactionController();
    }
}
