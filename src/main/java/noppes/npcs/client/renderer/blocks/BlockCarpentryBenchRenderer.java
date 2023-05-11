package noppes.npcs.client.renderer.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.blocks.ModelCarpentryBench;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class BlockCarpentryBenchRenderer extends TileEntitySpecialRenderer
{
    private ModelCarpentryBench model;
    private static ResourceLocation TEXTURE;
    
    public BlockCarpentryBenchRenderer() {
        this.model = new ModelCarpentryBench();
    }
    
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int rotation = 0;
        if (te != null && te.getPos() != BlockPos.ORIGIN) {
            rotation = te.getBlockMetadata() % 4;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.translate((float)x + 0.5f, (float)y + 1.4f, (float)z + 0.5f);
        GlStateManager.scale(0.95f, 0.95f, 0.95f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate((float)(90 * rotation), 0.0f, 1.0f, 0.0f);
        this.bindTexture(BlockCarpentryBenchRenderer.TEXTURE);
        this.model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
    
    static {
        TEXTURE = new ResourceLocation("customnpcs", "textures/models/carpentrybench.png");
    }
}
