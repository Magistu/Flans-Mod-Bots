package noppes.npcs.controllers.data;

import noppes.npcs.api.CustomNPCsException;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.FactionController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import java.util.Set;
import net.minecraft.scoreboard.ServerScoreboard;
import noppes.npcs.CustomNpcs;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.constants.EnumAvailabilityScoreboard;
import noppes.npcs.constants.EnumAvailabilityFaction;
import noppes.npcs.constants.EnumAvailabilityFactionType;
import noppes.npcs.constants.EnumDayTime;
import noppes.npcs.constants.EnumAvailabilityQuest;
import noppes.npcs.constants.EnumAvailabilityDialog;
import java.util.HashSet;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.ICompatibilty;

public class Availability implements ICompatibilty, IAvailability
{
    public static HashSet<String> scores;
    public int version;
    public EnumAvailabilityDialog dialogAvailable;
    public EnumAvailabilityDialog dialog2Available;
    public EnumAvailabilityDialog dialog3Available;
    public EnumAvailabilityDialog dialog4Available;
    public int dialogId;
    public int dialog2Id;
    public int dialog3Id;
    public int dialog4Id;
    public EnumAvailabilityQuest questAvailable;
    public EnumAvailabilityQuest quest2Available;
    public EnumAvailabilityQuest quest3Available;
    public EnumAvailabilityQuest quest4Available;
    public int questId;
    public int quest2Id;
    public int quest3Id;
    public int quest4Id;
    public EnumDayTime daytime;
    public int factionId;
    public int faction2Id;
    public EnumAvailabilityFactionType factionAvailable;
    public EnumAvailabilityFactionType faction2Available;
    public EnumAvailabilityFaction factionStance;
    public EnumAvailabilityFaction faction2Stance;
    public EnumAvailabilityScoreboard scoreboardType;
    public EnumAvailabilityScoreboard scoreboard2Type;
    public String scoreboardObjective;
    public String scoreboard2Objective;
    public int scoreboardValue;
    public int scoreboard2Value;
    public int minPlayerLevel;
    private boolean hasOptions;
    
    public Availability() {
        this.version = VersionCompatibility.ModRev;
        this.dialogAvailable = EnumAvailabilityDialog.Always;
        this.dialog2Available = EnumAvailabilityDialog.Always;
        this.dialog3Available = EnumAvailabilityDialog.Always;
        this.dialog4Available = EnumAvailabilityDialog.Always;
        this.dialogId = -1;
        this.dialog2Id = -1;
        this.dialog3Id = -1;
        this.dialog4Id = -1;
        this.questAvailable = EnumAvailabilityQuest.Always;
        this.quest2Available = EnumAvailabilityQuest.Always;
        this.quest3Available = EnumAvailabilityQuest.Always;
        this.quest4Available = EnumAvailabilityQuest.Always;
        this.questId = -1;
        this.quest2Id = -1;
        this.quest3Id = -1;
        this.quest4Id = -1;
        this.daytime = EnumDayTime.Always;
        this.factionId = -1;
        this.faction2Id = -1;
        this.factionAvailable = EnumAvailabilityFactionType.Always;
        this.faction2Available = EnumAvailabilityFactionType.Always;
        this.factionStance = EnumAvailabilityFaction.Friendly;
        this.faction2Stance = EnumAvailabilityFaction.Friendly;
        this.scoreboardType = EnumAvailabilityScoreboard.EQUAL;
        this.scoreboard2Type = EnumAvailabilityScoreboard.EQUAL;
        this.scoreboardObjective = "";
        this.scoreboard2Objective = "";
        this.scoreboardValue = 1;
        this.scoreboard2Value = 1;
        this.minPlayerLevel = 0;
        this.hasOptions = false;
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        this.version = compound.getInteger("ModRev");
        VersionCompatibility.CheckAvailabilityCompatibility(this, compound);
        this.dialogAvailable = EnumAvailabilityDialog.values()[compound.getInteger("AvailabilityDialog")];
        this.dialog2Available = EnumAvailabilityDialog.values()[compound.getInteger("AvailabilityDialog2")];
        this.dialog3Available = EnumAvailabilityDialog.values()[compound.getInteger("AvailabilityDialog3")];
        this.dialog4Available = EnumAvailabilityDialog.values()[compound.getInteger("AvailabilityDialog4")];
        this.dialogId = compound.getInteger("AvailabilityDialogId");
        this.dialog2Id = compound.getInteger("AvailabilityDialog2Id");
        this.dialog3Id = compound.getInteger("AvailabilityDialog3Id");
        this.dialog4Id = compound.getInteger("AvailabilityDialog4Id");
        this.questAvailable = EnumAvailabilityQuest.values()[compound.getInteger("AvailabilityQuest")];
        this.quest2Available = EnumAvailabilityQuest.values()[compound.getInteger("AvailabilityQuest2")];
        this.quest3Available = EnumAvailabilityQuest.values()[compound.getInteger("AvailabilityQuest3")];
        this.quest4Available = EnumAvailabilityQuest.values()[compound.getInteger("AvailabilityQuest4")];
        this.questId = compound.getInteger("AvailabilityQuestId");
        this.quest2Id = compound.getInteger("AvailabilityQuest2Id");
        this.quest3Id = compound.getInteger("AvailabilityQuest3Id");
        this.quest4Id = compound.getInteger("AvailabilityQuest4Id");
        this.setFactionAvailability(compound.getInteger("AvailabilityFaction"));
        this.setFactionAvailabilityStance(compound.getInteger("AvailabilityFactionStance"));
        this.setFaction2Availability(compound.getInteger("AvailabilityFaction2"));
        this.setFaction2AvailabilityStance(compound.getInteger("AvailabilityFaction2Stance"));
        this.factionId = compound.getInteger("AvailabilityFactionId");
        this.faction2Id = compound.getInteger("AvailabilityFaction2Id");
        this.scoreboardObjective = compound.getString("AvailabilityScoreboardObjective");
        this.scoreboard2Objective = compound.getString("AvailabilityScoreboard2Objective");
        this.initScore(this.scoreboardObjective);
        this.initScore(this.scoreboard2Objective);
        this.scoreboardType = EnumAvailabilityScoreboard.values()[compound.getInteger("AvailabilityScoreboardType")];
        this.scoreboard2Type = EnumAvailabilityScoreboard.values()[compound.getInteger("AvailabilityScoreboard2Type")];
        this.scoreboardValue = compound.getInteger("AvailabilityScoreboardValue");
        this.scoreboard2Value = compound.getInteger("AvailabilityScoreboard2Value");
        this.daytime = EnumDayTime.values()[compound.getInteger("AvailabilityDayTime")];
        this.minPlayerLevel = compound.getInteger("AvailabilityMinPlayerLevel");
        this.hasOptions = this.checkHasOptions();
    }
    
    private void initScore(String objective) {
        if (objective.isEmpty()) {
            return;
        }
        if (CustomNpcs.Server == null) {
            return;
        }
        Availability.scores.add(objective);
        for (WorldServer world : CustomNpcs.Server.worlds) {
            ServerScoreboard board = (ServerScoreboard)world.getScoreboard();
            ScoreObjective so = board.getObjective(objective);
            if (so != null) {
                Set<ScoreObjective> addedObjectives = (Set<ScoreObjective>)ObfuscationReflectionHelper.getPrivateValue((Class)ServerScoreboard.class, (Object)board, 1);
                if (!addedObjectives.contains(so)) {
                    board.addObjective(so);
                }
            }
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("ModRev", this.version);
        compound.setInteger("AvailabilityDialog", this.dialogAvailable.ordinal());
        compound.setInteger("AvailabilityDialog2", this.dialog2Available.ordinal());
        compound.setInteger("AvailabilityDialog3", this.dialog3Available.ordinal());
        compound.setInteger("AvailabilityDialog4", this.dialog4Available.ordinal());
        compound.setInteger("AvailabilityDialogId", this.dialogId);
        compound.setInteger("AvailabilityDialog2Id", this.dialog2Id);
        compound.setInteger("AvailabilityDialog3Id", this.dialog3Id);
        compound.setInteger("AvailabilityDialog4Id", this.dialog4Id);
        compound.setInteger("AvailabilityQuest", this.questAvailable.ordinal());
        compound.setInteger("AvailabilityQuest2", this.quest2Available.ordinal());
        compound.setInteger("AvailabilityQuest3", this.quest3Available.ordinal());
        compound.setInteger("AvailabilityQuest4", this.quest4Available.ordinal());
        compound.setInteger("AvailabilityQuestId", this.questId);
        compound.setInteger("AvailabilityQuest2Id", this.quest2Id);
        compound.setInteger("AvailabilityQuest3Id", this.quest3Id);
        compound.setInteger("AvailabilityQuest4Id", this.quest4Id);
        compound.setInteger("AvailabilityFaction", this.factionAvailable.ordinal());
        compound.setInteger("AvailabilityFaction2", this.faction2Available.ordinal());
        compound.setInteger("AvailabilityFactionStance", this.factionStance.ordinal());
        compound.setInteger("AvailabilityFaction2Stance", this.faction2Stance.ordinal());
        compound.setInteger("AvailabilityFactionId", this.factionId);
        compound.setInteger("AvailabilityFaction2Id", this.faction2Id);
        compound.setString("AvailabilityScoreboardObjective", this.scoreboardObjective);
        compound.setString("AvailabilityScoreboard2Objective", this.scoreboard2Objective);
        compound.setInteger("AvailabilityScoreboardType", this.scoreboardType.ordinal());
        compound.setInteger("AvailabilityScoreboard2Type", this.scoreboard2Type.ordinal());
        compound.setInteger("AvailabilityScoreboardValue", this.scoreboardValue);
        compound.setInteger("AvailabilityScoreboard2Value", this.scoreboard2Value);
        compound.setInteger("AvailabilityDayTime", this.daytime.ordinal());
        compound.setInteger("AvailabilityMinPlayerLevel", this.minPlayerLevel);
        return compound;
    }
    
    public void setFactionAvailability(int value) {
        this.factionAvailable = EnumAvailabilityFactionType.values()[value];
        this.hasOptions = this.checkHasOptions();
    }
    
    public void setFaction2Availability(int value) {
        this.faction2Available = EnumAvailabilityFactionType.values()[value];
        this.hasOptions = this.checkHasOptions();
    }
    
    public void setFactionAvailabilityStance(int integer) {
        this.factionStance = EnumAvailabilityFaction.values()[integer];
    }
    
    public void setFaction2AvailabilityStance(int integer) {
        this.faction2Stance = EnumAvailabilityFaction.values()[integer];
    }
    
    public boolean isAvailable(EntityPlayer player) {
        if (!this.hasOptions) {
            return true;
        }
        if (this.daytime == EnumDayTime.Day) {
            long time = player.world.getWorldTime() % 24000L;
            if (time > 12000L) {
                return false;
            }
        }
        if (this.daytime == EnumDayTime.Night) {
            long time = player.world.getWorldTime() % 24000L;
            if (time < 12000L) {
                return false;
            }
        }
        return this.dialogAvailable(this.dialogId, this.dialogAvailable, player) && this.dialogAvailable(this.dialog2Id, this.dialog2Available, player) && this.dialogAvailable(this.dialog3Id, this.dialog3Available, player) && this.dialogAvailable(this.dialog4Id, this.dialog4Available, player) && this.questAvailable(this.questId, this.questAvailable, player) && this.questAvailable(this.quest2Id, this.quest2Available, player) && this.questAvailable(this.quest3Id, this.quest3Available, player) && this.questAvailable(this.quest4Id, this.quest4Available, player) && this.factionAvailable(this.factionId, this.factionStance, this.factionAvailable, player) && this.factionAvailable(this.faction2Id, this.faction2Stance, this.faction2Available, player) && this.scoreboardAvailable(player, this.scoreboardObjective, this.scoreboardType, this.scoreboardValue) && this.scoreboardAvailable(player, this.scoreboard2Objective, this.scoreboard2Type, this.scoreboard2Value) && player.experienceLevel >= this.minPlayerLevel;
    }
    
    private boolean scoreboardAvailable(EntityPlayer player, String objective, EnumAvailabilityScoreboard type, int value) {
        if (objective.isEmpty()) {
            return true;
        }
        ScoreObjective sbObjective = player.getWorldScoreboard().getObjective(objective);
        if (sbObjective == null) {
            return false;
        }
        if (!player.getWorldScoreboard().entityHasObjective(player.getName(), sbObjective)) {
            return false;
        }
        int i = player.getWorldScoreboard().getOrCreateScore(player.getName(), sbObjective).getScorePoints();
        if (type == EnumAvailabilityScoreboard.EQUAL) {
            return i == value;
        }
        if (type == EnumAvailabilityScoreboard.BIGGER) {
            return i > value;
        }
        return i < value;
    }
    
    private boolean factionAvailable(int id, EnumAvailabilityFaction stance, EnumAvailabilityFactionType available, EntityPlayer player) {
        if (available == EnumAvailabilityFactionType.Always) {
            return true;
        }
        Faction faction = FactionController.instance.getFaction(id);
        if (faction == null) {
            return true;
        }
        PlayerFactionData data = PlayerData.get(player).factionData;
        int points = data.getFactionPoints(player, id);
        EnumAvailabilityFaction current = EnumAvailabilityFaction.Neutral;
        if (points < faction.neutralPoints) {
            current = EnumAvailabilityFaction.Hostile;
        }
        if (points >= faction.friendlyPoints) {
            current = EnumAvailabilityFaction.Friendly;
        }
        return (available == EnumAvailabilityFactionType.Is && stance == current) || (available == EnumAvailabilityFactionType.IsNot && stance != current);
    }
    
    public boolean dialogAvailable(int id, EnumAvailabilityDialog en, EntityPlayer player) {
        if (en == EnumAvailabilityDialog.Always) {
            return true;
        }
        boolean hasRead = PlayerData.get(player).dialogData.dialogsRead.contains(id);
        return (hasRead && en == EnumAvailabilityDialog.After) || (!hasRead && en == EnumAvailabilityDialog.Before);
    }
    
    public boolean questAvailable(int id, EnumAvailabilityQuest en, EntityPlayer player) {
        return en == EnumAvailabilityQuest.Always || (en == EnumAvailabilityQuest.After && PlayerQuestController.isQuestFinished(player, id)) || (en == EnumAvailabilityQuest.Before && !PlayerQuestController.isQuestFinished(player, id)) || (en == EnumAvailabilityQuest.Active && PlayerQuestController.isQuestActive(player, id)) || (en == EnumAvailabilityQuest.NotActive && !PlayerQuestController.isQuestActive(player, id)) || (en == EnumAvailabilityQuest.Completed && PlayerQuestController.isQuestCompleted(player, id)) || (en == EnumAvailabilityQuest.CanStart && PlayerQuestController.canQuestBeAccepted(player, id));
    }
    
    @Override
    public int getVersion() {
        return this.version;
    }
    
    @Override
    public void setVersion(int version) {
        this.version = version;
    }
    
    @Override
    public boolean isAvailable(IPlayer player) {
        return this.isAvailable(player.getMCEntity());
    }
    
    @Override
    public int getDaytime() {
        return this.daytime.ordinal();
    }
    
    @Override
    public void setDaytime(int type) {
        this.daytime = EnumDayTime.values()[MathHelper.clamp(type, 0, 2)];
        this.hasOptions = this.checkHasOptions();
    }
    
    @Override
    public int getMinPlayerLevel() {
        return this.minPlayerLevel;
    }
    
    @Override
    public void setMinPlayerLevel(int level) {
        this.minPlayerLevel = level;
        this.hasOptions = this.checkHasOptions();
    }
    
    @Override
    public int getDialog(int i) {
        if (i < 0 && i > 3) {
            throw new CustomNPCsException(i + " isnt between 0 and 3", new Object[0]);
        }
        if (i == 0) {
            return this.dialogId;
        }
        if (i == 1) {
            return this.dialog2Id;
        }
        if (i == 2) {
            return this.dialog3Id;
        }
        return this.dialog4Id;
    }
    
    @Override
    public void setDialog(int i, int id, int type) {
        if (i < 0 && i > 3) {
            throw new CustomNPCsException(i + " isnt between 0 and 3", new Object[0]);
        }
        EnumAvailabilityDialog e = EnumAvailabilityDialog.values()[MathHelper.clamp(type, 0, 2)];
        if (i == 0) {
            this.dialogId = id;
            this.dialogAvailable = e;
        }
        else if (i == 1) {
            this.dialog2Id = id;
            this.dialog2Available = e;
        }
        else if (i == 2) {
            this.dialog3Id = id;
            this.dialog3Available = e;
        }
        else if (i == 3) {
            this.dialog4Id = id;
            this.dialog4Available = e;
        }
        this.hasOptions = this.checkHasOptions();
    }
    
    @Override
    public void removeDialog(int i) {
        if (i < 0 && i > 3) {
            throw new CustomNPCsException(i + " isnt between 0 and 3", new Object[0]);
        }
        if (i == 0) {
            this.dialogId = -1;
            this.dialogAvailable = EnumAvailabilityDialog.Always;
        }
        else if (i == 1) {
            this.dialog2Id = -1;
            this.dialog2Available = EnumAvailabilityDialog.Always;
        }
        else if (i == 2) {
            this.dialog3Id = -1;
            this.dialog3Available = EnumAvailabilityDialog.Always;
        }
        else if (i == 3) {
            this.dialog4Id = -1;
            this.dialog4Available = EnumAvailabilityDialog.Always;
        }
        this.hasOptions = this.checkHasOptions();
    }
    
    @Override
    public int getQuest(int i) {
        if (i < 0 && i > 3) {
            throw new CustomNPCsException(i + " isnt between 0 and 3", new Object[0]);
        }
        if (i == 0) {
            return this.questId;
        }
        if (i == 1) {
            return this.quest2Id;
        }
        if (i == 2) {
            return this.quest3Id;
        }
        return this.quest4Id;
    }
    
    @Override
    public void setQuest(int i, int id, int type) {
        if (i < 0 && i > 3) {
            throw new CustomNPCsException(i + " isnt between 0 and 3", new Object[0]);
        }
        EnumAvailabilityQuest e = EnumAvailabilityQuest.values()[MathHelper.clamp(type, 0, 5)];
        if (i == 0) {
            this.questId = id;
            this.questAvailable = e;
        }
        else if (i == 1) {
            this.quest2Id = id;
            this.quest2Available = e;
        }
        else if (i == 2) {
            this.quest3Id = id;
            this.quest3Available = e;
        }
        else if (i == 3) {
            this.quest4Id = id;
            this.quest4Available = e;
        }
        this.hasOptions = this.checkHasOptions();
    }
    
    @Override
    public void removeQuest(int i) {
        if (i < 0 && i > 3) {
            throw new CustomNPCsException(i + " isnt between 0 and 3", new Object[0]);
        }
        if (i == 0) {
            this.questId = -1;
            this.questAvailable = EnumAvailabilityQuest.Always;
        }
        else if (i == 1) {
            this.quest2Id = -1;
            this.quest2Available = EnumAvailabilityQuest.Always;
        }
        else if (i == 2) {
            this.quest3Id = -1;
            this.quest3Available = EnumAvailabilityQuest.Always;
        }
        else if (i == 3) {
            this.quest4Id = -1;
            this.quest4Available = EnumAvailabilityQuest.Always;
        }
        this.hasOptions = this.checkHasOptions();
    }
    
    @Override
    public void setFaction(int i, int id, int type, int stance) {
        if (i < 0 && i > 1) {
            throw new CustomNPCsException(i + " isnt between 0 and 1", new Object[0]);
        }
        EnumAvailabilityFactionType e = EnumAvailabilityFactionType.values()[MathHelper.clamp(type, 0, 2)];
        EnumAvailabilityFaction ee = EnumAvailabilityFaction.values()[MathHelper.clamp(stance, 0, 2)];
        if (i == 0) {
            this.factionId = id;
            this.factionAvailable = e;
            this.factionStance = ee;
        }
        else if (i == 1) {
            this.faction2Id = id;
            this.faction2Available = e;
            this.faction2Stance = ee;
        }
        this.hasOptions = this.checkHasOptions();
    }
    
    @Override
    public void setScoreboard(int i, String objective, int type, int value) {
        if (i < 0 && i > 1) {
            throw new CustomNPCsException(i + " isnt between 0 and 1", new Object[0]);
        }
        if (objective == null) {
            objective = "";
        }
        EnumAvailabilityScoreboard e = EnumAvailabilityScoreboard.values()[MathHelper.clamp(type, 0, 2)];
        if (i == 0) {
            this.scoreboardObjective = objective;
            this.scoreboardType = e;
            this.scoreboardValue = value;
        }
        else if (i == 1) {
            this.scoreboard2Objective = objective;
            this.scoreboard2Type = e;
            this.scoreboard2Value = value;
        }
        this.hasOptions = this.checkHasOptions();
    }
    
    @Override
    public void removeFaction(int i) {
        if (i < 0 && i > 1) {
            throw new CustomNPCsException(i + " isnt between 0 and 1", new Object[0]);
        }
        if (i == 0) {
            this.factionId = -1;
            this.factionAvailable = EnumAvailabilityFactionType.Always;
            this.factionStance = EnumAvailabilityFaction.Friendly;
        }
        else if (i == 1) {
            this.faction2Id = -1;
            this.faction2Available = EnumAvailabilityFactionType.Always;
            this.faction2Stance = EnumAvailabilityFaction.Friendly;
        }
        this.hasOptions = this.checkHasOptions();
    }
    
    private boolean checkHasOptions() {
        return this.dialogAvailable != EnumAvailabilityDialog.Always || this.dialog2Available != EnumAvailabilityDialog.Always || this.dialog3Available != EnumAvailabilityDialog.Always || this.dialog4Available != EnumAvailabilityDialog.Always || (this.questAvailable != EnumAvailabilityQuest.Always || this.quest2Available != EnumAvailabilityQuest.Always || this.quest3Available != EnumAvailabilityQuest.Always || this.quest4Available != EnumAvailabilityQuest.Always) || (this.daytime != EnumDayTime.Always || this.minPlayerLevel > 0) || (this.factionAvailable != EnumAvailabilityFactionType.Always || this.faction2Available != EnumAvailabilityFactionType.Always) || (!this.scoreboardObjective.isEmpty() || !this.scoreboard2Objective.isEmpty());
    }
    
    public boolean hasOptions() {
        return this.hasOptions;
    }
    
    static {
        Availability.scores = new HashSet<String>();
    }
}
