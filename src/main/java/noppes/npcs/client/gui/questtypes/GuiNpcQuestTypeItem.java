package noppes.npcs.client.gui.questtypes;

import noppes.npcs.client.gui.util.GuiNpcTextField;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.global.GuiNPCManageQuest;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.quests.QuestItem;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.inventory.Container;
import noppes.npcs.containers.ContainerNpcQuestTypeItem;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;

public class GuiNpcQuestTypeItem extends GuiContainerNPCInterface implements ITextfieldListener
{
    private Quest quest;
    private static ResourceLocation field_110422_t;
    
    public GuiNpcQuestTypeItem(EntityNPCInterface npc, ContainerNpcQuestTypeItem container) {
        super(npc, container);
        this.quest = NoppesUtilServer.getEditingQuest((EntityPlayer)this.player);
        this.title = "";
        this.ySize = 202;
        this.closeOnEsc = false;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(0, "quest.takeitems", this.guiLeft + 4, this.guiTop + 8));
        this.addButton(new GuiNpcButton(0, this.guiLeft + 90, this.guiTop + 3, 60, 20, new String[] { "gui.yes", "gui.no" }, (int)(((QuestItem)this.quest.questInterface).leaveItems ? 1 : 0)));
        this.addLabel(new GuiNpcLabel(1, "gui.ignoreDamage", this.guiLeft + 4, this.guiTop + 29));
        this.addButton(new GuiNpcButtonYesNo(1, this.guiLeft + 90, this.guiTop + 24, 50, 20, ((QuestItem)this.quest.questInterface).ignoreDamage));
        this.addLabel(new GuiNpcLabel(2, "gui.ignoreNBT", this.guiLeft + 62, this.guiTop + 51));
        this.addButton(new GuiNpcButtonYesNo(2, this.guiLeft + 120, this.guiTop + 46, 50, 20, ((QuestItem)this.quest.questInterface).ignoreNBT));
        this.addButton(new GuiNpcButton(5, this.guiLeft, this.guiTop + this.ySize, 98, 20, "gui.back"));
    }
    
    public void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            ((QuestItem)this.quest.questInterface).leaveItems = (((GuiNpcButton)guibutton).getValue() == 1);
        }
        if (guibutton.id == 1) {
            ((QuestItem)this.quest.questInterface).ignoreDamage = ((GuiNpcButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 2) {
            ((QuestItem)this.quest.questInterface).ignoreNBT = ((GuiNpcButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 5) {
            NoppesUtil.openGUI((EntityPlayer)this.player, GuiNPCManageQuest.Instance);
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        this.drawWorldBackground(0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(GuiNpcQuestTypeItem.field_110422_t);
        int l = (this.width - this.xSize) / 2;
        int i2 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(l, i2, 0, 0, this.xSize, this.ySize);
        super.drawGuiContainerBackgroundLayer(f, i, j);
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public void unFocused(GuiNpcTextField textfield) {
        this.quest.rewardExp = textfield.getInteger();
    }
    
    static {
        field_110422_t = new ResourceLocation("customnpcs", "textures/gui/followersetup.png");
    }
}
