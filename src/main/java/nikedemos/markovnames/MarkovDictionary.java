package nikedemos.markovnames;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Loader;
import java.io.InputStream;
import net.minecraft.util.ResourceLocation;
import java.util.Random;

public class MarkovDictionary
{
    private Random rng;
    private int sequenceLen;
    private HashMap2D<String, String, Integer> occurrences;
    
    public MarkovDictionary(String dictionary, int seqlen, Random rng) {
        this.sequenceLen = 3;
        this.occurrences = new HashMap2D<String, String, Integer>();
        this.rng = rng;
        try {
            this.applyDictionary(dictionary, seqlen);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public MarkovDictionary(String dictionary, int seqlen) {
        this(dictionary, seqlen, new Random());
    }
    
    public MarkovDictionary(String dictionary) {
        this(dictionary, 3, new Random());
    }
    
    public MarkovDictionary(String dictionary, Random rng) {
        this(dictionary, 3, rng);
    }
    
    private InputStream getResource(ResourceLocation resourceLocation) {
        ModContainer container = Loader.instance().activeModContainer();
        if (container == null) {
            throw new RuntimeException("Failed to find current mod while looking for resource " + resourceLocation);
        }
        String resourcePath = String.format("/%s/%s/%s", "assets", resourceLocation.getNamespace(), resourceLocation.getPath());
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = container.getMod().getClass().getResourceAsStream(resourcePath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (resourceAsStream != null) {
            return resourceAsStream;
        }
        throw new RuntimeException("Could not find resource " + resourceLocation);
    }
    
    public String getCapitalized(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
    
    public static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path, new String[0]));
        return new String(encoded, StandardCharsets.UTF_8);
    }
    
    public void incrementSafe(String str1, String str2) {
        if (this.occurrences.containsKeys(str1, str2)) {
            int curr = this.occurrences.get(str1, str2);
            this.occurrences.put(str1, str2, curr + 1);
        }
        else {
            this.occurrences.put(str1, str2, 1);
        }
    }
    
    public String generateWord() {
        int allEntries = 0;
        for (Map.Entry<String, Map<String, Integer>> pair : this.occurrences.mMap.entrySet()) {
            String k = pair.getKey();
            if (k.startsWith("_[") && k.endsWith("_")) {
                allEntries += this.occurrences.get(k, "_TOTAL_");
            }
        }
        if (allEntries == 0) {
            return "Noppes";
        }
        int randomNumber = this.rng.nextInt(allEntries);
        Iterator<Map.Entry<String, Map<String, Integer>>> it = this.occurrences.mMap.entrySet().iterator();
        StringBuilder sequence = new StringBuilder("");
        while (it.hasNext()) {
            Map.Entry<String, Map<String, Integer>> pair2 = it.next();
            String j = pair2.getKey();
            if (j.startsWith("_[") && j.endsWith("_")) {
                int topLevelEntries = this.occurrences.get(j, "_TOTAL_");
                if (randomNumber < topLevelEntries) {
                    sequence.append(j.substring(1, this.sequenceLen + 1));
                    break;
                }
                randomNumber -= topLevelEntries;
            }
        }
        StringBuilder word = new StringBuilder("");
        word.append((CharSequence)sequence);
        while (sequence.charAt(sequence.length() - 1) != ']') {
            int subSize = 0;
            for (Map.Entry<String, Integer> entry : this.occurrences.mMap.get(sequence.toString()).entrySet()) {
                subSize += entry.getValue();
            }
            randomNumber = this.rng.nextInt(subSize);
            Iterator<Map.Entry<String, Integer>> m = this.occurrences.mMap.get(sequence.toString()).entrySet().iterator();
            String chosen = "";
            while (m.hasNext()) {
                Map.Entry<String, Integer> entry2 = m.next();
                int occu = this.occurrences.get(sequence.toString(), entry2.getKey());
                if (randomNumber < occu) {
                    chosen = entry2.getKey();
                    break;
                }
                randomNumber -= occu;
            }
            word.append(chosen);
            sequence.delete(0, 1);
            sequence.append(chosen);
        }
        return this.getPost(word.substring(1, word.length() - 1));
    }
    
    public String getPost(String str) {
        return this.getCapitalized(str);
    }
    
    public void applyDictionary(String dictionaryFile, int seqLen) throws IOException {
        StringBuilder input = new StringBuilder();
        ResourceLocation resource = new ResourceLocation("customnpcs:markovnames/" + dictionaryFile);
        BufferedReader readIn = new BufferedReader(new InputStreamReader(this.getResource(resource), "UTF-8"));
        for (String line = readIn.readLine(); line != null; line = readIn.readLine()) {
            input.append(line).append(" ");
        }
        readIn.close();
        if (input.length() == 0) {
            throw new RuntimeException("Resource was empty: + " + resource);
        }
        if (this.sequenceLen != seqLen) {
            this.sequenceLen = seqLen;
            this.occurrences.clear();
        }
        String input_str = '[' + input.toString().toLowerCase().replaceAll("[\\t\\n\\r\\s]+", "][") + ']';
        for (int maxCursorPos = input_str.length() - 1 - this.sequenceLen, i = 0; i <= maxCursorPos; ++i) {
            String seqCurr = input_str.substring(i, i + this.sequenceLen);
            String seqNext = input_str.substring(i + this.sequenceLen, i + this.sequenceLen + 1);
            this.incrementSafe(seqCurr, seqNext);
            StringBuilder meta = new StringBuilder("_").append(seqCurr).append("_");
            this.incrementSafe(meta.toString(), "_TOTAL_");
        }
    }
}
