package noppes.npcs.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import noppes.npcs.controllers.data.PlayerQuestData;
import java.util.Iterator;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.quests.QuestLocation;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.controllers.data.PlayerData;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import net.minecraft.util.ITickable;

public class TileWaypoint extends TileNpcEntity implements ITickable
{
    public String name;
    private int ticks;
    private List<EntityPlayer> recentlyChecked;
    private List<EntityPlayer> toCheck;
    public int range;
    
    public TileWaypoint() {
        this.name = "";
        this.ticks = 10;
        this.recentlyChecked = new ArrayList<EntityPlayer>();
        this.range = 10;
    }
    
    public void update() {
        if (this.world.isRemote || this.name.isEmpty()) {
            return;
        }
        --this.ticks;
        if (this.ticks > 0) {
            return;
        }
        this.ticks = 10;
        (this.toCheck = this.getPlayerList(this.range, this.range, this.range)).removeAll(this.recentlyChecked);
        List<EntityPlayer> listMax = this.getPlayerList(this.range + 10, this.range + 10, this.range + 10);
        this.recentlyChecked.retainAll(listMax);
        this.recentlyChecked.addAll(this.toCheck);
        if (this.toCheck.isEmpty()) {
            return;
        }
        for (EntityPlayer player : this.toCheck) {
            PlayerData pdata = PlayerData.get(player);
            PlayerQuestData playerdata = pdata.questData;
            for (QuestData data : playerdata.activeQuests.values()) {
                if (data.quest.type != 3) {
                    continue;
                }
                QuestLocation quest = (QuestLocation)data.quest.questInterface;
                if (!quest.setFound(data, this.name)) {
                    continue;
                }
                player.sendMessage((ITextComponent)new TextComponentTranslation(this.name + " " + I18n.translateToLocal("quest.found"), new Object[0]));
                playerdata.checkQuestCompletion(player, 3);
                pdata.updateClient = true;
            }
        }
    }
    
    private List<EntityPlayer> getPlayerList(int x, int y, int z) {
        return (List<EntityPlayer>)this.world.getEntitiesWithinAABB((Class)EntityPlayer.class, new AxisAlignedBB(this.pos, this.pos.add(1, 1, 1)).grow((double)x, (double)y, (double)z));
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.name = compound.getString("LocationName");
        this.range = compound.getInteger("LocationRange");
        if (this.range < 2) {
            this.range = 2;
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (!this.name.isEmpty()) {
            compound.setString("LocationName", this.name);
        }
        compound.setInteger("LocationRange", this.range);
        return super.writeToNBT(compound);
    }
}
