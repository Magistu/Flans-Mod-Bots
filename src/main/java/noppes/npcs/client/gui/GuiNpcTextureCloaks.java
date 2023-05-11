package noppes.npcs.client.gui;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiNpcTextureCloaks extends GuiNpcSelectionInterface
{
    public GuiNpcTextureCloaks(EntityNPCInterface npc, GuiScreen parent) {
        super(npc, parent, npc.display.getCapeTexture().isEmpty() ? "customnpcs:textures/cloak/" : npc.display.getCapeTexture());
        this.title = "Select Cloak";
    }
    
    @Override
    public void initGui() {
        super.initGui();
        int index = this.npc.display.getCapeTexture().lastIndexOf("/");
        if (index > 0) {
            String asset = this.npc.display.getCapeTexture().substring(index + 1);
            if (this.npc.display.getCapeTexture().equals(this.assets.getAsset(asset))) {
                this.slot.selected = asset;
            }
        }
    }
    
    @Override
    public void drawScreen(int i, int j, float f) {
        int l = -50;
        int i2 = this.height / 2 + 30;
        this.drawNpc((EntityLivingBase)this.npc, l, i2, 2.0f, 180);
        super.drawScreen(i, j, f);
    }
    
    @Override
    public void elementClicked() {
        if (this.dataTextures.contains(this.slot.selected) && this.slot.selected != null) {
            this.npc.display.setCapeTexture(this.assets.getAsset(this.slot.selected));
        }
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public String[] getExtension() {
        return new String[] { "png" };
    }
}
