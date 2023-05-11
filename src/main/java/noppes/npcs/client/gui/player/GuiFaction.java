package noppes.npcs.client.gui.player;

import java.util.Iterator;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.controllers.data.PlayerFactionData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.CustomNpcResourceListener;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.GlStateManager;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabFactions;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.constants.EnumPlayerPacket;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiButtonNextPage;
import noppes.npcs.controllers.data.Faction;
import java.util.ArrayList;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.GuiNPCInterface;

public class GuiFaction extends GuiNPCInterface implements IGuiData
{
    private int xSize;
    private int ySize;
    private int guiLeft;
    private int guiTop;
    private ArrayList<Faction> playerFactions;
    private int page;
    private int pages;
    private GuiButtonNextPage buttonNextPage;
    private GuiButtonNextPage buttonPreviousPage;
    private ResourceLocation indicator;
    
    public GuiFaction() {
        this.playerFactions = new ArrayList<Faction>();
        this.page = 0;
        this.pages = 1;
        this.xSize = 200;
        this.ySize = 195;
        this.drawDefaultBackground = false;
        this.title = "";
        NoppesUtilPlayer.sendData(EnumPlayerPacket.FactionsGet, new Object[0]);
        this.indicator = this.getResource("standardbg.png");
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2 + 12;
        TabRegistry.updateTabValues(this.guiLeft, this.guiTop + 8, InventoryTabFactions.class);
        TabRegistry.addTabsToList(this.buttonList);
        this.buttonList.add(this.buttonNextPage = new GuiButtonNextPage(1, this.guiLeft + this.xSize - 43, this.guiTop + 180, true));
        this.buttonList.add(this.buttonPreviousPage = new GuiButtonNextPage(2, this.guiLeft + 20, this.guiTop + 180, false));
        this.updateButtons();
    }
    
    @Override
    public void drawScreen(int i, int j, float f) {
        this.drawDefaultBackground();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.indicator);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop + 8, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(this.guiLeft + 4, this.guiTop + 8, 56, 0, 200, this.ySize);
        if (this.playerFactions.isEmpty()) {
            String noFaction = I18n.translateToLocal("faction.nostanding");
            this.fontRenderer.drawString(noFaction, this.guiLeft + (this.xSize - this.fontRenderer.getStringWidth(noFaction)) / 2, this.guiTop + 80, CustomNpcResourceListener.DefaultTextColor);
        }
        else {
            this.renderScreen();
        }
        super.drawScreen(i, j, f);
    }
    
    private void renderScreen() {
        int size = 5;
        if (this.playerFactions.size() % 5 != 0 && this.page == this.pages) {
            size = this.playerFactions.size() % 5;
        }
        for (int id = 0; id < size; ++id) {
            this.drawHorizontalLine(this.guiLeft + 2, this.guiLeft + this.xSize, this.guiTop + 14 + id * 30, -16777216 + CustomNpcResourceListener.DefaultTextColor);
            Faction faction = this.playerFactions.get((this.page - 1) * 5 + id);
            String name = faction.name;
            String points = " : " + faction.defaultPoints;
            String standing = I18n.translateToLocal("faction.friendly");
            int color = 65280;
            if (faction.defaultPoints < faction.neutralPoints) {
                standing = I18n.translateToLocal("faction.unfriendly");
                color = 16711680;
                points = points + "/" + faction.neutralPoints;
            }
            else if (faction.defaultPoints < faction.friendlyPoints) {
                standing = I18n.translateToLocal("faction.neutral");
                color = 15924992;
                points = points + "/" + faction.friendlyPoints;
            }
            else {
                points += "/-";
            }
            this.fontRenderer.drawString(name, this.guiLeft + (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, this.guiTop + 19 + id * 30, faction.color);
            this.fontRenderer.drawString(standing, this.width / 2 - this.fontRenderer.getStringWidth(standing) - 1, this.guiTop + 33 + id * 30, color);
            this.fontRenderer.drawString(points, this.width / 2, this.guiTop + 33 + id * 30, CustomNpcResourceListener.DefaultTextColor);
        }
        this.drawHorizontalLine(this.guiLeft + 2, this.guiLeft + this.xSize, this.guiTop + 14 + size * 30, -16777216 + CustomNpcResourceListener.DefaultTextColor);
        if (this.pages > 1) {
            String s = this.page + "/" + this.pages;
            this.fontRenderer.drawString(s, this.guiLeft + (this.xSize - this.fontRenderer.getStringWidth(s)) / 2, this.guiTop + 203, CustomNpcResourceListener.DefaultTextColor);
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (!(guibutton instanceof GuiButtonNextPage)) {
            return;
        }
        int id = guibutton.id;
        if (id == 1) {
            ++this.page;
        }
        if (id == 2) {
            --this.page;
        }
        this.updateButtons();
    }
    
    private void updateButtons() {
        this.buttonNextPage.setVisible(this.page < this.pages);
        this.buttonPreviousPage.setVisible(this.page > 1);
    }
    
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
    }
    
    @Override
    public void keyTyped(char c, int i) {
        if (i == 1 || this.isInventoryKey(i)) {
            this.close();
        }
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.playerFactions = new ArrayList<Faction>();
        NBTTagList list = compound.getTagList("FactionList", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            Faction faction = new Faction();
            faction.readNBT(list.getCompoundTagAt(i));
            this.playerFactions.add(faction);
        }
        PlayerFactionData data = new PlayerFactionData();
        data.loadNBTData(compound);
        for (int id : data.factionData.keySet()) {
            int points = data.factionData.get(id);
            for (Faction faction2 : this.playerFactions) {
                if (faction2.id == id) {
                    faction2.defaultPoints = points;
                }
            }
        }
        this.pages = (this.playerFactions.size() - 1) / 5;
        ++this.pages;
        this.page = 1;
        this.updateButtons();
    }
}
