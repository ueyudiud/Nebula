/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.render;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The icon register.
 * 
 * @author ueyudiud
 * @see nebula.client.render.IIconLoader
 * @see nebula.client.NebulaTextureHandler
 * @see net.minecraft.client.renderer.texture.TextureMap
 */
@FunctionalInterface
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public interface IIconRegister
{
	default TextureAtlasSprite registerIcon(String key) { return registerIcon(new ResourceLocation(key)); }
	default TextureAtlasSprite registerIcon(String domain, String path) { return registerIcon(new ResourceLocation(domain, path)); }
	
	TextureAtlasSprite registerIcon(ResourceLocation location);
}
