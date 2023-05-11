package nikedemos.markovnames.generators;

import nikedemos.markovnames.MarkovDictionary;
import java.util.Random;

public class MarkovAztec extends MarkovGenerator
{
    public MarkovAztec(int seqlen, Random rng) {
        this.rng = rng;
        this.markov = new MarkovDictionary("aztec_given.txt", seqlen, rng);
    }
    
    public MarkovAztec(int seqlen) {
        this(seqlen, new Random());
    }
    
    public MarkovAztec() {
        this(3, new Random());
    }
    
    @Override
    public String fetch(int gender) {
        return this.markov.generateWord();
    }
}
