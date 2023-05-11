package noppes.npcs.client.gui.global;

import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.client.gui.SubGuiMailmanSendSetup;
import noppes.npcs.client.gui.select.GuiQuestSelection;
import noppes.npcs.client.gui.SubGuiNpcFactionOptions;
import noppes.npcs.client.gui.SubGuiNpcCommand;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeManual;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeLocation;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeKill;
import noppes.npcs.client.gui.questtypes.GuiNpcQuestTypeDialog;
import noppes.npcs.client.Client;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.client.gui.SubGuiNpcTextArea;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiNpcButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.GuiSelectionListener;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class GuiQuestEdit extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener, ITextfieldListener
{
    private Quest quest;
    private boolean questlogTA;
    
    public GuiQuestEdit(Quest quest) {
        this.questlogTA = false;
        this.quest = quest;
        this.setBackground("menubg.png");
        this.xSize = 386;
        this.ySize = 226;
        NoppesUtilServer.setEditingQuest((EntityPlayer)this.player, quest);
        this.closeOnEsc = true;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(1, "gui.title", this.guiLeft + 4, this.guiTop + 8));
        this.addTextField(new GuiNpcTextField(1, this, this.fontRenderer, this.guiLeft + 46, this.guiTop + 3, 220, 20, this.quest.title));
        this.addLabel(new GuiNpcLabel(0, "ID", this.guiLeft + 268, this.guiTop + 4));
        this.addLabel(new GuiNpcLabel(2, this.quest.id + "", this.guiLeft + 268, this.guiTop + 14));
        this.addLabel(new GuiNpcLabel(3, "quest.completedtext", this.guiLeft + 4, this.guiTop + 30));
        this.addButton(new GuiNpcButton(3, this.guiLeft + 120, this.guiTop + 25, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(4, "quest.questlogtext", this.guiLeft + 4, this.guiTop + 51));
        this.addButton(new GuiNpcButton(4, this.guiLeft + 120, this.guiTop + 46, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(5, "quest.reward", this.guiLeft + 4, this.guiTop + 72));
        this.addButton(new GuiNpcButton(5, this.guiLeft + 120, this.guiTop + 67, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(6, "gui.type", this.guiLeft + 4, this.guiTop + 93));
        this.addButton(new GuiButtonBiDirectional(6, this.guiLeft + 70, this.guiTop + 88, 90, 20, new String[] { "quest.item", "quest.dialog", "quest.kill", "quest.location", "quest.areakill", "quest.manual" }, this.quest.type));
        this.addButton(new GuiNpcButton(7, this.guiLeft + 162, this.guiTop + 88, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(8, "quest.repeatable", this.guiLeft + 4, this.guiTop + 114));
        this.addButton(new GuiButtonBiDirectional(8, this.guiLeft + 70, this.guiTop + 109, 140, 20, new String[] { "gui.no", "gui.yes", "quest.mcdaily", "quest.mcweekly", "quest.rldaily", "quest.rlweekly" }, this.quest.repeat.ordinal()));
        this.addButton(new GuiNpcButton(9, this.guiLeft + 4, this.guiTop + 131, 90, 20, new String[] { "quest.npc", "quest.instant" }, this.quest.completion.ordinal()));
        if (this.quest.completerNpc.isEmpty()) {
            this.quest.completerNpc = this.npc.display.getName();
        }
        this.addTextField(new GuiNpcTextField(2, this, this.fontRenderer, this.guiLeft + 96, this.guiTop + 131, 114, 20, this.quest.completerNpc));
        this.getTextField(2).enabled = (this.quest.completion == EnumQuestCompletion.Npc);
        this.addLabel(new GuiNpcLabel(10, "faction.options", this.guiLeft + 214, this.guiTop + 30));
        this.addButton(new GuiNpcButton(10, this.guiLeft + 330, this.guiTop + 25, 50, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(15, "advMode.command", this.guiLeft + 214, this.guiTop + 52));
        this.addButton(new GuiNpcButton(15, this.guiLeft + 330, this.guiTop + 47, 50, 20, "selectServer.edit"));
        this.addButton(new GuiNpcButton(13, this.guiLeft + 4, this.guiTop + 153, 164, 20, "mailbox.setup"));
        this.addButton(new GuiNpcButton(14, this.guiLeft + 170, this.guiTop + 153, 20, 20, "X"));
        if (!this.quest.mail.subject.isEmpty()) {
            this.getButton(13).setDisplayText(this.quest.mail.subject);
        }
        this.addButton(new GuiNpcButton(11, this.guiLeft + 4, this.guiTop + 175, 164, 20, "quest.next"));
        this.addButton(new GuiNpcButton(12, this.guiLeft + 170, this.guiTop + 175, 20, 20, "X"));
        if (!this.quest.nextQuestTitle.isEmpty()) {
            this.getButton(11).setDisplayText(this.quest.nextQuestTitle);
        }
        this.addButton(new GuiNpcButton(66, this.guiLeft + 362, this.guiTop + 4, 20, 20, "X"));
    }
    
    @Override
    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton)guibutton;
        if (button.id == 3) {
            this.questlogTA = false;
            this.setSubGui(new SubGuiNpcTextArea(this.quest.completeText));
        }
        if (button.id == 4) {
            this.questlogTA = true;
            this.setSubGui(new SubGuiNpcTextArea(this.quest.logText));
        }
        if (button.id == 5) {
            Client.sendData(EnumPacketServer.QuestOpenGui, EnumGuiType.QuestReward, this.quest.writeToNBT(new NBTTagCompound()));
        }
        if (button.id == 6) {
            this.quest.setType(button.getValue());
        }
        if (button.id == 7) {
            if (this.quest.type == 0) {
                Client.sendData(EnumPacketServer.QuestOpenGui, EnumGuiType.QuestItem, this.quest.writeToNBT(new NBTTagCompound()));
            }
            if (this.quest.type == 1) {
                this.setSubGui(new GuiNpcQuestTypeDialog(this.npc, this.quest, this.parent));
            }
            if (this.quest.type == 2) {
                this.setSubGui(new GuiNpcQuestTypeKill(this.npc, this.quest, this.parent));
            }
            if (this.quest.type == 3) {
                this.setSubGui(new GuiNpcQuestTypeLocation(this.npc, this.quest, this.parent));
            }
            if (this.quest.type == 4) {
                this.setSubGui(new GuiNpcQuestTypeKill(this.npc, this.quest, this.parent));
            }
            if (this.quest.type == 5) {
                this.setSubGui(new GuiNpcQuestTypeManual(this.npc, this.quest, this.parent));
            }
        }
        if (button.id == 8) {
            this.quest.repeat = EnumQuestRepeat.values()[button.getValue()];
        }
        if (button.id == 9) {
            this.quest.completion = EnumQuestCompletion.values()[button.getValue()];
            this.getTextField(2).enabled = (this.quest.completion == EnumQuestCompletion.Npc);
        }
        if (button.id == 15) {
            this.setSubGui(new SubGuiNpcCommand(this.quest.command));
        }
        if (button.id == 10) {
            this.setSubGui(new SubGuiNpcFactionOptions(this.quest.factionOptions));
        }
        if (button.id == 11) {
            this.setSubGui(new GuiQuestSelection(this.quest.nextQuestid));
        }
        if (button.id == 12) {
            this.quest.nextQuestid = -1;
            this.initGui();
        }
        if (button.id == 13) {
            this.setSubGui(new SubGuiMailmanSendSetup(this.quest.mail));
        }
        if (button.id == 14) {
            this.quest.mail = new PlayerMail();
            this.initGui();
        }
        if (button.id == 66) {
            this.close();
        }
    }
    
    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (guiNpcTextField.id == 1) {
            this.quest.title = guiNpcTextField.getText();
            while (QuestController.instance.containsQuestName(this.quest.category, this.quest)) {
                StringBuilder sb = new StringBuilder();
                Quest quest = this.quest;
                quest.title = sb.append(quest.title).append("_").toString();
            }
        }
        if (guiNpcTextField.id == 2) {
            this.quest.completerNpc = guiNpcTextField.getText();
        }
    }
    
    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiNpcTextArea) {
            SubGuiNpcTextArea gui = (SubGuiNpcTextArea)subgui;
            if (this.questlogTA) {
                this.quest.logText = gui.text;
            }
            else {
                this.quest.completeText = gui.text;
            }
        }
        else if (subgui instanceof SubGuiNpcCommand) {
            SubGuiNpcCommand sub = (SubGuiNpcCommand)subgui;
            this.quest.command = sub.command;
        }
        else {
            this.initGui();
        }
    }
    
    @Override
    public void selected(int id, String name) {
        this.quest.nextQuestid = id;
        this.quest.nextQuestTitle = name;
    }
    
    @Override
    public void close() {
        super.close();
    }
    
    @Override
    public void save() {
        GuiNpcTextField.unfocus();
        Client.sendData(EnumPacketServer.QuestSave, this.quest.category.id, this.quest.writeToNBT(new NBTTagCompound()));
    }
}
