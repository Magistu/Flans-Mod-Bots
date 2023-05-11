package noppes.npcs.controllers;

import java.util.Map;
import java.util.List;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.constants.EnumScriptType;

public interface IScriptHandler
{
    void runScript(EnumScriptType p0, Event p1);
    
    boolean isClient();
    
    boolean getEnabled();
    
    void setEnabled(boolean p0);
    
    String getLanguage();
    
    void setLanguage(String p0);
    
    List<ScriptContainer> getScripts();
    
    String noticeString();
    
    Map<Long, String> getConsoleText();
    
    void clearConsole();
}
