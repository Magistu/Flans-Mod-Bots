package noppes.npcs.client.gui.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.RenderHelper;
import noppes.npcs.NoppesUtilServer;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.client.CustomNpcResourceListener;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.constants.EnumPlayerPacket;
import net.minecraft.inventory.Container;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;

public class GuiNpcFollower extends GuiContainerNPCInterface implements IGuiData
{
    private ResourceLocation resource;
    private EntityNPCInterface npc;
    private RoleFollower role;
    
    public GuiNpcFollower(EntityNPCInterface npc, ContainerNPCFollower container) {
        super(npc, container);
        this.resource = new ResourceLocation("customnpcs", "textures/gui/follower.png");
        this.npc = npc;
        this.role = (RoleFollower)npc.roleInterface;
        this.closeOnEsc = true;
        NoppesUtilPlayer.sendData(EnumPlayerPacket.RoleGet, new Object[0]);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.addButton(new GuiNpcButton(4, this.guiLeft + 100, this.guiTop + 110, 50, 20, new String[] { I18n.translateToLocal("follower.waiting"), I18n.translateToLocal("follower.following") }, (int)(this.role.isFollowing ? 1 : 0)));
        if (!this.role.infiniteDays) {
            this.addButton(new GuiNpcButton(5, this.guiLeft + 8, this.guiTop + 30, 50, 20, I18n.translateToLocal("follower.hire")));
        }
    }
    
    public void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        int id = guibutton.id;
        if (id == 4) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.FollowerState, new Object[0]);
        }
        if (id == 5) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.FollowerExtend, new Object[0]);
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRenderer.drawString(I18n.translateToLocal("follower.health") + ": " + this.npc.getHealth() + "/" + this.npc.getMaxHealth(), 62, 70, CustomNpcResourceListener.DefaultTextColor);
        if (!this.role.infiniteDays) {
            if (this.role.getDays() <= 1) {
                this.fontRenderer.drawString(I18n.translateToLocal("follower.daysleft") + ": " + I18n.translateToLocal("follower.lastday"), 62, 94, CustomNpcResourceListener.DefaultTextColor);
            }
            else {
                this.fontRenderer.drawString(I18n.translateToLocal("follower.daysleft") + ": " + (this.role.getDays() - 1), 62, 94, CustomNpcResourceListener.DefaultTextColor);
            }
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.resource);
        int l = this.guiLeft;
        int i2 = this.guiTop;
        this.drawTexturedModalRect(l, i2, 0, 0, this.xSize, this.ySize);
        int index = 0;
        if (!this.role.infiniteDays) {
            for (int slot = 0; slot < this.role.inventory.items.size(); ++slot) {
                ItemStack itemstack = (ItemStack)this.role.inventory.items.get(slot);
                if (!NoppesUtilServer.IsItemStackNull(itemstack)) {
                    int days = 1;
                    if (this.role.rates.containsKey(slot)) {
                        days = this.role.rates.get(slot);
                    }
                    int yOffset = index * 20;
                    int x = this.guiLeft + 68;
                    int y = this.guiTop + yOffset + 4;
                    GlStateManager.enableRescaleNormal();
                    RenderHelper.enableGUIStandardItemLighting();
                    this.itemRender.renderItemAndEffectIntoGUI(itemstack, x + 11, y);
                    this.itemRender.renderItemOverlays(this.fontRenderer, itemstack, x + 11, y);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.disableRescaleNormal();
                    String daysS = days + " " + ((days == 1) ? I18n.translateToLocal("follower.day") : I18n.translateToLocal("follower.days"));
                    this.fontRenderer.drawString(" = " + daysS, x + 27, y + 4, CustomNpcResourceListener.DefaultTextColor);
                    if (this.isPointInRegion(x - this.guiLeft + 11, y - this.guiTop, 16, 16, this.mouseX, this.mouseY)) {
                        this.renderToolTip(itemstack, this.mouseX, this.mouseY);
                    }
                    ++index;
                }
            }
        }
        this.drawNpc(33, 131);
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.npc.roleInterface.readFromNBT(compound);
        this.initGui();
    }
}
