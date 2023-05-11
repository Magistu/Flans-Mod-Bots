package noppes.npcs.client.gui.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.ArrayList;
import noppes.npcs.config.TrueTypeFont;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Comparator;

public class TextContainer
{
    private static char colorChar = '\uffff';
    private static Comparator<MarkUp> MarkUpComparator;
    public Pattern regexString;
    public Pattern regexFunction;
    public Pattern regexWord;
    public Pattern regexNumber;
    public Pattern regexComment;
    public String text;
    public List<MarkUp> makeup;
    public List<LineData> lines;
    private TrueTypeFont font;
    public int lineHeight;
    public int totalHeight;
    public int visibleLines;
    public int linesCount;
    
    public TextContainer(String text) {
        this.regexString = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1", 8);
        this.regexFunction = Pattern.compile("\\b(if|else|switch|with|for|while|in|var|const|let|throw|then|function|continue|break|foreach|return|try|catch|finally|do|this|typeof|instanceof|new)(?=[^\\w])");
        this.regexWord = Pattern.compile("[\\p{L}-]+|\\n|$");
        this.regexNumber = Pattern.compile("\\b-?(?:0[xX][\\dA-Fa-f]+|0[bB][01]+|0[oO][0-7]+|\\d*\\.?\\d+(?:[Ee][+-]?\\d+)?(?:[fFbBdDlLsS])?|NaN|null|Infinity|unidentified|true|false)\\b");
        this.regexComment = Pattern.compile("\\/\\*[\\s\\S]*?(?:\\*\\/|$)|\\/\\/.*|#.*");
        this.makeup = new ArrayList<MarkUp>();
        this.lines = new ArrayList<LineData>();
        this.visibleLines = 1;
        (this.text = text).replaceAll("\\r?\\n|\\r", "\n");
        double l = 1.0;
    }
    
    public void init(TrueTypeFont font, int width, int height) {
        this.font = font;
        this.lineHeight = font.height(this.text);
        if (this.lineHeight == 0) {
            this.lineHeight = 12;
        }
        String[] split = this.text.split("\n");
        int totalChars = 0;
        for (String l : split) {
            StringBuilder line = new StringBuilder();
            Matcher m = this.regexWord.matcher(l);
            int i = 0;
            while (m.find()) {
                String word = l.substring(i, m.start());
                if (font.width((Object)line + word) > width - 10) {
                    this.lines.add(new LineData(line.toString(), totalChars, totalChars + line.length()));
                    totalChars += line.length();
                    line = new StringBuilder();
                }
                line.append(word);
                i = m.start();
            }
            this.lines.add(new LineData(line.toString(), totalChars, totalChars + line.length() + 1));
            totalChars += line.length() + 1;
        }
        this.linesCount = this.lines.size();
        this.totalHeight = this.linesCount * this.lineHeight;
        this.visibleLines = Math.max(height / this.lineHeight, 1);
    }
    
    public void formatCodeText() {
        MarkUp markup = null;
        for (int start = 0; (markup = this.getNextMatching(start)) != null; start = markup.end) {
            this.makeup.add(markup);
        }
    }
    
    private MarkUp getNextMatching(int start) {
        MarkUp markup = null;
        String s = this.text.substring(start);
        Matcher matcher = this.regexNumber.matcher(s);
        if (matcher.find()) {
            markup = new MarkUp(matcher.start(), matcher.end(), '6', 0);
        }
        matcher = this.regexFunction.matcher(s);
        if (matcher.find()) {
            MarkUp markup2 = new MarkUp(matcher.start(), matcher.end(), '2', 0);
            if (this.compareMarkUps(markup, markup2)) {
                markup = markup2;
            }
        }
        matcher = this.regexString.matcher(s);
        if (matcher.find()) {
            MarkUp markup2 = new MarkUp(matcher.start(), matcher.end(), '4', 7);
            if (this.compareMarkUps(markup, markup2)) {
                markup = markup2;
            }
        }
        matcher = this.regexComment.matcher(s);
        if (matcher.find()) {
            MarkUp markup2 = new MarkUp(matcher.start(), matcher.end(), '8', 7);
            if (this.compareMarkUps(markup, markup2)) {
                markup = markup2;
            }
        }
        if (markup != null) {
            MarkUp markUp = markup;
            markUp.start += start;
            MarkUp markUp2 = markup;
            markUp2.end += start;
        }
        return markup;
    }
    
    public boolean compareMarkUps(MarkUp mu1, MarkUp mu2) {
        return mu1 == null || mu1.start > mu2.start;
    }
    
    public void addMakeUp(int start, int end, char c, int level) {
        if (!this.removeConflictingMarkUp(start, end, level)) {
            return;
        }
        this.makeup.add(new MarkUp(start, end, c, level));
    }
    
    private boolean removeConflictingMarkUp(int start, int end, int level) {
        List<MarkUp> conflicting = new ArrayList<MarkUp>();
        for (MarkUp m : this.makeup) {
            if ((start >= m.start && start <= m.end) || (end >= m.start && end <= m.end) || (start < m.start && end > m.start)) {
                if (level < m.level || (level == m.level && m.start <= start)) {
                    return false;
                }
                conflicting.add(m);
            }
        }
        this.makeup.removeAll(conflicting);
        return true;
    }
    
    public String getFormattedString() {
        StringBuilder builder = new StringBuilder(this.text);
        for (MarkUp entry : this.makeup) {
            builder.insert(entry.start, Character.toString('\uffff') + Character.toString(entry.c));
            builder.insert(entry.end, Character.toString('\uffff') + Character.toString('r'));
        }
        return builder.toString();
    }
    
    static {
        MarkUpComparator = ((o1, o2) -> {
            if (o1.start > o2.start) {
                return 1;
            }
            else if (o1.start < o2.start) {
                return -1;
            }
            else {
                return 0;
            }
        });
    }
    
    class LineData
    {
        public String text;
        public int start;
        public int end;
        
        public LineData(String text, int start, int end) {
            this.text = text;
            this.start = start;
            this.end = end;
        }
        
        public String getFormattedString() {
            StringBuilder builder = new StringBuilder(this.text);
            int found = 0;
            for (MarkUp entry : TextContainer.this.makeup) {
                if (entry.start >= this.start && entry.start < this.end) {
                    builder.insert(entry.start - this.start + found * 2, Character.toString('\uffff') + Character.toString(entry.c));
                    ++found;
                }
                if (entry.start < this.start && entry.end > this.start) {
                    builder.insert(0, Character.toString('\uffff') + Character.toString(entry.c));
                    ++found;
                }
                if (entry.end >= this.start && entry.end < this.end) {
                    builder.insert(entry.end - this.start + found * 2, Character.toString('\uffff') + Character.toString('r'));
                    ++found;
                }
            }
            return builder.toString();
        }
    }
    
    class MarkUp
    {
        public int start;
        public int end;
        public int level;
        public char c;
        
        public MarkUp(int start, int end, char c, int level) {
            this.start = start;
            this.end = end;
            this.c = c;
            this.level = level;
        }
    }
}
