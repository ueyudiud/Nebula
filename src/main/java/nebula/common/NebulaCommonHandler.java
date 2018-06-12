/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common;

import static nebula.common.util.L.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import nebula.Nebula;
import nebula.common.item.IItemBehaviorsAndProperties.IIB_BlockHarvested;
import nebula.common.network.packet.PacketChunkNetData;
import nebula.common.util.Players;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

/**
 * Internal handler, use for hook. Do not use any method in it.
 * @author ueyudiud
 */
public final class NebulaCommonHandler
{
	private NebulaCommonHandler() { }
	
	public static final ResourceLocation TILE_PROPERTY = new ResourceLocation(Nebula.MODID, "tile/property");
	
	static final Map<EntityPlayer, Long> PLAYER_KEY_MAP = new WeakHashMap<>();
	static final Map<Integer, Map<Integer, ListMultimap<Long, BlockPos>>> SYNC_NETWORK_MAP = new HashMap<>();
	
	/**
	 * Put new binding state.
	 * @param player the player.
	 * @param state the key input state.
	 * @return if state has changed.
	 */
	public static boolean setKeybinding(EntityPlayer player, long state)
	{
		Long old = PLAYER_KEY_MAP.put(player, state);
		return old != null && old != state;
	}
	
	public static void markTileEntityForUpdate(int dim, BlockPos pos, int type)
	{
		get(SYNC_NETWORK_MAP, dim).computeIfAbsent(type, i -> LinkedListMultimap.create()).put((long) pos.getZ() << 32 | pos.getX(), pos.toImmutable());
	}
	
	@SubscribeEvent
	public static void on(PlayerLoggedOutEvent event)
	{
		PLAYER_KEY_MAP.remove(event.player);
	}
	
	@SubscribeEvent
	public static void onHarvestBlock(HarvestDropsEvent event)
	{
		if (event.getHarvester() == null) return;
		ItemStack stack = event.getHarvester().getHeldItemMainhand();
		if (stack == null)
		{
			stack = event.getHarvester().getHeldItemOffhand();
		}
		if (stack != null && stack.getItem() instanceof IIB_BlockHarvested)
		{
			((IIB_BlockHarvested) stack.getItem()).onBlockHarvested(stack, event);
			Players.destoryPlayerCurrentItem(event.getHarvester());
		}
	}
	
	@SubscribeEvent
	public static void onServerTickingEnd(TickEvent.ServerTickEvent event)
	{
		if (event.phase == Phase.END)
		{
			for (Entry<Integer, Map<Integer, ListMultimap<Long, BlockPos>>> entry : SYNC_NETWORK_MAP.entrySet())
			{
				int dim = entry.getKey();
				World world = DimensionManager.getWorld(dim);
				if (world != null)
				{
					for (Entry<Integer, ListMultimap<Long, BlockPos>> entry2 : entry.getValue().entrySet())
					{
						int mark = entry2.getKey();
						for (Entry<Long, List<BlockPos>> entry3 : ((Map<Long, List<BlockPos>>)(Map) entry2.getValue().asMap()).entrySet())
						{
							int chunkX = (int) (entry3.getKey() & 0xFFFFFFFFL);
							int chunkZ = (int) (entry3.getKey() >>> 32 & 0xFFFFFFFFL);
							int centerX = chunkX + 8;
							int centerZ = chunkZ + 8;
							Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
							Nebula.network.sendLargeToNearby(new PacketChunkNetData(mark, chunk, entry3.getValue()), dim, centerX, 128, centerZ, 128.0F);
						}
					}
					entry.getValue().clear();
				}
				else
				{
					SYNC_NETWORK_MAP.remove(dim);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onCapabilityWraped(AttachCapabilitiesEvent.TileEntity event)
	{
		//		TileEntity tile = event.getObject();
		//		event.addCapability(TILE_PROPERTY, new TileEntityCapabilityWrapper(tile));
	}
}
