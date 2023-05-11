package noppes.npcs.controllers;

import noppes.npcs.api.handler.data.IDialog;
import java.util.Collection;
import java.util.ArrayList;
import noppes.npcs.api.handler.data.IDialogCategory;
import java.util.List;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.controllers.data.DialogOption;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import java.io.InputStream;
import net.minecraft.nbt.CompressedStreamTools;
import java.io.FileInputStream;
import noppes.npcs.util.NBTJsonUtil;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import java.util.HashMap;
import noppes.npcs.api.handler.IDialogHandler;

public class DialogController implements IDialogHandler
{
    public HashMap<Integer, DialogCategory> categoriesSync;
    public HashMap<Integer, DialogCategory> categories;
    public HashMap<Integer, Dialog> dialogs;
    public static DialogController instance;
    private int lastUsedDialogID;
    private int lastUsedCatID;
    
    public DialogController() {
        this.categoriesSync = new HashMap<Integer, DialogCategory>();
        this.categories = new HashMap<Integer, DialogCategory>();
        this.dialogs = new HashMap<Integer, Dialog>();
        this.lastUsedDialogID = 0;
        this.lastUsedCatID = 0;
        DialogController.instance = this;
    }
    
    public void load() {
        LogWriter.info("Loading Dialogs");
        this.loadCategories();
        LogWriter.info("Done loading Dialogs");
    }
    
    private void loadCategories() {
        this.categories.clear();
        this.dialogs.clear();
        this.lastUsedCatID = 0;
        this.lastUsedDialogID = 0;
        try {
            File file = new File(CustomNpcs.getWorldSaveDirectory(), "dialog.dat");
            if (file.exists()) {
                this.loadCategoriesOld(file);
                file.delete();
                file = new File(CustomNpcs.getWorldSaveDirectory(), "dialog.dat_old");
                if (file.exists()) {
                    file.delete();
                }
                return;
            }
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
        File dir = this.getDir();
        if (!dir.exists()) {
            dir.mkdir();
            this.loadDefaultDialogs();
        }
        else {
            for (File file2 : dir.listFiles()) {
                if (file2.isDirectory()) {
                    DialogCategory category = this.loadCategoryDir(file2);
                    Iterator<Map.Entry<Integer, Dialog>> ite = category.dialogs.entrySet().iterator();
                    while (ite.hasNext()) {
                        Map.Entry<Integer, Dialog> entry = ite.next();
                        int id = entry.getKey();
                        if (id > this.lastUsedDialogID) {
                            this.lastUsedDialogID = id;
                        }
                        Dialog dialog = entry.getValue();
                        if (this.dialogs.containsKey(id)) {
                            LogWriter.error("Duplicate id " + dialog.id + " from category " + category.title);
                            ite.remove();
                        }
                        else {
                            this.dialogs.put(id, dialog);
                        }
                    }
                    ++this.lastUsedCatID;
                    category.id = this.lastUsedCatID;
                    this.categories.put(category.id, category);
                }
            }
        }
    }
    
    private DialogCategory loadCategoryDir(File dir) {
        DialogCategory category = new DialogCategory();
        category.title = dir.getName();
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                if (file.getName().endsWith(".json")) {
                    try {
                        Dialog dialog = new Dialog(category);
                        dialog.id = Integer.parseInt(file.getName().substring(0, file.getName().length() - 5));
                        dialog.readNBTPartial(NBTJsonUtil.LoadFile(file));
                        category.dialogs.put(dialog.id, dialog);
                    }
                    catch (Exception e) {
                        LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                    }
                }
            }
        }
        return category;
    }
    
    private void loadCategoriesOld(File file) throws Exception {
        NBTTagCompound nbttagcompound1 = CompressedStreamTools.readCompressed((InputStream)new FileInputStream(file));
        NBTTagList list = nbttagcompound1.getTagList("Data", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.tagCount(); ++i) {
            DialogCategory category = new DialogCategory();
            category.readNBT(list.getCompoundTagAt(i));
            this.saveCategory(category);
            Iterator<Map.Entry<Integer, Dialog>> ita = category.dialogs.entrySet().iterator();
            while (ita.hasNext()) {
                Map.Entry<Integer, Dialog> entry = ita.next();
                Dialog dialog = entry.getValue();
                dialog.id = entry.getKey();
                if (this.dialogs.containsKey(dialog.id)) {
                    ita.remove();
                }
                else {
                    this.saveDialog(category, dialog);
                }
            }
        }
    }
    
    private void loadDefaultDialogs() {
        DialogCategory cat = new DialogCategory();
        cat.id = this.lastUsedCatID++;
        cat.title = "Villager";
        Dialog dia1 = new Dialog(cat);
        dia1.id = 1;
        dia1.title = "Start";
        dia1.text = "Hello {player}, \n\nWelcome to our village. I hope you enjoy your stay";
        Dialog dia2 = new Dialog(cat);
        dia2.id = 2;
        dia2.title = "Ask about village";
        dia2.text = "This village has been around for ages. Enjoy your stay here.";
        Dialog dia3 = new Dialog(cat);
        dia3.id = 3;
        dia3.title = "Who are you";
        dia3.text = "I'm a villager here. I have lived in this village my whole life.";
        cat.dialogs.put(dia1.id, dia1);
        cat.dialogs.put(dia2.id, dia2);
        cat.dialogs.put(dia3.id, dia3);
        DialogOption option = new DialogOption();
        option.title = "Tell me something about this village";
        option.dialogId = 2;
        option.optionType = 1;
        DialogOption option2 = new DialogOption();
        option2.title = "Who are you?";
        option2.dialogId = 3;
        option2.optionType = 1;
        DialogOption option3 = new DialogOption();
        option3.title = "Goodbye";
        option3.optionType = 0;
        dia1.options.put(0, option2);
        dia1.options.put(1, option);
        dia1.options.put(2, option3);
        DialogOption option4 = new DialogOption();
        option4.title = "Back";
        option4.dialogId = 1;
        dia2.options.put(1, option4);
        dia3.options.put(1, option4);
        this.lastUsedDialogID = 3;
        this.lastUsedCatID = 1;
        this.saveCategory(cat);
        this.saveDialog(cat, dia1);
        this.saveDialog(cat, dia2);
        this.saveDialog(cat, dia3);
    }
    
    public void saveCategory(DialogCategory category) {
        category.title = NoppesStringUtils.cleanFileName(category.title);
        if (this.categories.containsKey(category.id)) {
            DialogCategory currentCategory = this.categories.get(category.id);
            if (!currentCategory.title.equals(category.title)) {
                while (this.containsCategoryName(category)) {
                    category.title += "_";
                }
                File newdir = new File(this.getDir(), category.title);
                File olddir = new File(this.getDir(), currentCategory.title);
                if (newdir.exists()) {
                    return;
                }
                if (!olddir.renameTo(newdir)) {
                    return;
                }
            }
            category.dialogs = currentCategory.dialogs;
        }
        else {
            if (category.id < 0) {
                ++this.lastUsedCatID;
                category.id = this.lastUsedCatID;
            }
            while (this.containsCategoryName(category)) {
                category.title += "_";
            }
            File dir = new File(this.getDir(), category.title);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        this.categories.put(category.id, category);
        Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, 5, category.writeNBT(new NBTTagCompound()));
    }
    
    public void removeCategory(int category) {
        DialogCategory cat = this.categories.get(category);
        if (cat == null) {
            return;
        }
        File dir = new File(this.getDir(), cat.title);
        if (!dir.delete()) {
            return;
        }
        for (int dia : cat.dialogs.keySet()) {
            this.dialogs.remove(dia);
        }
        this.categories.remove(category);
        Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, 5, category);
    }
    
    public boolean containsCategoryName(DialogCategory category) {
        for (DialogCategory cat : this.categories.values()) {
            if (category.id != cat.id && cat.title.equalsIgnoreCase(category.title)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsDialogName(DialogCategory category, Dialog dialog) {
        for (Dialog dia : category.dialogs.values()) {
            if (dia.id != dialog.id && dia.title.equalsIgnoreCase(dialog.title)) {
                return true;
            }
        }
        return false;
    }
    
    public Dialog saveDialog(DialogCategory category, Dialog dialog) {
        if (category == null) {
            return dialog;
        }
        while (this.containsDialogName(dialog.category, dialog)) {
            dialog.title += "_";
        }
        if (dialog.id < 0) {
            ++this.lastUsedDialogID;
            dialog.id = this.lastUsedDialogID;
        }
        this.dialogs.put(dialog.id, dialog);
        category.dialogs.put(dialog.id, dialog);
        File dir = new File(this.getDir(), category.title);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, dialog.id + ".json_new");
        File file2 = new File(dir, dialog.id + ".json");
        try {
            NBTTagCompound compound = dialog.writeToNBT(new NBTTagCompound());
            NBTJsonUtil.SaveFile(file, compound);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, 4, compound, category.id);
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
        return dialog;
    }
    
    public void removeDialog(Dialog dialog) {
        DialogCategory category = dialog.category;
        File file = new File(new File(this.getDir(), category.title), dialog.id + ".json");
        if (!file.delete()) {
            return;
        }
        category.dialogs.remove(dialog.id);
        this.dialogs.remove(dialog.id);
        Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, 4, dialog.id);
    }
    
    private File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), "dialogs");
    }
    
    public boolean hasDialog(int dialogId) {
        return this.dialogs.containsKey(dialogId);
    }
    
    @Override
    public List<IDialogCategory> categories() {
        return new ArrayList<IDialogCategory>(this.categories.values());
    }
    
    @Override
    public IDialog get(int id) {
        return this.dialogs.get(id);
    }
    
    static {
        DialogController.instance = new DialogController();
    }
}
