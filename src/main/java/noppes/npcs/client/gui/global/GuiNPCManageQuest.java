package noppes.npcs.client.gui.global;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.Client;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.client.gui.GuiYesNo;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.client.gui.SubGuiEditText;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.gui.GuiButton;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import java.util.HashMap;
import net.minecraft.client.gui.GuiYesNoCallback;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.GuiNPCInterface2;

public class GuiNPCManageQuest extends GuiNPCInterface2 implements ISubGuiListener, ICustomScrollListener, GuiYesNoCallback
{
    private HashMap<String, QuestCategory> categoryData;
    private HashMap<String, Quest> questData;
    private GuiCustomScroll scrollCategories;
    private GuiCustomScroll scrollQuests;
    public static GuiScreen Instance;
    private QuestCategory selectedCategory;
    private Quest selectedQuest;
    
    public GuiNPCManageQuest(EntityNPCInterface npc) {
        super(npc);
        this.categoryData = new HashMap<String, QuestCategory>();
        this.questData = new HashMap<String, Quest>();
        GuiNPCManageQuest.Instance = this;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(0, "gui.categories", this.guiLeft + 8, this.guiTop + 4));
        this.addLabel(new GuiNpcLabel(1, "quest.quests", this.guiLeft + 175, this.guiTop + 4));
        this.addLabel(new GuiNpcLabel(3, "quest.quests", this.guiLeft + 356, this.guiTop + 8));
        this.addButton(new GuiNpcButton(13, this.guiLeft + 356, this.guiTop + 18, 58, 20, "selectServer.edit", this.selectedQuest != null));
        this.addButton(new GuiNpcButton(12, this.guiLeft + 356, this.guiTop + 41, 58, 20, "gui.remove", this.selectedQuest != null));
        this.addButton(new GuiNpcButton(11, this.guiLeft + 356, this.guiTop + 64, 58, 20, "gui.add", this.selectedCategory != null));
        this.addLabel(new GuiNpcLabel(2, "gui.categories", this.guiLeft + 356, this.guiTop + 110));
        this.addButton(new GuiNpcButton(3, this.guiLeft + 356, this.guiTop + 120, 58, 20, "selectServer.edit", this.selectedCategory != null));
        this.addButton(new GuiNpcButton(2, this.guiLeft + 356, this.guiTop + 143, 58, 20, "gui.remove", this.selectedCategory != null));
        this.addButton(new GuiNpcButton(1, this.guiLeft + 356, this.guiTop + 166, 58, 20, "gui.add"));
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
        this.scrollCategories.guiLeft = this.guiLeft + 4;
        this.scrollCategories.guiTop = this.guiTop + 14;
        this.addScroll(this.scrollCategories);
        if (this.scrollQuests == null) {
            (this.scrollQuests = new GuiCustomScroll(this, 1)).setSize(170, 200);
        }
        this.scrollQuests.setList(Lists.newArrayList((Iterable)questData.keySet()));
        this.scrollQuests.guiLeft = this.guiLeft + 175;
        this.scrollQuests.guiTop = this.guiTop + 14;
        this.addScroll(this.scrollQuests);
    }
    
    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton)guibutton;
        if (button.id == 1) {
            this.setSubGui(new SubGuiEditText(1, I18n.translateToLocal("gui.new")));
        }
        if (button.id == 2) {
            GuiYesNo guiyesno = new GuiYesNo((GuiYesNoCallback)this, this.selectedCategory.title, I18n.translateToLocal("gui.deleteMessage"), 2);
            this.displayGuiScreen((GuiScreen)guiyesno);
        }
        if (button.id == 3) {
            this.setSubGui(new SubGuiEditText(3, this.selectedCategory.title));
        }
        if (button.id == 11) {
            this.setSubGui(new SubGuiEditText(11, I18n.translateToLocal("gui.new")));
        }
        if (button.id == 12) {
            GuiYesNo guiyesno = new GuiYesNo((GuiYesNoCallback)this, this.selectedQuest.title, I18n.translateToLocal("gui.deleteMessage"), 12);
            this.displayGuiScreen((GuiScreen)guiyesno);
        }
        if (button.id == 13) {
            this.setSubGui(new GuiQuestEdit(this.selectedQuest));
        }
    }
    
    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiEditText && ((SubGuiEditText)subgui).cancelled) {
            return;
        }
        if (subgui.id == 1) {
            QuestCategory category = new QuestCategory();
            category.title = ((SubGuiEditText)subgui).text;
            while (QuestController.instance.containsCategoryName(category)) {
                StringBuilder sb = new StringBuilder();
                QuestCategory questCategory = category;
                questCategory.title = sb.append(questCategory.title).append("_").toString();
            }
            Client.sendData(EnumPacketServer.QuestCategorySave, category.writeNBT(new NBTTagCompound()));
        }
        if (subgui.id == 3) {
            this.selectedCategory.title = ((SubGuiEditText)subgui).text;
            while (QuestController.instance.containsCategoryName(this.selectedCategory)) {
                StringBuilder sb2 = new StringBuilder();
                QuestCategory selectedCategory = this.selectedCategory;
                selectedCategory.title = sb2.append(selectedCategory.title).append("_").toString();
            }
            Client.sendData(EnumPacketServer.QuestCategorySave, this.selectedCategory.writeNBT(new NBTTagCompound()));
        }
        if (subgui.id == 11) {
            Quest quest = new Quest(this.selectedCategory);
            quest.title = ((SubGuiEditText)subgui).text;
            while (QuestController.instance.containsQuestName(this.selectedCategory, quest)) {
                StringBuilder sb3 = new StringBuilder();
                Quest quest2 = quest;
                quest2.title = sb3.append(quest2.title).append("_").toString();
            }
            Client.sendData(EnumPacketServer.QuestSave, this.selectedCategory.id, quest.writeToNBT(new NBTTagCompound()));
        }
        if (subgui instanceof GuiQuestEdit) {
            this.initGui();
        }
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
        if (this.selectedQuest != null && scroll.id == 1) {
            this.setSubGui(new GuiQuestEdit(this.selectedQuest));
        }
    }
    
    public void close() {
        super.close();
    }
    
    @Override
    public void save() {
        GuiNpcTextField.unfocus();
    }
    
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI((EntityPlayer)this.player, this);
        if (!result) {
            return;
        }
        if (id == 2) {
            Client.sendData(EnumPacketServer.QuestCategoryRemove, this.selectedCategory.id);
        }
        if (id == 12) {
            Client.sendData(EnumPacketServer.QuestRemove, this.selectedQuest.id);
        }
    }
}
