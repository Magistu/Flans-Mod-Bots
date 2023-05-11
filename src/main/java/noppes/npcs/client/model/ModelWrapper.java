package noppes.npcs.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBase;

public class ModelWrapper extends ModelBase
{
    public ModelBase wrapped;
    public ResourceLocation texture;
    public ModelBase mainModelOld;
    
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(this.texture);
        this.wrapped.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }
}
