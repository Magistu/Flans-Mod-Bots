package noppes.npcs.api.entity.data;

import noppes.npcs.api.entity.IPlayer;

public interface INPCDisplay
{
    String getName();
    
    void setName(String p0);
    
    String getTitle();
    
    void setTitle(String p0);
    
    String getSkinUrl();
    
    void setSkinUrl(String p0);
    
    String getSkinPlayer();
    
    void setSkinPlayer(String p0);
    
    String getSkinTexture();
    
    void setSkinTexture(String p0);
    
    boolean getHasLivingAnimation();
    
    void setHasLivingAnimation(boolean p0);
    
    int getVisible();
    
    void setVisible(int p0);
    
    boolean isVisibleTo(IPlayer p0);
    
    int getBossbar();
    
    void setBossbar(int p0);
    
    int getSize();
    
    void setSize(int p0);
    
    int getTint();
    
    void setTint(int p0);
    
    int getShowName();
    
    void setShowName(int p0);
    
    void setCapeTexture(String p0);
    
    String getCapeTexture();
    
    void setOverlayTexture(String p0);
    
    String getOverlayTexture();
    
    void setModelScale(int p0, float p1, float p2, float p3);
    
    float[] getModelScale(int p0);
    
    int getBossColor();
    
    void setBossColor(int p0);
    
    void setModel(String p0);
    
    String getModel();
    
    void setHasHitbox(boolean p0);
    
    boolean getHasHitbox();
}
