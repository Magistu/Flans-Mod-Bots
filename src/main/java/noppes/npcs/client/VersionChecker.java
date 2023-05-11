package noppes.npcs.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.client.Minecraft;

public class VersionChecker extends Thread
{
    @Override
    public void run() {
        String name = "§2CustomNpcs§f";
        String link = "§9§nClick here";
        String text = name + " installed. For more info " + link;
        try {
            EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().player;
        }
        catch (NoSuchMethodError e2) {
            return;
        }
        EntityPlayer player;
        while ((player = (EntityPlayer)Minecraft.getMinecraft().player) == null) {
            try {
                Thread.sleep(2000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TextComponentTranslation message = new TextComponentTranslation(text, new Object[0]);
        message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/customnpcs/"));
        player.sendMessage((ITextComponent)message);
    }
}
