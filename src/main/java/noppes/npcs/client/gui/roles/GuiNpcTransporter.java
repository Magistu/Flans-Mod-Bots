package noppes.npcs.client.gui.roles;

import net.minecraft.nbt.NBTTagCompound;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.client.gui.GuiScreen;
import java.util.Collection;
import java.util.Vector;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import java.util.HashMap;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.IScrollData;
import noppes.npcs.client.gui.util.GuiNPCInterface2;

public class GuiNpcTransporter extends GuiNPCInterface2 implements IScrollData, IGuiData
{
    private GuiCustomScroll scroll;
    public TransportLocation location;
    private HashMap<String, Integer> data;
    
    public GuiNpcTransporter(EntityNPCInterface npc) {
        super(npc);
        this.location = new TransportLocation();
        this.data = new HashMap<String, Integer>();
    }
    
    @Override
    public void initPacket() {
        Client.sendData(EnumPacketServer.TransportCategoriesGet, new Object[0]);
        Client.sendData(EnumPacketServer.TransportGetLocation, new Object[0]);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        Vector<String> list = new Vector<String>();
        list.addAll(this.data.keySet());
        if (this.scroll == null) {
            (this.scroll = new GuiCustomScroll(this, 0)).setSize(143, 208);
        }
        this.scroll.guiLeft = this.guiLeft + 214;
        this.scroll.guiTop = this.guiTop + 4;
        this.addScroll(this.scroll);
        this.addLabel(new GuiNpcLabel(0, "gui.name", this.guiLeft + 4, this.height + 8));
        this.addTextField(new GuiNpcTextField(0, this, this.fontRenderer, this.guiLeft + 60, this.guiTop + 3, 140, 20, this.location.name));
        this.addButton(new GuiNpcButton(0, this.guiLeft + 4, this.guiTop + 31, new String[] { "transporter.discovered", "transporter.start", "transporter.interaction" }, this.location.type));
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton)guibutton;
        if (button.id == 0) {
            this.location.type = button.getValue();
        }
    }
    
    @Override
    public void save() {
        if (!this.scroll.hasSelected()) {
            return;
        }
        String name = this.getTextField(0).getText();
        if (!name.isEmpty()) {
            this.location.name = name;
        }
        this.location.pos = new BlockPos((Entity)this.player);
        this.location.dimension = this.player.dimension;
        int cat = this.data.get(this.scroll.getSelected());
        Client.sendData(EnumPacketServer.TransportSave, cat, this.location.writeNBT());
    }
    
    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        this.data = data;
        this.scroll.setList(list);
    }
    
    @Override
    public void setSelected(String selected) {
        this.scroll.setSelected(selected);
    }
    
    @Override
    public void setGuiData(NBTTagCompound compound) {
        TransportLocation loc = new TransportLocation();
        loc.readNBT(compound);
        this.location = loc;
        this.initGui();
    }
}
