package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.entity.data.DataMelee;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiNpcMeleeProperties extends SubGuiInterface implements ITextfieldListener
{
    private DataMelee stats;
    private String[] potionNames;
    
    public SubGuiNpcMeleeProperties(DataMelee stats) {
        this.potionNames = new String[] { "gui.none", "tile.fire.name", "effect.poison", "effect.hunger", "effect.weakness", "effect.moveSlowdown", "effect.confusion", "effect.blindness", "effect.wither" };
        this.stats = stats;
        this.setBackground("menubg.png");
        this.xSize = 256;
        this.ySize = 216;
        this.closeOnEsc = true;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(1, "stats.meleestrength", this.guiLeft + 5, this.guiTop + 15));
        this.addTextField(new GuiNpcTextField(1, this, this.fontRenderer, this.guiLeft + 85, this.guiTop + 10, 50, 18, this.stats.getStrength() + ""));
        this.getTextField(1).numbersOnly = true;
        this.getTextField(1).setMinMaxDefault(0, Integer.MAX_VALUE, 5);
        this.addLabel(new GuiNpcLabel(2, "stats.meleerange", this.guiLeft + 5, this.guiTop + 45));
        this.addTextField(new GuiNpcTextField(2, this, this.fontRenderer, this.guiLeft + 85, this.guiTop + 40, 50, 18, this.stats.getRange() + ""));
        this.getTextField(2).numbersOnly = true;
        this.getTextField(2).setMinMaxDefault(1, 30, 2);
        this.addLabel(new GuiNpcLabel(3, "stats.meleespeed", this.guiLeft + 5, this.guiTop + 75));
        this.addTextField(new GuiNpcTextField(3, this, this.fontRenderer, this.guiLeft + 85, this.guiTop + 70, 50, 18, this.stats.getDelay() + ""));
        this.getTextField(3).numbersOnly = true;
        this.getTextField(3).setMinMaxDefault(1, 1000, 20);
        this.addLabel(new GuiNpcLabel(4, "enchantment.knockback", this.guiLeft + 5, this.guiTop + 105));
        this.addTextField(new GuiNpcTextField(4, this, this.fontRenderer, this.guiLeft + 85, this.guiTop + 100, 50, 18, this.stats.getKnockback() + ""));
        this.getTextField(4).numbersOnly = true;
        this.getTextField(4).setMinMaxDefault(0, 4, 0);
        this.addLabel(new GuiNpcLabel(5, "stats.meleeeffect", this.guiLeft + 5, this.guiTop + 135));
        this.addButton(new GuiButtonBiDirectional(5, this.guiLeft + 85, this.guiTop + 130, 100, 20, this.potionNames, this.stats.getEffectType()));
        if (this.stats.getEffectType() != 0) {
            this.addLabel(new GuiNpcLabel(6, "gui.time", this.guiLeft + 5, this.guiTop + 165));
            this.addTextField(new GuiNpcTextField(6, this, this.fontRenderer, this.guiLeft + 85, this.guiTop + 160, 50, 18, this.stats.getEffectTime() + ""));
            this.getTextField(6).numbersOnly = true;
            this.getTextField(6).setMinMaxDefault(1, 99999, 5);
            if (this.stats.getEffectType() != 1) {
                this.addLabel(new GuiNpcLabel(7, "stats.amplify", this.guiLeft + 5, this.guiTop + 195));
                this.addButton(new GuiButtonBiDirectional(7, this.guiLeft + 85, this.guiTop + 190, 52, 20, new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }, this.stats.getEffectStrength()));
            }
        }
        this.addButton(new GuiNpcButton(66, this.guiLeft + 164, this.guiTop + 192, 90, 20, "gui.done"));
    }
    
    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 1) {
            this.stats.setStrength(textfield.getInteger());
        }
        else if (textfield.id == 2) {
            this.stats.setRange(textfield.getInteger());
        }
        else if (textfield.id == 3) {
            this.stats.setDelay(textfield.getInteger());
        }
        else if (textfield.id == 4) {
            this.stats.setKnockback(textfield.getInteger());
        }
        else if (textfield.id == 6) {
            this.stats.setEffect(this.stats.getEffectType(), this.stats.getEffectStrength(), textfield.getInteger());
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton)guibutton;
        if (button.id == 5) {
            this.stats.setEffect(button.getValue(), this.stats.getEffectStrength(), this.stats.getEffectTime());
            this.initGui();
        }
        if (button.id == 7) {
            this.stats.setEffect(this.stats.getEffectType(), button.getValue(), this.stats.getEffectTime());
        }
        if (button.id == 66) {
            this.close();
        }
    }
}
