package noppes.npcs.client.gui;

import noppes.npcs.NoppesStringUtils;
import noppes.npcs.constants.EnumAvailabilityScoreboard;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiNpcAvailabilityScoreboard extends SubGuiInterface implements ITextfieldListener
{
    private Availability availabitily;
    private boolean selectFaction;
    private int slot;
    
    public SubGuiNpcAvailabilityScoreboard(Availability availabitily) {
        this.selectFaction = false;
        this.slot = 0;
        this.availabitily = availabitily;
        this.setBackground("menubg.png");
        this.xSize = 316;
        this.ySize = 216;
        this.closeOnEsc = true;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(1, "availability.available", this.guiLeft, this.guiTop + 4));
        this.getLabel(1).center(this.xSize);
        int y = this.guiTop + 12;
        this.addTextField(new GuiNpcTextField(10, this, this.guiLeft + 4, y, 140, 20, this.availabitily.scoreboardObjective));
        this.addButton(new GuiNpcButton(0, this.guiLeft + 148, y, 90, 20, new String[] { "availability.smaller", "availability.equals", "availability.bigger" }, this.availabitily.scoreboardType.ordinal()));
        this.addTextField(new GuiNpcTextField(20, this, this.guiLeft + 244, y, 60, 20, this.availabitily.scoreboardValue + ""));
        this.getTextField(20).numbersOnly = true;
        y += 23;
        this.addTextField(new GuiNpcTextField(11, this, this.guiLeft + 4, y, 140, 20, this.availabitily.scoreboard2Objective));
        this.addButton(new GuiNpcButton(1, this.guiLeft + 148, y, 90, 20, new String[] { "availability.smaller", "availability.equals", "availability.bigger" }, this.availabitily.scoreboard2Type.ordinal()));
        this.addTextField(new GuiNpcTextField(21, this, this.guiLeft + 244, y, 60, 20, this.availabitily.scoreboard2Value + ""));
        this.getTextField(21).numbersOnly = true;
        this.addButton(new GuiNpcButton(66, this.guiLeft + 82, this.guiTop + 192, 98, 20, "gui.done"));
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton)guibutton;
        if (guibutton.id == 0) {
            this.availabitily.scoreboardType = EnumAvailabilityScoreboard.values()[button.getValue()];
        }
        if (guibutton.id == 1) {
            this.availabitily.scoreboard2Type = EnumAvailabilityScoreboard.values()[button.getValue()];
        }
        if (guibutton.id == 66) {
            this.close();
        }
    }
    
    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 10) {
            this.availabitily.scoreboardObjective = textfield.getText();
        }
        if (textfield.id == 11) {
            this.availabitily.scoreboard2Objective = textfield.getText();
        }
        if (textfield.id == 20) {
            this.availabitily.scoreboardValue = NoppesStringUtils.parseInt(textfield.getText(), 0);
        }
        if (textfield.id == 21) {
            this.availabitily.scoreboard2Value = NoppesStringUtils.parseInt(textfield.getText(), 0);
        }
    }
}
