package noppes.npcs.client.gui.model;

import noppes.npcs.ModelEyeData;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.client.gui.util.GuiColorButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import net.minecraft.client.gui.GuiButton;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiScreen;
import java.util.Arrays;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.ITextfieldListener;

public class GuiCreationParts extends GuiCreationScreenInterface implements ITextfieldListener, ICustomScrollListener
{
    private GuiCustomScroll scroll;
    private GuiPart[] parts;
    private static int selected;

    public GuiCreationParts(final EntityNPCInterface npc) {
        super(npc);
        this.parts = new GuiPart[] { new GuiPart(EnumParts.EARS).setTypes(new String[] { "gui.none", "gui.normal", "ears.bunny" }), new GuiPartHorns(), new GuiPartHair(), new GuiPart(EnumParts.MOHAWK).setTypes(new String[] { "gui.none", "1", "2" }).noPlayerOptions(), new GuiPartSnout(), new GuiPartBeard(), new GuiPart(EnumParts.FIN).setTypes(new String[] { "gui.none", "fin.shark", "fin.reptile" }), new GuiPart(EnumParts.BREASTS).setTypes(new String[] { "gui.none", "1", "2", "3" }).noPlayerOptions(), new GuiPartWings(), new GuiPartClaws(), new GuiPart(EnumParts.SKIRT).setTypes(new String[] { "gui.none", "gui.normal" }), new GuiPartLegs(), new GuiPartTail(), new GuiPartEyes(), new GuiPartParticles() };
        this.active = 2;
        this.closeOnEsc = false;
        Arrays.sort(this.parts, (o1, o2) -> {
        	String s1 = I18n.translateToLocal("part." + o1.part.name);
        	String s2 = I18n.translateToLocal("part." + o2.part.name);
            return s1.compareToIgnoreCase(s2);
        });
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (this.entity != null) {
            this.openGui(new GuiCreationExtra(this.npc));
            return;
        }
        if (this.scroll == null) {
            List<String> list = new ArrayList<String>();
            for (GuiPart part : this.parts) {
                list.add(I18n.translateToLocal("part." + part.part.name));
            }
            (this.scroll = new GuiCustomScroll(this, 0)).setUnsortedList(list);
        }
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 46;
        this.scroll.setSize(100, this.ySize - 74);
        this.addScroll(this.scroll);
        if (this.parts[GuiCreationParts.selected] != null) {
            this.scroll.setSelected(I18n.translateToLocal("part." + this.parts[GuiCreationParts.selected].part.name));
            this.parts[GuiCreationParts.selected].initGui();
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (this.parts[GuiCreationParts.selected] != null) {
            this.parts[GuiCreationParts.selected].actionPerformed(btn);
        }
    }
    
    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 23) {}
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        if (scroll.selected >= 0) {
            GuiCreationParts.selected = scroll.selected;
            this.initGui();
        }
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
    
    static {
        GuiCreationParts.selected = 0;
    }
    
    class GuiPart
    {
        EnumParts part;
        private int paterns;
        protected String[] types;
        protected ModelPartData data;
        protected boolean hasPlayerOption;
        protected boolean noPlayerTypes;
        protected boolean canBeDeleted;
        
        public GuiPart(EnumParts part) {
            this.paterns = 0;
            this.types = new String[] { "gui.none" };
            this.hasPlayerOption = true;
            this.noPlayerTypes = false;
            this.canBeDeleted = true;
            this.part = part;
            this.data = GuiCreationParts.this.playerdata.getPartData(part);
        }
        
        public int initGui() {
            this.data = GuiCreationParts.this.playerdata.getPartData(this.part);
            int y = GuiCreationParts.this.guiTop + 50;
            if (this.data == null || !this.data.playerTexture || !this.noPlayerTypes) {
                GuiCreationParts.this.addLabel(new GuiNpcLabel(20, "gui.type", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts.this.addButton(new GuiButtonBiDirectional(20, GuiCreationParts.this.guiLeft + 145, y, 100, 20, this.types, (this.data == null) ? 0 : (this.data.type + 1)));
                y += 25;
            }
            if (this.data != null && this.hasPlayerOption) {
                GuiCreationParts.this.addLabel(new GuiNpcLabel(21, "gui.playerskin", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts.this.addButton(new GuiNpcButtonYesNo(21, GuiCreationParts.this.guiLeft + 170, y, this.data.playerTexture));
                y += 25;
            }
            if (this.data != null && !this.data.playerTexture) {
                GuiCreationParts.this.addLabel(new GuiNpcLabel(23, "gui.color", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts.this.addButton(new GuiColorButton(23, GuiCreationParts.this.guiLeft + 170, y, this.data.color));
                y += 25;
            }
            return y;
        }
        
        protected void actionPerformed(GuiButton btn) {
            if (btn.id == 20) {
                int i = ((GuiNpcButton)btn).getValue();
                if (i == 0 && this.canBeDeleted) {
                    GuiCreationParts.this.playerdata.removePart(this.part);
                }
                else {
                    this.data = GuiCreationParts.this.playerdata.getOrCreatePart(this.part);
                    this.data.pattern = 0;
                    this.data.setType(i - 1);
                }
                GuiCreationParts.this.initGui();
            }
            if (btn.id == 22) {
                this.data.pattern = (byte)((GuiNpcButton)btn).getValue();
            }
            if (btn.id == 21) {
                this.data.playerTexture = ((GuiNpcButtonYesNo)btn).getBoolean();
                GuiCreationParts.this.initGui();
            }
            if (btn.id == 23) {
                GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.data.color, color -> this.data.color = color));
            }
        }
        
        public GuiPart noPlayerOptions() {
            this.hasPlayerOption = false;
            return this;
        }
        
        public GuiPart noPlayerTypes() {
            this.noPlayerTypes = true;
            return this;
        }
        
        public GuiPart setTypes(String[] types) {
            this.types = types;
            return this;
        }
    }
    
    class GuiPartTail extends GuiPart
    {
        public GuiPartTail() {
            super(EnumParts.TAIL);
            this.types = new String[] { "gui.none", "part.tail", "tail.dragon", "tail.horse", "tail.squirrel", "tail.fin", "tail.rodent", "tail.bird", "tail.fox" };
        }
        
        @Override
        public int initGui() {
            this.data = GuiCreationParts.this.playerdata.getPartData(this.part);
            this.hasPlayerOption = (this.data != null && (this.data.type == 0 || this.data.type == 1 || this.data.type == 6 || this.data.type == 7));
            int y = super.initGui();
            if (this.data != null && this.data.type == 0) {
                GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "1", "2" }, this.data.pattern));
            }
            return y;
        }
    }
    
    class GuiPartHorns extends GuiPart
    {
        public GuiPartHorns() {
            super(EnumParts.HORNS);
            this.types = new String[] { "gui.none", "horns.bull", "horns.antlers", "horns.antenna" };
        }
        
        @Override
        public int initGui() {
            int y = super.initGui();
            if (this.data != null && this.data.type == 2) {
                GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "1", "2" }, this.data.pattern));
            }
            return y;
        }
    }
    
    class GuiPartHair extends GuiPart
    {
        public GuiPartHair() {
            super(EnumParts.HAIR);
            this.types = new String[] { "gui.none", "1", "2", "3", "4" };
            this.noPlayerTypes();
        }
    }
    
    class GuiPartSnout extends GuiPart
    {
        public GuiPartSnout() {
            super(EnumParts.SNOUT);
            this.types = new String[] { "gui.none", "snout.small", "snout.medium", "snout.large", "snout.bunny", "snout.beak" };
        }
    }
    
    class GuiPartBeard extends GuiPart
    {
        public GuiPartBeard() {
            super(EnumParts.BEARD);
            this.types = new String[] { "gui.none", "1", "2", "3", "4" };
            this.noPlayerTypes();
        }
    }
    
    class GuiPartEyes extends GuiPart
    {
        private ModelEyeData eyes;
        
        public GuiPartEyes() {
            super(EnumParts.EYES);
            this.types = new String[] { "gui.none", "1", "2" };
            this.noPlayerOptions();
            this.canBeDeleted = false;
            this.eyes = (ModelEyeData)this.data;
        }
        
        @Override
        public int initGui() {
            int y = super.initGui();
            if (this.data != null && this.eyes.isEnabled()) {
                GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "gui.both", "gui.left", "gui.right" }, this.data.pattern));
                GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.draw", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts this$0 = GuiCreationParts.this;
                int id = 37;
                int x = GuiCreationParts.this.guiLeft + 145;
                y += 25;
                this$0.addButton(new GuiButtonBiDirectional(id, x, y, 100, 20, new String[] { I18n.translateToLocal("gui.down") + "x2", "gui.down", "gui.normal", "gui.up" }, this.eyes.eyePos + 1));
                GuiCreationParts.this.addLabel(new GuiNpcLabel(37, "gui.position", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts this$2 = GuiCreationParts.this;
                int id2 = 34;
                int x2 = GuiCreationParts.this.guiLeft + 145;
                y += 25;
                this$2.addButton(new GuiNpcButtonYesNo(id2, x2, y, this.eyes.glint));
                GuiCreationParts.this.addLabel(new GuiNpcLabel(34, "eye.glint", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts this$3 = GuiCreationParts.this;
                int id3 = 35;
                int x3 = GuiCreationParts.this.guiLeft + 170;
                y += 25;
                this$3.addButton(new GuiColorButton(id3, x3, y, this.eyes.browColor));
                GuiCreationParts.this.addLabel(new GuiNpcLabel(35, "eye.brow", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
                GuiCreationParts.this.addButton(new GuiButtonBiDirectional(38, GuiCreationParts.this.guiLeft + 225, y, 50, 20, new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8" }, this.eyes.browThickness));
                GuiCreationParts this$4 = GuiCreationParts.this;
                int id4 = 36;
                int x4 = GuiCreationParts.this.guiLeft + 170;
                y += 25;
                this$4.addButton(new GuiColorButton(id4, x4, y, this.eyes.skinColor));
                GuiCreationParts.this.addLabel(new GuiNpcLabel(36, "eye.lid", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
            }
            return y;
        }
        
        @Override
        protected void actionPerformed(GuiButton btn) {
            if (btn.id == 34) {
                this.eyes.glint = ((GuiNpcButtonYesNo)btn).getBoolean();
            }
            if (btn.id == 35) {
                GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.eyes.browColor, color -> this.eyes.browColor = color));
            }
            if (btn.id == 36) {
                GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.eyes.skinColor, color -> this.eyes.skinColor = color));
            }
            if (btn.id == 37) {
                this.eyes.eyePos = ((GuiButtonBiDirectional)btn).getValue() - 1;
            }
            if (btn.id == 38) {
                this.eyes.browThickness = ((GuiButtonBiDirectional)btn).getValue();
            }
            super.actionPerformed(btn);
        }
    }
    
    class GuiPartWings extends GuiPart
    {
        public GuiPartWings() {
            super(EnumParts.WINGS);
            this.setTypes(new String[] { "gui.none", "1", "2", "3", "4" });
        }
        
        @Override
        public int initGui() {
            int y = super.initGui();
            if (this.data == null) {
                return y;
            }
            return y;
        }
        
        @Override
        protected void actionPerformed(GuiButton btn) {
            if (btn.id == 34) {}
            super.actionPerformed(btn);
        }
    }
    
    class GuiPartClaws extends GuiPart
    {
        public GuiPartClaws() {
            super(EnumParts.CLAWS);
            this.types = new String[] { "gui.none", "gui.show" };
        }
        
        @Override
        public int initGui() {
            int y = super.initGui();
            if (this.data == null) {
                return y;
            }
            GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
            GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "gui.both", "gui.left", "gui.right" }, this.data.pattern));
            return y;
        }
    }
    
    class GuiPartParticles extends GuiPart
    {
        public GuiPartParticles() {
            super(EnumParts.PARTICLES);
            this.types = new String[] { "gui.none", "1", "2" };
        }
        
        @Override
        public int initGui() {
            int y = super.initGui();
            if (this.data == null) {
                return y;
            }
            return y;
        }
    }
    
    class GuiPartLegs extends GuiPart
    {
        public GuiPartLegs() {
            super(EnumParts.LEGS);
            this.types = new String[] { "gui.none", "gui.normal", "legs.naga", "legs.spider", "legs.horse", "legs.mermaid", "legs.digitigrade" };
            this.canBeDeleted = false;
        }
        
        @Override
        public int initGui() {
            this.hasPlayerOption = (this.data.type == 1 || this.data.type == 5);
            return super.initGui();
        }
        
        @Override
        protected void actionPerformed(GuiButton btn) {
            if (btn.id == 20) {
                int i = ((GuiNpcButton)btn).getValue();
                this.data.playerTexture = (i <= 1);
            }
            super.actionPerformed(btn);
        }
    }
}
