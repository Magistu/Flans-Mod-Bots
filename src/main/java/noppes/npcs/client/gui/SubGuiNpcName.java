package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.FontRenderer;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiNpcName extends SubGuiInterface implements ITextfieldListener
{
    private DataDisplay display;
    
    public SubGuiNpcName(DataDisplay display) {
        this.display = display;
        this.setBackground("menubg.png");
        this.xSize = 256;
        this.ySize = 216;
        this.closeOnEsc = true;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        int y = this.guiTop + 4;
        this.addButton(new GuiNpcButton(66, this.guiLeft + this.xSize - 24, y, 20, 20, "X"));
        int id = 0;
        FontRenderer fontRenderer = this.fontRenderer;
        int i = this.guiLeft + 4;
        y += 50;
        this.addTextField(new GuiNpcTextField(id, this, fontRenderer, i, y, 226, 20, this.display.getName()));
        int id2 = 1;
        int x = this.guiLeft + 4;
        y += 22;
        this.addButton(new GuiButtonBiDirectional(id2, x, y, 200, 20, new String[] { "markov.roman.name", "markov.japanese.name", "markov.slavic.name", "markov.welsh.name", "markov.sami.name", "markov.oldNorse.name", "markov.ancientGreek.name", "markov.aztec.name", "markov.classicCNPCs.name", "markov.spanish.name" }, this.display.getMarkovGeneratorId()));
        int id3 = 2;
        int x2 = this.guiLeft + 64;
        y += 22;
        this.addButton(new GuiButtonBiDirectional(id3, x2, y, 120, 20, new String[] { "markov.gender.either", "markov.gender.male", "markov.gender.female" }, this.display.getMarkovGender()));
        this.addLabel(new GuiNpcLabel(2, "markov.gender.name", this.guiLeft + 5, y + 5));
        int j = 3;
        int k = this.guiLeft + 4;
        y += 42;
        this.addButton(new GuiNpcButton(j, k, y, 70, 20, "markov.generate"));
    }
    
    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 0) {
            if (!textfield.isEmpty()) {
                this.display.setName(textfield.getText());
            }
            else {
                textfield.setText(this.display.getName());
            }
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton)guibutton;
        if (button.id == 1) {
            this.display.setMarkovGeneratorId(button.getValue());
        }
        if (button.id == 2) {
            this.display.setMarkovGender(button.getValue());
        }
        if (button.id == 3) {
            String name = this.display.getRandomName();
            this.display.setName(name);
            this.getTextField(0).setText(name);
        }
        if (button.id == 66) {
            this.close();
        }
    }
}
