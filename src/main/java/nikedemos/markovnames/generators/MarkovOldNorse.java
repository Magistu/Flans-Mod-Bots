package nikedemos.markovnames.generators;

import net.minecraft.util.text.TextComponentTranslation;
import java.util.Random;
import nikedemos.markovnames.MarkovDictionary;

public class MarkovOldNorse extends MarkovGenerator
{
    public MarkovDictionary markov2;
    
    public MarkovOldNorse(int seqlen, Random rng) {
        this.rng = rng;
        this.markov = new MarkovDictionary("old_norse_bothgenders.txt", seqlen, rng);
        this.name = new TextComponentTranslation("markov.oldNorse", new Object[0]).toString();
    }
    
    public MarkovOldNorse(int seqlen) {
        this(seqlen, new Random());
    }
    
    public MarkovOldNorse() {
        this(4, new Random());
    }
    
    @Override
    public String fetch(int gender) {
        return this.markov.generateWord();
    }
}
