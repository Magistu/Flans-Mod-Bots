package noppes.npcs.client.renderer;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.RenderLiving;
import noppes.npcs.client.layer.LayerSlimeNpc;
import net.minecraft.client.model.ModelBase;

public class RenderNpcSlime extends RenderNPCInterface
{
    private ModelBase scaleAmount;
    
    public RenderNpcSlime(ModelBase par1ModelBase, ModelBase par2ModelBase, float par3) {
        super(par1ModelBase, par3);
        this.scaleAmount = par2ModelBase;
        this.addLayer((LayerRenderer)new LayerSlimeNpc(this));
    }
}
