package noppes.npcs.client.renderer;

import net.minecraft.client.model.ModelBase;
import noppes.npcs.client.model.ModelNpcCrystal;

public class RenderNpcCrystal extends RenderNPCInterface
{
    ModelNpcCrystal mainmodel;
    
    public RenderNpcCrystal(ModelNpcCrystal model) {
        super(model, 0.0f);
        this.mainmodel = model;
    }
}
