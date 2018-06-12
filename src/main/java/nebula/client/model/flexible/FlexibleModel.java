/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model.flexible;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import nebula.V;
import nebula.base.function.F;
import nebula.client.model.ModelBase;
import nebula.client.model.ModelHelper;
import nebula.client.model.flexible.FlexibleBakedModel.BakedModelPartWrapper;
import nebula.client.util.IIconCollection;
import nebula.common.util.L;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class FlexibleModel implements ModelBase, IRetexturableModel, IRecolorableModel
{
	static class ModelPartWrapper
	{
		private final INebulaModelPart part;
		private final Function<ItemStack, String> itemDataGen;
		private final Function<IBlockState, String> blockDataGen;
		private final ToIntFunction<ItemStack> itemColor;
		private final ToIntFunction<IBlockState> blockColor;
		
		ModelPartWrapper(
				INebulaModelPart part,
				Function<ItemStack, String> itemFunc,
				Function<IBlockState, String> blockFunc,
				ToIntFunction<ItemStack> itemColor,
				ToIntFunction<IBlockState> blockColor)
		{
			this.part = part;
			this.itemDataGen = itemFunc;
			this.blockDataGen = blockFunc;
			this.itemColor = itemColor;
			this.blockColor = blockColor;
		}
		
		FlexibleBakedModel.BakedModelPartWrapper bake(TRSRTransformation transformation, VertexFormat format,
				Function<String, IIconCollection> iconHandlerGetter, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
		{
			return new BakedModelPartWrapper(this.part.bake(format, iconHandlerGetter, bakedTextureGetter, transformation), this.itemDataGen, this.blockDataGen);
		}
	}
	
	private final String tag;
	
	private List<ModelPartWrapper>						parts;
	private boolean										gui3D;
	private boolean										builtIn;
	private boolean										ao;
	private Item										item;
	ImmutableMap<TransformType, TRSRTransformation>		transforms;
	
	private Map<String, String> retextures;
	
	/**
	 * Item layer model constructor.
	 * 
	 * @param location
	 */
	public FlexibleModel(String collection)
	{
		this.tag = collection;
		
		this.item = null;
		this.transforms = ModelHelper.ITEM_STANDARD_TRANSFORMS;
		this.parts = ImmutableList.of(new ModelPartWrapper(new ModelPartItemLayer(0, collection),
				(Function<ItemStack, String>) NebulaModelLoader.NORMAL_METAGENERATOR, (Function<IBlockState, String>) NebulaModelLoader.NORMAL_METAGENERATOR,
				(ToIntFunction<ItemStack>) NebulaModelLoader.NORMAL_MULTIPLIER, (ToIntFunction<IBlockState>) NebulaModelLoader.NORMAL_MULTIPLIER));
		this.gui3D = false;
		this.ao = true;
		this.builtIn = false;
	}
	
	public FlexibleModel(String tag, Item item, ImmutableMap<TransformType, TRSRTransformation> transforms, List<ModelPartWrapper> parts, boolean gui3D, boolean ao, boolean builtIn)
	{
		this.tag = tag;
		this.item = item;
		this.transforms = transforms;
		this.parts = parts;
		this.gui3D = gui3D;
		this.ao = ao;
		this.builtIn = builtIn;
	}
	
	private Map<String, IIconCollection>	resources;
	private Collection<ResourceLocation>	textures;
	
	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return this.parts.stream().flatMap(p->p.part.getDependencies().stream()).collect(Collectors.toSet());
	}
	
	public void loadResources()
	{
		try
		{
			Set<String> keys = new HashSet<>();
			keys.add("#particle");
			this.textures = new HashSet<>();
			this.parts.forEach(part -> {
				keys.addAll(part.part.getResources());
				this.textures.addAll(part.part.getDirectResources());
			});
			this.resources = new HashMap<>();
			keys.forEach(key -> {
				IIconCollection handler = getIconHandler(key);
				this.textures.addAll(handler.resources());
				this.resources.put(key, handler);
			});
			this.textures = ImmutableList.copyOf(this.textures);
			this.resources = ImmutableMap.copyOf(this.resources);
		}
		catch (Exception exception)
		{
			this.textures = ImmutableList.of();
			this.resources = ImmutableMap.of();
			NebulaModelLoader.INSTANCE.warn("Wrong model textures data got. item: " + this.item + ", parts: " + this.parts);
		}
	}
	
	@Override
	public Collection<ResourceLocation> getTextures()
	{
		if (this.textures == null)
		{
			loadResources();
		}
		return this.textures;
	}
	
	public IIconCollection getIconHandler(String key)
	{
		return key == null ? NebulaModelLoader.ICON_HANDLER_MISSING : $getIconHandler(key, new ArrayList<>());
	}
	
	private IIconCollection $getIconHandler(String key, List<String> keys)
	{
		switch (key.charAt(0))
		{
		case '#':
			key = key.substring(1);
			if (keys.contains(key))
			{
				V.warn("The '{}' is looped loading, use missingno texture instead.", key);
				return NebulaModelLoader.ICON_HANDLER_MISSING;
			}
			keys.add(key);
			if (this.retextures == null || !this.retextures.containsKey(key)) return NebulaModelLoader.ICON_HANDLER_MISSING;
			return $getIconHandler(this.retextures.get(key), keys);
		case '{':
			return TemplateIconHandler.fromJson(key);
		default:
			return NebulaModelLoader.loadIconHandler(key);
		}
	}
	
	/**
	 * Bake model.
	 * 
	 * @param state Unused.
	 */
	@Override
	public IBakedModel bake(@Nullable IModelState state, VertexFormat format, com.google.common.base.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		try
		{
			TRSRTransformation transformation = state.apply(Optional.absent()).or(TRSRTransformation.identity());
			FlexibleBakedModel.BakedModelPartWrapper[] parts = new FlexibleBakedModel.BakedModelPartWrapper[this.parts.size()];
			final Function<String, IIconCollection> iconHandlerGetter = L.toFunction(this.resources, NebulaModelLoader.ICON_HANDLER_MISSING);
			for (int i = 0; i < parts.length; ++i)
			{
				parts[i] = this.parts.get(i).bake(transformation, format, iconHandlerGetter, F.cast(bakedTextureGetter));
			}
			IIconCollection particleSource = getIconHandler("#particle");
			TextureAtlasSprite particle = bakedTextureGetter.apply(particleSource.build().getOrDefault(NebulaModelLoader.NORMAL, TextureMap.LOCATION_MISSING_TEXTURE));
			return new FlexibleBakedModel(this.transforms, parts, particle, this.gui3D, this.ao, this.builtIn);
		}
		catch (Exception exception)
		{
			NebulaModelLoader.INSTANCE.warn("Failed to load " + this.tag, exception);
			return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
		}
	}
	
	@Override
	public FlexibleModel retexture(ImmutableMap<String, String> textures)
	{
		if (textures == null || textures.isEmpty()) return this;
		Map<String, String> builder = new HashMap<>();
		if (this.retextures != null) builder.putAll(this.retextures);
		builder.putAll(textures);
		FlexibleModel model = new FlexibleModel(this.tag, this.item, this.transforms, this.parts, this.ao, this.gui3D, this.builtIn);
		model.retextures = ImmutableMap.copyOf(builder);
		return model;
	}
	
	@Override
	public void registerColorMultiplier(BlockColors colors)
	{
		if (this.item != null && this.parts.stream().anyMatch(p -> p.blockColor != NebulaModelLoader.NORMAL_MULTIPLIER))
		{
			ToIntFunction<IBlockState>[] functions = new ToIntFunction[this.parts.size()];
			for (int i = 0; i < functions.length; ++i)
			{
				functions[i] = this.parts.get(i).blockColor;
			}
			colors.registerBlockColorHandler((state, worldIn, pos, tintIndex) ->
			tintIndex >= functions.length || tintIndex < 0 ? -1 : functions[tintIndex].applyAsInt(state), Block.getBlockFromItem(this.item));
		}
	}
	
	@Override
	public void registerColorMultiplier(ItemColors colors)
	{
		if (this.item != null && this.parts.stream().anyMatch(p -> p.itemColor != NebulaModelLoader.NORMAL_MULTIPLIER))
		{
			ToIntFunction<ItemStack>[] functions = new ToIntFunction[this.parts.size()];
			for (int i = 0; i < functions.length; ++i)
			{
				functions[i] = this.parts.get(i).itemColor;
			}
			colors.registerItemColorHandler((stack, tintIndex) ->
			tintIndex >= functions.length || tintIndex < 0 ? -1 : functions[tintIndex].applyAsInt(stack), this.item);
		}
	}
}
