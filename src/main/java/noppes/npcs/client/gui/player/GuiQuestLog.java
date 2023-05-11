package noppes.npcs.client.gui.player;

import net.minecraft.util.math.MathHelper;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.api.handler.data.IQuestObjective;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.CustomNpcResourceListener;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.GuiButton;
import java.util.Iterator;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiButtonNextPage;
import net.minecraft.client.gui.GuiScreen;
import java.util.Comparator;
import java.util.Collections;
import noppes.npcs.util.NaturalOrderComparator;
import java.util.Collection;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabQuests;
import java.util.ArrayList;
import noppes.npcs.controllers.PlayerQuestController;
import net.minecraft.client.Minecraft;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.GuiMenuSideButton;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.Quest;
import java.util.List;
import java.util.HashMap;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.ITopButtonListener;
import noppes.npcs.client.gui.util.GuiNPCInterface;

public class GuiQuestLog extends GuiNPCInterface implements ITopButtonListener, ICustomScrollListener
{
    private ResourceLocation resource;
    public HashMap<String, List<Quest>> activeQuests;
    private HashMap<String, Quest> categoryQuests;
    public Quest selectedQuest;
    public String selectedCategory;
    private EntityPlayer player;
    private GuiCustomScroll scroll;
    private HashMap<Integer, GuiMenuSideButton> sideButtons;
    private boolean noQuests;
    private int maxLines = 10;
    private int currentPage;
    private int maxPages;
    TextBlockClient textblock;
    private Minecraft mc;
    
    public GuiQuestLog(EntityPlayer player) {
        this.resource = new ResourceLocation("customnpcs", "textures/gui/standardbg.png");
        this.activeQuests = new HashMap<String, List<Quest>>();
        this.categoryQuests = new HashMap<String, Quest>();
        this.selectedQuest = null;
        this.selectedCategory = "";
        this.sideButtons = new HashMap<Integer, GuiMenuSideButton>();
        this.noQuests = false;
        this.currentPage = 0;
        this.maxPages = 1;
        this.textblock = null;
        this.mc = Minecraft.getMinecraft();
        this.player = player;
        this.xSize = 280;
        this.ySize = 180;
        this.drawDefaultBackground = false;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        for (Quest quest : PlayerQuestController.getActiveQuests(this.player)) {
            String category = quest.category.title;
            if (!this.activeQuests.containsKey(category)) {
                this.activeQuests.put(category, new ArrayList<Quest>());
            }
            List<Quest> list = this.activeQuests.get(category);
            list.add(quest);
        }
        this.sideButtons.clear();
        this.guiTop += 10;
        TabRegistry.updateTabValues(this.guiLeft, this.guiTop, InventoryTabQuests.class);
        TabRegistry.addTabsToList(this.buttonList);
        this.noQuests = false;
        if (this.activeQuests.isEmpty()) {
            this.noQuests = true;
            return;
        }
        List<String> categories = new ArrayList<String>();
        categories.addAll(this.activeQuests.keySet());
        Collections.sort(categories, new NaturalOrderComparator());
        int i = 0;
        for (String category2 : categories) {
            if (this.selectedCategory.isEmpty()) {
                this.selectedCategory = category2;
            }
            this.sideButtons.put(i, new GuiMenuSideButton(i, this.guiLeft - 69, this.guiTop + 2 + i * 21, 70, 22, category2));
            ++i;
        }
        this.sideButtons.get(categories.indexOf(this.selectedCategory)).active = true;
        if (this.scroll == null) {
            this.scroll = new GuiCustomScroll(this, 0);
        }
        HashMap<String, Quest> categoryQuests = new HashMap<String, Quest>();
        for (Quest q : this.activeQuests.get(this.selectedCategory)) {
            categoryQuests.put(q.title, q);
        }
        this.categoryQuests = categoryQuests;
        this.scroll.setList(new ArrayList<String>(categoryQuests.keySet()));
        this.scroll.setSize(134, 174);
        this.scroll.guiLeft = this.guiLeft + 5;
        this.scroll.guiTop = this.guiTop + 15;
        this.addScroll(this.scroll);
        this.addButton(new GuiButtonNextPage(1, this.guiLeft + 286, this.guiTop + 114, true));
        this.addButton(new GuiButtonNextPage(2, this.guiLeft + 144, this.guiTop + 114, false));
        this.getButton(1).visible = (this.selectedQuest != null && this.currentPage < this.maxPages - 1);
        this.getButton(2).visible = (this.selectedQuest != null && this.currentPage > 0);
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (!(guibutton instanceof GuiButtonNextPage)) {
            return;
        }
        if (guibutton.id == 1) {
            ++this.currentPage;
            this.initGui();
        }
        if (guibutton.id == 2) {
            --this.currentPage;
            this.initGui();
        }
    }
    
    @Override
    public void drawScreen(int i, int j, float f) {
        if (this.scroll != null) {
            this.scroll.visible = !this.noQuests;
        }
        this.drawDefaultBackground();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.resource);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 252, 195);
        this.drawTexturedModalRect(this.guiLeft + 252, this.guiTop, 188, 0, 67, 195);
        super.drawScreen(i, j, f);
        if (this.noQuests) {
            this.mc.fontRenderer.drawString(I18n.translateToLocal("quest.noquests"), this.guiLeft + 84, this.guiTop + 80, CustomNpcResourceListener.DefaultTextColor);
            return;
        }
        for (GuiMenuSideButton button : this.sideButtons.values().toArray(new GuiMenuSideButton[this.sideButtons.size()])) {
            button.drawButton(this.mc, i, j, f);
        }
        this.mc.fontRenderer.drawString(this.selectedCategory, this.guiLeft + 5, this.guiTop + 5, CustomNpcResourceListener.DefaultTextColor);
        if (this.selectedQuest == null) {
            return;
        }
        this.drawProgress();
        this.drawQuestText();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.guiLeft + 148), (float)this.guiTop, 0.0f);
        GlStateManager.scale(1.24f, 1.24f, 1.24f);
        String title = I18n.translateToLocal(this.selectedQuest.title);
        this.fontRenderer.drawString(title, (130 - this.fontRenderer.getStringWidth(title)) / 2, 4, CustomNpcResourceListener.DefaultTextColor);
        GlStateManager.popMatrix();
        this.drawHorizontalLine(this.guiLeft + 142, this.guiLeft + 312, this.guiTop + 17, -16777216 + CustomNpcResourceListener.DefaultTextColor);
    }
    
    private void drawQuestText() {
        if (this.textblock == null) {
            return;
        }
        int yoffset = this.guiTop + 5;
        for (int i = 0; i < 10; ++i) {
            int index = i + this.currentPage * 10;
            if (index < this.textblock.lines.size()) {
                String text = this.textblock.lines.get(index).getFormattedText();
                this.fontRenderer.drawString(text, this.guiLeft + 142, this.guiTop + 20 + i * this.fontRenderer.FONT_HEIGHT, CustomNpcResourceListener.DefaultTextColor);
            }
        }
    }
    
    private void drawProgress() {
        String title = I18n.translateToLocal("quest.objectives") + ":";
        this.mc.fontRenderer.drawString(title, this.guiLeft + 142, this.guiTop + 130, CustomNpcResourceListener.DefaultTextColor);
        this.drawHorizontalLine(this.guiLeft + 142, this.guiLeft + 312, this.guiTop + 140, -16777216 + CustomNpcResourceListener.DefaultTextColor);
        int yoffset = this.guiTop + 144;
        for (IQuestObjective objective : this.selectedQuest.questInterface.getObjectives(this.player)) {
            this.mc.fontRenderer.drawString("- " + objective.getText(), this.guiLeft + 142, yoffset, CustomNpcResourceListener.DefaultTextColor);
            yoffset += 10;
        }
        this.drawHorizontalLine(this.guiLeft + 142, this.guiLeft + 312, this.guiTop + 178, -16777216 + CustomNpcResourceListener.DefaultTextColor);
        String complete = this.selectedQuest.getNpcName();
        if (complete != null && !complete.isEmpty()) {
            this.mc.fontRenderer.drawString(I18n.translateToLocalFormatted("quest.completewith", new Object[] { complete }), this.guiLeft + 142, this.guiTop + 182, CustomNpcResourceListener.DefaultTextColor);
        }
    }
    
    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        if (k == 0) {
            if (this.scroll != null) {
                this.scroll.mouseClicked(i, j, k);
            }
            for (GuiMenuSideButton button : new ArrayList<GuiMenuSideButton>(this.sideButtons.values())) {
                if (button.mousePressed(this.mc, i, j)) {
                    this.sideButtonPressed(button);
                }
            }
        }
    }
    
    private void sideButtonPressed(GuiMenuSideButton button) {
        if (button.active) {
            return;
        }
        NoppesUtil.clickSound();
        this.selectedCategory = button.displayString;
        this.selectedQuest = null;
        this.initGui();
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        if (!scroll.hasSelected()) {
            return;
        }
        this.selectedQuest = this.categoryQuests.get(scroll.getSelected());
        this.textblock = new TextBlockClient(this.selectedQuest.getLogText(), 172, true, new Object[] { this.player });
        if (this.textblock.lines.size() > 10) {
            this.maxPages = MathHelper.ceil(1.0f * this.textblock.lines.size() / 10.0f);
        }
        this.currentPage = 0;
        this.initGui();
    }
    
    @Override
    public void keyTyped(char c, int i) {
        if (i == 1 || i == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
