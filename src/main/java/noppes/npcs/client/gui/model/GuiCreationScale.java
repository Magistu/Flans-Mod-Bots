package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcSlider;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.gui.GuiScreen;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.constants.EnumParts;
import java.util.List;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.ISliderListener;

public class GuiCreationScale extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener
{
    private GuiCustomScroll scroll;
    private List<EnumParts> data;
    private static EnumParts selected;
    
    public GuiCreationScale(EntityNPCInterface npc) {
        super(npc);
        this.data = new ArrayList<EnumParts>();
        this.active = 3;
        this.xOffset = 140;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (this.scroll == null) {
            this.scroll = new GuiCustomScroll(this, 0);
        }
        ArrayList list = new ArrayList();
        EnumParts[] parts = { EnumParts.HEAD, EnumParts.BODY, EnumParts.ARM_LEFT, EnumParts.ARM_RIGHT, EnumParts.LEG_LEFT, EnumParts.LEG_RIGHT };
        this.data.clear();
        for (EnumParts part : parts) {
            Label_0210: {
                if (part == EnumParts.ARM_RIGHT) {
                    ModelPartConfig config = this.playerdata.getPartConfig(EnumParts.ARM_LEFT);
                    if (!config.notShared) {
                        break Label_0210;
                    }
                }
                if (part == EnumParts.LEG_RIGHT) {
                    ModelPartConfig config = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
                    if (!config.notShared) {
                        break Label_0210;
                    }
                }
                this.data.add(part);
                list.add(I18n.translateToLocal("part." + part.name));
            }
        }
        this.scroll.setUnsortedList(list);
        this.scroll.setSelected(I18n.translateToLocal("part." + GuiCreationScale.selected.name));
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 46;
        this.scroll.setSize(100, this.ySize - 74);
        this.addScroll(this.scroll);
        ModelPartConfig config2 = this.playerdata.getPartConfig(GuiCreationScale.selected);
        int y = this.guiTop + 65;
        this.addLabel(new GuiNpcLabel(10, "scale.width", this.guiLeft + 102, y + 5, 16777215));
        this.addSlider(new GuiNpcSlider(this, 10, this.guiLeft + 150, y, 100, 20, config2.scaleX - 0.5f));
        y += 22;
        this.addLabel(new GuiNpcLabel(11, "scale.height", this.guiLeft + 102, y + 5, 16777215));
        this.addSlider(new GuiNpcSlider(this, 11, this.guiLeft + 150, y, 100, 20, config2.scaleY - 0.5f));
        y += 22;
        this.addLabel(new GuiNpcLabel(12, "scale.depth", this.guiLeft + 102, y + 5, 16777215));
        this.addSlider(new GuiNpcSlider(this, 12, this.guiLeft + 150, y, 100, 20, config2.scaleZ - 0.5f));
        if (GuiCreationScale.selected == EnumParts.ARM_LEFT || GuiCreationScale.selected == EnumParts.LEG_LEFT) {
            y += 22;
            this.addLabel(new GuiNpcLabel(13, "scale.shared", this.guiLeft + 102, y + 5, 16777215));
            this.addButton(new GuiNpcButton(13, this.guiLeft + 150, y, 50, 20, new String[] { "gui.no", "gui.yes" }, (int)(config2.notShared ? 0 : 1)));
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn.id == 13) {
            boolean bo = ((GuiNpcButton)btn).getValue() == 0;
            this.playerdata.getPartConfig(GuiCreationScale.selected).notShared = bo;
            this.initGui();
        }
    }
    
    @Override
    public void mouseDragged(GuiNpcSlider slider) {
        super.mouseDragged(slider);
        if (slider.id >= 10 && slider.id <= 12) {
            int percent = (int)(50.0f + slider.sliderValue * 100.0f);
            slider.setString(percent + "%");
            ModelPartConfig config = this.playerdata.getPartConfig(GuiCreationScale.selected);
            if (slider.id == 10) {
                config.scaleX = slider.sliderValue + 0.5f;
            }
            if (slider.id == 11) {
                config.scaleY = slider.sliderValue + 0.5f;
            }
            if (slider.id == 12) {
                config.scaleZ = slider.sliderValue + 0.5f;
            }
            this.updateTransate();
        }
    }
    
    private void updateTransate() {
        for (EnumParts part : EnumParts.values()) {
            ModelPartConfig config = this.playerdata.getPartConfig(part);
            if (config != null) {
                if (part == EnumParts.HEAD) {
                    config.setTranslate(0.0f, this.playerdata.getBodyY(), 0.0f);
                }
                else if (part == EnumParts.ARM_LEFT) {
                    ModelPartConfig body = this.playerdata.getPartConfig(EnumParts.BODY);
                    float x = (1.0f - body.scaleX) * 0.25f + (1.0f - config.scaleX) * 0.075f;
                    float y = this.playerdata.getBodyY() + (1.0f - config.scaleY) * -0.1f;
                    config.setTranslate(-x, y, 0.0f);
                    if (!config.notShared) {
                        ModelPartConfig arm = this.playerdata.getPartConfig(EnumParts.ARM_RIGHT);
                        arm.copyValues(config);
                    }
                }
                else if (part == EnumParts.ARM_RIGHT) {
                    ModelPartConfig body = this.playerdata.getPartConfig(EnumParts.BODY);
                    float x = (1.0f - body.scaleX) * 0.25f + (1.0f - config.scaleX) * 0.075f;
                    float y = this.playerdata.getBodyY() + (1.0f - config.scaleY) * -0.1f;
                    config.setTranslate(x, y, 0.0f);
                }
                else if (part == EnumParts.LEG_LEFT) {
                    config.setTranslate(config.scaleX * 0.125f - 0.113f, this.playerdata.getLegsY(), 0.0f);
                    if (!config.notShared) {
                        ModelPartConfig leg = this.playerdata.getPartConfig(EnumParts.LEG_RIGHT);
                        leg.copyValues(config);
                    }
                }
                else if (part == EnumParts.LEG_RIGHT) {
                    config.setTranslate((1.0f - config.scaleX) * 0.125f, this.playerdata.getLegsY(), 0.0f);
                }
                else if (part == EnumParts.BODY) {
                    config.setTranslate(0.0f, this.playerdata.getBodyY(), 0.0f);
                }
            }
        }
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        if (scroll.selected >= 0) {
            GuiCreationScale.selected = this.data.get(scroll.selected);
            this.initGui();
        }
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
    
    static {
        GuiCreationScale.selected = EnumParts.HEAD;
    }
}
