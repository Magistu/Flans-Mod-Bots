package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import noppes.npcs.controllers.PixelmonHelper;
import net.minecraft.entity.passive.EntityAnimal;
import noppes.npcs.api.entity.IPixelmon;
import net.minecraft.entity.passive.EntityTameable;

public class PixelmonWrapper<T extends EntityTameable> extends AnimalWrapper<T> implements IPixelmon
{
    public PixelmonWrapper(T entity) {
        super(entity);
    }
    
    @Override
    public Object getPokemonData() {
        return PixelmonHelper.getPokemonData((Entity)this.entity);
    }
    
    @Override
    public int getType() {
        return 8;
    }
    
    @Override
    public boolean typeOf(int type) {
        return type == 8 || super.typeOf(type);
    }
}
