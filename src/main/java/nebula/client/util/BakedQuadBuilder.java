/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.util;

import java.util.function.Consumer;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import nebula.common.util.Maths;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class BakedQuadBuilder
{
	private final VertexFormat			format;
	private final IModelModifier		modifier;
	private final Consumer<BakedQuad>	consumer;
	private UnpackedBakedQuad.Builder	builder;
	private TextureAtlasSprite			icon;
	private Point3f						p = new Point3f();
	private Vector3f					n = new Vector3f();
	private Vector4f					c = new Vector4f(1, 1, 1, 1);
	private float						tu, tv;
	
	private boolean textureScaleFlag = true;
	
	public BakedQuadBuilder(VertexFormat format, IModelModifier modifier, Consumer<BakedQuad> consumer)
	{
		this.format = format;
		this.modifier = modifier;
		this.consumer = consumer;
	}
	
	public void switchTextureScale()
	{
		this.textureScaleFlag = !this.textureScaleFlag;
	}
	
	public void nextQuad()
	{
		this.builder = new UnpackedBakedQuad.Builder(this.format);
	}
	
	public void startQuad(EnumFacing facing)
	{
		startQuad(facing, -1, null);
	}
	
	public void startQuad(EnumFacing facing, int tindex, TextureAtlasSprite icon)
	{
		this.builder = new UnpackedBakedQuad.Builder(this.format);
		this.builder.setQuadOrientation(this.modifier.rotateFacing(facing));
		this.builder.setQuadTint(tindex);
		this.builder.setTexture(this.icon = icon);
	}
	
	public void endQuad()
	{
		this.p.set(0, 0, 0);
		this.c.set(1, 1, 1, 1);
		this.n.set(0, 0, 0);
		this.tu = this.tv = 0;
		this.consumer.accept(this.builder.build());
	}
	
	public void color(float r, float g, float b, float a)
	{
		this.c.set(r, g, b, a);
		if (this.modifier != null)
		{
			this.modifier.recolor(this.c);
		}
	}
	
	public void normal(float x, float y, float z)
	{
		this.n.set(x, y, z);
		if (this.modifier != null)
		{
			this.modifier.transform(this.n);
		}
	}
	
	public void pos(float x, float y, float z)
	{
		this.p.set(x, y, z);
		if (this.modifier != null)
		{
			this.modifier.transform(this.p);
		}
		put();
	}
	
	public void pos(float x, float y, float z, float u, float v)
	{
		this.p.set(x, y, z);
		if (this.modifier != null)
		{
			this.modifier.transform(this.p);
		}
		uv(u, v);
		put();
	}
	
	public void uv(float u, float v)
	{
		if (this.textureScaleFlag)
		{
			this.tu = this.icon.getInterpolatedU(u);
			this.tv = this.icon.getInterpolatedV(v);
		}
		else
		{
			this.tu = Maths.lerp(this.icon.getMinU(), this.icon.getMaxU(), u);
			this.tv = Maths.lerp(this.icon.getMinV(), this.icon.getMaxV(), v);
		}
	}
	
	private void put()
	{
		for (int e = 0; e < this.format.getElementCount(); e++)
		{
			switch (this.format.getElement(e).getUsage())
			{
			case POSITION:
				this.builder.put(e, this.p.x, this.p.y, this.p.z);
				break;
			case UV:
				if (this.format.getElement(e).getIndex() == 0)
					this.builder.put(e, this.tu, this.tv, 0, 1);
				else
					this.builder.put(e);
				break;
			case NORMAL:
				this.builder.put(e, this.n.x, this.n.y, this.n.z);
				break;
			case COLOR:
				this.builder.put(e, this.c.x, this.c.y, this.c.z, this.c.w);
				break;
			default:
				this.builder.put(e);
				break;
			}
		}
	}
}
