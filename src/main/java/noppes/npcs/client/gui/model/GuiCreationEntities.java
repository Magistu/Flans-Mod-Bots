package noppes.npcs.client.gui.model;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.gui.GuiButton;
import java.util.Map;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcButton;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNpcClassicPlayer;
import noppes.npcs.entity.EntityNpcAlex;
import noppes.npcs.entity.EntityNPC64x32;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import java.lang.reflect.Modifier;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import java.util.HashMap;
import noppes.npcs.client.gui.util.ICustomScrollListener;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ICustomScrollListener
{
    public HashMap<String, Class<? extends EntityLivingBase>> data;
    private List<String> list;
    private GuiCustomScroll scroll;
    private boolean resetToSelected;
    
    public GuiCreationEntities(EntityNPCInterface npc) {
        super(npc);
        this.data = new HashMap<String, Class<? extends EntityLivingBase>>();
        this.resetToSelected = true;
        for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
            String name = ent.getName();
            Class<? extends Entity> c = (Class<? extends Entity>)ent.getEntityClass();
            try {
                if (!EntityLiving.class.isAssignableFrom(c) || c.getConstructor(World.class) == null || Modifier.isAbstract(c.getModifiers()) || !(Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject((Class)c) instanceof RenderLivingBase)) {
                    continue;
                }
                String s = name;
                if (s.toLowerCase().contains("customnpc")) {
                    continue;
                }
                this.data.put(name, c.asSubclass(EntityLivingBase.class));
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
            catch (NoSuchMethodException ex) {}
        }
        this.data.put("NPC 64x32", (Class<? extends EntityLivingBase>)EntityNPC64x32.class);
        this.data.put("NPC Alex Arms", (Class<? extends EntityLivingBase>)EntityNpcAlex.class);
        this.data.put("NPC Classic Player", (Class<? extends EntityLivingBase>)EntityNpcClassicPlayer.class);
        (this.list = new ArrayList<String>(this.data.keySet())).add("NPC");
        Collections.sort(this.list, String.CASE_INSENSITIVE_ORDER);
        this.active = 1;
        this.xOffset = 60;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new GuiNpcButton(10, this.guiLeft, this.guiTop + 46, 120, 20, "Reset To NPC"));
        if (this.scroll == null) {
            (this.scroll = new GuiCustomScroll(this, 0)).setUnsortedList(this.list);
        }
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 68;
        this.scroll.setSize(100, this.ySize - 96);
        String selected = "NPC";
        if (this.entity != null) {
            for (Map.Entry<String, Class<? extends EntityLivingBase>> en : this.data.entrySet()) {
                if (en.getValue().toString().equals(this.entity.getClass().toString())) {
                    selected = en.getKey();
                }
            }
        }
        this.scroll.setSelected(selected);
        if (this.resetToSelected) {
            this.scroll.scrollTo(this.scroll.getSelected());
            this.resetToSelected = false;
        }
        this.addScroll(this.scroll);
    }
    
    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn.id == 10) {
            this.playerdata.setEntityClass(null);
            this.resetToSelected = true;
            this.initGui();
        }
    }
    
    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        this.playerdata.setEntityClass(this.data.get(scroll.getSelected()));
        Entity entity = (Entity)this.playerdata.getEntity(this.npc);
        if (entity != null) {
            RenderLivingBase render = (RenderLivingBase)this.mc.getRenderManager().getEntityClassRenderObject((Class)entity.getClass());
            if (!NPCRendererHelper.getTexture(render, entity).equals(TextureMap.LOCATION_MISSING_TEXTURE.toString())) {
                this.npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
            }
        }
        else {
            this.npc.display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
        }
        this.initGui();
    }
    
    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
