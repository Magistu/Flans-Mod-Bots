package noppes.npcs.client.gui.player;

import noppes.npcs.client.CustomNpcResourceListener;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.controllers.RecipeController;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.containers.ContainerCarpentryBench;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;

public class GuiNpcCarpentryBench extends GuiContainerNPCInterface
{
    private ResourceLocation resource;
    private ContainerCarpentryBench container;
    private GuiNpcButton button;
    
    public GuiNpcCarpentryBench(ContainerCarpentryBench container) {
        super(null, container);
        this.resource = new ResourceLocation("customnpcs", "textures/gui/carpentry.png");
        this.container = container;
        this.title = "";
        this.allowUserInput = false;
        this.closeOnEsc = true;
        this.ySize = 180;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addButton(this.button = new GuiNpcButton(0, this.guiLeft + 158, this.guiTop + 4, 12, 20, "..."));
    }
    
    @Override
    public void buttonEvent(GuiButton guibutton) {
        this.displayGuiScreen(new GuiRecipes());
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        this.button.enabled = (RecipeController.instance != null && !RecipeController.instance.anvilRecipes.isEmpty());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.resource);
        int l = (this.width - this.xSize) / 2;
        int i2 = (this.height - this.ySize) / 2;
        String title = I18n.translateToLocal("tile.npccarpentybench.name");
        this.drawTexturedModalRect(l, i2, 0, 0, this.xSize, this.ySize);
        super.drawGuiContainerBackgroundLayer(f, i, j);
        this.fontRenderer.drawString(title, this.guiLeft + 4, this.guiTop + 4, CustomNpcResourceListener.DefaultTextColor);
        this.fontRenderer.drawString(I18n.translateToLocal("container.inventory"), this.guiLeft + 4, this.guiTop + 87, CustomNpcResourceListener.DefaultTextColor);
    }
    
    @Override
    public void save() {
    }
}
