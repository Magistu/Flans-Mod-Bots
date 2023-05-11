package noppes.npcs.client.renderer;

import noppes.npcs.client.model.Model2DRenderer;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import noppes.npcs.controllers.data.MarkData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class MarkRenderer
{
    public static ResourceLocation markExclamation;
    public static ResourceLocation markQuestion;
    public static ResourceLocation markPointer;
    public static ResourceLocation markCross;
    public static ResourceLocation markSkull;
    public static ResourceLocation markStar;
    public static int displayList;
    
    public static void render(EntityLivingBase entity, double x, double y, double z, MarkData.Mark mark) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        int color = mark.color;
        float red = (color >> 16 & 0xFF) / 255.0f;
        float blue = (color >> 8 & 0xFF) / 255.0f;
        float green = (color & 0xFF) / 255.0f;
        GlStateManager.color(red, blue, green);
        GlStateManager.translate(x, y + entity.height + 0.6, z);
        GlStateManager.rotate(-entity.rotationYawHead, 0.0f, 1.0f, 0.0f);
        if (mark.type == 2) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(MarkRenderer.markExclamation);
        }
        else if (mark.type == 1) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(MarkRenderer.markQuestion);
        }
        else if (mark.type == 3) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(MarkRenderer.markPointer);
        }
        else if (mark.type == 5) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(MarkRenderer.markCross);
        }
        else if (mark.type == 4) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(MarkRenderer.markSkull);
        }
        else if (mark.type == 6) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(MarkRenderer.markStar);
        }
        if (MarkRenderer.displayList >= 0) {
            GlStateManager.callList(MarkRenderer.displayList);
        }
        else {
            GL11.glNewList(MarkRenderer.displayList = GLAllocation.generateDisplayLists(1), 4864);
            GlStateManager.translate(-0.5, 0.0, 0.0);
            Model2DRenderer.renderItemIn2D(Tessellator.getInstance().getBuffer(), 0.0f, 0.0f, 1.0f, 1.0f, 32, 32, 0.0625f);
            GL11.glEndList();
        }
        GlStateManager.popMatrix();
    }
    
    static {
        MarkRenderer.markExclamation = new ResourceLocation("customnpcs", "textures/marks/exclamation.png");
        MarkRenderer.markQuestion = new ResourceLocation("customnpcs", "textures/marks/question.png");
        MarkRenderer.markPointer = new ResourceLocation("customnpcs", "textures/marks/pointer.png");
        MarkRenderer.markCross = new ResourceLocation("customnpcs", "textures/marks/cross.png");
        MarkRenderer.markSkull = new ResourceLocation("customnpcs", "textures/marks/skull.png");
        MarkRenderer.markStar = new ResourceLocation("customnpcs", "textures/marks/star.png");
        MarkRenderer.displayList = -1;
    }
}
