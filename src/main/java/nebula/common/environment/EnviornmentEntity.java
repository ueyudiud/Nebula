/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.environment;

import nebula.base.collection.IPropertyMap.IProperty;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnviornmentEntity implements IEnvironment
{
	Entity entity;
	
	public EnviornmentEntity(Entity entity)
	{
		this.entity = entity;
	}
	
	@Override
	public long worldTime()
	{
		return this.entity.world.getWorldTime();
	}
	
	@Override
	public float biomeTemperature()
	{
		return getBiome().getFloatTemperature(pos());
	}
	
	@Override
	public World world()
	{
		return this.entity.world;
	}
	
	@Override
	public BlockPos pos()
	{
		return this.entity.getPosition();
	}
	
	@Override
	public <T> T getValue(IProperty<T> property)
	{
		return null;
	}
}
