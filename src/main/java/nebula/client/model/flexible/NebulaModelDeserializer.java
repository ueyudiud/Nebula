/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model.flexible;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;

import nebula.Nebula;
import nebula.base.J;
import nebula.base.JsonDeserializerGateway;
import nebula.base.R;
import nebula.client.model.ModelHelper;
import nebula.common.util.Jsons;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public enum NebulaModelDeserializer implements JsonDeserializer<IModel>
{
	GENERAL(Nebula.MODID, "general")
	{
		@Override
		public FlexibleModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		{
			throw new JsonParseException("The general model are not available yet.");// XXX
		}
	},
	ITEM(Nebula.MODID, "item")
	{
		@Override
		public FlexibleModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		{
			List<FlexibleModel.ModelPartWrapper> parts;
			JsonObject object = json.getAsJsonObject();
			if (object.has("layers"))
			{
				JsonArray array = object.getAsJsonArray("layers");
				parts = new ArrayList<>(array.size());
				for (JsonElement json1 : array)
				{
					parts.add(loadLayer(json1, context, parts.size()));
				}
			}
			else
			{
				parts = ImmutableList.of(loadLayer(object.get("layer"), context, 0));
			}
			
			FlexibleModel model = new FlexibleModel(Objects.toString(NebulaModelLoader.INSTANCE.currentLocation), NebulaModelLoader.INSTANCE.currentItem, deserializeOrDefault(object, context, ModelHelper.ITEM_STANDARD_TRANSFORMS), parts, false, true, false);
			return (FlexibleModel) loadRetexture(model, object);
		}
		
		private FlexibleModel.ModelPartWrapper loadLayer(JsonElement json, JsonDeserializationContext context, int index)
		{
			ModelPartItemLayer layer = new ModelPartItemLayer();
			Function<ItemStack, String> itemmeta = (Function<ItemStack, String>) NebulaModelLoader.NORMAL_METAGENERATOR;
			ToIntFunction<ItemStack> itemcolor = (ToIntFunction<ItemStack>) NebulaModelLoader.NORMAL_MULTIPLIER;
			layer.index = index;
			if (json.isJsonObject())
			{
				JsonObject object2 = json.getAsJsonObject();
				layer.icon = object2.get("texture").getAsString();
				layer.zOffset = J.getOrDefault(object2, "zOffset", 0.5F);
				if (object2.has("meta"))
				{
					itemmeta = context.deserialize(object2.get("meta"), SubmetaLoader.ItemSubmetaGetter.class);
				}
				if (object2.has("colorMultiplier"))
				{
					itemcolor = NebulaModelLoader.loadItemColorMultiplier(object2.get("colorMultiplier").getAsString());
				}
				switch (J.getOrDefault(object2, "type", "normal"))
				{
				case "flat":
					layer = new ModelPartItemLayerFlat(layer);
					break;
				case "convert":
					layer = new ModelPartItemLayerConvert(layer, Jsons.getOrDefault(object2, "convert", "#convert"));
					break;
				case "normal":
					break;
				default:
					throw new JsonParseException("Unknown item layer type of " + object2.get("type").getAsString());
				}
			}
			else
			{
				layer.icon = json.getAsString();
				layer.zOffset = 0.5F;
			}
			return new FlexibleModel.ModelPartWrapper(layer,
					itemmeta,  (Function<IBlockState, String>) NebulaModelLoader.NORMAL_METAGENERATOR,
					itemcolor, (ToIntFunction<IBlockState>) NebulaModelLoader.NORMAL_MULTIPLIER);
		}
	},
	BLOCK(Nebula.MODID, "block")
	{
		@Override
		public FlexibleModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		{
			JsonObject object = json.getAsJsonObject();
			
			JsonArray array = object.getAsJsonArray("part");
			List<FlexibleModel.ModelPartWrapper> parts = J.getAsList(array, j -> {
				INebulaModelPart part = deserialize(j, context);
				Function<ItemStack, String> itemmeta = (Function<ItemStack, String>) NebulaModelLoader.NORMAL_METAGENERATOR;
				Function<IBlockState, String> blockmeta = (Function<IBlockState, String>) NebulaModelLoader.NORMAL_METAGENERATOR;
				if (j.isJsonObject())
				{
					JsonObject obj = j.getAsJsonObject();
					if (obj.has("itemmeta"))
					{
						itemmeta = context.deserialize(obj.get("itemmeta"), SubmetaLoader.ItemSubmetaGetter.class);
					}
					if (obj.has("blockmeta"))
					{
						blockmeta = context.deserialize(obj.get("blockmeta"), SubmetaLoader.BlockSubmetaGetter.class);
					}
				}
				return new FlexibleModel.ModelPartWrapper(part, itemmeta, blockmeta,
						(ToIntFunction<ItemStack>) NebulaModelLoader.NORMAL_MULTIPLIER, (ToIntFunction<IBlockState>) NebulaModelLoader.NORMAL_MULTIPLIER);
			});
			FlexibleModel model = new FlexibleModel(Objects.toString(NebulaModelLoader.INSTANCE.currentLocation),
					NebulaModelLoader.INSTANCE.currentItem,
					deserializeOrDefault(object, context, ModelHelper.BLOCK_STANDARD_TRANSFORMS),
					parts, J.getOrDefault(object, "gui3D", true), J.getOrDefault(object, "smooth_lighting", true),
					J.getOrDefault(object, "builtIn", false));
			return (FlexibleModel) loadRetexture(model, object);
		}
	},
	VANILLA_ITEM("minecraft", "item/generated")
	{
		@Override
		public IModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		{
			JsonObject object = json.getAsJsonObject();
			return loadRetexture(ItemLayerModel.INSTANCE, object);
		}
	},
	VANILLA("minecraft", "generated")
	{
		private final Object	instance;
		private final Method	loadModel;
		
		{
			try
			{
				Class<?> clazz = Class.forName("net.minecraftforge.client.model.ModelLoader$VanillaLoader");
				this.instance = R.getValue(clazz, "INSTANCE", "INSTANCE", null, true);
				this.loadModel = R.getMethod(clazz, "loadModel", ResourceLocation.class);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public IModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
		{
			try
			{
				return (IModel) this.loadModel.invoke(this.instance, NebulaModelLoader.INSTANCE.currentLocation);
			}
			catch (Exception exception)
			{
				return ModelLoaderRegistry.getMissingModel();
			}
		}
	};
	
	static class Transform
	{
		Map<TransformType, TRSRTransformation> map = new EnumMap<>(TransformType.class);
	}
	
	static final JsonDeserializerGateway<INebulaModelPart> BLOCK_MODEL_PART_DESERIALIZERS = new JsonDeserializerGateway<INebulaModelPart>("type", ModelPartVerticalCube.LOADER)
	{
		@Override
		public INebulaModelPart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if (json.isJsonObject())
			{
				JsonObject obj = json.getAsJsonObject();
				INebulaModelPart part = super.deserialize(obj, typeOfT, context);
				if (obj.has("textures"))
				{
					part = part.retexture(Jsons.getAsMap(obj.getAsJsonObject("textures"), JsonElement::getAsString));
				}
				return part;
			}
			else if (json.isJsonPrimitive())
				return NebulaModelLoader.getModelPart(json.getAsString());
			else
				throw new JsonParseException("Unknown model part, got: " + json);
		}
	}.setThrowExceptionWhenNoMatched();
	
	public static INebulaModelPart deserialize(JsonElement json, JsonDeserializationContext context)
	{
		if (json.isJsonPrimitive())
		{
			return NebulaModelLoader.getModelPart(json.getAsString());
		}
		else if (json.isJsonArray())
		{
			if (json.getAsJsonArray().size() == 6 && json.getAsJsonArray().get(0).isJsonPrimitive())
				//Vertical cube predicated.
			{
				JsonObject object = new JsonObject();
				object.add("pos", json);
				return ModelPartVerticalCube.LOADER.deserialize(object, ModelPartVerticalCube.class, context);
			}
			return ModelPartMulti.LOADER.deserialize(json, INebulaModelPart.class, context);
		}
		return BLOCK_MODEL_PART_DESERIALIZERS.deserialize(json, INebulaModelPart.class, context);
	}
	
	protected IModel loadRetexture(IRetexturableModel model, JsonObject object)
	{
		if (object.has("textures"))
		{
			return model.retexture(ImmutableMap.copyOf(J.getAsMap(object.getAsJsonObject("textures"), JsonElement::getAsString)));
		}
		return model;
	}
	
	static
	{
		BLOCK_MODEL_PART_DESERIALIZERS.addDeserializer("void", (j, t, c) -> INebulaModelPart.VOID);
		BLOCK_MODEL_PART_DESERIALIZERS.addDeserializer("nebula:cube", ModelPartVerticalCube.LOADER);
		BLOCK_MODEL_PART_DESERIALIZERS.addDeserializer("nebula:quad", ModelPartQuad.LOADER);
		BLOCK_MODEL_PART_DESERIALIZERS.addDeserializer("multi", ModelPartMulti.LOADER);
	}
	
	public static void registerBlockDeserializer(String key, JsonDeserializer<? extends INebulaModelPart> deserializer)
	{
		BLOCK_MODEL_PART_DESERIALIZERS.addDeserializer(key, deserializer);
	}
	
	public static ImmutableMap<TransformType, TRSRTransformation> deserializeOrDefault(JsonObject object, JsonDeserializationContext context, ImmutableMap<TransformType, TRSRTransformation> defaultTransformation)
	{
		return object.has("transform") ? ImmutableMap.copyOf(context.<Transform> deserialize(object, Transform.class).map) : defaultTransformation;
	}
	
	NebulaModelDeserializer(String modid, String path)
	{
		NebulaModelLoader.registerDeserializer(new ResourceLocation(modid, path), this);
	}
	
	/* Unused, override in each deserializer. */
	public IModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
	{
		return null;
	}
}
