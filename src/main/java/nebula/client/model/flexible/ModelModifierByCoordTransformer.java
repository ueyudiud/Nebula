/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model.flexible;

import javax.annotation.Nullable;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import nebula.client.util.CoordTransformer;
import nebula.client.util.IModelModifier;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class ModelModifierByCoordTransformer implements IModelModifier
{
	TRSRTransformation	m1;
	CoordTransformer	m2;
	
	public ModelModifierByCoordTransformer(@Nullable TRSRTransformation m1, @Nullable CoordTransformer m2)
	{
		this.m1 = m1;
		this.m2 = m2;
	}
	
	@Override
	public void transform(Point3f point)
	{
		if (this.m2 != null) this.m2.transform(point);
		if (this.m1 != null) this.m1.getMatrix().transform(point);
	}
	
	@Override
	public void transform(Vector3f normal)
	{
		if (this.m2 != null) this.m2.transform(normal);
		if (this.m1 != null) this.m1.getMatrix().transform(normal);
	}
}
