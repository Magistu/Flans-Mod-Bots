package noppes.npcs.client.renderer;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBase;
import noppes.npcs.entity.EntityNPCInterface;

public class RenderNpcDragon<T extends EntityNPCInterface> extends RenderNPCInterface<T>
{
    public RenderNpcDragon(ModelBase model, float f) {
        super(model, f);
    }
    
    @Override
    protected void preRenderCallback(T npc, float f) {
        GlStateManager.translate(0.0f, 0.0f, 0.120000005f * npc.display.getSize());
        super.preRenderCallback(npc, f);
    }
}
