package noppes.npcs.client.gui.player;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import net.minecraft.client.gui.GuiYesNoCallback;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.GuiNPCInterface;

public class GuiMailbox extends GuiNPCInterface implements IGuiData, ICustomScrollListener, GuiYesNoCallback
{
    private GuiCustomScroll scroll;
    private PlayerMailData data;
    private PlayerMail selected;
    
    public GuiMailbox() {
        this.xSize = 256;
        this.setBackground("menubg.png");
        NoppesUtilPlayer.sendData(EnumPlayerPacket.MailGet, new Object[0]);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (this.scroll == null) {
            (this.scroll = new GuiCustomScroll(this, 0)).setSize(165, 186);
        }
        this.scroll.guiLeft = this.guiLeft + 4;
        this.scroll.guiTop = this.guiTop + 4;
        this.addScroll(this.scroll);
        String title = I18n.translateToLocal("mailbox.name");
        int x = (this.xSize - this.fontRenderer.getStringWidth(title)) / 2;
        this.addLabel(new GuiNpcLabel(0, title, this.guiLeft + x, this.guiTop - 8));
        if (this.selected != null) {
            this.addLabel(new GuiNpcLabel(3, I18n.translateToLocal("mailbox.sender") + ":", this.guiLeft + 170, this.guiTop + 6));
            this.addLabel(new GuiNpcLabel(1, this.selected.sender, this.guiLeft + 174, this.guiTop + 18));
            this.addLabel(new GuiNpcLabel(2, I18n.translateToLocalFormatted("mailbox.timesend", new Object[] { this.getTimePast() }), this.guiLeft + 174, this.guiTop + 30));
        }
        this.addButton(new GuiNpcButton(0, this.guiLeft + 4, this.guiTop + 192, 82, 20, "mailbox.read"));
        this.addButton(new GuiNpcButton(1, this.guiLeft + 88, this.guiTop + 192, 82, 20, "selectWorld.deleteButton"));
        this.getButton(1).setEnabled(this.selected != null);
    }
    
    private String getTimePast() {
        if (this.selected.timePast > 86400000L) {
            int days = (int)(this.selected.timePast / 86400000L);
            if (days == 1) {
                return days + " " + I18n.translateToLocal("mailbox.day");
            }
            return days + " " + I18n.translateToLocal("mailbox.days");
        }
        else if (this.selected.timePast > 3600000L) {
            int hours = (int)(this.selected.timePast / 3600000L);
            if (hours == 1) {
                return hours + " " + I18n.translateToLocal("mailbox.hour");
            }
            return hours + " " + I18n.translateToLocal("mailbox.hours");
        }
        else {
            int minutes = (int)(this.selected.timePast / 60000L);
            if (minutes == 1) {
                return minutes + " " + I18n.translateToLocal("mailbox.minutes");
            }
            return minutes + " " + I18n.translateToLocal("mailbox.minutes");
        }
    }
    
    public void confirmClicked(boolean flag, int i) {
        if (flag && this.selected != null) {
            NoppesUtilPlayer.sendData(EnumPlayerPacket.MailDelete, this.selected.time, this.selected.sender);
            this.selected = null;
        }
        NoppesUtil.openGUI((EntityPlayer)this.player, this);
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;
        if (this.scroll.selected < 0) {
            return;
        }
        if (id == 0) {
            GuiMailmanWrite.parent = this;
            GuiMailmanWrite.mail = this.selected;
            NoppesUtilPlayer.sendData(EnumPlayerPacket.MailboxOpenMail, this.selected.time, this.selected.sender);
            this.selected = null;
            this.scroll.selected = -1;
        }
        if (id == 1) {
            GuiYesNo guiyesno = new GuiYesNo((GuiYesNoCallback)this, "", I18n.translateToLocal("gui.deleteMessage"), 0);
            this.displayGuiScreen((GuiScreen)guiyesno);
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
    public void setGuiData(NBTTagCompound compound) {
        PlayerMailData data = new PlayerMailData();
        data.loadNBTData(compound);
        List<String> list = new ArrayList<String>();
        Collections.sort(data.playermail, (o1, o2) -> {
            if (o1.time == o2.time) {
                return 0;
            }
            else {
                return (o1.time > o2.time) ? -1 : 1;
            }
        });
        for (PlayerMail mail : data.playermail) {
            list.add(mail.subject);
        }
        this.data = data;
        this.scroll.clear();
        this.selected = null;
        this.scroll.setUnsortedList(list);
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        this.selected = this.data.playermail.get(guiCustomScroll.selected);
        this.initGui();
        if (this.selected != null && !this.selected.beenRead) {
            this.selected.beenRead = true;
            NoppesUtilPlayer.sendData(EnumPlayerPacket.MailRead, this.selected.time, this.selected.sender);
        }
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
