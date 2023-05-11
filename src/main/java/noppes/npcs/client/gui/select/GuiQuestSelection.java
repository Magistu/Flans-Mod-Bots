package noppes.npcs.client.gui.select;

import net.minecraft.client.gui.GuiButton;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.client.gui.util.GuiSelectionListener;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import java.util.HashMap;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class GuiQuestSelection extends SubGuiInterface implements ICustomScrollListener
{
    private HashMap<String, QuestCategory> categoryData;
    private HashMap<String, Quest> questData;
    private GuiCustomScroll scrollCategories;
    private GuiCustomScroll scrollQuests;
    private QuestCategory selectedCategory;
    public Quest selectedQuest;
    private GuiSelectionListener listener;
    
    public GuiQuestSelection(int quest) {
        this.categoryData = new HashMap<String, QuestCategory>();
        this.questData = new HashMap<String, Quest>();
        this.drawDefaultBackground = false;
        this.title = "";
        this.setBackground("menubg.png");
        this.xSize = 366;
        this.ySize = 226;
        this.selectedQuest = QuestController.instance.quests.get(quest);
        if (this.selectedQuest != null) {
            this.selectedCategory = this.selectedQuest.category;
        }
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (this.parent instanceof GuiSelectionListener) {
            this.listener = (GuiSelectionListener)this.parent;
        }
        this.addLabel(new GuiNpcLabel(0, "gui.categories", this.guiLeft + 8, this.guiTop + 4));
        this.addLabel(new GuiNpcLabel(1, "quest.quests", this.guiLeft + 175, this.guiTop + 4));
        this.addButton(new GuiNpcButton(2, this.guiLeft + this.xSize - 26, this.guiTop + 4, 20, 20, "X"));
        HashMap<String, QuestCategory> categoryData = new HashMap<String, QuestCategory>();
        HashMap<String, Quest> questData = new HashMap<String, Quest>();
        for (QuestCategory category : QuestController.instance.categories.values()) {
            categoryData.put(category.title, category);
        }
        this.categoryData = categoryData;
        if (this.selectedCategory != null) {
            for (Quest quest : this.selectedCategory.quests.values()) {
                questData.put(quest.title, quest);
            }
        }
        this.questData = questData;
        if (this.scrollCategories == null) {
            (this.scrollCategories = new GuiCustomScroll(this, 0)).setSize(170, 200);
        }
        this.scrollCategories.setList(Lists.newArrayList((Iterable)categoryData.keySet()));
        if (this.selectedCategory != null) {
            this.scrollCategories.setSelected(this.selectedCategory.title);
        }
        this.scrollCategories.guiLeft = this.guiLeft + 4;
        this.scrollCategories.guiTop = this.guiTop + 14;
        this.addScroll(this.scrollCategories);
        if (this.scrollQuests == null) {
            (this.scrollQuests = new GuiCustomScroll(this, 1)).setSize(170, 200);
        }
        this.scrollQuests.setList(Lists.newArrayList((Iterable)questData.keySet()));
        if (this.selectedQuest != null) {
            this.scrollQuests.setSelected(this.selectedQuest.title);
        }
        this.scrollQuests.guiLeft = this.guiLeft + 175;
        this.scrollQuests.guiTop = this.guiTop + 14;
        this.addScroll(this.scrollQuests);
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            this.selectedCategory = this.categoryData.get(this.scrollCategories.getSelected());
            this.selectedQuest = null;
            this.scrollQuests.selected = -1;
        }
        if (guiCustomScroll.id == 1) {
            this.selectedQuest = this.questData.get(this.scrollQuests.getSelected());
        }
        this.initGui();
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        if (this.selectedQuest == null) {
            return;
        }
        if (this.listener != null) {
            this.listener.selected(this.selectedQuest.id, this.selectedQuest.title);
        }
        this.close();
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (id == 2) {
            if (this.selectedQuest != null) {
                this.scrollDoubleClicked(null, null);
            }
            else {
                this.close();
            }
        }
    }
}
