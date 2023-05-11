package noppes.npcs.client;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Iterator;
import noppes.npcs.client.renderer.MarkRenderer;
import noppes.npcs.controllers.data.MarkData;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import noppes.npcs.schematics.SchematicWrapper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumBlockRenderType;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3i;
import noppes.npcs.blocks.tiles.TileBuilder;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ClientEventHandler
{
    private int displayList;
    
    public ClientEventHandler() {
        this.displayList = -1;
    }
    
    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().player;
        if (TileBuilder.DrawPos == null || TileBuilder.DrawPos.distanceSq((Vec3i)player.getPosition()) > 1000000.0) {
            return;
        }
        TileEntity te = player.world.getTileEntity(TileBuilder.DrawPos);
        if (te == null || !(te instanceof TileBuilder)) {
            return;
        }
        TileBuilder tile = (TileBuilder)te;
        SchematicWrapper schem = tile.getSchematic();
        if (schem == null) {
            return;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(TileBuilder.DrawPos.getX() - TileEntityRendererDispatcher.staticPlayerX, TileBuilder.DrawPos.getY() - TileEntityRendererDispatcher.staticPlayerY + 0.01, TileBuilder.DrawPos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);
        GlStateManager.translate(1.0f, (float)tile.yOffest, 1.0f);
        if (tile.rotation % 2 == 0) {
            this.drawSelectionBox(new BlockPos((int)schem.schema.getWidth(), (int)schem.schema.getHeight(), (int)schem.schema.getLength()));
        }
        else {
            this.drawSelectionBox(new BlockPos((int)schem.schema.getLength(), (int)schem.schema.getHeight(), (int)schem.schema.getWidth()));
        }
        if (TileBuilder.Compiled) {
            GlStateManager.callList(this.displayList);
        }
        else {
            BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            if (this.displayList >= 0) {
                GLAllocation.deleteDisplayLists(this.displayList);
            }
            GL11.glNewList(this.displayList = GLAllocation.generateDisplayLists(1), 4864);
            try {
                for (int i = 0; i < schem.size && i < 25000; ++i) {
                    int posX = i % schem.schema.getWidth();
                    int posZ = (i - posX) / schem.schema.getWidth() % schem.schema.getLength();
                    int posY = ((i - posX) / schem.schema.getWidth() - posZ) / schem.schema.getLength();
                    IBlockState state = schem.schema.getBlockState(posX, posY, posZ);
                    if (state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                        BlockPos pos = schem.rotatePos(posX, posY, posZ, tile.rotation);
                        GlStateManager.pushMatrix();
                        GlStateManager.pushAttrib();
                        GlStateManager.enableRescaleNormal();
                        GlStateManager.translate((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
                        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
                        state = schem.rotationState(state, tile.rotation);
                        try {
                            dispatcher.renderBlockBrightness(state, 1.0f);
                            if (GL11.glGetError() != 0) {
                                break;
                            }
                        }
                        catch (Exception ex) {}
                        finally {
                            GlStateManager.popAttrib();
                            GlStateManager.disableRescaleNormal();
                            GlStateManager.popMatrix();
                        }
                    }
                }
            }
            catch (Exception e) {
                LogWriter.error("Error preview builder block", e);
            }
            finally {
                GL11.glEndList();
                if (GL11.glGetError() == 0) {
                    TileBuilder.Compiled = true;
                }
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translate(-1.0f, 0.0f, -1.0f);
        GlStateManager.popMatrix();
    }
    
    @SubscribeEvent
    public void post(RenderLivingEvent.Post event) {
        MarkData data = MarkData.get(event.getEntity());
        EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().player;
        for (MarkData.Mark m : data.marks) {
            if (m.getType() != 0 && m.availability.isAvailable(player)) {
                MarkRenderer.render(event.getEntity(), event.getX(), event.getY(), event.getZ(), m);
                break;
            }
        }
    }
    
    public void drawSelectionBox(BlockPos pos) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        AxisAlignedBB bb = new AxisAlignedBB(BlockPos.ORIGIN, pos);
        RenderGlobal.drawSelectionBoundingBox(bb, 1.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
    }
}
