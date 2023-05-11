package noppes.npcs.entity.data;

import noppes.npcs.roles.JobFarmer;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.roles.JobPuppet;
import noppes.npcs.roles.JobChunkLoader;
import noppes.npcs.roles.JobConversation;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.roles.JobFollower;
import noppes.npcs.roles.JobItemGiver;
import noppes.npcs.roles.JobGuard;
import noppes.npcs.roles.JobHealer;
import noppes.npcs.roles.JobBard;
import noppes.npcs.roles.RoleDialog;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.RolePostman;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.roles.RoleBank;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.util.ValueUtil;
import java.util.Iterator;
import noppes.npcs.controllers.data.DialogOption;
import java.util.HashMap;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.FactionOptions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.api.entity.data.INPCAdvanced;

public class DataAdvanced implements INPCAdvanced
{
    public Lines interactLines;
    public Lines worldLines;
    public Lines attackLines;
    public Lines killedLines;
    public Lines killLines;
    public Lines npcInteractLines;
    public boolean orderedLines;
    private String idleSound;
    private String angrySound;
    private String hurtSound;
    private String deathSound;
    private String stepSound;
    private EntityNPCInterface npc;
    public FactionOptions factions;
    public int role;
    public int job;
    public boolean attackOtherFactions;
    public boolean defendFaction;
    public boolean disablePitch;
    public DataScenes scenes;
    
    public DataAdvanced(EntityNPCInterface npc) {
        this.interactLines = new Lines();
        this.worldLines = new Lines();
        this.attackLines = new Lines();
        this.killedLines = new Lines();
        this.killLines = new Lines();
        this.npcInteractLines = new Lines();
        this.orderedLines = false;
        this.idleSound = "";
        this.angrySound = "";
        this.hurtSound = "minecraft:entity.player.hurt";
        this.deathSound = "minecraft:entity.player.hurt";
        this.stepSound = "";
        this.factions = new FactionOptions();
        this.role = 0;
        this.job = 0;
        this.attackOtherFactions = false;
        this.defendFaction = false;
        this.disablePitch = false;
        this.npc = npc;
        this.scenes = new DataScenes(npc);
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("NpcLines", (NBTBase)this.worldLines.writeToNBT());
        compound.setTag("NpcKilledLines", (NBTBase)this.killedLines.writeToNBT());
        compound.setTag("NpcInteractLines", (NBTBase)this.interactLines.writeToNBT());
        compound.setTag("NpcAttackLines", (NBTBase)this.attackLines.writeToNBT());
        compound.setTag("NpcKillLines", (NBTBase)this.killLines.writeToNBT());
        compound.setTag("NpcInteractNPCLines", (NBTBase)this.npcInteractLines.writeToNBT());
        compound.setBoolean("OrderedLines", this.orderedLines);
        compound.setString("NpcIdleSound", this.idleSound);
        compound.setString("NpcAngrySound", this.angrySound);
        compound.setString("NpcHurtSound", this.hurtSound);
        compound.setString("NpcDeathSound", this.deathSound);
        compound.setString("NpcStepSound", this.stepSound);
        compound.setInteger("FactionID", this.npc.getFaction().id);
        compound.setBoolean("AttackOtherFactions", this.attackOtherFactions);
        compound.setBoolean("DefendFaction", this.defendFaction);
        compound.setBoolean("DisablePitch", this.disablePitch);
        compound.setInteger("Role", this.role);
        compound.setInteger("NpcJob", this.job);
        compound.setTag("FactionPoints", (NBTBase)this.factions.writeToNBT(new NBTTagCompound()));
        compound.setTag("NPCDialogOptions", (NBTBase)this.nbtDialogs(this.npc.dialogs));
        compound.setTag("NpcScenes", (NBTBase)this.scenes.writeToNBT(new NBTTagCompound()));
        return compound;
    }
    
    public void readToNBT(NBTTagCompound compound) {
        this.interactLines.readNBT(compound.getCompoundTag("NpcInteractLines"));
        this.worldLines.readNBT(compound.getCompoundTag("NpcLines"));
        this.attackLines.readNBT(compound.getCompoundTag("NpcAttackLines"));
        this.killedLines.readNBT(compound.getCompoundTag("NpcKilledLines"));
        this.killLines.readNBT(compound.getCompoundTag("NpcKillLines"));
        this.npcInteractLines.readNBT(compound.getCompoundTag("NpcInteractNPCLines"));
        this.orderedLines = compound.getBoolean("OrderedLines");
        this.idleSound = compound.getString("NpcIdleSound");
        this.angrySound = compound.getString("NpcAngrySound");
        this.hurtSound = compound.getString("NpcHurtSound");
        this.deathSound = compound.getString("NpcDeathSound");
        this.stepSound = compound.getString("NpcStepSound");
        this.npc.setFaction(compound.getInteger("FactionID"));
        this.npc.faction = this.npc.getFaction();
        this.attackOtherFactions = compound.getBoolean("AttackOtherFactions");
        this.defendFaction = compound.getBoolean("DefendFaction");
        this.disablePitch = compound.getBoolean("DisablePitch");
        this.setRole(compound.getInteger("Role"));
        this.setJob(compound.getInteger("NpcJob"));
        this.factions.readFromNBT(compound.getCompoundTag("FactionPoints"));
        this.npc.dialogs = this.getDialogs(compound.getTagList("NPCDialogOptions", 10));
        this.scenes.readFromNBT(compound.getCompoundTag("NpcScenes"));
    }
    
    private HashMap<Integer, DialogOption> getDialogs(NBTTagList tagList) {
        HashMap<Integer, DialogOption> map = new HashMap<Integer, DialogOption>();
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
            int slot = nbttagcompound.getInteger("DialogSlot");
            DialogOption option = new DialogOption();
            option.readNBT(nbttagcompound.getCompoundTag("NPCDialog"));
            option.optionType = 1;
            map.put(slot, option);
        }
        return map;
    }
    
    private NBTTagList nbtDialogs(HashMap<Integer, DialogOption> dialogs2) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int slot : dialogs2.keySet()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("DialogSlot", slot);
            nbttagcompound.setTag("NPCDialog", (NBTBase)dialogs2.get(slot).writeNBT());
            nbttaglist.appendTag((NBTBase)nbttagcompound);
        }
        return nbttaglist;
    }
    
    private Lines getLines(int type) {
        if (type == 0) {
            return this.interactLines;
        }
        if (type == 1) {
            return this.attackLines;
        }
        if (type == 2) {
            return this.worldLines;
        }
        if (type == 3) {
            return this.killedLines;
        }
        if (type == 4) {
            return this.killLines;
        }
        if (type == 5) {
            return this.npcInteractLines;
        }
        return null;
    }
    
    @Override
    public void setLine(int type, int slot, String text, String sound) {
        slot = ValueUtil.CorrectInt(slot, 0, 7);
        Lines lines = this.getLines(type);
        if (text == null || text.isEmpty()) {
            lines.lines.remove(slot);
        }
        else {
            Line line = lines.lines.get(slot);
            if (line == null) {
                lines.lines.put(slot, line = new Line());
            }
            line.setText(text);
            line.setSound(sound);
        }
    }
    
    @Override
    public String getLine(int type, int slot) {
        Line line = this.getLines(type).lines.get(slot);
        if (line == null) {
            return null;
        }
        return line.getText();
    }
    
    @Override
    public int getLineCount(int type) {
        return this.getLines(type).lines.size();
    }
    
    @Override
    public String getSound(int type) {
        String sound = null;
        if (type == 0) {
            sound = this.idleSound;
        }
        else if (type == 1) {
            sound = this.angrySound;
        }
        else if (type == 2) {
            sound = this.hurtSound;
        }
        else if (type == 3) {
            sound = this.deathSound;
        }
        else if (type == 4) {
            sound = this.stepSound;
        }
        if (sound != null && sound.isEmpty()) {
            return null;
        }
        return sound;
    }
    
    public void playSound(int type, float volume, float pitch) {
        String sound = this.getSound(type);
        if (sound == null) {
            return;
        }
        BlockPos pos = this.npc.getPosition();
        Server.sendRangedData((Entity)this.npc, 16, EnumPacketClient.PLAY_SOUND, sound, pos.getX(), pos.getY(), pos.getZ(), volume, pitch);
    }
    
    @Override
    public void setSound(int type, String sound) {
        if (sound == null) {
            sound = "";
        }
        if (type == 0) {
            this.idleSound = sound;
        }
        else if (type == 1) {
            this.angrySound = sound;
        }
        else if (type == 2) {
            this.hurtSound = sound;
        }
        else if (type == 3) {
            this.deathSound = sound;
        }
        else if (type == 4) {
            this.stepSound = sound;
        }
    }
    
    public Line getInteractLine() {
        return this.interactLines.getLine(!this.orderedLines);
    }
    
    public Line getAttackLine() {
        return this.attackLines.getLine(!this.orderedLines);
    }
    
    public Line getKilledLine() {
        return this.killedLines.getLine(!this.orderedLines);
    }
    
    public Line getKillLine() {
        return this.killLines.getLine(!this.orderedLines);
    }
    
    public Line getWorldLine() {
        return this.worldLines.getLine(!this.orderedLines);
    }
    
    public Line getNPCInteractLine() {
        return this.npcInteractLines.getLine(!this.orderedLines);
    }
    
    public void setRole(int i) {
        if (8 <= i) {
            i -= 2;
        }
        this.role = i % 8;
        if (this.role == 0) {
            this.npc.roleInterface = null;
        }
        else if (this.role == 3 && !(this.npc.roleInterface instanceof RoleBank)) {
            this.npc.roleInterface = new RoleBank(this.npc);
        }
        else if (this.role == 2 && !(this.npc.roleInterface instanceof RoleFollower)) {
            this.npc.roleInterface = new RoleFollower(this.npc);
        }
        else if (this.role == 5 && !(this.npc.roleInterface instanceof RolePostman)) {
            this.npc.roleInterface = new RolePostman(this.npc);
        }
        else if (this.role == 1 && !(this.npc.roleInterface instanceof RoleTrader)) {
            this.npc.roleInterface = new RoleTrader(this.npc);
        }
        else if (this.role == 4 && !(this.npc.roleInterface instanceof RoleTransporter)) {
            this.npc.roleInterface = new RoleTransporter(this.npc);
        }
        else if (this.role == 6 && !(this.npc.roleInterface instanceof RoleCompanion)) {
            this.npc.roleInterface = new RoleCompanion(this.npc);
        }
        else if (this.role == 7 && !(this.npc.roleInterface instanceof RoleDialog)) {
            this.npc.roleInterface = new RoleDialog(this.npc);
        }
    }
    
    public void setJob(int i) {
        if (this.npc.jobInterface != null && !this.npc.world.isRemote) {
            this.npc.jobInterface.reset();
        }
        this.job = i % 12;
        if (this.job == 0) {
            this.npc.jobInterface = null;
        }
        else if (this.job == 1 && !(this.npc.jobInterface instanceof JobBard)) {
            this.npc.jobInterface = new JobBard(this.npc);
        }
        else if (this.job == 2 && !(this.npc.jobInterface instanceof JobHealer)) {
            this.npc.jobInterface = new JobHealer(this.npc);
        }
        else if (this.job == 3 && !(this.npc.jobInterface instanceof JobGuard)) {
            this.npc.jobInterface = new JobGuard(this.npc);
        }
        else if (this.job == 4 && !(this.npc.jobInterface instanceof JobItemGiver)) {
            this.npc.jobInterface = new JobItemGiver(this.npc);
        }
        else if (this.job == 5 && !(this.npc.jobInterface instanceof JobFollower)) {
            this.npc.jobInterface = new JobFollower(this.npc);
        }
        else if (this.job == 6 && !(this.npc.jobInterface instanceof JobSpawner)) {
            this.npc.jobInterface = new JobSpawner(this.npc);
        }
        else if (this.job == 7 && !(this.npc.jobInterface instanceof JobConversation)) {
            this.npc.jobInterface = new JobConversation(this.npc);
        }
        else if (this.job == 8 && !(this.npc.jobInterface instanceof JobChunkLoader)) {
            this.npc.jobInterface = new JobChunkLoader(this.npc);
        }
        else if (this.job == 9 && !(this.npc.jobInterface instanceof JobPuppet)) {
            this.npc.jobInterface = new JobPuppet(this.npc);
        }
        else if (this.job == 10 && !(this.npc.jobInterface instanceof JobBuilder)) {
            this.npc.jobInterface = new JobBuilder(this.npc);
        }
        else if (this.job == 11 && !(this.npc.jobInterface instanceof JobFarmer)) {
            this.npc.jobInterface = new JobFarmer(this.npc);
        }
    }
    
    public boolean hasWorldLines() {
        return !this.worldLines.isEmpty();
    }
}
