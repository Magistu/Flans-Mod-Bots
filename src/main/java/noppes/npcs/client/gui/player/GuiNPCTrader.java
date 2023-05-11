package noppes.npcs.client.gui.player;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.CustomNpcResourceListener;
import net.minecraft.client.renderer.RenderHelper;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.containers.ContainerNPCTrader;
import noppes.npcs.roles.RoleTrader;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;

public class GuiNPCTrader extends GuiContainerNPCInterface
{
    private ResourceLocation resource;
    private ResourceLocation slot;
    private RoleTrader role;
    private ContainerNPCTrader container;
    
    public GuiNPCTrader(EntityNPCInterface npc, ContainerNPCTrader container) {
        super(npc, container);
        this.resource = new ResourceLocation("customnpcs", "textures/gui/trader.png");
        this.slot = new ResourceLocation("customnpcs", "textures/gui/slot.png");
        this.container = container;
        this.role = (RoleTrader)npc.roleInterface;
        this.closeOnEsc = true;
        this.ySize = 224;
        this.xSize = 223;
        this.title = "role.trader";
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        this.drawWorldBackground(0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.resource);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GlStateManager.enableRescaleNormal();
        this.mc.renderEngine.bindTexture(this.slot);
        for (int slot = 0; slot < 18; ++slot) {
            int x = this.guiLeft + slot % 3 * 72 + 10;
            int y = this.guiTop + slot / 3 * 21 + 6;
            ItemStack item = (ItemStack)this.role.inventoryCurrency.items.get(slot);
            ItemStack item2 = (ItemStack)this.role.inventoryCurrency.items.get(slot + 18);
            if (NoppesUtilServer.IsItemStackNull(item)) {
                item = item2;
                item2 = ItemStack.EMPTY;
            }
            if (NoppesUtilPlayer.compareItems(item, item2, false, false)) {
                item = item.copy();
                item.setCount(item.getCount() + item2.getCount());
                item2 = ItemStack.EMPTY;
            }
            ItemStack sold = (ItemStack)this.role.inventorySold.items.get(slot);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.renderEngine.bindTexture(this.slot);
            this.drawTexturedModalRect(x + 42, y, 0, 0, 18, 18);
            if (!NoppesUtilServer.IsItemStackNull(item) && !NoppesUtilServer.IsItemStackNull(sold)) {
                RenderHelper.enableGUIStandardItemLighting();
                if (!NoppesUtilServer.IsItemStackNull(item2)) {
                    this.itemRender.renderItemAndEffectIntoGUI(item2, x, y + 1);
                    this.itemRender.renderItemOverlays(this.fontRenderer, item2, x, y + 1);
                }
                this.itemRender.renderItemAndEffectIntoGUI(item, x + 18, y + 1);
                this.itemRender.renderItemOverlays(this.fontRenderer, item, x + 18, y + 1);
                RenderHelper.disableStandardItemLighting();
                this.fontRenderer.drawString("=", x + 36, y + 5, CustomNpcResourceListener.DefaultTextColor);
            }
        }
        GlStateManager.disableRescaleNormal();
        super.drawGuiContainerBackgroundLayer(f, i, j);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        for (int slot = 0; slot < 18; ++slot) {
            int x = slot % 3 * 72 + 10;
            int y = slot / 3 * 21 + 6;
            ItemStack item = (ItemStack)this.role.inventoryCurrency.items.get(slot);
            ItemStack item2 = (ItemStack)this.role.inventoryCurrency.items.get(slot + 18);
            if (NoppesUtilServer.IsItemStackNull(item)) {
                item = item2;
                item2 = ItemStack.EMPTY;
            }
            if (NoppesUtilPlayer.compareItems(item, item2, this.role.ignoreDamage, this.role.ignoreNBT)) {
                item = item.copy();
                item.setCount(item.getCount() + item2.getCount());
                item2 = ItemStack.EMPTY;
            }
            ItemStack sold = (ItemStack)this.role.inventorySold.items.get(slot);
            if (!NoppesUtilServer.IsItemStackNull(sold)) {
                if (this.isPointInRegion(x + 43, y + 1, 16, 16, par1, par2)) {
                    if (!this.container.canBuy(item, item2, (EntityPlayer)this.player)) {
                        GlStateManager.translate(0.0f, 0.0f, 300.0f);
                        if (!item.isEmpty() && !NoppesUtilPlayer.compareItems((EntityPlayer)this.player, item, this.role.ignoreDamage, this.role.ignoreNBT)) {
                            this.drawGradientRect(x + 17, y, x + 35, y + 18, 1886851088, 1886851088);
                        }
                        if (!item2.isEmpty() && !NoppesUtilPlayer.compareItems((EntityPlayer)this.player, item2, this.role.ignoreDamage, this.role.ignoreNBT)) {
                            this.drawGradientRect(x - 1, y, x + 17, y + 18, 1886851088, 1886851088);
                        }
                        String title = I18n.translateToLocal("trader.insufficient");
                        this.fontRenderer.drawString(title, (this.xSize - this.fontRenderer.getStringWidth(title)) / 2, 131, 14483456);
                        GlStateManager.translate(0.0f, 0.0f, -300.0f);
                    }
                    else {
                        String title = I18n.translateToLocal("trader.sufficient");
                        this.fontRenderer.drawString(title, (this.xSize - this.fontRenderer.getStringWidth(title)) / 2, 131, 56576);
                    }
                }
                if (this.isPointInRegion(x, y, 16, 16, par1, par2) && !NoppesUtilServer.IsItemStackNull(item2)) {
                    this.renderToolTip(item2, par1 - this.guiLeft, par2 - this.guiTop);
                }
                if (this.isPointInRegion(x + 18, y, 16, 16, par1, par2)) {
                    this.renderToolTip(item, par1 - this.guiLeft, par2 - this.guiTop);
                }
            }
        }
    }
    
    @Override
    public void save() {
    }
}
