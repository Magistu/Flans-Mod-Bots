package noppes.npcs.client.gui;

import java.util.List;
import java.util.Vector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import java.util.HashMap;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.IScrollData;
import noppes.npcs.client.gui.util.GuiNPCInterface;

public class GuiNpcDimension extends GuiNPCInterface implements IScrollData
{
    private GuiCustomScroll scroll;
    private HashMap<String, Integer> data;
    
    public GuiNpcDimension() {
        this.data = new HashMap<String, Integer>();
        this.xSize = 256;
        this.setBackground("menubg.png");
    }
    
    @Override
    public void initPacket() {
        Client.sendData(EnumPacketServer.DimensionsGet, new Object[0]);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (this.scroll == null) {
            (this.scroll = new GuiCustomScroll(this, 0)).setSize(165, 208);
        }
        this.scroll.guiLeft = this.guiLeft + 4;
        this.scroll.guiTop = this.guiTop + 4;
        this.addScroll(this.scroll);
        String title = I18n.translateToLocal("Dimensions");
        int x = (this.xSize - this.fontRenderer.getStringWidth(title)) / 2;
        this.addLabel(new GuiNpcLabel(0, title, this.guiLeft + x, this.guiTop - 8));
        this.addButton(new GuiNpcButton(4, this.guiLeft + 170, this.guiTop + 72, 82, 20, "remote.tp"));
    }
    
    public void confirmClicked(boolean flag, int i) {
        if (flag) {
            Client.sendData(EnumPacketServer.RemoteDelete, this.data.get(this.scroll.getSelected()));
        }
        NoppesUtil.openGUI((EntityPlayer)this.player, this);
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (!this.data.containsKey(this.scroll.getSelected())) {
            return;
        }
        if (id == 4) {
            Client.sendData(EnumPacketServer.DimensionTeleport, this.data.get(this.scroll.getSelected()));
            this.close();
        }
    }
    
    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        this.scroll.mouseClicked(i, j, k);
    }
    
    @Override
    public void keyTyped(char c, int i) {
        if (i == 1 || this.isInventoryKey(i)) {
            this.close();
        }
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        this.scroll.setList(list);
        this.data = data;
    }
    
    @Override
    public void setSelected(String selected) {
        this.getButton(3).setDisplayText(selected);
    }
}
