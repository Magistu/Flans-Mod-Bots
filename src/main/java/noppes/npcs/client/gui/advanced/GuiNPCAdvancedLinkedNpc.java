package noppes.npcs.client.gui.advanced;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.client.gui.GuiScreen;
import java.util.List;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.IScrollData;
import noppes.npcs.client.gui.util.GuiNPCInterface2;

public class GuiNPCAdvancedLinkedNpc extends GuiNPCInterface2 implements IScrollData, ICustomScrollListener
{
    private GuiCustomScroll scroll;
    private List<String> data;
    public static GuiScreen Instance;
    
    public GuiNPCAdvancedLinkedNpc(EntityNPCInterface npc) {
        super(npc);
        this.data = new ArrayList<String>();
        GuiNPCAdvancedLinkedNpc.Instance = this;
    }
    
    @Override
    public void initPacket() {
        Client.sendData(EnumPacketServer.LinkedGetAll, new Object[0]);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new GuiNpcButton(1, this.guiLeft + 358, this.guiTop + 38, 58, 20, "gui.clear"));
        if (this.scroll == null) {
            (this.scroll = new GuiCustomScroll(this, 0)).setSize(143, 208);
        }
        this.scroll.guiLeft = this.guiLeft + 137;
        this.scroll.guiTop = this.guiTop + 4;
        this.scroll.setSelected(this.npc.linkedName);
        this.scroll.setList(this.data);
        this.addScroll(this.scroll);
    }
    
    @Override
    public void buttonEvent(GuiButton button) {
        if (button.id == 1) {
            Client.sendData(EnumPacketServer.LinkedSet, "");
        }
    }
    
    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        this.data = new ArrayList<String>(list);
        this.initGui();
    }
    
    @Override
    public void setSelected(String selected) {
        this.scroll.setSelected(selected);
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        Client.sendData(EnumPacketServer.LinkedSet, guiCustomScroll.getSelected());
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
