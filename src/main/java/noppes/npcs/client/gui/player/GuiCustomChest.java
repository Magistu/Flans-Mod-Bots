package noppes.npcs.client.gui.player;

import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.constants.EnumPlayerPacket;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.client.CustomNpcResourceListener;
import net.minecraft.inventory.Container;
import noppes.npcs.containers.ContainerCustomChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiCustomChest extends GuiContainer
{
    private static ResourceLocation CHEST_GUI_TEXTURE;
    private IInventory upperChestInventory;
    private int inventoryRows;
    public String title;
    
    public GuiCustomChest(ContainerCustomChest container) {
        super((Container)container);
        this.title = null;
        this.inventoryRows = container.rows;
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.title != null && !this.title.isEmpty()) {
            this.fontRenderer.drawString(this.title, (this.width - this.fontRenderer.getStringWidth(this.title)) / 2, (this.height - this.ySize) / 2 + 5, CustomNpcResourceListener.DefaultTextColor);
        }
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiCustomChest.CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
    
    public void onGuiClosed() {
        super.onGuiClosed();
        NoppesUtilPlayer.sendData(EnumPlayerPacket.CloseGui, new Object[0]);
    }
    
    static {
        CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    }
}
