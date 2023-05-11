package noppes.npcs.client.controllers;

import noppes.npcs.CustomNpcs;
import java.io.File;
import noppes.npcs.controllers.ServerCloneController;

public class ClientCloneController extends ServerCloneController
{
    public static ClientCloneController Instance;
    
    @Override
    public File getDir() {
        File dir = new File(CustomNpcs.Dir, "clones");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }
    
    static {
        ClientCloneController.Instance = new ClientCloneController();
    }
}
