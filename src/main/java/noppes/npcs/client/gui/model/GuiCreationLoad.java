package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import java.util.Iterator;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.controllers.Preset;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.controllers.PresetController;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import java.util.List;
import noppes.npcs.client.gui.util.ICustomScrollListener;

public class GuiCreationLoad extends GuiCreationScreenInterface implements ICustomScrollListener
{
    private List<String> list;
    private GuiCustomScroll scroll;
    
    public GuiCreationLoad(EntityNPCInterface npc) {
        super(npc);
        this.list = new ArrayList<String>();
        this.active = 5;
        this.xOffset = 60;
        PresetController.instance.load();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (this.scroll == null) {
            this.scroll = new GuiCustomScroll(this, 0);
        }
        this.list.clear();
        for (Preset preset : PresetController.instance.presets.values()) {
            this.list.add(preset.name);
        }
        this.scroll.setList(this.list);
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 45;
        this.scroll.setSize(100, this.ySize - 96);
        this.addScroll(this.scroll);
        this.addButton(new GuiNpcButton(10, this.guiLeft, this.guiTop + this.ySize - 46, 120, 20, "gui.remove"));
    }
    
    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn.id == 10 && this.scroll.hasSelected()) {
            PresetController.instance.removePreset(this.scroll.getSelected());
            this.initGui();
        }
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        Preset preset = PresetController.instance.getPreset(scroll.getSelected());
        this.playerdata.readFromNBT(preset.data.writeToNBT());
        this.initGui();
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
