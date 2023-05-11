package noppes.npcs.api.wrapper;

import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import net.minecraft.util.DamageSource;
import noppes.npcs.api.IDamageSource;

public class DamageSourceWrapper implements IDamageSource
{
    private DamageSource source;
    
    public DamageSourceWrapper(DamageSource source) {
        this.source = source;
    }
    
    @Override
    public String getType() {
        return this.source.getDamageType();
    }
    
    @Override
    public boolean isUnblockable() {
        return this.source.isUnblockable();
    }
    
    @Override
    public boolean isProjectile() {
        return this.source.isProjectile();
    }
    
    @Override
    public DamageSource getMCDamageSource() {
        return this.source;
    }
    
    @Override
    public IEntity getTrueSource() {
        return NpcAPI.Instance().getIEntity(this.source.getTrueSource());
    }
    
    @Override
    public IEntity getImmediateSource() {
        return NpcAPI.Instance().getIEntity(this.source.getImmediateSource());
    }
}
