package noppes.npcs.client;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;
import noppes.npcs.NoppesStringUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.Style;
import noppes.npcs.TextBlock;

public class TextBlockClient extends TextBlock
{
    private Style style;
    public int color;
    private String name;
    private ICommandSender sender;
    
    public TextBlockClient(String name, String text, int lineWidth, int color, Object... obs) {
        this(text, lineWidth, false, obs);
        this.color = color;
        this.name = name;
    }
    
    public TextBlockClient(ICommandSender sender, String text, int lineWidth, int color, Object... obs) {
        this(text, lineWidth, false, obs);
        this.color = color;
        this.sender = sender;
    }
    
    public String getName() {
        if (this.sender != null) {
            return this.sender.getName();
        }
        return this.name;
    }
    
    public TextBlockClient(String text, int lineWidth, boolean mcFont, Object... obs) {
        this.color = 14737632;
        this.style = new Style();
        text = NoppesStringUtils.formatText(text, obs);
        String line = "";
        text = text.replace("\n", " \n ");
        text = text.replace("\r", " \r ");
        String[] words = text.split(" ");
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        for (String word : words) {
            Label_0235: {
                if (!word.isEmpty()) {
                    if (word.length() == 1) {
                        char c = word.charAt(0);
                        if (c == '\r' || c == '\n') {
                            this.addLine(line);
                            line = "";
                            break Label_0235;
                        }
                    }
                    String newLine;
                    if (line.isEmpty()) {
                        newLine = word;
                    }
                    else {
                        newLine = line + " " + word;
                    }
                    if ((mcFont ? font.getStringWidth(newLine) : ClientProxy.Font.width(newLine)) > lineWidth) {
                        this.addLine(line);
                        line = word.trim();
                    }
                    else {
                        line = newLine;
                    }
                }
            }
        }
        if (!line.isEmpty()) {
            this.addLine(line);
        }
    }
    
    private void addLine(String text) {
        TextComponentString line = new TextComponentString(text);
        line.setStyle(this.style);
        this.lines.add((ITextComponent)line);
    }
}
