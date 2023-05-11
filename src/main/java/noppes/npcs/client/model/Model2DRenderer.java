package noppes.npcs.client.model;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class Model2DRenderer extends ModelRenderer
{
    private boolean isCompiled;
    private int displayList;
    private float x1;
    private float x2;
    private float y1;
    private float y2;
    private int width;
    private int height;
    private float rotationOffsetX;
    private float rotationOffsetY;
    private float rotationOffsetZ;
    private float scaleX;
    private float scaleY;
    private float thickness;
    
    public Model2DRenderer(ModelBase modelBase, float x, float y, int width, int height, int textureWidth, int textureHeight) {
        super(modelBase);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.thickness = 1.0f;
        this.width = width;
        this.height = height;
        this.textureWidth = (float)textureWidth;
        this.textureHeight = (float)textureHeight;
        this.x1 = x / textureWidth;
        this.y1 = y / textureHeight;
        this.x2 = (x + width) / textureWidth;
        this.y2 = (y + height) / textureHeight;
    }
    
    public Model2DRenderer(ModelBase modelBase, float x, float y, int width, int height) {
        this(modelBase, x, y, width, height, modelBase.textureWidth, modelBase.textureHeight);
    }
    
    public void render(float scale) {
        if (!this.showModel || this.isHidden) {
            return;
        }
        if (!this.isCompiled) {
            this.compile(scale);
        }
        GlStateManager.pushMatrix();
        this.postRender(scale);
        GlStateManager.callList(this.displayList);
        GlStateManager.popMatrix();
    }
    
    public void setRotationOffset(float x, float y, float z) {
        this.rotationOffsetX = x;
        this.rotationOffsetY = y;
        this.rotationOffsetZ = z;
    }
    
    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }
    
    public void setScale(float x, float y) {
        this.scaleX = x;
        this.scaleY = y;
    }
    
    public void setThickness(float thickness) {
        this.thickness = thickness;
    }
    
    @SideOnly(Side.CLIENT)
    private void compile(float scale) {
        GlStateManager.glNewList(this.displayList = GLAllocation.generateDisplayLists(1), 4864);
        GlStateManager.translate(this.rotationOffsetX * scale, this.rotationOffsetY * scale, this.rotationOffsetZ * scale);
        GlStateManager.scale(this.scaleX * this.width / this.height, this.scaleY, this.thickness);
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
        if (this.mirror) {
            GlStateManager.translate(0.0f, 0.0f, -1.0f * scale);
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        }
        renderItemIn2D(Tessellator.getInstance().getBuffer(), this.x1, this.y1, this.x2, this.y2, this.width, this.height, scale);
        GL11.glEndList();
        this.isCompiled = true;
    }
    
    public static void renderItemIn2D(BufferBuilder worldrenderer, float p_78439_1_, float p_78439_2_, float p_78439_3_, float p_78439_4_, int p_78439_5_, int p_78439_6_, float p_78439_7_) {
        Tessellator tessellator = Tessellator.getInstance();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        worldrenderer.pos(0.0, 0.0, 0.0).tex((double)p_78439_1_, (double)p_78439_4_).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(1.0, 0.0, 0.0).tex((double)p_78439_3_, (double)p_78439_4_).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(1.0, 1.0, 0.0).tex((double)p_78439_3_, (double)p_78439_2_).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(0.0, 1.0, 0.0).tex((double)p_78439_1_, (double)p_78439_2_).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(0.0, 1.0, (double)(0.0f - p_78439_7_)).tex((double)p_78439_1_, (double)p_78439_2_).normal(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(1.0, 1.0, (double)(0.0f - p_78439_7_)).tex((double)p_78439_3_, (double)p_78439_2_).normal(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(1.0, 0.0, (double)(0.0f - p_78439_7_)).tex((double)p_78439_3_, (double)p_78439_4_).normal(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(0.0, 0.0, (double)(0.0f - p_78439_7_)).tex((double)p_78439_1_, (double)p_78439_4_).normal(0.0f, 0.0f, -1.0f).endVertex();
        float f5 = 0.5f * (p_78439_1_ - p_78439_3_) / p_78439_5_;
        float f6 = 0.5f * (p_78439_4_ - p_78439_2_) / p_78439_6_;
        for (int k = 0; k < p_78439_5_; ++k) {
            float f7 = k / (float)p_78439_5_;
            float f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            worldrenderer.pos((double)f7, 0.0, (double)(0.0f - p_78439_7_)).tex((double)f8, (double)p_78439_4_).normal(-1.0f, 0.0f, 0.0f).endVertex();
            worldrenderer.pos((double)f7, 0.0, 0.0).tex((double)f8, (double)p_78439_4_).normal(-1.0f, 0.0f, 0.0f).endVertex();
            worldrenderer.pos((double)f7, 1.0, 0.0).tex((double)f8, (double)p_78439_2_).normal(-1.0f, 0.0f, 0.0f).endVertex();
            worldrenderer.pos((double)f7, 1.0, (double)(0.0f - p_78439_7_)).tex((double)f8, (double)p_78439_2_).normal(-1.0f, 0.0f, 0.0f).endVertex();
        }
        for (int k = 0; k < p_78439_5_; ++k) {
            float f7 = k / (float)p_78439_5_;
            float f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
            float f9 = f7 + 1.0f / p_78439_5_;
            worldrenderer.pos((double)f9, 1.0, (double)(0.0f - p_78439_7_)).tex((double)f8, (double)p_78439_2_).normal(1.0f, 0.0f, 0.0f).endVertex();
            worldrenderer.pos((double)f9, 1.0, 0.0).tex((double)f8, (double)p_78439_2_).normal(1.0f, 0.0f, 0.0f).endVertex();
            worldrenderer.pos((double)f9, 0.0, 0.0).tex((double)f8, (double)p_78439_4_).normal(1.0f, 0.0f, 0.0f).endVertex();
            worldrenderer.pos((double)f9, 0.0, (double)(0.0f - p_78439_7_)).tex((double)f8, (double)p_78439_4_).normal(1.0f, 0.0f, 0.0f).endVertex();
        }
        for (int k = 0; k < p_78439_6_; ++k) {
            float f7 = k / (float)p_78439_6_;
            float f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            float f9 = f7 + 1.0f / p_78439_6_;
            worldrenderer.pos(0.0, (double)f9, 0.0).tex((double)p_78439_1_, (double)f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            worldrenderer.pos(1.0, (double)f9, 0.0).tex((double)p_78439_3_, (double)f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            worldrenderer.pos(1.0, (double)f9, (double)(0.0f - p_78439_7_)).tex((double)p_78439_3_, (double)f8).normal(0.0f, 1.0f, 0.0f).endVertex();
            worldrenderer.pos(0.0, (double)f9, (double)(0.0f - p_78439_7_)).tex((double)p_78439_1_, (double)f8).normal(0.0f, 1.0f, 0.0f).endVertex();
        }
        for (int k = 0; k < p_78439_6_; ++k) {
            float f7 = k / (float)p_78439_6_;
            float f8 = p_78439_4_ + (p_78439_2_ - p_78439_4_) * f7 - f6;
            worldrenderer.pos(1.0, (double)f7, 0.0).tex((double)p_78439_3_, (double)f8).normal(0.0f, -1.0f, 0.0f).endVertex();
            worldrenderer.pos(0.0, (double)f7, 0.0).tex((double)p_78439_1_, (double)f8).normal(0.0f, -1.0f, 0.0f).endVertex();
            worldrenderer.pos(0.0, (double)f7, (double)(0.0f - p_78439_7_)).tex((double)p_78439_1_, (double)f8).normal(0.0f, -1.0f, 0.0f).endVertex();
            worldrenderer.pos(1.0, (double)f7, (double)(0.0f - p_78439_7_)).tex((double)p_78439_3_, (double)f8).normal(0.0f, -1.0f, 0.0f).endVertex();
        }
        tessellator.draw();
    }
}
