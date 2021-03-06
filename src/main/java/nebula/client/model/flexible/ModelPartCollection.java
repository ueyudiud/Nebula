/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model.flexible;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializer;

import nebula.base.J;
import nebula.base.function.F;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class ModelPartCollection
{
	static final ModelPartCollection EMPTY = new ModelPartCollection(ImmutableMap.of());
	
	private ImmutableMap<String, INebulaModelPart> variants;
	
	private ModelPartCollection(Map<String, INebulaModelPart> variants)
	{
		this.variants = ImmutableMap.copyOf(variants);
	}
	
	public INebulaModelPart getModelPart(String key)
	{
		if (!this.variants.containsKey(key)) throw new RuntimeException("The model part variant '" + key + "' not found.");
		return this.variants.get(key);
	}
	
	static final JsonDeserializer<ModelPartCollection> DESERIALIZER =
			(json, typeOfType, context) -> new ModelPartCollection(J.getAsMap(json.getAsJsonObject(), F.const2f(context::deserialize, INebulaModelPart.class)));
}
