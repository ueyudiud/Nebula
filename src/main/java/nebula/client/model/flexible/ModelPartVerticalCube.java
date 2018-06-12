/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model.flexible;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;

import nebula.base.A;
import nebula.base.Cache;
import nebula.base.J;
import nebula.client.util.*;
import nebula.common.util.Direction;
import nebula.common.util.L;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class ModelPartVerticalCube implements INebulaModelPart, Cloneable
{
	static final JsonDeserializer<ModelPartVerticalCube> LOADER = (json, typeOfT, context) -> {
		JsonObject object = json.getAsJsonObject();
		ModelPartVerticalCube part = new ModelPartVerticalCube();
		part.tint = J.getOrDefault(object, "tint", -1);
		boolean flag = object.has("face") || object.has("faces");
		if (object.has("pos"))
		{
			JsonElement element1 = object.get("pos");
			if (element1.isJsonArray())
			{
				float[] array = J.getFloat6(object.get("pos"));
				part.xyzPos = array;
				part.fullCube = false;
				part.normalizeUVPos();
				if (flag) part.renderFlag = 0;
			}
		}
		else if (object.has("from")/* && object.has("to") */)// Minecraft loader.
		{
			float[] array1 = J.getFloat3(object.get("from"));
			float[] array2 = J.getFloat3(object.get("to"));
			part.xyzPos = new float[] { array1[0], array1[1], array1[2], array2[0], array2[1], array2[2] };
			part.fullCube = false;
			part.normalizeUVPos();
			if (flag) part.renderFlag = 0;
		}
		if (object.has("layer"))// For normal layer offset, expand cube size to
			// make sure the new layer cover will on.
		{
			int layer = object.get("layer").getAsInt();
			part.xyzPos[0] -= 1.6E-3F * layer;
			part.xyzPos[1] -= 1.6E-3F * layer;
			part.xyzPos[2] -= 1.6E-3F * layer;
			part.xyzPos[3] += 1.6E-3F * layer;
			part.xyzPos[4] += 1.6E-3F * layer;
			part.xyzPos[5] += 1.6E-3F * layer;
		}
		if (flag)
		{
			if (object.has("face"))
			{
				JsonArray array = object.getAsJsonArray("face");
				for (JsonElement element : array)
				{
					loadFaceData(part, element);
				}
			}
			else
			{
				JsonObject object1 = object.getAsJsonObject("faces");
				for (Entry<String, JsonElement> entry : object1.entrySet())
				{
					loadFaceData(part, EnumFacing.byName(entry.getKey()), entry.getValue());
				}
			}
		}
		return part;
	};
	
	static void loadFaceData(ModelPartVerticalCube cube, EnumFacing facing, JsonElement json) throws JsonParseException
	{
		if (!json.isJsonObject()) throw new JsonParseException("The face data should be json object.");
		JsonObject object = json.getAsJsonObject();
		
		int side = facing.getIndex();
		cube.renderFlag |= 1 << side;
		String location = object.get("texture").getAsString();
		if (object.has("uv"))
		{
			cube.uvPos[side] = J.getFloat4(object.get("uv"));
		}
		cube.icons[side] = location;
	}
	
	static void loadFaceData(ModelPartVerticalCube cube, JsonElement json) throws JsonParseException
	{
		if (!json.isJsonObject()) throw new JsonParseException("The face data should be json object.");
		JsonObject object = json.getAsJsonObject();
		
		Direction direction = Direction.valueOf(object.get("side").getAsString());
		int side = direction.ordinal();
		cube.renderFlag |= direction.flag;
		String location = object.get("texture").getAsString();
		if (object.has("uv"))
		{
			cube.uvPos[side] = J.getFloat4(object.get("uv"));
		}
		cube.icons[side] = location;
	}
	
	float[]		xyzPos	= { 0, 0, 0, 16, 16, 16 };
	float[][]	uvPos	= { { 0, 0, 16, 16 }, { 0, 0, 16, 16 }, { 0, 0, 16, 16 }, { 0, 0, 16, 16 }, { 0, 0, 16, 16 }, { 0, 0, 16, 16 } };
	
	byte				renderFlag	= 0x3F;
	CoordTransformer	transformer	= new CoordTransformer();
	boolean				uvLock;
	
	int tint = -1;
	
	// int rotateX = 0;
	// int rotateY = 0;
	
	float red = 1.0F, green = 1.0F, blue = 1.0F, alpha = 1.0F;
	
	boolean		fullCube	= true;
	String[]	icons		= new String[6];
	
	{
		Arrays.fill(this.icons, "all");
	}
	
	private void normalizeUVPos()
	{
		this.uvPos[0][0] = this.xyzPos[0];
		this.uvPos[0][1] = 16.0F - this.xyzPos[5];
		this.uvPos[0][2] = this.xyzPos[3];
		this.uvPos[0][3] = 16.0F - this.xyzPos[2];
		this.uvPos[1][0] = this.xyzPos[0];
		this.uvPos[1][1] = this.xyzPos[2];
		this.uvPos[1][2] = this.xyzPos[3];
		this.uvPos[1][3] = this.xyzPos[5];
		this.uvPos[2][0] = 16.0F - this.xyzPos[3];
		this.uvPos[2][1] = 16.0F - this.xyzPos[4];
		this.uvPos[2][2] = 16.0F - this.xyzPos[0];
		this.uvPos[2][3] = 16.0F - this.xyzPos[1];
		this.uvPos[3][0] = this.xyzPos[0];
		this.uvPos[3][1] = 16.0F - this.xyzPos[4];
		this.uvPos[3][2] = this.xyzPos[3];
		this.uvPos[3][3] = 16.0F - this.xyzPos[1];
		this.uvPos[4][0] = this.xyzPos[5];
		this.uvPos[4][1] = 16.0F - this.xyzPos[4];
		this.uvPos[4][2] = this.xyzPos[2];
		this.uvPos[4][3] = 16.0F - this.xyzPos[1];
		this.uvPos[5][0] = 16.0F - this.xyzPos[2];
		this.uvPos[5][1] = 16.0F - this.xyzPos[4];
		this.uvPos[5][2] = 16.0F - this.xyzPos[5];
		this.uvPos[5][3] = 16.0F - this.xyzPos[1];
	}
	
	@Override
	public Collection<String> getResources()
	{
		List<String> list = nebula.base.collection.A.nonnull();
		list.addAll(A.argument(this.icons));
		return list;
	}
	
	private float[][] datas(int side)
	{
		switch (side)
		{
		case 0 :
			return new float[][] {
				{ this.xyzPos[0], this.xyzPos[1], this.xyzPos[5], this.uvPos[0][0], this.uvPos[0][1] },
				{ this.xyzPos[0], this.xyzPos[1], this.xyzPos[2], this.uvPos[0][0], this.uvPos[0][3] },
				{ this.xyzPos[3], this.xyzPos[1], this.xyzPos[2], this.uvPos[0][2], this.uvPos[0][3] },
				{ this.xyzPos[3], this.xyzPos[1], this.xyzPos[5], this.uvPos[0][2], this.uvPos[0][1] }
			};
		case 1 :
			return new float[][] {
				{ this.xyzPos[0], this.xyzPos[4], this.xyzPos[2], this.uvPos[1][0], this.uvPos[1][1] },
				{ this.xyzPos[0], this.xyzPos[4], this.xyzPos[5], this.uvPos[1][0], this.uvPos[1][3] },
				{ this.xyzPos[3], this.xyzPos[4], this.xyzPos[5], this.uvPos[1][2], this.uvPos[1][3] },
				{ this.xyzPos[3], this.xyzPos[4], this.xyzPos[2], this.uvPos[1][2], this.uvPos[1][1] }
			};
		case 2 :
			return new float[][] {
				{ this.xyzPos[3], this.xyzPos[4], this.xyzPos[2], this.uvPos[2][0], this.uvPos[2][1] },
				{ this.xyzPos[3], this.xyzPos[1], this.xyzPos[2], this.uvPos[2][0], this.uvPos[2][3] },
				{ this.xyzPos[0], this.xyzPos[1], this.xyzPos[2], this.uvPos[2][2], this.uvPos[2][3] },
				{ this.xyzPos[0], this.xyzPos[4], this.xyzPos[2], this.uvPos[2][2], this.uvPos[2][1] }
			};
		case 3 :
			return new float[][] {
				{ this.xyzPos[0], this.xyzPos[4], this.xyzPos[5], this.uvPos[3][0], this.uvPos[3][1] },
				{ this.xyzPos[0], this.xyzPos[1], this.xyzPos[5], this.uvPos[3][0], this.uvPos[3][3] },
				{ this.xyzPos[3], this.xyzPos[1], this.xyzPos[5], this.uvPos[3][2], this.uvPos[3][3] },
				{ this.xyzPos[3], this.xyzPos[4], this.xyzPos[5], this.uvPos[3][2], this.uvPos[3][1] }
			};
		case 4 :
			return new float[][] {
				{ this.xyzPos[0], this.xyzPos[4], this.xyzPos[2], this.uvPos[4][0], this.uvPos[4][1] },
				{ this.xyzPos[0], this.xyzPos[1], this.xyzPos[2], this.uvPos[4][0], this.uvPos[4][3] },
				{ this.xyzPos[0], this.xyzPos[1], this.xyzPos[5], this.uvPos[4][2], this.uvPos[4][3] },
				{ this.xyzPos[0], this.xyzPos[4], this.xyzPos[5], this.uvPos[4][2], this.uvPos[4][1] }
			};
		case 5 :
			return new float[][] {
				{ this.xyzPos[3], this.xyzPos[4], this.xyzPos[5], this.uvPos[5][0], this.uvPos[5][1] },
				{ this.xyzPos[3], this.xyzPos[1], this.xyzPos[5], this.uvPos[5][0], this.uvPos[5][3] },
				{ this.xyzPos[3], this.xyzPos[1], this.xyzPos[2], this.uvPos[5][2], this.uvPos[5][3] },
				{ this.xyzPos[3], this.xyzPos[4], this.xyzPos[2], this.uvPos[5][2], this.uvPos[5][1] }
			};
		default :
			return null;
		}
	}
	
	private void putValue(BakedQuadBuilder builder, EnumFacing facing, float[][] data, TextureAtlasSprite baseIcon)
	{
		builder.startQuad(facing, this.tint, baseIcon);
		builder.normal(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
		builder.color(this.red, this.green, this.blue, this.alpha);
		for (float[] array : data)
		{
			builder.pos(array[0] / 16.0F, array[1] / 16.0F, array[2] / 16.0F, array[3], array[4]);
		}
		builder.endQuad();
	}
	
	@Override
	public INebulaBakedModelPart bake(VertexFormat format, Function<String, IIconCollection> iconHandlerGetter,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, TRSRTransformation transformation)
	{
		final TextureAtlasSprite baseIcon = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		IModelModifier modifier = new ModelModifierByCoordTransformer(transformation, this.transformer);
		Cache<Map<String, ResourceLocation>> collection = new Cache<>();
		if (this.fullCube)
		{
			Cache<Integer> i0 = new Cache<>();
			Map<String, BakedQuad>[] quadMaps = new Map[6];
			BakedQuadBuilder builder = new BakedQuadBuilder(format, modifier, quad ->
			quadMaps[i0.get()] = ImmutableMap.copyOf(
					Maps.transformValues(collection.get(), location ->
					new BakedQuadRetex(quad, bakedTextureGetter.apply(location)))));
			for (int i = 0; i < 6; ++i)
			{
				if ((this.renderFlag & (1 << i)) != 0 && this.icons[i] != null)
				{
					i0.set(i);
					collection.set(iconHandlerGetter.apply(this.icons[i]).build());
					putValue(builder, EnumFacing.VALUES[i], datas(i), baseIcon);
				}
				else
				{
					quadMaps[i] = ImmutableMap.of();
				}
			}
			return new BakedModelPartFullCube(quadMaps);
		}
		else
		{
			Map<String, List<BakedQuad>> quadMap = new HashMap<>();
			BakedQuadBuilder builder = new BakedQuadBuilder(format, modifier, quad ->
			collection.get().forEach((key, loc) ->
			L.put(quadMap, key, new BakedQuadRetex(quad, bakedTextureGetter.apply(loc)))));
			for (int i = 0; i < 6; ++i)
			{
				if ((this.renderFlag & (1 << i)) != 0 && this.icons[i] != null)
				{
					collection.set(iconHandlerGetter.apply(this.icons[i]).build());
					putValue(builder, EnumFacing.VALUES[i], datas(i), baseIcon);
				}
			}
			quadMap.replaceAll((name, list) ->
			list instanceof ImmutableList ? (ImmutableList) list : ImmutableList.copyOf(list));
			return new INebulaBakedModelPart.BakedModelPart(ImmutableMap.copyOf(quadMap));
		}
	}
	
	@Override
	protected ModelPartVerticalCube clone()
	{
		try
		{
			return (ModelPartVerticalCube) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError(e);
		}
	}
	
	@Override
	public INebulaModelPart retexture(Map<String, String> retexture)
	{
		String[] icons = this.icons.clone();
		ModelPartVerticalCube part = clone();
		part.icons = icons;
		if (retexture.containsKey("all"))
		{
			Arrays.fill(icons, retexture.get("all"));
		}
		for (Entry<String, String> entry : retexture.entrySet())
		{
			switch (entry.getKey())
			{
			case "down":
				icons[0] = entry.getValue();
				break;
			case "up":
				icons[1] = entry.getValue();
				break;
			case "north":
				icons[2] = entry.getValue();
				break;
			case "south":
				icons[3] = entry.getValue();
				break;
			case "west":
				icons[4] = entry.getValue();
				break;
			case "east":
				icons[5] = entry.getValue();
				break;
			default:
				break;
			}
		}
		return part;
	}
	
	static class BakedModelPartFullCube implements INebulaBakedModelPart
	{
		final Map<String, BakedQuad>[]	quads;
		
		BakedModelPartFullCube(Map<String, BakedQuad>[] quadMaps)
		{
			this.quads = quadMaps;
		}
		
		@Override
		public List<BakedQuad> getQuads(EnumFacing facing, String key)
		{
			BakedQuad quad;
			return facing == null ||
					(quad = this.quads[facing.getIndex()].get(key)) == null ? ImmutableList.of() : ImmutableList.of(quad);
		}
	}
}
