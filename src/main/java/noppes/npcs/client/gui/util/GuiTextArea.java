package noppes.npcs.client.gui.util;

import java.awt.Font;
import noppes.npcs.CustomNpcs;
import java.util.Iterator;
import net.minecraft.util.ChatAllowedCharacters;
import noppes.npcs.NoppesStringUtils;
import net.minecraft.client.gui.GuiScreen;
import java.util.regex.Matcher;
import net.minecraft.client.Minecraft;
import java.util.Collection;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;
import java.util.List;
import noppes.npcs.config.TrueTypeFont;
import net.minecraft.client.gui.Gui;

public class GuiTextArea extends Gui implements IGui, IKeyListener, IMouseListener
{
    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    private int cursorCounter;
    private ITextChangeListener listener;
    private static TrueTypeFont font;
    public String text;
    private TextContainer container;
    public boolean active;
    public boolean enabled;
    public boolean visible;
    public boolean clicked;
    public boolean doubleClicked;
    public boolean clickScrolling;
    private int startSelection;
    private int endSelection;
    private int cursorPosition;
    private int scrolledLine;
    private boolean enableCodeHighlighting;
    private static char colorChar = '\uffff';
    public List<UndoData> undoList;
    public List<UndoData> redoList;
    public boolean undoing;
    private long lastClicked;
    
    public GuiTextArea(int id, int x, int y, int width, int height, String text) {
        this.text = null;
        this.container = null;
        this.active = false;
        this.enabled = true;
        this.visible = true;
        this.clicked = false;
        this.doubleClicked = false;
        this.clickScrolling = false;
        this.scrolledLine = 0;
        this.enableCodeHighlighting = false;
        this.undoList = new ArrayList<UndoData>();
        this.redoList = new ArrayList<UndoData>();
        this.undoing = false;
        this.lastClicked = 0L;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.undoing = true;
        this.setText(text);
        this.undoing = false;
        GuiTextArea.font.setSpecial('\uffff');
    }
    
    public void drawScreen(int xMouse, int yMouse) {
        if (!this.visible) {
            return;
        }
        drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
        drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        this.container.visibleLines = this.height / this.container.lineHeight;
        if (this.clicked) {
            this.clicked = Mouse.isButtonDown(0);
            int i = this.getSelectionPos(xMouse, yMouse);
            if (i != this.cursorPosition) {
                if (this.doubleClicked) {
                    int cursorPosition = this.cursorPosition;
                    this.endSelection = cursorPosition;
                    this.startSelection = cursorPosition;
                    this.doubleClicked = false;
                }
                this.setCursor(i, true);
            }
        }
        else if (this.doubleClicked) {
            this.doubleClicked = false;
        }
        if (this.clickScrolling) {
            this.clickScrolling = Mouse.isButtonDown(0);
            int diff = this.container.linesCount - this.container.visibleLines;
            this.scrolledLine = Math.min(Math.max((int)(1.0f * diff * (yMouse - this.y) / this.height), 0), diff);
        }
        int startBracket = 0;
        int endBracket = 0;
        if (this.endSelection - this.startSelection == 1 || (this.startSelection == this.endSelection && this.startSelection < this.text.length())) {
            char c = this.text.charAt(this.startSelection);
            int found = 0;
            if (c == '{') {
                found = this.findClosingBracket(this.text.substring(this.startSelection), '{', '}');
            }
            else if (c == '[') {
                found = this.findClosingBracket(this.text.substring(this.startSelection), '[', ']');
            }
            else if (c == '(') {
                found = this.findClosingBracket(this.text.substring(this.startSelection), '(', ')');
            }
            else if (c == '}') {
                found = this.findOpeningBracket(this.text.substring(0, this.startSelection + 1), '{', '}');
            }
            else if (c == ']') {
                found = this.findOpeningBracket(this.text.substring(0, this.startSelection + 1), '[', ']');
            }
            else if (c == ')') {
                found = this.findOpeningBracket(this.text.substring(0, this.startSelection + 1), '(', ')');
            }
            if (found != 0) {
                startBracket = this.startSelection;
                endBracket = this.startSelection + found;
            }
        }
        List<TextContainer.LineData> list = new ArrayList<TextContainer.LineData>(this.container.lines);
        String wordHightLight = null;
        if (this.startSelection != this.endSelection) {
            Matcher m = this.container.regexWord.matcher(this.text);
            while (m.find()) {
                if (m.start() == this.startSelection && m.end() == this.endSelection) {
                    wordHightLight = this.text.substring(this.startSelection, this.endSelection);
                }
            }
        }
        for (int j = 0; j < list.size(); ++j) {
            TextContainer.LineData data = list.get(j);
            String line = data.text;
            int w = line.length();
            if (startBracket != endBracket) {
                if (startBracket >= data.start && startBracket < data.end) {
                    int s = GuiTextArea.font.width(line.substring(0, startBracket - data.start));
                    int e = GuiTextArea.font.width(line.substring(0, startBracket - data.start + 1)) + 1;
                    int posY = this.y + 1 + (j - this.scrolledLine) * this.container.lineHeight;
                    drawRect(this.x + 1 + s, posY, this.x + 1 + e, posY + this.container.lineHeight + 1, -1728001024);
                }
                if (endBracket >= data.start && endBracket < data.end) {
                    int s = GuiTextArea.font.width(line.substring(0, endBracket - data.start));
                    int e = GuiTextArea.font.width(line.substring(0, endBracket - data.start + 1)) + 1;
                    int posY = this.y + 1 + (j - this.scrolledLine) * this.container.lineHeight;
                    drawRect(this.x + 1 + s, posY, this.x + 1 + e, posY + this.container.lineHeight + 1, -1728001024);
                }
            }
            if (j >= this.scrolledLine && j < this.scrolledLine + this.container.visibleLines) {
                if (wordHightLight != null) {
                    Matcher k = this.container.regexWord.matcher(line);
                    while (k.find()) {
                        if (line.substring(k.start(), k.end()).equals(wordHightLight)) {
                            int s2 = GuiTextArea.font.width(line.substring(0, k.start()));
                            int e2 = GuiTextArea.font.width(line.substring(0, k.end())) + 1;
                            int posY2 = this.y + 1 + (j - this.scrolledLine) * this.container.lineHeight;
                            drawRect(this.x + 1 + s2, posY2, this.x + 1 + e2, posY2 + this.container.lineHeight + 1, -1728033792);
                        }
                    }
                }
                if (this.startSelection != this.endSelection && this.endSelection > data.start && this.startSelection <= data.end && this.startSelection < data.end) {
                    int s = GuiTextArea.font.width(line.substring(0, Math.max(this.startSelection - data.start, 0)));
                    int e = GuiTextArea.font.width(line.substring(0, Math.min(this.endSelection - data.start, w))) + 1;
                    int posY = this.y + 1 + (j - this.scrolledLine) * this.container.lineHeight;
                    drawRect(this.x + 1 + s, posY, this.x + 1 + e, posY + this.container.lineHeight + 1, -1728052993);
                }
                int yPos = this.y + (j - this.scrolledLine) * this.container.lineHeight + 1;
                GuiTextArea.font.draw(data.getFormattedString(), (float)(this.x + 1), (float)yPos, -2039584);
                if (this.active && this.isEnabled() && this.cursorCounter / 6 % 2 == 0 && this.cursorPosition >= data.start && this.cursorPosition < data.end) {
                    int posX = this.x + GuiTextArea.font.width(line.substring(0, this.cursorPosition - data.start));
                    drawRect(posX + 1, yPos, posX + 2, yPos + 1 + this.container.lineHeight, -3092272);
                }
            }
        }
        if (this.hasVerticalScrollbar()) {
            Minecraft.getMinecraft().renderEngine.bindTexture(GuiCustomScroll.resource);
            int sbSize = Math.max((int)(1.0f * this.container.visibleLines / this.container.linesCount * this.height), 2);
            int posX2 = this.x + this.width - 6;
            int posY3 = (int)(this.y + 1.0f * this.scrolledLine / this.container.linesCount * (this.height - 4)) + 1;
            drawRect(posX2, posY3, posX2 + 5, posY3 + sbSize, -2039584);
        }
    }
    
    private int findClosingBracket(String str, char s, char e) {
        int found = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c == s) {
                ++found;
            }
            else if (c == e && --found == 0) {
                return i;
            }
        }
        return 0;
    }
    
    private int findOpeningBracket(String str, char s, char e) {
        int found = 0;
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i >= 0; --i) {
            char c = chars[i];
            if (c == e) {
                ++found;
            }
            else if (c == s && --found == 0) {
                return i - chars.length + 1;
            }
        }
        return 0;
    }
    
    private int getSelectionPos(int xMouse, int yMouse) {
        xMouse -= this.x + 1;
        yMouse -= this.y + 1;
        List<TextContainer.LineData> list = new ArrayList<TextContainer.LineData>(this.container.lines);
        for (int i = 0; i < list.size(); ++i) {
            TextContainer.LineData data = list.get(i);
            if (i >= this.scrolledLine && i < this.scrolledLine + this.container.visibleLines) {
                int yPos = (i - this.scrolledLine) * this.container.lineHeight;
                if (yMouse >= yPos && yMouse < yPos + this.container.lineHeight) {
                    int lineWidth = 0;
                    char[] chars = data.text.toCharArray();
                    for (int j = 1; j <= chars.length; ++j) {
                        int w = GuiTextArea.font.width(data.text.substring(0, j));
                        if (xMouse < lineWidth + (w - lineWidth) / 2) {
                            return data.start + j - 1;
                        }
                        lineWidth = w;
                    }
                    return data.end - 1;
                }
            }
        }
        return this.container.text.length();
    }
    
    public int getID() {
        return this.id;
    }
    
    public void keyTyped(char c, int i) {
        if (!this.active) {
            return;
        }
        if (GuiScreen.isKeyComboCtrlA(i)) {
            int n = 0;
            this.cursorPosition = n;
            this.startSelection = n;
            this.endSelection = this.text.length();
            return;
        }
        if (!this.isEnabled()) {
            return;
        }
        String original = this.text;
        if (i == 203) {
            int j = 1;
            if (GuiScreen.isCtrlKeyDown()) {
                Matcher m = this.container.regexWord.matcher(this.text.substring(0, this.cursorPosition));
                while (m.find()) {
                    if (m.start() == m.end()) {
                        continue;
                    }
                    j = this.cursorPosition - m.start();
                }
            }
            this.setCursor(this.cursorPosition - j, GuiScreen.isShiftKeyDown());
            return;
        }
        if (i == 205) {
            int j = 1;
            if (GuiScreen.isCtrlKeyDown()) {
                Matcher m = this.container.regexWord.matcher(this.text.substring(this.cursorPosition));
                if ((m.find() && m.start() > 0) || m.find()) {
                    j = m.start();
                }
            }
            this.setCursor(this.cursorPosition + j, GuiScreen.isShiftKeyDown());
            return;
        }
        if (i == 200) {
            this.setCursor(this.cursorUp(), GuiScreen.isShiftKeyDown());
            return;
        }
        if (i == 208) {
            this.setCursor(this.cursorDown(), GuiScreen.isShiftKeyDown());
            return;
        }
        if (i == 211) {
            String s = this.getSelectionAfterText();
            if (!s.isEmpty() && this.startSelection == this.endSelection) {
                s = s.substring(1);
            }
            this.setText(this.getSelectionBeforeText() + s);
            int startSelection = this.startSelection;
            this.cursorPosition = startSelection;
            this.endSelection = startSelection;
            return;
        }
        if (i == 14) {
            String s = this.getSelectionBeforeText();
            if (this.startSelection > 0 && this.startSelection == this.endSelection) {
                s = s.substring(0, s.length() - 1);
                --this.startSelection;
            }
            this.setText(s + this.getSelectionAfterText());
            int startSelection2 = this.startSelection;
            this.cursorPosition = startSelection2;
            this.endSelection = startSelection2;
            return;
        }
        if (GuiScreen.isKeyComboCtrlX(i)) {
            if (this.startSelection != this.endSelection) {
                NoppesStringUtils.setClipboardContents(this.text.substring(this.startSelection, this.endSelection));
                String s = this.getSelectionBeforeText();
                this.setText(s + this.getSelectionAfterText());
                int length = s.length();
                this.cursorPosition = length;
                this.startSelection = length;
                this.endSelection = length;
            }
            return;
        }
        if (GuiScreen.isKeyComboCtrlC(i)) {
            if (this.startSelection != this.endSelection) {
                NoppesStringUtils.setClipboardContents(this.text.substring(this.startSelection, this.endSelection));
            }
            return;
        }
        if (GuiScreen.isKeyComboCtrlV(i)) {
            this.addText(NoppesStringUtils.getClipboardContents());
            return;
        }
        if (i == 44 && GuiScreen.isCtrlKeyDown()) {
            if (this.undoList.isEmpty()) {
                return;
            }
            this.undoing = true;
            this.redoList.add(new UndoData(this.text, this.cursorPosition));
            UndoData data = this.undoList.remove(this.undoList.size() - 1);
            this.setText(data.text);
            int cursorPosition = data.cursorPosition;
            this.cursorPosition = cursorPosition;
            this.startSelection = cursorPosition;
            this.endSelection = cursorPosition;
            this.undoing = false;
        }
        else {
            if (i != 21 || !GuiScreen.isCtrlKeyDown()) {
                if (i == 15) {
                    this.addText("    ");
                }
                if (i == 28) {
                    this.addText(Character.toString('\n') + this.getIndentCurrentLine());
                }
                if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                    this.addText(Character.toString(c));
                }
                return;
            }
            if (this.redoList.isEmpty()) {
                return;
            }
            this.undoing = true;
            this.undoList.add(new UndoData(this.text, this.cursorPosition));
            UndoData data = this.redoList.remove(this.redoList.size() - 1);
            this.setText(data.text);
            int cursorPosition2 = data.cursorPosition;
            this.cursorPosition = cursorPosition2;
            this.startSelection = cursorPosition2;
            this.endSelection = cursorPosition2;
            this.undoing = false;
        }
    }
    
    private String getIndentCurrentLine() {
        for (TextContainer.LineData data : this.container.lines) {
            if (this.cursorPosition > data.start && this.cursorPosition <= data.end) {
                int i;
                for (i = 0; i < data.text.length() && data.text.charAt(i) == ' '; ++i) {}
                return data.text.substring(0, i);
            }
        }
        return "";
    }
    
    private void setCursor(int i, boolean select) {
        i = Math.min(Math.max(i, 0), this.text.length());
        if (i == this.cursorPosition) {
            return;
        }
        if (!select) {
            int endSelection = i;
            this.cursorPosition = endSelection;
            this.startSelection = endSelection;
            this.endSelection = endSelection;
            return;
        }
        int diff = this.cursorPosition - i;
        if (this.cursorPosition == this.startSelection) {
            this.startSelection -= diff;
        }
        else if (this.cursorPosition == this.endSelection) {
            this.endSelection -= diff;
        }
        if (this.startSelection > this.endSelection) {
            int j = this.endSelection;
            this.endSelection = this.startSelection;
            this.startSelection = j;
        }
        this.cursorPosition = i;
    }
    
    private void addText(String s) {
        this.setText(this.getSelectionBeforeText() + s + this.getSelectionAfterText());
        int endSelection = this.startSelection + s.length();
        this.cursorPosition = endSelection;
        this.startSelection = endSelection;
        this.endSelection = endSelection;
    }
    
    private int cursorUp() {
        int i = 0;
        while (i < this.container.lines.size()) {
            TextContainer.LineData data = this.container.lines.get(i);
            if (this.cursorPosition >= data.start && this.cursorPosition < data.end) {
                if (i == 0) {
                    return 0;
                }
                int linePos = this.cursorPosition - data.start;
                return this.getSelectionPos(this.x + 1 + GuiTextArea.font.width(data.text.substring(0, this.cursorPosition - data.start)), this.y + 1 + (i - 1 - this.scrolledLine) * this.container.lineHeight);
            }
            else {
                ++i;
            }
        }
        return 0;
    }
    
    private int cursorDown() {
        for (int i = 0; i < this.container.lines.size(); ++i) {
            TextContainer.LineData data = this.container.lines.get(i);
            if (this.cursorPosition >= data.start && this.cursorPosition < data.end) {
                int linePos = this.cursorPosition - data.start;
                return this.getSelectionPos(this.x + 1 + GuiTextArea.font.width(data.text.substring(0, this.cursorPosition - data.start)), this.y + 1 + (i + 1 - this.scrolledLine) * this.container.lineHeight);
            }
        }
        return this.text.length();
    }
    
    public String getSelectionBeforeText() {
        if (this.startSelection == 0) {
            return "";
        }
        return this.text.substring(0, this.startSelection);
    }
    
    public String getSelectionAfterText() {
        return this.text.substring(this.endSelection);
    }
    
    public boolean mouseClicked(int xMouse, int yMouse, int mouseButton) {
        this.active = (xMouse >= this.x && xMouse < this.x + this.width && yMouse >= this.y && yMouse < this.y + this.height);
        if (this.active) {
            int selectionPos = this.getSelectionPos(xMouse, yMouse);
            this.cursorPosition = selectionPos;
            this.endSelection = selectionPos;
            this.startSelection = selectionPos;
            this.clicked = (mouseButton == 0);
            this.doubleClicked = false;
            long time = System.currentTimeMillis();
            if (this.clicked && this.container.linesCount * this.container.lineHeight > this.height && xMouse > this.x + this.width - 8) {
                this.clicked = false;
                this.clickScrolling = true;
            }
            else if (time - this.lastClicked < 500L) {
                this.doubleClicked = true;
                Matcher m = this.container.regexWord.matcher(this.text);
                while (m.find()) {
                    if (this.cursorPosition > m.start() && this.cursorPosition < m.end()) {
                        this.startSelection = m.start();
                        this.endSelection = m.end();
                        break;
                    }
                }
            }
            this.lastClicked = time;
        }
        return this.active;
    }
    
    public void updateScreen() {
        ++this.cursorCounter;
        int k2 = Mouse.getDWheel();
        if (k2 != 0) {
            this.scrolledLine += ((k2 > 0) ? -1 : 1);
            this.scrolledLine = Math.max(Math.min(this.scrolledLine, this.container.linesCount - this.height / this.container.lineHeight), 0);
        }
    }
    
    public void setText(String text) {
        text = text.replace("\r", "");
        if (this.text != null && this.text.equals(text)) {
            return;
        }
        if (this.listener != null) {
            this.listener.textUpdate(text);
        }
        if (!this.undoing) {
            this.undoList.add(new UndoData(this.text, this.cursorPosition));
            this.redoList.clear();
        }
        this.text = text;
        (this.container = new TextContainer(text)).init(GuiTextArea.font, this.width, this.height);
        if (this.enableCodeHighlighting) {
            this.container.formatCodeText();
        }
        if (this.scrolledLine > this.container.linesCount - this.container.visibleLines) {
            this.scrolledLine = Math.max(0, this.container.linesCount - this.container.visibleLines);
        }
    }
    
    public String getText() {
        return this.text;
    }
    
    public boolean isEnabled() {
        return this.enabled && this.visible;
    }
    
    public boolean hasVerticalScrollbar() {
        return this.container.visibleLines < this.container.linesCount;
    }
    
    public void enableCodeHighlighting() {
        this.enableCodeHighlighting = true;
        this.container.formatCodeText();
    }
    
    public void setListener(ITextChangeListener listener) {
        this.listener = listener;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    static {
        GuiTextArea.font = new TrueTypeFont(new Font("Arial Unicode MS", 0, CustomNpcs.FontSize), 1.0f);
    }
    
    class UndoData
    {
        public String text;
        public int cursorPosition;
        
        public UndoData(String text, int cursorPosition) {
            this.text = text;
            this.cursorPosition = cursorPosition;
        }
    }
}
