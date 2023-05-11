package noppes.npcs.api.wrapper;

import java.util.Iterator;
import java.util.Collection;
import net.minecraft.scoreboard.Score;
import noppes.npcs.api.IScoreboardScore;
import noppes.npcs.api.CustomNPCsException;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreObjective;
import noppes.npcs.api.IScoreboardObjective;

public class ScoreboardObjectiveWrapper implements IScoreboardObjective
{
    private ScoreObjective objective;
    private Scoreboard board;
    
    protected ScoreboardObjectiveWrapper(Scoreboard board, ScoreObjective objective) {
        this.objective = objective;
        this.board = board;
    }
    
    @Override
    public String getName() {
        return this.objective.getName();
    }
    
    @Override
    public String getDisplayName() {
        return this.objective.getDisplayName();
    }
    
    @Override
    public void setDisplayName(String name) {
        if (name.length() <= 0 || name.length() > 32) {
            throw new CustomNPCsException("Score objective display name must be between 1-32 characters: %s", new Object[] { name });
        }
        this.objective.setDisplayName(name);
    }
    
    @Override
    public String getCriteria() {
        return this.objective.getCriteria().getName();
    }
    
    @Override
    public boolean isReadyOnly() {
        return this.objective.getCriteria().isReadOnly();
    }
    
    @Override
    public IScoreboardScore[] getScores() {
        Collection<Score> list = (Collection<Score>)this.board.getSortedScores(this.objective);
        IScoreboardScore[] scores = new IScoreboardScore[list.size()];
        int i = 0;
        for (Score score : list) {
            scores[i] = new ScoreboardScoreWrapper(score);
            ++i;
        }
        return scores;
    }
    
    @Override
    public IScoreboardScore getScore(String player) {
        if (!this.hasScore(player)) {
            return null;
        }
        return new ScoreboardScoreWrapper(this.board.getOrCreateScore(player, this.objective));
    }
    
    @Override
    public IScoreboardScore createScore(String player) {
        return new ScoreboardScoreWrapper(this.board.getOrCreateScore(player, this.objective));
    }
    
    @Override
    public void removeScore(String player) {
        this.board.removeObjectiveFromEntity(player, this.objective);
    }
    
    @Override
    public boolean hasScore(String player) {
        return this.board.entityHasObjective(player, this.objective);
    }
}
