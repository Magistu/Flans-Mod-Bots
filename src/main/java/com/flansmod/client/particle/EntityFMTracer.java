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

public class EntityFMTracer extends Particle
{
	public static ResourceLocation icon = new ResourceLocation("flansmod", "particle/FMTracer.png");
	public EntityFMTracer(World w, double px, double py, double pz, double mx, double my, double mz)
	{
		super(w, px, py, pz, mx, my, mz);
		this.particleMaxAge = 6;
		this.particleAge = 0;
		this.particleGravity = 1;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		FlansMod.proxy.spawnParticle("flansmod.fmsmoke",
				this.posX,
				this.posY,
				this.posZ,
				0,0,0);

		icon = new ResourceLocation("flansmod", "particle/FMTracer.png");
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
        //func_98187_b() = bindTexture();
    	GL11.glPushMatrix();
    	WorldRenderer worldrenderer = FlansModClient.getWorldRenderer();
    	worldrenderer.startDrawingQuads();
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.001F);
		GL11.glEnable(GL11.GL_BLEND);
		int srcBlend = GL11.glGetInteger(GL11.GL_BLEND_SRC);
		int dstBlend = GL11.glGetInteger(GL11.GL_BLEND_DST);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false); 
    	FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("flansmod", "particle/FMTracer.png"));

        float scale = 0.6F - ((this.particleAge)*0.1F);
        float xPos = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) par2 - interpPosX);
        float yPos = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) par2 - interpPosY);
        float zPos = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) par2 - interpPosZ);
        float colorIntensity = 1F;
        //par1Tessellator.setColorOpaque_F(this.particleRed * colorIntensity, this.particleGreen * colorIntensity, this.particleBlue * colorIntensity);//, 1.0F);
        worldrenderer.tessellator.getBuffer().color(this.particleRed * colorIntensity, this.particleGreen * (colorIntensity - this.particleAge*0.2F), this.particleBlue * (colorIntensity - this.particleAge*0.2F), (1F - this.particleAge*0.1F));
        worldrenderer.addVertexWithUV((double) (xPos - par3 * scale - par6 * scale), (double) (yPos - par4 * scale), (double) (zPos - par5 * scale - par7 * scale), 0D, 1D);
        worldrenderer.addVertexWithUV((double) (xPos - par3 * scale + par6 * scale), (double) (yPos + par4 * scale), (double) (zPos - par5 * scale + par7 * scale), 1D, 1D);
        worldrenderer.addVertexWithUV((double) (xPos + par3 * scale + par6 * scale), (double) (yPos + par4 * scale), (double) (zPos + par5 * scale + par7 * scale), 1D, 0D);
        worldrenderer.addVertexWithUV((double) (xPos + par3 * scale - par6 * scale), (double) (yPos - par4 * scale), (double) (zPos + par5 * scale - par7 * scale), 0D, 0D);
        worldrenderer.draw();
        //GL11.glBlendFunc(srcBlend, dstBlend);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true); 
        GL11.glPopMatrix();

    }

	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		//this.renderDistanceWeight = 2000.0D;
		if(this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}
		
		this.move(this.motionX, this.motionY, this.motionZ);
		if(this.onGround)
		{
			setExpired();
		}
	}
}
