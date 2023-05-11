package noppes.npcs.client.gui.swing;

import net.minecraft.client.Minecraft;
import java.awt.event.WindowEvent;
import java.awt.Component;
import javax.swing.JScrollPane;
import org.lwjgl.opengl.Display;
import javax.swing.JTextArea;
import noppes.npcs.client.gui.util.IJTextAreaListener;
import java.awt.event.WindowListener;
import javax.swing.JDialog;

public class GuiJTextArea extends JDialog implements WindowListener
{
    public IJTextAreaListener listener;
    private JTextArea area;
    
    public GuiJTextArea(String text) {
        this.setDefaultCloseOperation(2);
        this.setSize(Display.getWidth() - 40, Display.getHeight() - 40);
        this.setLocation(Display.getX() + 20, Display.getY() + 20);
        JScrollPane scroll = new JScrollPane(this.area = new JTextArea(text));
        scroll.setVerticalScrollBarPolicy(22);
        this.add(scroll);
        this.addWindowListener(this);
        this.setVisible(true);
    }
    
    public GuiJTextArea setListener(IJTextAreaListener listener) {
        this.listener = listener;
        return this;
    }
    
    @Override
    public void windowOpened(WindowEvent e) {
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
    }
    
    @Override
    public void windowClosed(WindowEvent e) {
        if (this.listener == null) {
            return;
        }
        Minecraft.getMinecraft().addScheduledTask(() -> this.listener.saveText(this.area.getText()));
    }
    
    @Override
    public void windowIconified(WindowEvent e) {
    }
    
    @Override
    public void windowDeiconified(WindowEvent e) {
    }
    
    @Override
    public void windowActivated(WindowEvent e) {
    }
    
    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
