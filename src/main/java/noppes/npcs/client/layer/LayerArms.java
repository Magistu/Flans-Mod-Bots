package noppes.npcs.client.layer;

import noppes.npcs.ModelPartData;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.constants.EnumParts;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.client.model.Model2DRenderer;

public class LayerArms extends LayerInterface
{
    private Model2DRenderer lClaw;
    private Model2DRenderer rClaw;
    
    public LayerArms(RenderNPCInterface render) {
        super(render);
        this.createParts();
    }
    
    private void createParts() {
        (this.lClaw = new Model2DRenderer((ModelBase)this.model, 0.0f, 16.0f, 4, 4)).setRotationPoint(3.0f, 14.0f, -2.0f);
        this.lClaw.rotateAngleY = -1.5707964f;
        this.lClaw.setScale(0.25f);
        (this.rClaw = new Model2DRenderer((ModelBase)this.model, 0.0f, 16.0f, 4, 4)).setRotationPoint(-2.0f, 14.0f, -2.0f);
        this.rClaw.rotateAngleY = -1.5707964f;
        this.rClaw.setScale(0.25f);
    }
    
    @Override
    public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
        ModelPartData data = this.playerdata.getPartData(EnumParts.CLAWS);
        if (data == null) {
            return;
        }
        this.preRender(data);
        if (data.pattern == 0 || data.pattern == 1) {
            GlStateManager.pushMatrix();
            this.model.bipedLeftArm.postRender(0.0625f);
            this.lClaw.render(par7);
            GlStateManager.popMatrix();
        }
        if (data.pattern == 0 || data.pattern == 2) {
            GlStateManager.pushMatrix();
            this.model.bipedRightArm.postRender(0.0625f);
            this.rClaw.render(par7);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void rotate(float par2, float par3, float par4, float par5, float par6, float par7) {
    }
}
