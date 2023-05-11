package com.flansmod.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.roles.JobPuppet;

import com.flansmod.client.tmt.ModelRendererTurbo;
import com.flansmod.common.teams.ArmourType;

public class ModelCustomArmour extends ModelBiped
{
	public ArmourType type;
	
	public ModelRendererTurbo[] headModel = new ModelRendererTurbo[0];
	public ModelRendererTurbo[] bodyModel = new ModelRendererTurbo[0];
	public ModelRendererTurbo[] leftArmModel = new ModelRendererTurbo[0];
	public ModelRendererTurbo[] rightArmModel = new ModelRendererTurbo[0];
	public ModelRendererTurbo[] leftLegModel = new ModelRendererTurbo[0];
	public ModelRendererTurbo[] rightLegModel = new ModelRendererTurbo[0];
	public ModelRendererTurbo[] skirtFrontModel = new ModelRendererTurbo[0]; //Acts like a leg piece, but its pitch is set to the maximum of the two legs
	public ModelRendererTurbo[] skirtRearModel = new ModelRendererTurbo[0]; //Acts like a leg piece, but its pitch is set to the minimum of the two legs
	
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		GlStateManager.pushMatrix();
		GlStateManager.scale(type.modelScale, type.modelScale, type.modelScale);
		isSneak = entity.isSneaking();
		/*ItemStack itemstack = ((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		rightArmPose = itemstack.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
		
		if(!itemstack.isEmpty())
		{
			EnumAction enumaction = itemstack.getItemUseAction();
			if(enumaction == EnumAction.BLOCK)
			{
				rightArmPose = ArmPose.BLOCK;
			}
			else if(enumaction == EnumAction.BOW)
			{
				rightArmPose = ArmPose.BOW_AND_ARROW;
			}
		}*/

		if (entity.getClass().getName() == "noppes.npcs.entity.EntityCustomNpc") 
		{
		    try 
		    {
				int currentAnimation = (int) entity.getClass().getSuperclass().getSuperclass().getDeclaredField("currentAnimation").get(entity);
				 
				if (!isRiding) 
				{
					isRiding = (currentAnimation == 1);
				}
				
				if (isSneak && (currentAnimation == 7 || ((EntityLivingBase) entity).isPlayerSleeping()))
					isSneak = false;
				
				if (currentAnimation == 6)
				      rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW; 
				
				isSneak = entity.isSneaking();
				    
				bipedBody.rotationPointX = bipedBody.rotationPointY = bipedBody.rotationPointZ = 0.0F;
				bipedBody.rotateAngleX = bipedBody.rotateAngleY = bipedBody.rotateAngleZ = 0.0F;
				    
				bipedHead.rotateAngleX = 0.0F;
				bipedHead.rotateAngleZ = 0.0F;
				    
				bipedHead.rotationPointX = 0.0F;
				bipedHead.rotationPointY = 0.0F;
				bipedHead.rotationPointZ = 0.0F;
				    
				bipedLeftLeg.rotateAngleX = 0.0F;
				bipedLeftLeg.rotateAngleY = 0.0F;
				bipedLeftLeg.rotateAngleZ = 0.0F;
				bipedRightLeg.rotateAngleX = 0.0F;
				bipedRightLeg.rotateAngleY = 0.0F;
				bipedRightLeg.rotateAngleZ = 0.0F;
				bipedLeftArm.rotationPointX = 0.0F;
				bipedLeftArm.rotationPointY = 2.0F;
				bipedLeftArm.rotationPointZ = 0.0F;
				bipedRightArm.rotationPointX = 0.0F;
				bipedRightArm.rotationPointY = 2.0F;
				bipedRightArm.rotationPointZ = 0.0F;
				    
				setRotationAngles(f, f1, f2, f3, f4, f5, entity);
				    
				if (((EntityLivingBase) entity).isPlayerSleeping()) 
				{
					if (bipedHead.rotateAngleX < 0.0F) 
					{
						bipedHead.rotateAngleX = 0.0F;
				    	bipedHeadwear.rotateAngleX = 0.0F;
					}
				} 
				else if (currentAnimation == 9) 
				{
					bipedHead.rotateAngleX = 0.7F;
				} 
				else if (currentAnimation == 3) 
				{
					setRotationAnglesHug(f, f1, f2, f3, f4, f5, entity);
				} 
				else if (currentAnimation == 7) 
				{
					setRotationAnglesCrawling(f, f1, f2, f3, f4, f5, entity);
			    } 
				else if (currentAnimation == 10) 
			    {
			    	setRotationAnglesWaving(f, f1, f2, f3, f4, f5, entity);
			    }
			    else if (currentAnimation == 5) 
			    {
			    	setRotationAnglesDancing(f, f1, f2, f3, f4, f5, entity);
			    }
			    else if (currentAnimation == 11) 
			    {
			    	setRotationAnglesBow(f, f1, f2, f3, f4, f5, entity);
			    }
			    else if (currentAnimation == 13) 
			    {
			    	setRotationAnglesYes(f, f1, f2, f3, f4, f5, entity);
			    }
			    else if (currentAnimation == 12) 
			    {
			    	setRotationAnglesNo(f, f1, f2, f3, f4, f5, entity);
			    }
			    else if (currentAnimation == 8) 
			    {
			    	setRotationAnglesPoint(f, f1, f2, f3, f4, f5, entity);
			    }
				
				if (((EntityCustomNpc)entity).advanced.job == 9) {
				      JobPuppet job = (JobPuppet) ((EntityCustomNpc)entity).jobInterface;
				      if (job.isActive()) {
				        float pi = 3.1415927F;
				        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
				        if (!job.head.disabled) {
				          bipedHead.rotateAngleX = job.getRotationX(job.head, job.head2, partialTicks) * pi;
				          bipedHead.rotateAngleY = job.getRotationY(job.head, job.head2, partialTicks) * pi;
				          bipedHead.rotateAngleZ = job.getRotationZ(job.head, job.head2, partialTicks) * pi;
				        } 
				        
				        if (!job.body.disabled) {
				          bipedBody.rotateAngleX = job.getRotationX(job.body, job.body2, partialTicks) * pi;
				          bipedBody.rotateAngleY = job.getRotationY(job.body, job.body2, partialTicks) * pi;
				          bipedBody.rotateAngleZ = job.getRotationZ(job.body, job.body2, partialTicks) * pi;
				        } 
				        
				        if (!job.larm.disabled) {
				          bipedLeftArm.rotateAngleX = job.getRotationX(job.larm, job.larm2, partialTicks) * pi;
				          bipedLeftArm.rotateAngleY = job.getRotationY(job.larm, job.larm2, partialTicks) * pi;
				          bipedLeftArm.rotateAngleZ = job.getRotationZ(job.larm, job.larm2, partialTicks) * pi;
				          
				          if (((EntityCustomNpc)entity).display.getHasLivingAnimation()) {
				            bipedLeftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
				            bipedLeftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
				          } 
				        } 
				        
				        if (!job.rarm.disabled) {
				          bipedRightArm.rotateAngleX = job.getRotationX(job.rarm, job.rarm2, partialTicks) * pi;
				          bipedRightArm.rotateAngleY = job.getRotationY(job.rarm, job.rarm2, partialTicks) * pi;
				          bipedRightArm.rotateAngleZ = job.getRotationZ(job.rarm, job.rarm2, partialTicks) * pi;
				          
				          if (((EntityCustomNpc)entity).display.getHasLivingAnimation()) {
				            bipedRightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
				            bipedRightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
				          } 
				        } 
				        
				        if (!job.rleg.disabled) {
				          bipedRightLeg.rotateAngleX = job.getRotationX(job.rleg, job.rleg2, partialTicks) * pi;
				          bipedRightLeg.rotateAngleY = job.getRotationY(job.rleg, job.rleg2, partialTicks) * pi;
				          bipedRightLeg.rotateAngleZ = job.getRotationZ(job.rleg, job.rleg2, partialTicks) * pi;
				        } 
				        
				        if (!job.lleg.disabled) {
				          bipedLeftLeg.rotateAngleX = job.getRotationX(job.lleg, job.lleg2, partialTicks) * pi;
				          bipedLeftLeg.rotateAngleY = job.getRotationY(job.lleg, job.lleg2, partialTicks) * pi;
				          bipedLeftLeg.rotateAngleZ = job.getRotationZ(job.lleg, job.lleg2, partialTicks) * pi;
				        } 
				      } 
				    }
			} 
		    catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) 
		    {
				e.printStackTrace();
			}
		}
		else
		{
			setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		}
		
		if(isSneak)
		{
			GlStateManager.translate(0.0F, 0.2F, 0.0F);
		}
		
		render(headModel, bipedHead, f5, type.modelScale);
		render(bodyModel, bipedBody, f5, type.modelScale);
		render(leftArmModel, bipedLeftArm, f5, type.modelScale);
		render(rightArmModel, bipedRightArm, f5, type.modelScale);
		render(leftLegModel, bipedLeftLeg, f5, type.modelScale);
		render(rightLegModel, bipedRightLeg, f5, type.modelScale);
		
		//Skirt front
		{
			for(ModelRendererTurbo mod : skirtFrontModel)
			{
				mod.rotationPointX = (bipedLeftLeg.rotationPointX + bipedRightLeg.rotationPointX) / 2F / type.modelScale;
				mod.rotationPointY = (bipedLeftLeg.rotationPointY + bipedRightLeg.rotationPointY) / 2F / type.modelScale;
				mod.rotationPointZ = (bipedLeftLeg.rotationPointZ + bipedRightLeg.rotationPointZ) / 2F / type.modelScale;
				mod.rotateAngleX = Math.min(bipedLeftLeg.rotateAngleX, bipedRightLeg.rotateAngleX);
				mod.rotateAngleY = bipedLeftLeg.rotateAngleY;
				mod.rotateAngleZ = bipedLeftLeg.rotateAngleZ;
				mod.render(f5);
			}
		}
		
		//Skirt back
		{
			for(ModelRendererTurbo mod : skirtRearModel)
			{
				mod.rotationPointX = (bipedLeftLeg.rotationPointX + bipedRightLeg.rotationPointX) / 2F / type.modelScale;
				mod.rotationPointY = (bipedLeftLeg.rotationPointY + bipedRightLeg.rotationPointY) / 2F / type.modelScale;
				mod.rotationPointZ = (bipedLeftLeg.rotationPointZ + bipedRightLeg.rotationPointZ) / 2F / type.modelScale;
				mod.rotateAngleX = Math.max(bipedLeftLeg.rotateAngleX, bipedRightLeg.rotateAngleX);
				mod.rotateAngleY = bipedLeftLeg.rotateAngleY;
				mod.rotateAngleZ = bipedLeftLeg.rotateAngleZ;
				mod.render(f5);
			}
		}
		GlStateManager.popMatrix();
	}
	
	public void render(ModelRendererTurbo[] models, ModelRenderer bodyPart, float f5, float scale)
	{
		setBodyPart(models, bodyPart, scale);
		for(ModelRendererTurbo mod : models)
		{
			mod.rotateAngleX = bodyPart.rotateAngleX;
			mod.rotateAngleY = bodyPart.rotateAngleY;
			mod.rotateAngleZ = bodyPart.rotateAngleZ;
			mod.render(f5);
		}
	}
	
	public void setBodyPart(ModelRendererTurbo[] models, ModelRenderer bodyPart, float scale)
	{
		for(ModelRendererTurbo mod : models)
		{
			mod.rotationPointX = bodyPart.rotationPointX / scale;
			mod.rotationPointY = bodyPart.rotationPointY / scale;
			mod.rotationPointZ = bodyPart.rotationPointZ / scale;
		}
	}
	
	public void setRotationAnglesBow(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) 
	{
		try 
		{
			int animationStart = (int) entity.getClass().getSuperclass().getSuperclass().getDeclaredField("animationStart").get(entity);
			
			float ticks = (entity.ticksExisted - animationStart) / 10.0F;
		    if (ticks > 1.0F)
		      ticks = 1.0F; 
		    float ticks2 = (entity.ticksExisted + 1 - animationStart) / 10.0F;
		    if (ticks2 > 1.0F)
		      ticks2 = 1.0F; 
		    ticks += (ticks2 - ticks) * Minecraft.getMinecraft().getRenderPartialTicks();
		    
		    bipedBody.rotateAngleX = ticks;
		    bipedHead.rotateAngleX = ticks;
		    bipedLeftArm.rotateAngleX = ticks;
		    bipedRightArm.rotateAngleX = ticks;
		    
		    bipedBody.rotationPointZ = -ticks * 10.0F;
		    bipedBody.rotationPointY = ticks * 6.0F;
		    
		    bipedHead.rotationPointZ = -ticks * 10.0F;
		    bipedHead.rotationPointY = ticks * 6.0F;
		    
		    bipedLeftArm.rotationPointZ = -ticks * 10.0F;
		    bipedLeftArm.rotationPointY += ticks * 6.0F;
		    
		    bipedRightArm.rotationPointZ = -ticks * 10.0F;
		    bipedRightArm.rotationPointY += ticks * 6.0F;
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void setRotationAnglesDancing(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) 
	{
	    float dancing = entity.ticksExisted / 4.0F;
	    float dancing2 = (entity.ticksExisted + 1) / 4.0F;
	    dancing += (dancing2 - dancing) * Minecraft.getMinecraft().getRenderPartialTicks();
	    
	    float x = (float)Math.sin(dancing);
	    float y = (float)Math.abs(Math.cos(dancing));
	    
	    bipedHead.rotationPointX = x * 0.75F;
	    bipedHead.rotationPointY = y * 1.25F - 0.02F;
	    bipedHead.rotationPointZ = -y * 0.75F;
	    
	    bipedLeftArm.rotationPointX += x * 0.25F;
	    bipedLeftArm.rotationPointY += y * 1.25F;
	    
	    bipedRightArm.rotationPointX += x * 0.25F;
	    bipedRightArm.rotationPointY += y * 1.25F;
	    
	    bipedBody.rotationPointX = x * 0.25F;
	}
	
	public void setRotationAnglesCrawling(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) 
	{
	    bipedHead.rotateAngleZ = -par4 / 57.295776F;
	    bipedHead.rotateAngleY = 0.0F;
	    bipedHead.rotateAngleX = -0.95993114F;
	    
	    bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX;
	    bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY;
	    bipedHeadwear.rotateAngleZ = bipedHead.rotateAngleZ;
	    
	    if (par2 > 0.25D)
	      par2 = 0.25F; 
	    float movement = MathHelper.cos(par1 * 0.8F + 3.1415927F) * par2;
	    
	    bipedLeftArm.rotateAngleX = 3.1415927F - movement * 0.25F;
	    bipedLeftArm.rotateAngleY = movement * -0.46F;
	    bipedLeftArm.rotateAngleZ = movement * -0.2F;
	    bipedLeftArm.rotationPointY = 2.0F - movement * 9.0F;
	    
	    bipedRightArm.rotateAngleX = 3.1415927F + movement * 0.25F;
	    bipedRightArm.rotateAngleY = movement * -0.4F;
	    bipedRightArm.rotateAngleZ = movement * -0.2F;
	    bipedRightArm.rotationPointY = 2.0F + movement * 9.0F;
	    
	    bipedBody.rotateAngleY = movement * 0.1F;
	    bipedBody.rotateAngleX = 0.0F;
	    bipedBody.rotateAngleZ = movement * 0.1F;
	    
	    bipedLeftLeg.rotateAngleX = movement * 0.1F;
	    bipedLeftLeg.rotateAngleY = movement * 0.1F;
	    bipedLeftLeg.rotateAngleZ = -0.122173056F - movement * 0.25F;
	    bipedLeftLeg.rotationPointY = 10.4F + movement * 9.0F;
	    bipedLeftLeg.rotationPointZ = movement * 0.6F;
	    
	    bipedRightLeg.rotateAngleX = movement * -0.1F;
	    bipedRightLeg.rotateAngleY = movement * 0.1F;
	    bipedRightLeg.rotateAngleZ = 0.122173056F - movement * 0.25F;
	    bipedRightLeg.rotationPointY = 10.4F - movement * 9.0F;
	    bipedRightLeg.rotationPointZ = movement * -0.6F;
	}
	
	public void setRotationAnglesHug(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) 
	{
	    float f6 = MathHelper.sin(swingProgress * 3.141593F);
	    float f7 = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * 3.141593F);
	    bipedRightArm.rotateAngleZ = 0.0F;
	    bipedLeftArm.rotateAngleZ = 0.0F;
	    bipedRightArm.rotateAngleY = -(0.1F - f6 * 0.6F);
	    bipedLeftArm.rotateAngleY = 0.1F;
	    bipedRightArm.rotateAngleX = -1.570796F;
	    bipedLeftArm.rotateAngleX = -1.570796F;
	    bipedRightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
	    
	    bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
	    bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
	    bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
	    bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
	}
	
	public void setRotationAnglesNo(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) 
	{
		try 
		{
			int animationStart = (int) entity.getClass().getSuperclass().getSuperclass().getDeclaredField("animationStart").get(entity);
			
			float ticks = (entity.ticksExisted - animationStart) / 8.0F;
		    float ticks2 = (entity.ticksExisted + 1 - animationStart) / 8.0F;
		    ticks += (ticks2 - ticks) * Minecraft.getMinecraft().getRenderPartialTicks();
		    
		    ticks %= 2.0F;
		    float ani = ticks - 0.5F;
		    if (ticks > 1.0F)
		      ani = 1.5F - ticks; 
		    bipedHead.rotateAngleY = ani;
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void setRotationAnglesYes(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) 
	{
		try 
		{
			int animationStart = (int) entity.getClass().getSuperclass().getSuperclass().getDeclaredField("animationStart").get(entity);
	
		    float ticks = (entity.ticksExisted - animationStart) / 8.0F;
		    float ticks2 = (entity.ticksExisted + 1 - animationStart) / 8.0F;
		    ticks += (ticks2 - ticks) * Minecraft.getMinecraft().getRenderPartialTicks();
		    
		    ticks %= 2.0F;
		    float ani = ticks - 0.5F;
		    if (ticks > 1.0F)
		      ani = 1.5F - ticks; 
		    bipedHead.rotateAngleX = ani;
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void setRotationAnglesWaving(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) 
	{
	    float f = MathHelper.sin(entity.ticksExisted * 0.27F);
	    float f2 = MathHelper.sin((entity.ticksExisted + 1) * 0.27F);
	    f += (f2 - f) * Minecraft.getMinecraft().getRenderPartialTicks();
	    
	    bipedRightArm.rotateAngleX = -0.1F;
	    bipedRightArm.rotateAngleY = 0.0F;
	    bipedRightArm.rotateAngleZ = (float)(2.141592653589793D - (f * 0.5F));
	}
	
	public void setRotationAnglesPoint(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) 
	{
	    bipedRightArm.rotateAngleX = -1.570796F;
	    bipedRightArm.rotateAngleY = par4 / 57.295776F;
	    bipedRightArm.rotateAngleZ = 0.0F;
	}
	
	
}
