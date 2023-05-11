package micdoodle8.mods.galacticraft.api.client.tabs;

import java.util.Iterator;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.gui.FontRenderer;
import java.util.List;
import java.util.Arrays;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.util.CustomNPCsScheduler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.gui.player.GuiQuestLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.NoppesStringUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;

public class InventoryTabQuests extends AbstractTab
{
    public InventoryTabQuests() {
        super(0, 0, 0, new ItemStack(Items.BOOK));
        this.displayString = NoppesStringUtils.translate("quest.quest") + "(" + GameSettings.getKeyDisplayString(ClientProxy.QuestLog.getKeyCode()) + ")";
    }
    
    @Override
    public void onTabClicked() {
        Minecraft mc = Minecraft.getMinecraft();
        CustomNPCsScheduler.runTack(() -> {
            mc.displayGuiScreen((GuiScreen)new GuiQuestLog((EntityPlayer)mc.player));
        });
    }
    
    @Override
    public boolean shouldAddToList() {
        return true;
    }
    
    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (!this.enabled || !this.visible) {
            super.drawButton(minecraft, mouseX, mouseY, partialTicks);
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        if (hovered) {
            int x = mouseX + mc.fontRenderer.getStringWidth(this.displayString);
            GlStateManager.translate((float)x, (float)(this.y + 2), 0.0f);
            this.drawHoveringText(Arrays.asList(this.displayString), 0, 0, mc.fontRenderer);
            GlStateManager.translate((float)(-x), (float)(-(this.y + 2)), 0.0f);
        }
        super.drawButton(minecraft, mouseX, mouseY, partialTicks);
    }
    
    protected void drawHoveringText(List<String> list, int x, int y, FontRenderer font) {
        if (list.isEmpty()) {
            return;
        }
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        int k = 0;
        for (String s : list) {
            int l = font.getStringWidth(s);
            if (l > k) {
                k = l;
            }
        }
        int j2 = x + 12;
        int k2 = y - 12;
        int i1 = 8;
        if (list.size() > 1) {
            i1 += 2 + (list.size() - 1) * 10;
        }
        if (j2 + k > this.width) {
            j2 -= 28 + k;
        }
        if (k2 + i1 + 6 > this.height) {
            k2 = this.height - i1 - 6;
        }
        this.zLevel = 300.0f;
        this.itemRender.zLevel = 300.0f;
        int j3 = -267386864;
        this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j3, j3);
        this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j3, j3);
        this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j3, j3);
        this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j3, j3);
        this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j3, j3);
        int k3 = 1347420415;
        int l2 = (k3 & 0xFEFEFE) >> 1 | (k3 & 0xFF000000);
        this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k3, l2);
        this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k3, l2);
        this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k3, k3);
        this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l2, l2);
        for (int i2 = 0; i2 < list.size(); ++i2) {
            String s2 = list.get(i2);
            font.drawStringWithShadow(s2, (float)j2, (float)k2, -1);
            if (i2 == 0) {
                k2 += 2;
            }
            k2 += 10;
        }
        this.zLevel = 0.0f;
        this.itemRender.zLevel = 0.0f;
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
    }
}
