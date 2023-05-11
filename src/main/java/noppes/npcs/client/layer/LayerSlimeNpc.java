package noppes.npcs.client.layer;

import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.model.ModelNpcSlime;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerSlimeNpc implements LayerRenderer
{
    private RenderLiving renderer;
    private ModelBase slimeModel;
    
    public LayerSlimeNpc(RenderLiving renderer) {
        this.slimeModel = new ModelNpcSlime(0);
        this.renderer = renderer;
    }
    
    public boolean shouldCombineTextures() {
        return true;
    }
    
    public void doRenderLayer(EntityLivingBase living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (living.isInvisible()) {
            return;
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        this.slimeModel.setModelAttributes(this.renderer.getMainModel());
        this.slimeModel.render((Entity)living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
    }
}
