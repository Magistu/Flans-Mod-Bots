package noppes.npcs.client;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.stats.StatBasic;

public class QuestAchievement extends StatBasic
{
    public String description;
    public String message;
    
    public QuestAchievement(String message, String description) {
        super("", (ITextComponent)new TextComponentTranslation(message, new Object[0]));
        this.description = description;
        this.message = message;
    }
}
