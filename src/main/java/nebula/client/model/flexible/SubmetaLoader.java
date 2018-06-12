/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model.flexible;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.gson.*;

import nebula.base.A;
import nebula.base.Cache;
import nebula.base.J;
import nebula.base.S;
import nebula.base.function.F;
import nebula.client.blockstate.BlockStateTileEntityWapper;
import nebula.common.tile.ITilePropertiesAndBehavior.ITP_CustomModelData;
import nebula.common.util.ItemStacks;
import nebula.common.util.L;
import nebula.common.util.Properties;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public enum SubmetaLoader implements JsonDeserializer<Function<? extends Object, String>>
{
	BLOCK_LOADER
	{
		@Override
		public Function<IBlockState, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			Function<IBlockState, String> result = bmgCache.get(json);
			if (result != null)
			{
				return result;
			}
			if (json.isJsonPrimitive())
			{
				ResourceLocation location = new ResourceLocation(json.getAsString());
				try
				{
					result = BLOCK_META_GENERATOR_APPLIER.getOrDefault(location.getResourceDomain(), F.toNullf()).apply(location.getResourcePath());
					if (result == null)
					{
						result = SubmetaLoader.BLOCK_META_GENERATOR.getOrDefault(location, (Function<IBlockState, String>) NebulaModelLoader.NORMAL_METAGENERATOR);
					}
					else
					{
						bmgCache.put(json, result);
					}
				}
				catch (Exception exception)
				{
					result = (Function<IBlockState, String>) NebulaModelLoader.NORMAL_METAGENERATOR;
					//Should I store fail back function?
					bmgCache.put(json, result);
				}
				return result;
			}
			if (json.isJsonObject())
			{
				JsonObject object = json.getAsJsonObject();
				int marker = J.getOrDefault(object, "marker", 0);
				String key;
				switch (marker)
				{
				case 0 :
				{
					key = object.get("key").getAsString();
					result = new BlockSubmetaGetterFromState(key, J.getAsList(object.getAsJsonArray("formats"), JsonElement::getAsString), S.from(J.getAsList(object.getAsJsonArray("default"), JsonElement::getAsString)));
					break;
				}
				case 1 :
				{
					key = object.get("key").getAsString();
					if (object.has("formats"))
					{
						List<Function<IBlockState, String>> funcs = J.getAsList(object.getAsJsonArray("formats"), j -> deserialize(j, typeOfT, context));
						result = new BlockSubmetaGetterCompose(key, funcs);
					}
					else
					{
						result = F.anyf(key);
					}
					break;
				}
				default:
					throw new JsonParseException("Unsupported marker yet, got: " + marker);
				}
				bmgCache.put(json, result);
				return result;
			}
			else
				throw new JsonParseException("Unknown json type, got: " + json.getClass());
		}
	},
	ITEM_LOADER
	{
		@Override
		public Function<ItemStack, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			Function<ItemStack, String> result = imgCache.get(json);
			if (result != null)
			{
				return result;
			}
			if (json.isJsonPrimitive())
			{
				ResourceLocation location = new ResourceLocation(json.getAsString());
				try
				{
					result = ITEM_META_GENERATOR_APPLIER.getOrDefault(location.getResourceDomain(), F.toNullf()).apply(location.getResourcePath());
					if (result == null)
					{
						result = SubmetaLoader.ITEM_META_GENERATOR.getOrDefault(location, (Function<ItemStack, String>) NebulaModelLoader.NORMAL_METAGENERATOR);
					}
					else
					{
						imgCache.put(json, result);
					}
				}
				catch (Exception exception)
				{
					result = (Function<ItemStack, String>) NebulaModelLoader.NORMAL_METAGENERATOR;
					//Should I store fail back function?
					imgCache.put(json, result);
				}
				return result;
			}
			if (json.isJsonObject())
			{
				JsonObject object = json.getAsJsonObject();
				String key = object.get("key").getAsString();
				if (object.has("formats"))
				{
					List<Function<ItemStack, String>> funcs = J.getAsList(object.getAsJsonArray("formats"), j -> deserialize(j, typeOfT, context));
					result = new ItemSubmetaGetterCompose(key, funcs);
				}
				else
				{
					result = F.anyf(key);
				}
				imgCache.put(json, result);
				return result;
			}
			else
				throw new JsonParseException("Unknown json type, got: " + json.getClass());
		}
	};
	
	static final Map<String, Function<String, Function<ItemStack, String>>>		ITEM_META_GENERATOR_APPLIER		= new HashMap<>();
	static final Map<String, Function<String, Function<IBlockState, String>>>	BLOCK_META_GENERATOR_APPLIER	= new HashMap<>();
	static final Map<ResourceLocation, Function<ItemStack, String>>		ITEM_META_GENERATOR		= new HashMap<>();
	static final Map<ResourceLocation, Function<IBlockState, String>>	BLOCK_META_GENERATOR	= new HashMap<>();
	private static Map<JsonElement, Function<ItemStack, String>>	imgCache;
	private static Map<JsonElement, Function<IBlockState, String>>	bmgCache;
	
	static void onResourceReloadStart()
	{
		imgCache = new HashMap<>();
		bmgCache = new HashMap<>();
	}
	
	static void onResourceReloadEnd()
	{
		imgCache.clear();
		imgCache = null;
		bmgCache.clear();
		bmgCache = null;
	}
	
	static
	{
		BLOCK_META_GENERATOR_APPLIER.put("tile", path -> state -> {
			TileEntity tile = BlockStateTileEntityWapper.unwrap(state);
			return tile instanceof ITP_CustomModelData ? ((ITP_CustomModelData) tile).getCustomModelData(path) : NebulaModelLoader.NORMAL;
		});
		ITEM_META_GENERATOR_APPLIER.put("nbt", path -> {
			ItemSubmetaGetterNBT.Builder builder = ItemSubmetaGetterNBT.builder();
			StringTokenizer tokenizer = new StringTokenizer(path, "/\\");
			while (tokenizer.hasMoreTokens())
			{
				String token = tokenizer.nextToken();
				if (token.length() == 0)
				{
					NebulaModelLoader.INSTANCE.stream.println("Invalid nbt item meta generator. got: " + path);
					throw new IllegalArgumentException();
				}
				switch (token.charAt(0))
				{
				case '[':
					builder.appendAt(Short.parseShort(token.substring(1)));
					break;
				default :
					if (token.startsWith("\\["))
					{
						token = token.substring(2);//When first char in tag is '['.
					}
					builder.append(token);
					break;
				}
			}
			return builder.build();
		});
	}
	
	@Nonnull
	static Function<ItemStack, String> loadItemMetaGenerator(String path)
	{
		return NebulaModelLoader.GSON.fromJson(new JsonPrimitive(path), (Type) ItemSubmetaGetter.class);
	}
	
	@Nonnull
	static Function<IBlockState, String> loadBlockMetaGenerator(String path)
	{
		return NebulaModelLoader.GSON.fromJson(new JsonPrimitive(path), (Type) BlockSubmetaGetter.class);
	}
	
	@Override
	public Function<?, String> deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException
	{
		return null;
	}
	
	static abstract class BlockSubmetaGetter implements Function<IBlockState, String>
	{
		
	}
	
	static class BlockSubmetaGetterFromState extends BlockSubmetaGetter
	{
		String							key;
		Function<IBlockState, String>[]	formats;
		String[]						def;
		
		BlockSubmetaGetterFromState(String key, List<String> formats, final String[] def)
		{
			if (formats.size() != def.length) throw new IllegalArgumentException("The formats and default values length are not same.");
			this.key = key;
			this.formats = new Function[formats.size()];
			for (int i = 0; i < formats.size(); ++i)
			{
				String format = formats.get(i);
				if (format.length() == 0) throw new JsonParseException("Unsupported format. got:''");
				if (format.charAt(0) == '#')
				{
					this.formats[i] = NebulaModelLoader.loadBlockMetaGenerator(format.substring(1));
				}
				else
				{
					final Cache<IProperty<?>> property = new Cache<>();
					this.formats[i] = state -> {
						property.setIfAbsent(() -> state.getBlock().getBlockState().getProperty(format));
						return property.andThen(p -> Properties.name(state, p)).orElse("missing");
					};
				}
			}
			this.def = def;
		}
		
		@Override
		public String apply(IBlockState state)
		{
			return S.replaceatos(this.key, '%', state == null ? this.def : A.transform(this.formats, F.func(state)));
		}
	}
	
	private static class BlockSubmetaGetterCompose extends BlockSubmetaGetter
	{
		String								key;
		List<Function<IBlockState, String>>	formats;
		
		BlockSubmetaGetterCompose(String key, List<Function<IBlockState, String>> formats)
		{
			this.key = key;
			this.formats = formats;
		}
		
		@Override
		public String apply(IBlockState state)
		{
			return S.replaceatos(this.key, '%', this.formats.stream().map(F.func(state)).iterator());
		}
	}
	
	static abstract class ItemSubmetaGetter implements Function<ItemStack, String>
	{
	}
	
	static class ItemSubmetaGetterNBT extends ItemSubmetaGetter
	{
		static Builder builder()
		{
			return new Builder();
		}
		
		static class Builder
		{
			private LinkedList<Function<NBTBase, NBTBase>> list = new LinkedList<>();
			
			void append(String key)
			{
				this.list.add(L.withCastIn(L.toFunction(NBTTagCompound::getTag, key)));
			}
			
			void appendAt(int id)
			{
				this.list.add(L.withCastIn(L.toFunction(NBTTagList::get, id)));
			}
			
			private Function<NBTBase, NBTBase> transform()
			{
				Function<NBTBase, NBTBase> func = this.list.removeFirst();
				while (!this.list.isEmpty())
				{
					func = func.andThen(this.list.removeFirst());
				}
				return func;
			}
			
			ItemSubmetaGetterNBT build()
			{
				ItemSubmetaGetterNBT result = new ItemSubmetaGetterNBT();
				result.formats = transform();
				return result;
			}
		}
		
		Function<NBTBase, NBTBase> formats;
		
		@Override
		public String apply(ItemStack t)
		{
			NBTBase nbt = ItemStacks.getOrSetupNBT(t, false);
			try
			{
				nbt = this.formats.apply(nbt);
			}
			catch (ClassCastException | NullPointerException exception)
			{
				return "<error>";
			}
			if (nbt instanceof NBTTagDouble)
			{
				return Double.toString(((NBTTagDouble) nbt).getDouble());
			}
			else if (nbt instanceof NBTTagFloat)
			{
				return Float.toString(((NBTTagDouble) nbt).getFloat());
			}
			else if (nbt instanceof NBTTagLong)
			{
				return Long.toString(((NBTTagLong) nbt).getLong());
			}
			else if (nbt instanceof NBTPrimitive)
			{
				return Integer.toString(((NBTTagDouble) nbt).getInt());
			}
			else if (nbt instanceof NBTTagString)
			{
				return ((NBTTagString) nbt).getString();
			}
			else
			{
				return "<error>";
			}
		}
	}
	
	private static class ItemSubmetaGetterCompose extends ItemSubmetaGetter
	{
		String								key;
		List<Function<ItemStack, String>>	formats;
		
		ItemSubmetaGetterCompose(String key, List<Function<ItemStack, String>> formats)
		{
			this.key = key;
			this.formats = formats;
		}
		
		@Override
		public String apply(ItemStack stack)
		{
			return S.replaceatos(this.key, '%', this.formats.stream().map(F.func(stack)).iterator());
		}
	}
}
