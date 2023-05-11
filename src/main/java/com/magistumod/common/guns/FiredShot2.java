package com.magistumod.common.guns;

import java.util.Optional;

import javax.annotation.Nullable;

import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.FireableGun;
import com.flansmod.common.guns.FiredShot;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class FiredShot2 extends FiredShot{

	/**
	 * The weapon used to fire the shot
	 */
	private FireableGun weapon;
	/**
	 * The BulletType of the fired bullet
	 */
	private BulletType bullet;
	/**
	 * Optional containing a source, if one can be associated with the shot
	 */
	private Optional<Entity> source;
	/**
	 * Optional of the entity which fired the shot. Can be the same as the Player optional
	 */
	private Optional<? extends Entity> shooter;
	
	/**
	 * @param weapon weapon used to fire the shot
	 * @param bullet BulletType of the fired bullet
	 */
	public FiredShot2(FireableGun weapon, BulletType bullet)
	{
		super(weapon, bullet);
		this.weapon = weapon;
		this.bullet = bullet;
		this.source = Optional.empty();
		this.shooter = this.source;
	}
	
	/**
	 * @param weapon weapon used to fire the shot
	 * @param bullet BulletType of the fired bullet
	 * @param source The source who shot
	 */
	
	/**
	 * This constructor should be used when an entity shot, but no source is involved
	 * e.g a zombie holding a gun or a sentry
	 * 
	 * @param weapon weapon used to fire the shot
	 * @param bullet BulletType of the fired bullet
	 * @param shooter Entity which fired the shot
	 */
	public FiredShot2(FireableGun weapon, BulletType bullet, Entity shooter)
	{
		this(weapon, bullet, shooter, null);
	}
	
	/**
	 * This constructor should be used if a source causes a shot, but the source is actually not the entity shooting it
	 * e.g a source flying a plane
	 * 
	 * @param weapon  weapon used to fire the shot
	 * @param bullet  BulletType of the fired bullet
	 * @param shooter the Entity firing the shot
	 * @param source  the Player causing the shot
	 */
	public FiredShot2(FireableGun weapon, BulletType bullet, Entity shooter, @Nullable Entity source)
	{
		super(weapon, bullet, shooter, null);
		this.weapon = weapon;
		this.bullet = bullet;
		this.source = Optional.ofNullable(source);
		this.shooter = Optional.of(shooter);
	}
	
	/**
	 * @return The gun used for this shot
	 */
	public FireableGun getFireableGun()
	{
		return this.weapon;
	}
	
	/**
	 * @return The BulletType of the bullet used in the shot
	 */
	public BulletType getBulletType()
	{
		return this.bullet;
	}
	
	/**
	 * @return the matching DamageSource for the shot
	 */
	public DamageSource getDamageSource()
	{
		return getDamageSource(false);
	}

	/**
	 * @return the matching DamageSource for the shot with the additional 'headshot' information
	 */
	public DamageSource getDamageSource(Boolean headshot)
	{
		if (source.isPresent()) {
			return new EntityDamageSourceFlan2(weapon.getShortName(), source.get(), source.get(), weapon.getInfoType(), headshot);
		}
		return DamageSource.GENERIC;
	}
	
	/**
	 * @return Optional containing a source if one is involved in the cause of the shot
	 */
	public Optional<Entity> getSourceOptional()
	{
		return this.source;
	}
	
	/**
	 * @return Optional containing the Entity which shot. Might be the same as the source optional
	 */
	public Optional<? extends Entity> getShooterOptional()
	{
		return this.shooter;
	}
}
