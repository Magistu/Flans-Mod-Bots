package noppes.npcs.client.gui.select;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.gui.GuiButton;
import java.util.ArrayList;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcButton;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.Iterator;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.client.resources.DefaultResourcePack;
import noppes.npcs.CustomNpcs;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Loader;
import java.io.File;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.LegacyV2Adapter;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import java.util.HashSet;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.entity.EntityNPCInterface;
import java.util.List;
import java.util.HashMap;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class GuiTextureSelection extends SubGuiInterface implements ICustomScrollListener
{
    private String up;
    private GuiCustomScroll scrollCategories;
    private GuiCustomScroll scrollQuests;
    private String location;
    private String selectedDomain;
    public ResourceLocation selectedResource;
    private HashMap<String, List<TextureData>> domains;
    private HashMap<String, TextureData> textures;

    @SuppressWarnings("unused")
    public GuiTextureSelection(EntityNPCInterface npc, String texture) {
        this.up = "..<" + I18n.translateToLocal("gui.up") + ">..";
        this.location = "";
        this.domains = new HashMap<String, List<TextureData>>();
        this.textures = new HashMap<String, TextureData>();
        this.npc = npc;
        this.drawDefaultBackground = false;
        this.title = "";
        this.setBackground("menubg.png");
        this.xSize = 366;
        this.ySize = 226;
        SimpleReloadableResourceManager simplemanager = (SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();
        Map<String, FallbackResourceManager> map = (Map<String, FallbackResourceManager>)ObfuscationReflectionHelper.getPrivateValue((Class)SimpleReloadableResourceManager.class, (Object)simplemanager, 2);
        HashSet<String> set = new HashSet<String>();
        for (String name : map.keySet()) {
            FallbackResourceManager manager = map.get(name);
            List<IResourcePack> list = (List<IResourcePack>)ObfuscationReflectionHelper.getPrivateValue((Class)FallbackResourceManager.class, (Object)manager, 1);
            for (IResourcePack pack : list) {
                if (pack instanceof LegacyV2Adapter) {
                    pack = (IResourcePack)ObfuscationReflectionHelper.getPrivateValue((Class)LegacyV2Adapter.class, (Object)pack, 0);
                }
                if (pack instanceof AbstractResourcePack) {
                    AbstractResourcePack p = (AbstractResourcePack)pack;
                    File file = new File(p.getPackName());
                    if (file == null) {
                        continue;
                    }
                    set.add(file.getAbsolutePath());
                }
            }
        }
        for (String file2 : set) {
            File f = new File(file2);
            if (f.isDirectory()) {
                this.checkFolder(new File(f, "assets"), f.getAbsolutePath().length());
            }
            else {
                this.progressFile(f);
            }
        }
        for (ModContainer mod : Loader.instance().getModList()) {
            if (mod.getSource().exists()) {
                this.progressFile(mod.getSource());
            }
        }
        ResourcePackRepository repos = Minecraft.getMinecraft().getResourcePackRepository();
        repos.updateRepositoryEntriesAll();
        List<ResourcePackRepository.Entry> list2 = (List<ResourcePackRepository.Entry>)repos.getRepositoryEntries();
        if (repos.getServerResourcePack() != null) {
            AbstractResourcePack p2 = (AbstractResourcePack)repos.getServerResourcePack();
            File file3 = new File(p2.getPackName());
            if (file3 != null) {
                this.progressFile(file3);
            }
        }
        for (ResourcePackRepository.Entry entry : list2) {
            File file4 = new File(repos.getDirResourcepacks(), entry.getResourcePackName());
            if (file4.exists()) {
                this.progressFile(file4);
            }
        }
        this.checkFolder(new File(CustomNpcs.Dir, "assets"), CustomNpcs.Dir.getAbsolutePath().length());
        URL url = DefaultResourcePack.class.getResource("/");
        if (url != null) {
            File f2 = this.decodeFile(url.getFile());
            if (f2.isDirectory()) {
                this.checkFolder(new File(f2, "assets"), url.getFile().length());
            }
            else {
                this.progressFile(f2);
            }
        }
        url = CraftingManager.class.getResource("/assets/.mcassetsroot");
        if (url != null) {
            File f2 = this.decodeFile(url.getFile());
            if (f2.isDirectory()) {
                this.checkFolder(new File(f2, "assets"), url.getFile().length());
            }
            else {
                this.progressFile(f2);
            }
        }
        if (texture != null && !texture.isEmpty()) {
            this.selectedResource = new ResourceLocation(texture);
            this.selectedDomain = this.selectedResource.getNamespace();
            if (!this.domains.containsKey(this.selectedDomain)) {
                this.selectedDomain = null;
            }
            int i = this.selectedResource.getPath().lastIndexOf(47);
            this.location = this.selectedResource.getPath().substring(0, i + 1);
            if (this.location.startsWith("textures/")) {
                this.location = this.location.substring(9);
            }
        }
    }
    
    private File decodeFile(String url) {
        if (url.startsWith("file:")) {
            url = url.substring(5);
        }
        url = url.replace('/', File.separatorChar);
        int i = url.indexOf(33);
        if (i > 0) {
            url = url.substring(0, i);
        }
        try {
            url = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException ex) {}
        return new File(url);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (this.selectedDomain != null) {
            this.title = this.selectedDomain + ":" + this.location;
        }
        else {
            this.title = "";
        }
        this.addButton(new GuiNpcButton(2, this.guiLeft + 264, this.guiTop + 170, 90, 20, "gui.done"));
        this.addButton(new GuiNpcButton(1, this.guiLeft + 264, this.guiTop + 190, 90, 20, "gui.cancel"));
        if (this.scrollCategories == null) {
            (this.scrollCategories = new GuiCustomScroll(this, 0)).setSize(120, 200);
        }
        if (this.selectedDomain == null) {
            this.scrollCategories.setList(Lists.newArrayList((Iterable)this.domains.keySet()));
            if (this.selectedDomain != null) {
                this.scrollCategories.setSelected(this.selectedDomain);
            }
        }
        else {
            List<String> list = new ArrayList<String>();
            list.add(this.up);
            List<TextureData> data = this.domains.get(this.selectedDomain);
            for (TextureData td : data) {
                if (this.location.isEmpty() || (td.path.startsWith(this.location) && !td.path.equals(this.location))) {
                    String path = td.path.substring(this.location.length());
                    int i = path.indexOf(47);
                    if (i < 0) {
                        continue;
                    }
                    path = path.substring(0, i);
                    if (path.isEmpty() || list.contains(path)) {
                        continue;
                    }
                    list.add(path);
                }
            }
            this.scrollCategories.setList(list);
        }
        this.scrollCategories.guiLeft = this.guiLeft + 4;
        this.scrollCategories.guiTop = this.guiTop + 14;
        this.addScroll(this.scrollCategories);
        if (this.scrollQuests == null) {
            (this.scrollQuests = new GuiCustomScroll(this, 1)).setSize(130, 200);
        }
        if (this.selectedDomain != null) {
            this.textures.clear();
            List<TextureData> data2 = this.domains.get(this.selectedDomain);
            List<String> list2 = new ArrayList<String>();
            String loc = this.location;
            if (this.scrollCategories.hasSelected() && !this.scrollCategories.getSelected().equals(this.up)) {
                loc = loc + this.scrollCategories.getSelected() + '/';
            }
            for (TextureData td2 : data2) {
                if (td2.path.equals(loc) && !list2.contains(td2.name)) {
                    list2.add(td2.name);
                    this.textures.put(td2.name, td2);
                }
            }
            this.scrollQuests.setList(list2);
        }
        if (this.selectedResource != null) {
            this.scrollQuests.setSelected(this.selectedResource.getPath());
        }
        this.scrollQuests.guiLeft = this.guiLeft + 125;
        this.scrollQuests.guiTop = this.guiTop + 14;
        this.addScroll(this.scrollQuests);
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        if (guibutton.id == 2) {
            this.npc.display.setSkinTexture(this.selectedResource.toString());
        }
        this.npc.textureLocation = null;
        this.close();
        this.parent.initGui();
    }
    
    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
        this.npc.textureLocation = this.selectedResource;
        this.drawNpc((EntityLivingBase)this.npc, this.guiLeft + 276, this.guiTop + 140, 2.0f, 0);
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        if (scroll == this.scrollQuests) {
            if (scroll.id == 1) {
                TextureData data = this.textures.get(scroll.getSelected());
                this.selectedResource = new ResourceLocation(this.selectedDomain, data.absoluteName);
            }
        }
        else {
            this.initGui();
        }
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        if (scroll == this.scrollCategories) {
            if (this.selectedDomain == null) {
                this.selectedDomain = selection;
            }
            else if (selection.equals(this.up)) {
                int i = this.location.lastIndexOf(47, this.location.length() - 2);
                if (i < 0) {
                    if (this.location.isEmpty()) {
                        this.selectedDomain = null;
                    }
                    this.location = "";
                }
                else {
                    this.location = this.location.substring(0, i + 1);
                }
            }
            else {
                this.location = this.location + selection + '/';
            }
            this.scrollCategories.selected = -1;
            this.scrollQuests.selected = -1;
            this.initGui();
        }
        else {
            this.npc.display.setSkinTexture(this.selectedResource.toString());
            this.close();
            this.parent.initGui();
        }
    }
    
    private void progressFile(File file) {
        try {
            if (!file.isDirectory() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))) {
                ZipFile zip = new ZipFile(file);
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry zipentry = (ZipEntry)entries.nextElement();
                    String entryName = zipentry.getName();
                    this.addFile(entryName);
                }
                zip.close();
            }
            else if (file.isDirectory()) {
                int length = file.getAbsolutePath().length();
                this.checkFolder(file, length);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void checkFolder(File file, int length) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            String name = f.getAbsolutePath().substring(length);
            name = name.replace("\\", "/");
            if (!name.startsWith("/")) {
                name = "/" + name;
            }
            if (f.isDirectory()) {
                this.addFile(name + "/");
                this.checkFolder(f, length);
            }
            else {
                this.addFile(name);
            }
        }
    }
    
    private void addFile(String name) {
        name = name.toLowerCase();
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if (!name.startsWith("assets/") || !name.toLowerCase().endsWith(".png")) {
            return;
        }
        name = name.substring(7);
        int i = name.indexOf(47);
        String domain = name.substring(0, i);
        name = name.substring(i + 10);
        List<TextureData> list = this.domains.get(domain);
        if (list == null) {
            this.domains.put(domain, list = new ArrayList<TextureData>());
        }
        boolean contains = false;
        for (TextureData data : list) {
            if (data.absoluteName.equals(name)) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            list.add(new TextureData(domain, name));
        }
    }
    
    class TextureData
    {
        String domain;
        String absoluteName;
        String name;
        String path;
        
        public TextureData(String domain, String absoluteName) {
            this.domain = domain;
            int i = absoluteName.lastIndexOf(47);
            this.name = absoluteName.substring(i + 1);
            this.path = absoluteName.substring(0, i + 1);
            this.absoluteName = "textures/" + absoluteName;
        }
    }
}
