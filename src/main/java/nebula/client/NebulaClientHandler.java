/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

import nebula.Nebula;
import nebula.NebulaProxy;
import nebula.client.gui.GuiBackground;
import nebula.client.render.IIconLoader;
import nebula.client.render.IIconRegister;
import nebula.client.util.IRenderRegister;
import nebula.common.G;
import nebula.common.NebulaCommonHandler;
import nebula.common.network.packet.PacketKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Internal handler, use for hook. Do not use any method in it.
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public final class NebulaClientHandler
{
	private NebulaClientHandler() { }
	
	private static final Map<Class<?>, ResourceLocation> GUI_BACKGROUNDS = new HashMap<>();
	private static final List<IIconLoader> ICON_LOADERS = new ArrayList<>();
	
	static Collection<IRenderRegister> renderRegisters = new ArrayList<>(256);
	
	/**
	 * Get background ResourceLocation for a GUI, which is provided by
	 * {@link nebula.client.gui.GuiBackground}.
	 * @param clazz the GUI class.
	 * @return the resource location.
	 * @throws NullPointerException when class is null or it does not has GuiBackground annotation.
	 */
	public static ResourceLocation getBackgroundResourceLocation(Class<?> clazz)
	{
		return GUI_BACKGROUNDS.computeIfAbsent(clazz, c -> new ResourceLocation(c.getAnnotation(GuiBackground.class).value()));
	}
	
	/**
	 * Register Icon Loader to list.
	 * @param loader
	 */
	public static void addIconLoader(IIconLoader loader)
	{
		ICON_LOADERS.add(loader);
	}
	
	static void onMinecraftStartLoading()
	{
		ProgressBar bar = ProgressManager.push("Register resource formats.", renderRegisters.size());
		renderRegisters.forEach(
				((Consumer<IRenderRegister>) IRenderRegister::registerRender).andThen(
						r -> bar.step(r instanceof IForgeRegistryEntry<?> ? ((IForgeRegistryEntry<?>) r).getRegistryName().toString() : r.toString())));
		ProgressManager.pop(bar);
		renderRegisters = null;
	}
	
	@SubscribeEvent
	public static void on(ClientTickEvent event)
	{
		GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
		if (currentScreen == null || currentScreen.allowUserInput)
		{
			if (event.phase == Phase.START)
			{
				List<KeyBinding> bindings = NebulaProxy.clientProxy().keybindings;
				long v = 0;
				for (int i = 0; i < bindings.size(); ++i)
				{
					if (GameSettings.isKeyDown(bindings.get(i)))
					{
						v |= (1L << i);
					}
				}
				if (NebulaCommonHandler.setKeybinding(G.player(), v) && G.isSimulating())
				{
					Nebula.network.sendToServer(new PacketKey(v));
				}
			}
		}
		else
		{
			if (NebulaCommonHandler.setKeybinding(G.player(), 0) && G.isSimulating())
			{
				Nebula.network.sendToServer(new PacketKey(0L));
			}
		}
	}
	
	@SubscribeEvent
	public static void on(TextureStitchEvent.Pre event)
	{
		final TextureMap map = event.getMap();
		IIconRegister register = location -> map.registerSprite(location);
		for (IIconLoader loader : ICON_LOADERS)
		{
			loader.registerIcon(register);
		}
	}
}
