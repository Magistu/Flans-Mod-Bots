package com.magistumod.render;

import com.magistumod.Reference;
import com.magistumod.entity.FlansModShooter;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;

public class RenderSoldier extends RenderBiped<FlansModShooter>
{
	private static final ResourceLocation SOLDIER_SKIN = new ResourceLocation(Reference.MODID + ":textures/entity/soldier.png");
	
	public RenderSoldier(RenderManager man, ModelBiped model, float f)
	{
		super(man, model, f);
		
		this.addLayer(new LayerBipedArmor(this));
	}
	
	protected ResourceLocation getEntityTexture(FlansModShooter entity)
	{
		return SOLDIER_SKIN;
	}
	
    protected void preRenderCallback(FlansModShooter entity, float f){
    	GlStateManager.scale(0.95F, 0.95F, 0.95F);
    }
}
