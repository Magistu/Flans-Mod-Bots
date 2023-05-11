package noppes.npcs.client.gui.roles;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.client.CustomNpcResourceListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.inventory.Container;
import noppes.npcs.containers.ContainerNPCTraderSetup;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleTrader;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;

public class GuiNpcTraderSetup extends GuiContainerNPCInterface2 implements ITextfieldListener
{
    private ResourceLocation slot;
    private RoleTrader role;
    
    public GuiNpcTraderSetup(EntityNPCInterface npc, ContainerNPCTraderSetup container) {
        super(npc, container);
        this.slot = new ResourceLocation("customnpcs", "textures/gui/slot.png");
        this.ySize = 220;
        this.menuYOffset = 10;
        this.role = container.role;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.setBackground("tradersetup.png");
        this.addLabel(new GuiNpcLabel(0, "role.marketname", this.guiLeft + 214, this.guiTop + 150));
        this.addTextField(new GuiNpcTextField(0, (GuiScreen)this, this.guiLeft + 214, this.guiTop + 160, 180, 20, this.role.marketName));
        this.addLabel(new GuiNpcLabel(1, "gui.ignoreDamage", this.guiLeft + 260, this.guiTop + 29));
        this.addButton(new GuiNpcButtonYesNo(1, this.guiLeft + 340, this.guiTop + 24, this.role.ignoreDamage));
        this.addLabel(new GuiNpcLabel(2, "gui.ignoreNBT", this.guiLeft + 260, this.guiTop + 51));
        this.addButton(new GuiNpcButtonYesNo(2, this.guiLeft + 340, this.guiTop + 46, this.role.ignoreNBT));
    }
    
    @Override
    public void drawScreen(int i, int j, float f) {
        this.guiTop += 10;
        super.drawScreen(i, j, f);
        this.guiTop -= 10;
    }
    
    public void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) {
            this.role.ignoreDamage = ((GuiNpcButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 2) {
            this.role.ignoreNBT = ((GuiNpcButtonYesNo)guibutton).getBoolean();
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        for (int slot = 0; slot < 18; ++slot) {
            int x = this.guiLeft + slot % 3 * 94 + 7;
            int y = this.guiTop + slot / 3 * 22 + 4;
            this.mc.renderEngine.bindTexture(this.slot);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexturedModalRect(x - 1, y, 0, 0, 18, 18);
            this.drawTexturedModalRect(x + 17, y, 0, 0, 18, 18);
            this.fontRenderer.drawString("=", x + 36, y + 5, CustomNpcResourceListener.DefaultTextColor);
            this.mc.renderEngine.bindTexture(this.slot);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexturedModalRect(x + 42, y, 0, 0, 18, 18);
        }
    }
    
    @Override
    public void save() {
        Client.sendData(EnumPacketServer.TraderMarketSave, this.role.marketName, false);
        Client.sendData(EnumPacketServer.RoleSave, this.role.writeToNBT(new NBTTagCompound()));
    }
    
    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        String name = guiNpcTextField.getText();
        if (!name.equalsIgnoreCase(this.role.marketName)) {
            this.role.marketName = name;
            Client.sendData(EnumPacketServer.TraderMarketSave, this.role.marketName, true);
        }
    }
}
