package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IThrowable;
import net.minecraft.entity.projectile.EntityThrowable;

public class ThrowableWrapper<T extends EntityThrowable> extends EntityWrapper<T> implements IThrowable
{
    public ThrowableWrapper(T entity) {
        super(entity);
    }
    
    @Override
    public int getType() {
        return 11;
    }
    
    @Override
    public boolean typeOf(int type) {
        return type == 11 || super.typeOf(type);
    }
}
