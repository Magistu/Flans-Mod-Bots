package noppes.npcs.client.model.animation;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class AniHug
{
    public static void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity, ModelBiped base) {
        float f6 = MathHelper.sin(base.swingProgress * 3.141593f);
        float f7 = MathHelper.sin((1.0f - (1.0f - base.swingProgress) * (1.0f - base.swingProgress)) * 3.141593f);
        base.bipedRightArm.rotateAngleZ = 0.0f;
        base.bipedLeftArm.rotateAngleZ = 0.0f;
        base.bipedRightArm.rotateAngleY = -(0.1f - f6 * 0.6f);
        base.bipedLeftArm.rotateAngleY = 0.1f;
        base.bipedRightArm.rotateAngleX = -1.570796f;
        base.bipedLeftArm.rotateAngleX = -1.570796f;
        ModelRenderer bipedRightArm = base.bipedRightArm;
        bipedRightArm.rotateAngleX -= f6 * 1.2f - f7 * 0.4f;
        ModelRenderer bipedRightArm2 = base.bipedRightArm;
        bipedRightArm2.rotateAngleZ += MathHelper.cos(par3 * 0.09f) * 0.05f + 0.05f;
        ModelRenderer bipedLeftArm = base.bipedLeftArm;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09f) * 0.05f + 0.05f;
        ModelRenderer bipedRightArm3 = base.bipedRightArm;
        bipedRightArm3.rotateAngleX += MathHelper.sin(par3 * 0.067f) * 0.05f;
        ModelRenderer bipedLeftArm2 = base.bipedLeftArm;
        bipedLeftArm2.rotateAngleX -= MathHelper.sin(par3 * 0.067f) * 0.05f;
    }
}
