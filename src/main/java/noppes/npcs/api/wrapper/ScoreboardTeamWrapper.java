package noppes.npcs.api.wrapper;

import net.minecraft.util.text.TextFormatting;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import noppes.npcs.api.CustomNPCsException;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScorePlayerTeam;
import noppes.npcs.api.IScoreboardTeam;

public class ScoreboardTeamWrapper implements IScoreboardTeam
{
    private ScorePlayerTeam team;
    private Scoreboard board;
    
    protected ScoreboardTeamWrapper(ScorePlayerTeam team, Scoreboard board) {
        this.team = team;
        this.board = board;
    }
    
    @Override
    public String getName() {
        return this.team.getName();
    }
    
    @Override
    public String getDisplayName() {
        return this.team.getDisplayName();
    }
    
    @Override
    public void setDisplayName(String name) {
        if (name.length() <= 0 || name.length() > 32) {
            throw new CustomNPCsException("Score team display name must be between 1-32 characters: %s", new Object[] { name });
        }
        this.team.setDisplayName(name);
    }
    
    @Override
    public void addPlayer(String player) {
        this.board.addPlayerToTeam(player, this.getName());
    }
    
    @Override
    public void removePlayer(String player) {
        this.board.removePlayerFromTeam(player, this.team);
    }
    
    @Override
    public String[] getPlayers() {
        List<String> list = new ArrayList<String>(this.team.getMembershipCollection());
        return list.toArray(new String[list.size()]);
    }
    
    @Override
    public void clearPlayers() {
        List<String> list = new ArrayList<String>(this.team.getMembershipCollection());
        for (String player : list) {
            this.board.removePlayerFromTeam(player, this.team);
        }
    }
    
    @Override
    public boolean getFriendlyFire() {
        return this.team.getAllowFriendlyFire();
    }
    
    @Override
    public void setFriendlyFire(boolean bo) {
        this.team.setAllowFriendlyFire(bo);
    }
    
    @Override
    public void setColor(String color) {
        TextFormatting enumchatformatting = TextFormatting.getValueByName(color);
        if (enumchatformatting == null || enumchatformatting.isFancyStyling()) {
            throw new CustomNPCsException("Not a proper color name: %s", new Object[] { color });
        }
        this.team.setPrefix(enumchatformatting.toString());
        this.team.setSuffix(TextFormatting.RESET.toString());
    }
    
    @Override
    public String getColor() {
        String prefix = this.team.getPrefix();
        if (prefix == null || prefix.isEmpty()) {
            return null;
        }
        for (TextFormatting format : TextFormatting.values()) {
            if (prefix.equals(format.toString()) && format != TextFormatting.RESET) {
                return format.getFriendlyName();
            }
        }
        return null;
    }
    
    @Override
    public void setSeeInvisibleTeamPlayers(boolean bo) {
        this.team.setSeeFriendlyInvisiblesEnabled(bo);
    }
    
    @Override
    public boolean getSeeInvisibleTeamPlayers() {
        return this.team.getSeeFriendlyInvisiblesEnabled();
    }
    
    @Override
    public boolean hasPlayer(String player) {
        return this.board.getPlayersTeam(player) != null;
    }
}
