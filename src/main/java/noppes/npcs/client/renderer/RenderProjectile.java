package noppes.npcs.client.renderer;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.entity.EntityProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;

@SideOnly(Side.CLIENT)
public class RenderProjectile extends Render
{
    public boolean renderWithColor;
    private static ResourceLocation field_110780_a;
    private static ResourceLocation RES_ITEM_GLINT;
    
    public RenderProjectile() {
        super(Minecraft.getMinecraft().getRenderManager());
        this.renderWithColor = true;
    }
    
    public void doRenderProjectile(EntityProjectile projectile, double x, double y, double z, float entityYaw, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        float scale = projectile.getSize() / 10.0f;
        ItemStack item = projectile.getItemDisplay();
        GlStateManager.scale(scale, scale, scale);
        if (projectile.isArrow()) {
            this.bindEntityTexture((Entity)projectile);
            GlStateManager.rotate(projectile.prevRotationYaw + (projectile.rotationYaw - projectile.prevRotationYaw) * partialTicks - 90.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(projectile.prevRotationPitch + (projectile.rotationPitch - projectile.prevRotationPitch) * partialTicks, 0.0f, 0.0f, 1.0f);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder BufferBuilder = tessellator.getBuffer();
            int i = 0;
            float f = 0.0f;
            float f2 = 0.5f;
            float f3 = (0 + i * 10) / 32.0f;
            float f4 = (5 + i * 10) / 32.0f;
            float f5 = 0.0f;
            float f6 = 0.15625f;
            float f7 = (5 + i * 10) / 32.0f;
            float f8 = (10 + i * 10) / 32.0f;
            float f9 = 0.05625f;
            GlStateManager.enableRescaleNormal();
            float f10 = projectile.arrowShake - partialTicks;
            if (f10 > 0.0f) {
                float f11 = -MathHelper.sin(f10 * 3.0f) * f10;
                GlStateManager.rotate(f11, 0.0f, 0.0f, 1.0f);
            }
            GlStateManager.rotate(45.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(f9, f9, f9);
            GlStateManager.translate(-4.0f, 0.0f, 0.0f);
            if (this.renderOutlines) {
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode(this.getTeamColor((Entity)projectile));
            }
            GlStateManager.glNormal3f(f9, 0.0f, 0.0f);
            BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            BufferBuilder.pos(-7.0, -2.0, -2.0).tex((double)f5, (double)f7).endVertex();
            BufferBuilder.pos(-7.0, -2.0, 2.0).tex((double)f6, (double)f7).endVertex();
            BufferBuilder.pos(-7.0, 2.0, 2.0).tex((double)f6, (double)f8).endVertex();
            BufferBuilder.pos(-7.0, 2.0, -2.0).tex((double)f5, (double)f8).endVertex();
            tessellator.draw();
            GlStateManager.glNormal3f(-f9, 0.0f, 0.0f);
            BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            BufferBuilder.pos(-7.0, 2.0, -2.0).tex((double)f5, (double)f7).endVertex();
            BufferBuilder.pos(-7.0, 2.0, 2.0).tex((double)f6, (double)f7).endVertex();
            BufferBuilder.pos(-7.0, -2.0, 2.0).tex((double)f6, (double)f8).endVertex();
            BufferBuilder.pos(-7.0, -2.0, -2.0).tex((double)f5, (double)f8).endVertex();
            tessellator.draw();
            for (int j = 0; j < 4; ++j) {
                GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.glNormal3f(0.0f, 0.0f, f9);
                BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                BufferBuilder.pos(-8.0, -2.0, 0.0).tex((double)f, (double)f3).endVertex();
                BufferBuilder.pos(8.0, -2.0, 0.0).tex((double)f2, (double)f3).endVertex();
                BufferBuilder.pos(8.0, 2.0, 0.0).tex((double)f2, (double)f4).endVertex();
                BufferBuilder.pos(-8.0, 2.0, 0.0).tex((double)f, (double)f4).endVertex();
                tessellator.draw();
            }
            if (this.renderOutlines) {
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
            }
        }
        else if (projectile.is3D()) {
            GlStateManager.rotate(projectile.prevRotationYaw + (projectile.rotationYaw - projectile.prevRotationYaw) * partialTicks - 180.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(projectile.prevRotationPitch + (projectile.rotationPitch - projectile.prevRotationPitch) * partialTicks, 1.0f, 0.0f, 0.0f);
            GlStateManager.translate(0.0, -0.125, 0.25);
            if (item.getItem() instanceof ItemBlock && Block.getBlockFromItem(item.getItem()).getDefaultState().getRenderType() == EnumBlockRenderType.ENTITYBLOCK_ANIMATED) {
                GlStateManager.translate(0.0f, 0.1875f, -0.3125f);
                GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
                float f12 = 0.375f;
                GlStateManager.scale(-f12, -f12, f12);
            }
            try {
                mc.getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
            }
            catch (Exception e) {
                mc.getRenderItem().renderItem(new ItemStack(Blocks.DIRT), ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
            }
        }
        else {
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            try {
                mc.getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.NONE);
            }
            catch (Exception e) {
                mc.getRenderItem().renderItem(new ItemStack(Blocks.DIRT), ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.disableRescaleNormal();
        }
        if (projectile.is3D() && projectile.glows()) {
            GlStateManager.disableLighting();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }
    
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.doRenderProjectile((EntityProjectile)par1Entity, par2, par4, par6, par8, par9);
    }
    
    protected ResourceLocation func_110779_a(EntityProjectile projectile) {
        return projectile.isArrow() ? RenderProjectile.field_110780_a : TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
    
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.func_110779_a((EntityProjectile)par1Entity);
    }
    
    static {
        field_110780_a = new ResourceLocation("textures/entity/arrow.png");
        RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    }
}
