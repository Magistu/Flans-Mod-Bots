package noppes.npcs.client.gui.advanced;

import noppes.npcs.client.Client;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNPCLinesEdit;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.client.gui.util.GuiNPCInterface2;

public class GuiNPCLinesMenu extends GuiNPCInterface2
{
    public GuiNPCLinesMenu(EntityNPCInterface npc) {
        super(npc);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new GuiNpcButton(0, this.guiLeft + 85, this.guiTop + 20, "lines.world"));
        this.addButton(new GuiNpcButton(1, this.guiLeft + 85, this.guiTop + 43, "lines.attack"));
        this.addButton(new GuiNpcButton(2, this.guiLeft + 85, this.guiTop + 66, "lines.interact"));
        this.addButton(new GuiNpcButton(5, this.guiLeft + 85, this.guiTop + 89, "lines.killed"));
        this.addButton(new GuiNpcButton(6, this.guiLeft + 85, this.guiTop + 112, "lines.kill"));
        this.addButton(new GuiNpcButton(7, this.guiLeft + 85, this.guiTop + 135, "lines.npcinteract"));
        this.addLabel(new GuiNpcLabel(16, "lines.random", this.guiLeft + 85, this.guiTop + 157));
        this.addButton(new GuiNpcButtonYesNo(16, this.guiLeft + 175, this.guiTop + 152, !this.npc.advanced.orderedLines));
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (id == 0) {
            NoppesUtil.openGUI((EntityPlayer)this.player, new GuiNPCLinesEdit(this.npc, this.npc.advanced.worldLines));
        }
        if (id == 1) {
            NoppesUtil.openGUI((EntityPlayer)this.player, new GuiNPCLinesEdit(this.npc, this.npc.advanced.attackLines));
        }
        if (id == 2) {
            NoppesUtil.openGUI((EntityPlayer)this.player, new GuiNPCLinesEdit(this.npc, this.npc.advanced.interactLines));
        }
        if (id == 5) {
            NoppesUtil.openGUI((EntityPlayer)this.player, new GuiNPCLinesEdit(this.npc, this.npc.advanced.killedLines));
        }
        if (id == 6) {
            NoppesUtil.openGUI((EntityPlayer)this.player, new GuiNPCLinesEdit(this.npc, this.npc.advanced.killLines));
        }
        if (id == 7) {
            NoppesUtil.openGUI((EntityPlayer)this.player, new GuiNPCLinesEdit(this.npc, this.npc.advanced.npcInteractLines));
        }
        if (id == 16) {
            this.npc.advanced.orderedLines = !((GuiNpcButtonYesNo)guibutton).getBoolean();
        }
    }
    
    @Override
    public void save() {
        Client.sendData(EnumPacketServer.MainmenuAdvancedSave, this.npc.advanced.writeToNBT(new NBTTagCompound()));
    }
}
