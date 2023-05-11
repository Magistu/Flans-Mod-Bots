package noppes.npcs.controllers.data;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.controllers.FactionController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class FactionOptions
{
    public boolean decreaseFactionPoints;
    public boolean decreaseFaction2Points;
    public int factionId;
    public int faction2Id;
    public int factionPoints;
    public int faction2Points;
    
    public FactionOptions() {
        this.decreaseFactionPoints = false;
        this.decreaseFaction2Points = false;
        this.factionId = -1;
        this.faction2Id = -1;
        this.factionPoints = 100;
        this.faction2Points = 100;
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        this.factionId = compound.getInteger("OptionFactions1");
        this.faction2Id = compound.getInteger("OptionFactions2");
        this.decreaseFactionPoints = compound.getBoolean("DecreaseFaction1Points");
        this.decreaseFaction2Points = compound.getBoolean("DecreaseFaction2Points");
        this.factionPoints = compound.getInteger("OptionFaction1Points");
        this.faction2Points = compound.getInteger("OptionFaction2Points");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setInteger("OptionFactions1", this.factionId);
        par1NBTTagCompound.setInteger("OptionFactions2", this.faction2Id);
        par1NBTTagCompound.setInteger("OptionFaction1Points", this.factionPoints);
        par1NBTTagCompound.setInteger("OptionFaction2Points", this.faction2Points);
        par1NBTTagCompound.setBoolean("DecreaseFaction1Points", this.decreaseFactionPoints);
        par1NBTTagCompound.setBoolean("DecreaseFaction2Points", this.decreaseFaction2Points);
        return par1NBTTagCompound;
    }
    
    public boolean hasFaction(int id) {
        return this.factionId == id || this.faction2Id == id;
    }
    
    public void addPoints(EntityPlayer player) {
        if (this.factionId < 0 && this.faction2Id < 0) {
            return;
        }
        PlayerData playerdata = PlayerData.get(player);
        PlayerFactionData data = playerdata.factionData;
        if (this.factionId >= 0 && this.factionPoints > 0) {
            this.addPoints(player, data, this.factionId, this.decreaseFactionPoints, this.factionPoints);
        }
        if (this.faction2Id >= 0 && this.faction2Points > 0) {
            this.addPoints(player, data, this.faction2Id, this.decreaseFaction2Points, this.faction2Points);
        }
        playerdata.save(true);
    }
    
    private void addPoints(EntityPlayer player, PlayerFactionData data, int factionId, boolean decrease, int points) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null) {
            return;
        }
        if (!faction.hideFaction) {
            String message = decrease ? "faction.decreasepoints" : "faction.increasepoints";
            player.sendMessage((ITextComponent)new TextComponentTranslation(message, new Object[] { faction.name, points }));
        }
        data.increasePoints(player, factionId, decrease ? (-points) : points);
    }
}
