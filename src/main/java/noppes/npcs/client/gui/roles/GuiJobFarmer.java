package noppes.npcs.client.gui.roles;

import noppes.npcs.client.Client;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketServer;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobFarmer;
import noppes.npcs.client.gui.util.GuiNPCInterface2;

public class GuiJobFarmer extends GuiNPCInterface2
{
    private JobFarmer job;
    
    public GuiJobFarmer(EntityNPCInterface npc) {
        super(npc);
        this.job = (JobFarmer)npc.jobInterface;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(0, "farmer.itempicked", this.guiLeft + 10, this.guiTop + 20));
        this.addButton(new GuiNpcButton(0, this.guiLeft + 100, this.guiTop + 15, 160, 20, new String[] { "farmer.donothing", "farmer.chest", "farmer.drop" }, this.job.chestMode));
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 0) {
            this.job.chestMode = ((GuiNpcButton)guibutton).getValue();
        }
    }
    
    @Override
    public void save() {
        Client.sendData(EnumPacketServer.JobSave, this.job.writeToNBT(new NBTTagCompound()));
    }
}
