package noppes.npcs.client.gui.advanced;

import noppes.npcs.client.Client;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.client.gui.SubGuiNpcTextArea;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import net.minecraft.client.settings.GameSettings;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.client.gui.util.GuiNPCInterface2;

public class GuiNPCScenes extends GuiNPCInterface2
{
    private DataScenes scenes;
    private DataScenes.SceneContainer scene;
    
    public GuiNPCScenes(EntityNPCInterface npc) {
        super(npc);
        this.scenes = npc.advanced.scenes;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(102, "gui.button", this.guiLeft + 236, this.guiTop + 4));
        int y = this.guiTop + 14;
        for (int i = 0; i < this.scenes.scenes.size(); ++i) {
            DataScenes.SceneContainer scene = this.scenes.scenes.get(i);
            this.addLabel(new GuiNpcLabel(0 + i * 10, scene.name, this.guiLeft + 10, y + 5));
            this.addButton(new GuiNpcButton(1 + i * 10, this.guiLeft + 120, y, 60, 20, new String[] { "gui.disabled", "gui.enabled" }, (int)(scene.enabled ? 1 : 0)));
            this.addButton(new GuiNpcButton(2 + i * 10, this.guiLeft + 181, y, 50, 20, "selectServer.edit"));
            this.addButton(new GuiNpcButton(3 + i * 10, this.guiLeft + 293, y, 50, 20, "X"));
            if (CustomNpcs.SceneButtonsEnabled) {
                this.addButton(new GuiNpcButton(4 + i * 10, this.guiLeft + 232, y, 60, 20, new String[] { "gui.none", GameSettings.getKeyDisplayString(ClientProxy.Scene1.getKeyCode()), GameSettings.getKeyDisplayString(ClientProxy.Scene2.getKeyCode()), GameSettings.getKeyDisplayString(ClientProxy.Scene3.getKeyCode()) }, scene.btn));
            }
            y += 22;
        }
        if (this.scenes.scenes.size() < 6) {
            this.addTextField(new GuiNpcTextField(101, this, this.guiLeft + 4, y + 10, 190, 20, "Scene" + (this.scenes.scenes.size() + 1)));
            this.addButton(new GuiNpcButton(101, this.guiLeft + 204, y + 10, 60, 20, "gui.add"));
        }
    }
    
    @Override
    public void buttonEvent(GuiButton button) {
        if (button.id < 60) {
            DataScenes.SceneContainer scene = this.scenes.scenes.get(button.id / 10);
            if (button.id % 10 == 1) {
                scene.enabled = (((GuiNpcButton)button).getValue() == 1);
            }
            if (button.id % 10 == 2) {
                this.scene = scene;
                this.setSubGui(new SubGuiNpcTextArea(scene.lines));
            }
            if (button.id % 10 == 3) {
                this.scenes.scenes.remove(scene);
                this.initGui();
            }
            if (button.id % 10 == 4) {
                scene.btn = ((GuiNpcButton)button).getValue();
                this.initGui();
            }
        }
        if (button.id == 101) {
            this.scenes.addScene(this.getTextField(101).getText());
            this.initGui();
        }
    }
    
    @Override
    public void closeSubGui(SubGuiInterface gui) {
        super.closeSubGui(gui);
        if (gui instanceof SubGuiNpcTextArea) {
            this.scene.lines = ((SubGuiNpcTextArea)gui).text;
            this.scene = null;
        }
    }
    
    @Override
    public void save() {
        Client.sendData(EnumPacketServer.MainmenuAdvancedSave, this.npc.advanced.writeToNBT(new NBTTagCompound()));
    }
}
