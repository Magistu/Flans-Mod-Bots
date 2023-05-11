package noppes.npcs.client.gui.util;

import net.minecraft.client.renderer.GlStateManager;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.util.ResourceLocation;

public abstract class GuiContainerNPCInterface2 extends GuiContainerNPCInterface
{
    private ResourceLocation background;
    private ResourceLocation defaultBackground;
    private GuiNpcMenu menu;
    public int menuYOffset;
    
    public GuiContainerNPCInterface2(EntityNPCInterface npc, Container cont) {
        this(npc, cont, -1);
    }
    
    public GuiContainerNPCInterface2(EntityNPCInterface npc, Container cont, int activeMenu) {
        super(npc, cont);
        this.background = new ResourceLocation("customnpcs", "textures/gui/menubg.png");
        this.defaultBackground = new ResourceLocation("customnpcs", "textures/gui/menubg.png");
        this.menuYOffset = 0;
        this.xSize = 420;
        this.menu = new GuiNpcMenu((GuiScreen)this, activeMenu, npc);
        this.title = "";
    }
    
    public void setBackground(String texture) {
        this.background = new ResourceLocation("customnpcs", "textures/gui/" + texture);
    }
    
    @Override
    public ResourceLocation getResource(String texture) {
        return new ResourceLocation("customnpcs", "textures/gui/" + texture);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.menu.initGui(this.guiLeft, this.guiTop + this.menuYOffset, this.xSize);
    }
    
    @Override
    protected void mouseClicked(int i, int j, int k) throws IOException {
        super.mouseClicked(i, j, k);
        if (!this.hasSubGui()) {
            this.menu.mouseClicked(i, j, k);
        }
    }
    
    public void delete() {
        this.npc.delete();
        this.displayGuiScreen(null);
        this.mc.setIngameFocus();
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        this.drawDefaultBackground();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.background);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 256, 256);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.defaultBackground);
        this.drawTexturedModalRect(this.guiLeft + this.xSize - 200, this.guiTop, 26, 0, 200, 220);
        this.menu.drawElements(this.fontRenderer, i, j, this.mc, f);
        super.drawGuiContainerBackgroundLayer(f, i, j);
    }
}
