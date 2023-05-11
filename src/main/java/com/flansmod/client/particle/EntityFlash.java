package com.flansmod.client.particle;

import org.lwjgl.opengl.GL11;

import com.flansmod.client.FlansModClient;
import com.flansmod.client.util.WorldRenderer;
import com.flansmod.common.FlansMod;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFlash extends Particle
{
	public static ResourceLocation icon = new ResourceLocation("flansmod", "particle/Boom1.png");
	public EntityFlash(World w, double px, double py, double pz, double mx, double my, double mz)
	{
		super(w, px, py, pz, mx, my, mz);
		this.particleMaxAge = 6;
		this.particleGravity = 1;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		icon = new ResourceLocation("flansmod", "particle/Boom1.png");
	}
	
	public int getFXLayer()
	{
			 return 3;
	}

	public float getEntityBrightness(float f)
	{
			return 1.0F;
	}
	
	public int getBrightnessForRender(float par1)
	{
		return 15728880;
	}
	
    public void renderParticle(float par2, float par3, float par4, float par5, float par6, float par7)
    {
    	GL11.glPushMatrix();
        //func_98187_b() = bindTexture();
    	WorldRenderer worldrenderer = FlansModClient.getWorldRenderer();
    	worldrenderer.startDrawingQuads();
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.001F);
		GL11.glEnable(GL11.GL_BLEND);
		int srcBlend = GL11.glGetInteger(GL11.GL_BLEND_SRC);
		int dstBlend = GL11.glGetInteger(GL11.GL_BLEND_DST);
		GL11.glBlendFunc(1, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false); 
    	FMLClientHandler.instance().getClient().renderEngine.bindTexture(icon);

        float scale = 1F;
        float xPos = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) par2 - interpPosX);
        float yPos = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) par2 - interpPosY);
        float zPos = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) par2 - interpPosZ);
        float colorIntensity = 1.0F;
        worldrenderer.tessellator.getBuffer().color(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity, 1.0F);
        
        worldrenderer.addVertexWithUV((double) (xPos - par3 * scale - par6 * scale), (double) (yPos - par4 * scale), (double) (zPos - par5 * scale - par7 * scale), 0D, 1D);
        worldrenderer.addVertexWithUV((double) (xPos - par3 * scale + par6 * scale), (double) (yPos + par4 * scale), (double) (zPos - par5 * scale + par7 * scale), 1D, 1D);
        worldrenderer.addVertexWithUV((double) (xPos + par3 * scale + par6 * scale), (double) (yPos + par4 * scale), (double) (zPos + par5 * scale + par7 * scale), 1D, 0D);
        worldrenderer.addVertexWithUV((double) (xPos + par3 * scale - par6 * scale), (double) (yPos - par4 * scale), (double) (zPos + par5 * scale - par7 * scale), 0D, 0D);
        worldrenderer.draw();
		GL11.glBlendFunc(srcBlend, dstBlend);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true); 
		GL11.glPopMatrix();
    }

	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if(this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}

		if(this.onGround)
		{
			setExpired();
		}
		
		if(this.particleAge == 1)icon = new ResourceLocation("flansmod", "particle/Boom2.png");
		if(this.particleAge == 2)icon = new ResourceLocation("flansmod", "particle/Boom3.png");
		if(this.particleAge == 3)icon = new ResourceLocation("flansmod", "particle/Boom4.png");
		if(this.particleAge == 4)icon = new ResourceLocation("flansmod", "particle/Boom5.png");
		if(this.particleAge == 5)icon = new ResourceLocation("flansmod", "particle/Boom6.png");
		
    	FMLClientHandler.instance().getClient().renderEngine.bindTexture(icon);

	}
}
