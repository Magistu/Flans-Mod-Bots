package noppes.npcs;

import java.util.ArrayList;
import net.minecraft.util.text.ITextComponent;
import java.util.List;

public class TextBlock
{
    public List<ITextComponent> lines;
    
    public TextBlock() {
        this.lines = new ArrayList<ITextComponent>();
    }
}
