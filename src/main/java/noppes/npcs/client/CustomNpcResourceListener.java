package noppes.npcs.client;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import noppes.npcs.CustomNpcs;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class CustomNpcResourceListener implements IResourceManagerReloadListener
{
    public static int DefaultTextColor;
    
    public void onResourceManagerReload(IResourceManager var1) {
        if (var1 instanceof SimpleReloadableResourceManager) {
            this.createTextureCache();
            SimpleReloadableResourceManager simplemanager = (SimpleReloadableResourceManager)var1;
            FolderResourcePack pack = new FolderResourcePack(CustomNpcs.Dir);
            simplemanager.reloadResourcePack((IResourcePack)pack);
            try {
                CustomNpcResourceListener.DefaultTextColor = Integer.parseInt(I18n.translateToLocal("customnpcs.defaultTextColor"), 16);
            }
            catch (NumberFormatException e) {
                CustomNpcResourceListener.DefaultTextColor = 4210752;
            }
        }
    }
    
    private void createTextureCache() {
        this.enlargeTexture("planks_oak");
        this.enlargeTexture("planks_big_oak");
        this.enlargeTexture("planks_birch");
        this.enlargeTexture("planks_jungle");
        this.enlargeTexture("planks_spruce");
        this.enlargeTexture("planks_acacia");
        this.enlargeTexture("iron_block");
        this.enlargeTexture("diamond_block");
        this.enlargeTexture("stone");
        this.enlargeTexture("gold_block");
        this.enlargeTexture("wool_colored_white");
    }
    
    private void enlargeTexture(String texture) {
        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
        if (manager == null) {
            return;
        }
        ResourceLocation location = new ResourceLocation("customnpcs:textures/cache/" + texture + ".png");
        ITextureObject ob = manager.getTexture(location);
        if (ob == null || !(ob instanceof TextureCache)) {
            ob = (ITextureObject)new TextureCache(location);
            manager.loadTexture(location, ob);
        }
        ((TextureCache)ob).setImage(new ResourceLocation("textures/blocks/" + texture + ".png"));
    }
    
    static {
        CustomNpcResourceListener.DefaultTextColor = 4210752;
    }
}
