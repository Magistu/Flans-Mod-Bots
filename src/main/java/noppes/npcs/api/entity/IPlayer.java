package noppes.npcs.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.item.IItemStack;
import net.minecraft.entity.player.EntityPlayerMP;

public interface IPlayer<T extends EntityPlayerMP> extends IEntityLivingBase<T>
{
    String getDisplayName();
    
    boolean hasFinishedQuest(int p0);
    
    boolean hasActiveQuest(int p0);
    
    void startQuest(int p0);
    
    int factionStatus(int p0);
    
    void finishQuest(int p0);
    
    void stopQuest(int p0);
    
    void removeQuest(int p0);
    
    boolean hasReadDialog(int p0);
    
    void showDialog(int p0, String p1);
    
    void removeDialog(int p0);
    
    void addDialog(int p0);
    
    void addFactionPoints(int p0, int p1);
    
    int getFactionPoints(int p0);
    
    void message(String p0);
    
    int getGamemode();
    
    void setGamemode(int p0);
    
    @Deprecated
    int inventoryItemCount(IItemStack p0);
    
    @Deprecated
    int inventoryItemCount(String p0, int p1);
    
    IContainer getInventory();
    
    boolean removeItem(IItemStack p0, int p1);
    
    boolean removeItem(String p0, int p1, int p2);
    
    void removeAllItems(IItemStack p0);
    
    boolean giveItem(IItemStack p0);
    
    boolean giveItem(String p0, int p1, int p2);
    
    void setSpawnpoint(int p0, int p1, int p2);
    
    void resetSpawnpoint();
    
    boolean hasAchievement(String p0);
    
    int getExpLevel();
    
    void setExpLevel(int p0);
    
    boolean hasPermission(String p0);
    
    Object getPixelmonData();
    
    ITimers getTimers();
    
    void closeGui();
    
    T getMCEntity();
    
    IBlock getSpawnPoint();
    
    void setSpawnPoint(IBlock p0);
    
    int getHunger();
    
    void setHunger(int p0);
    
    void kick(String p0);
    
    void sendNotification(String p0, String p1, int p2);
    
    void sendMail(IPlayerMail p0);
    
    void clearData();
    
    IQuest[] getActiveQuests();
    
    IQuest[] getFinishedQuests();
    
    void updatePlayerInventory();
    
    void playSound(String p0, float p1, float p2);
    
    @Deprecated
    IContainer showChestGui(int p0);
    
    IContainer getOpenContainer();
    
    boolean canQuestBeAccepted(int p0);
    
    void showCustomGui(ICustomGui p0);
    
    ICustomGui getCustomGui();
}
