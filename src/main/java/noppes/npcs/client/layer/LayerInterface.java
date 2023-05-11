package noppes.npcs.client.layer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.client.ClientProxy;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.ModelPartData;
import net.minecraft.client.model.ModelBiped;
import noppes.npcs.ModelData;
import noppes.npcs.entity.EntityCustomNpc;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public abstract class LayerInterface implements LayerRenderer
{
    protected RenderLiving render;
    protected EntityCustomNpc npc;
    protected ModelData playerdata;
    public ModelBiped model;
    
    public LayerInterface(RenderLiving render) {
        this.render = render;
        this.model = (ModelBiped)render.getMainModel();
    }
    
    public void setColor(ModelPartData data, EntityLivingBase entity) {
    }
    
    public void preRender(ModelPartData data) {
        if (data.playerTexture) {
            ClientProxy.bindTexture(this.npc.textureLocation);
        }
        else {
            ClientProxy.bindTexture(data.getResource());
        }
        if (this.npc.hurtTime > 0 || this.npc.deathTime > 0) {
            return;
        }
        int color = data.color;
        if (this.npc.display.getTint() != 16777215) {
            if (data.color != 16777215) {
                color = this.blend(data.color, this.npc.display.getTint(), 0.5f);
            }
            else {
                color = this.npc.display.getTint();
            }
        }
        float red = (color >> 16 & 0xFF) / 255.0f;
        float green = (color >> 8 & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, this.npc.isInvisible() ? 0.15f : 0.99f);
    }
    
    private int blend(int color1, int color2, float ratio) {
        if (ratio >= 1.0f) {
            return color2;
        }
        if (ratio <= 0.0f) {
            return color1;
        }
        int aR = (color1 & 0xFF0000) >> 16;
        int aG = (color1 & 0xFF00) >> 8;
        int aB = color1 & 0xFF;
        int bR = (color2 & 0xFF0000) >> 16;
        int bG = (color2 & 0xFF00) >> 8;
        int bB = color2 & 0xFF;
        int R = (int)(aR + (bR - aR) * ratio);
        int G = (int)(aG + (bG - aG) * ratio);
        int B = (int)(aB + (bB - aB) * ratio);
        return R << 16 | G << 8 | B;
    }
    
    public void doRenderLayer(EntityLivingBase entity, float par2, float par3, float par8, float par4, float par5, float par6, float par7) {
        this.npc = (EntityCustomNpc)entity;
        if (this.npc.isInvisibleToPlayer((EntityPlayer)Minecraft.getMinecraft().player)) {
            return;
        }
        this.playerdata = this.npc.modelData;
        this.model = (ModelBiped)this.render.getMainModel();
        this.rotate(par2, par3, par4, par5, par6, par7);
        GlStateManager.pushMatrix();
        if (entity.isInvisible()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.15f);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569f);
        }
        if (this.npc.hurtTime > 0 || this.npc.deathTime > 0) {
            GlStateManager.color(1.0f, 0.0f, 0.0f, 0.3f);
        }
        if (this.npc.isSneaking()) {
            GlStateManager.translate(0.0f, 0.2f, 0.0f);
        }
        GlStateManager.enableRescaleNormal();
        this.render(par2, par3, par4, par5, par6, par7);
        GlStateManager.disableRescaleNormal();
        if (entity.isInvisible()) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }
    
    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    public boolean shouldCombineTextures() {
        return false;
    }
    
    public abstract void render(float p0, float p1, float p2, float p3, float p4, float p5);
    
    public abstract void rotate(float p0, float p1, float p2, float p3, float p4, float p5);
}
