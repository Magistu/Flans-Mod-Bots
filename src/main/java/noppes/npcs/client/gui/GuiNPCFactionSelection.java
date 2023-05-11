package noppes.npcs.client.gui;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.NoppesUtil;
import net.minecraft.client.gui.GuiButton;
import java.io.IOException;
import noppes.npcs.client.gui.util.GuiNpcButton;
import java.util.Collection;
import java.util.Vector;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.client.gui.util.GuiSelectionListener;
import java.util.HashMap;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNPCStringSlot;
import noppes.npcs.client.gui.util.IScrollData;
import noppes.npcs.client.gui.util.GuiNPCInterface;

public class GuiNPCFactionSelection extends GuiNPCInterface implements IScrollData
{
    private GuiNPCStringSlot slot;
    private GuiScreen parent;
    private HashMap<String, Integer> data;
    private int factionId;
    public GuiSelectionListener listener;
    
    public GuiNPCFactionSelection(EntityNPCInterface npc, GuiScreen parent, int dialog) {
        super(npc);
        this.data = new HashMap<String, Integer>();
        this.drawDefaultBackground = false;
        this.title = "Select Dialog Category";
        this.parent = parent;
        this.factionId = dialog;
        if (parent instanceof GuiSelectionListener) {
            this.listener = (GuiSelectionListener)parent;
        }
    }
    
    @Override
    public void initPacket() {
        Client.sendData(EnumPacketServer.FactionsGet, new Object[0]);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        Vector<String> list = new Vector<String>();
        (this.slot = new GuiNPCStringSlot(list, this, false, 18)).registerScrollButtons(4, 5);
        this.addButton(new GuiNpcButton(2, this.width / 2 - 100, this.height - 41, 98, 20, "gui.back"));
        this.addButton(new GuiNpcButton(4, this.width / 2 + 2, this.height - 41, 98, 20, "mco.template.button.select"));
    }
    
    @Override
    public void drawScreen(int i, int j, float f) {
        this.slot.drawScreen(i, j, f);
        super.drawScreen(i, j, f);
    }
    
    public void handleMouseInput() throws IOException {
        this.slot.handleMouseInput();
        super.handleMouseInput();
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (id == 2) {
            this.close();
            NoppesUtil.openGUI((EntityPlayer)this.player, this.parent);
        }
        if (id == 4) {
            this.doubleClicked();
        }
    }
    
    @Override
    public void doubleClicked() {
        if (this.slot.selected == null || this.slot.selected.isEmpty()) {
            return;
        }
        this.factionId = this.data.get(this.slot.selected);
        this.close();
        NoppesUtil.openGUI((EntityPlayer)this.player, this.parent);
    }
    
    @Override
    public void save() {
        if (this.factionId >= 0 && this.listener != null) {
            this.listener.selected(this.factionId, this.slot.selected);
        }
    }
    
    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        this.data = data;
        this.slot.setList(list);
        if (this.factionId >= 0) {
            for (String name : data.keySet()) {
                if (data.get(name) == this.factionId) {
                    this.slot.selected = name;
                }
            }
        }
    }
    
    @Override
    public void setSelected(String selected) {
    }
}
