package noppes.npcs.api.wrapper;

import net.minecraft.entity.EntityLiving;
import noppes.npcs.api.entity.IVillager;
import net.minecraft.entity.passive.EntityVillager;

public class VillagerWrapper<T extends EntityVillager> extends EntityLivingWrapper<T> implements IVillager
{
    public VillagerWrapper(T entity) {
        super(entity);
    }
    
    public int getProfession() {
        return this.entity.getProfession();
    }
    
    public String getCareer() {
        return this.entity.getProfessionForge().getCareer(this.entity.getEntityId()).getName();
    }
    
    @Override
    public int getType() {
        return 9;
    }
    
    @Override
    public boolean typeOf(int type) {
        return type == 9 || super.typeOf(type);
    }
}
