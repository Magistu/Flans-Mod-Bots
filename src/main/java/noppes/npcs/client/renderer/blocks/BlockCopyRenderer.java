package noppes.npcs.client.renderer.blocks;

import noppes.npcs.CustomItems;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.blocks.tiles.TileCopy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.schematics.Schematic;
import net.minecraft.item.ItemStack;

public class BlockCopyRenderer extends BlockRendererInterface
{
    private static ItemStack item;
    public static Schematic schematic;
    public static BlockPos pos;
    
    public void render(TileEntity var1, double x, double y, double z, float var8, int blockDamage, float alpha) {
        TileCopy tile = (TileCopy)var1;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.translate(x, y, z);
        this.drawSelectionBox(new BlockPos((int)tile.width, (int)tile.height, (int)tile.length));
        GlStateManager.translate(0.5f, 0.5f, 0.5f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        Minecraft.getMinecraft().getRenderItem().renderItem(BlockCopyRenderer.item, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();
    }
    
    public void drawSelectionBox(BlockPos pos) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        AxisAlignedBB bb = new AxisAlignedBB(BlockPos.ORIGIN, pos);
        GlStateManager.translate(0.001f, 0.001f, 0.001f);
        RenderGlobal.drawSelectionBoundingBox(bb, 1.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
    }
    
    static {
        item = new ItemStack(CustomItems.copy);
        BlockCopyRenderer.schematic = null;
        BlockCopyRenderer.pos = null;
    }
}
