package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IArrow;
import net.minecraft.entity.projectile.EntityArrow;

public class ArrowWrapper<T extends EntityArrow> extends EntityWrapper<T> implements IArrow
{
    public ArrowWrapper(T entity) {
        super(entity);
    }
    
    @Override
    public int getType() {
        return 4;
    }
    
    @Override
    public boolean typeOf(int type) {
        return type == 4 || super.typeOf(type);
    }
}
