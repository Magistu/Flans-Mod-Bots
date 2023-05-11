package noppes.npcs.client.gui.util;

import net.minecraft.client.gui.GuiScreen;

public class SubGuiInterface extends GuiNPCInterface
{
    public GuiScreen parent;
    public int id;
    
    @Override
    public void save() {
    }
    
    @Override
    public void close() {
        this.save();
        if (this.parent instanceof ISubGuiListener) {
            ((ISubGuiListener)this.parent).subGuiClosed(this);
        }
        if (this.parent instanceof GuiNPCInterface) {
            ((GuiNPCInterface)this.parent).closeSubGui(this);
        }
        else if (this.parent instanceof GuiContainerNPCInterface) {
            ((GuiContainerNPCInterface)this.parent).closeSubGui(this);
        }
        else {
            super.close();
        }
    }
    
    public GuiScreen getParent() {
        if (this.parent instanceof SubGuiInterface) {
            return ((SubGuiInterface)this.parent).getParent();
        }
        return this.parent;
    }
}
