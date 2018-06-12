/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model.flexible;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import nebula.V;
import nebula.client.model.BakedModelBase;
import nebula.client.model.ICustomItemRenderModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class FlexibleBakedModel implements BakedModelBase, ICustomItemRenderModel, IPerspectiveAwareModel
{
	static class BakedModelPartWrapper
	{
		private final INebulaBakedModelPart part;
		private final Function<ItemStack, String> itemDataGen;
		private final Function<IBlockState, String> blockDataGen;
		
		BakedModelPartWrapper(INebulaBakedModelPart part,
				Function<ItemStack, String> itemFunc,
				Function<IBlockState, String> blockFunc)
		{
			this.part = part;
			this.itemDataGen = itemFunc;
			this.blockDataGen = blockFunc;
		}
		
		void getQuads(ItemStack stack, EnumFacing side, long rand, List<BakedQuad> quads)
		{
			quads.addAll(this.part.getQuads(side, this.itemDataGen.apply(stack), rand));
		}
		
		void getQuads(IBlockState state, EnumFacing side, long rand, List<BakedQuad> quads)
		{
			quads.addAll(this.part.getQuads(side, this.blockDataGen.apply(state), rand));
		}
	}
	
	private final boolean											gui3d;
	private final boolean											builtIn;
	private final BakedModelPartWrapper[]							parts;
	private final TextureAtlasSprite								particle;
	
	/**
	 * The transformers of different camera type.
	 * @see #handlePerspective(TransformType)
	 */
	private final ImmutableMap<TransformType, TRSRTransformation>	transforms;
	/**
	 * Is model used ambient occlusion.
	 * @see #isAmbientOcclusion()
	 */
	private final boolean											ao;
	/**
	 * The model state marked, if model has crashed during loading quad, the
	 * model will be marked as problem model. To prevent crashing exception
	 * filled logs, the model will stop to bake quad until model is be reloaded.
	 */
	private boolean													errored	= false;
	
	public FlexibleBakedModel(ImmutableMap<TransformType, TRSRTransformation> transforms,
			BakedModelPartWrapper[] parts, TextureAtlasSprite particle,
			boolean gui3d, boolean ao, boolean builtIn)
	{
		this.transforms = transforms;
		this.gui3d = gui3d;
		this.ao = ao;
		this.builtIn = builtIn;
		this.parts = parts;
		this.particle = particle;
	}
	
	@Override
	public List<BakedQuad> getQuads(ItemStack stack, EnumFacing side, long rand)
	{
		if (this.errored) return ImmutableList.of();
		try
		{
			List<BakedQuad> quads = new ArrayList<>();
			for (BakedModelPartWrapper part : this.parts)
			{
				part.getQuads(stack, side, rand, quads);
			}
			return quads;
		}
		catch (Exception exception)
		{
			V.catching(exception);
			this.errored = true;
			return ImmutableList.of();
		}
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
	{
		if (this.errored) return ImmutableList.of();
		try
		{
			List<BakedQuad> quads = new ArrayList<>();
			for (BakedModelPartWrapper part : this.parts)
			{
				part.getQuads(state, side, rand, quads);
			}
			return quads;
		}
		catch (Exception exception)
		{
			V.catching(exception);
			this.errored = true;
			return ImmutableList.of();
		}
	}
	
	@Override
	public boolean isGui3d()
	{
		return this.gui3d;
	}
	
	@Override
	public boolean isBuiltInRenderer()
	{
		return this.builtIn;
	}
	
	@Override
	public boolean isAmbientOcclusion()
	{
		return this.ao;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return this.particle;
	}
	
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
	{
		return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, this.transforms, cameraTransformType);
	}
}
