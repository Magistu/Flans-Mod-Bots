package noppes.npcs.client.gui.advanced;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.client.gui.select.GuiDialogSelection;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.controllers.data.DialogOption;
import java.util.HashMap;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.GuiSelectionListener;
import noppes.npcs.client.gui.util.GuiNPCInterface2;

public class GuiNPCDialogNpcOptions extends GuiNPCInterface2 implements GuiSelectionListener, IGuiData
{
    private GuiScreen parent;
    private HashMap<Integer, DialogOption> data;
    private int selectedSlot;
    
    public GuiNPCDialogNpcOptions(EntityNPCInterface npc, GuiScreen parent) {
        super(npc);
        this.data = new HashMap<Integer, DialogOption>();
        this.parent = parent;
        this.drawDefaultBackground = true;
        Client.sendData(EnumPacketServer.DialogNpcGet, new Object[0]);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        for (int i = 0; i < 12; ++i) {
            int offset = (i >= 6) ? 200 : 0;
            this.addButton(new GuiNpcButton(i + 20, this.guiLeft + 20 + offset, this.guiTop + 13 + i % 6 * 22, 20, 20, "X"));
            this.addLabel(new GuiNpcLabel(i, "" + i, this.guiLeft + 6 + offset, this.guiTop + 18 + i % 6 * 22));
            String title = "dialog.selectoption";
            if (this.data.containsKey(i)) {
                title = this.data.get(i).title;
            }
            this.addButton(new GuiNpcButton(i, this.guiLeft + 44 + offset, this.guiTop + 13 + i % 6 * 22, 140, 20, title));
        }
    }
    
    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (id >= 0 && id < 20) {
            this.selectedSlot = id;
            int dialogID = -1;
            if (this.data.containsKey(id)) {
                dialogID = this.data.get(id).dialogId;
            }
            this.setSubGui(new GuiDialogSelection(dialogID));
        }
        if (id >= 20 && id < 40) {
            int slot = id - 20;
            this.data.remove(slot);
            Client.sendData(EnumPacketServer.DialogNpcRemove, slot);
            this.initGui();
        }
    }
    
    @Override
    public void save() {
    }
    
    @Override
    public void selected(int id, String name) {
        Client.sendData(EnumPacketServer.DialogNpcSet, this.selectedSlot, id);
    }
    
    @Override
    public void setGuiData(NBTTagCompound compound) {
        int pos = compound.getInteger("Position");
        DialogOption dialog = new DialogOption();
        dialog.readNBT(compound);
        this.data.put(pos, dialog);
        this.initGui();
    }
}
