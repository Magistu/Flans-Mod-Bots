package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiNpcFactionPoints extends SubGuiInterface implements ITextfieldListener
{
    private Faction faction;
    
    public SubGuiNpcFactionPoints(Faction faction) {
        this.faction = faction;
        this.setBackground("menubg.png");
        this.xSize = 256;
        this.ySize = 216;
        this.closeOnEsc = true;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(2, "faction.default", this.guiLeft + 4, this.guiTop + 33));
        this.addTextField(new GuiNpcTextField(2, this, this.guiLeft + 8 + this.fontRenderer.getStringWidth(this.getLabel(2).label), this.guiTop + 28, 70, 20, this.faction.defaultPoints + ""));
        this.getTextField(2).setMaxStringLength(6);
        this.getTextField(2).numbersOnly = true;
        String title = I18n.translateToLocal("faction.unfriendly") + "<->" + I18n.translateToLocal("faction.neutral");
        this.addLabel(new GuiNpcLabel(3, title, this.guiLeft + 4, this.guiTop + 80));
        this.addTextField(new GuiNpcTextField(3, this, this.guiLeft + 8 + this.fontRenderer.getStringWidth(title), this.guiTop + 75, 70, 20, this.faction.neutralPoints + ""));
        title = I18n.translateToLocal("faction.neutral") + "<->" + I18n.translateToLocal("faction.friendly");
        this.addLabel(new GuiNpcLabel(4, title, this.guiLeft + 4, this.guiTop + 105));
        this.addTextField(new GuiNpcTextField(4, this, this.guiLeft + 8 + this.fontRenderer.getStringWidth(title), this.guiTop + 100, 70, 20, this.faction.friendlyPoints + ""));
        this.getTextField(3).numbersOnly = true;
        this.getTextField(4).numbersOnly = true;
        if (this.getTextField(3).x > this.getTextField(4).x) {
            this.getTextField(4).x = this.getTextField(3).x;
        }
        else {
            this.getTextField(3).x = this.getTextField(4).x;
        }
        this.addButton(new GuiNpcButton(66, this.guiLeft + 20, this.guiTop + 192, 90, 20, "gui.done"));
    }
    
    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 2) {
            this.faction.defaultPoints = textfield.getInteger();
        }
        else if (textfield.id == 3) {
            this.faction.neutralPoints = textfield.getInteger();
        }
        else if (textfield.id == 4) {
            this.faction.friendlyPoints = textfield.getInteger();
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (id == 66) {
            this.close();
        }
    }
}
