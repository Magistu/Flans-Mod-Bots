package nikedemos.markovnames.generators;

import java.util.Random;
import nikedemos.markovnames.MarkovDictionary;

public class MarkovGenerator
{
    public MarkovDictionary markov;
    public Random rng;
    public String name;
    public String symbol;
    
    public MarkovGenerator(int seqlen, Random rng) {
        this.rng = rng;
    }
    
    public MarkovGenerator(int seqlen) {
        this(seqlen, new Random());
    }
    
    public MarkovGenerator() {
        this(3, new Random());
    }
    
    public String fetch(int gender) {
        return this.stylize(this.markov.generateWord());
    }
    
    public String fetch() {
        return this.fetch(0);
    }
    
    public String stylize(String str) {
        return str;
    }
    
    public String feminize(String element, boolean flag) {
        return element;
    }
}
