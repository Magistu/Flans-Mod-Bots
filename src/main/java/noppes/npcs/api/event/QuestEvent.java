package noppes.npcs.api.event;

import noppes.npcs.api.item.IItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IQuest;

public class QuestEvent extends CustomNPCsEvent
{
    public IQuest quest;
    public IPlayer player;
    
    public QuestEvent(IPlayer player, IQuest quest) {
        this.quest = quest;
        this.player = player;
    }
    
    @Cancelable
    public static class QuestStartEvent extends QuestEvent
    {
        public QuestStartEvent(IPlayer player, IQuest quest) {
            super(player, quest);
        }
    }
    
    public static class QuestCompletedEvent extends QuestEvent
    {
        public QuestCompletedEvent(IPlayer player, IQuest quest) {
            super(player, quest);
        }
    }
    
    public static class QuestTurnedInEvent extends QuestEvent
    {
        public int expReward;
        public IItemStack[] itemRewards;
        
        public QuestTurnedInEvent(IPlayer player, IQuest quest) {
            super(player, quest);
            this.itemRewards = new IItemStack[0];
        }
    }
}
