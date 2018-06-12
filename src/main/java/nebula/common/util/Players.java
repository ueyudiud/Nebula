/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.util;

import java.util.List;

import javax.annotation.Nullable;

import nebula.Nebula;
import nebula.NebulaProxy;
import nebula.base.A;
import nebula.common.G;
import nebula.common.foodstat.FoodStatExt;
import nebula.common.gui.Container00Base;
import nebula.common.item.ITool;
import nebula.common.network.packet.PacketGuiOpen;
import nebula.common.tool.EnumToolType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * @author ueyudiud
 */
public final class Players
{
	private Players()
	{
	}
	
	public static EntityPlayer player()
	{
		return NebulaProxy.playerInstance();
	}
	
	public static FoodStatExt getFoodStat(EntityPlayer player)
	{
		return (FoodStatExt) player.getFoodStats();
	}
	
	public static List<EnumToolType> getCurrentToolType(EntityPlayer player)
	{
		return ItemStacks.getCurrentToolType(player.getHeldItemMainhand());
	}
	
	public static boolean matchCurrentToolType(EntityPlayer player, EnumToolType...types)
	{
		ItemStack stack = player.getHeldItemMainhand();
		if (stack == null)
		{
			stack = player.getHeldItemOffhand();
		}
		if (stack == null)
		{
			return A.contain(types, EnumToolType.HAND);
		}
		if (stack.getItem() instanceof ITool)
		{
			return A.or(types, ((ITool) stack.getItem()).getToolTypes(stack)::contains);
		}
		else
		{
			return A.or(types, L.toPredicate(EnumToolType::toolMatch, stack));
		}
	}
	
	public static void destoryPlayerCurrentItem(@Nullable EntityPlayer player)
	{
		if (player == null) return;
		if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().stackSize <= 0)
		{
			player.renderBrokenItemStack(player.getHeldItemMainhand());
			player.setHeldItem(EnumHand.MAIN_HAND, null);
		}
		if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().stackSize <= 0)
		{
			player.renderBrokenItemStack(player.getHeldItemOffhand());
			player.setHeldItem(EnumHand.OFF_HAND, null);
		}
	}
	
	public static void validatePlayerCurrentItem(@Nullable EntityPlayer player)
	{
		if (player == null) return;
		if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().stackSize <= 0)
		{
			player.setHeldItem(EnumHand.MAIN_HAND, null);
		}
		if (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().stackSize <= 0)
		{
			player.setHeldItem(EnumHand.OFF_HAND, null);
		}
	}
	
	/**
	 * Open GUI to player.<p>
	 * The container should be instance of {@link nebula.common.gui.Container00Base}.
	 * @param player
	 * @param mod
	 * @param modGuiId
	 * @param world
	 * @param pos
	 */
	public static void openGui(EntityPlayer player, Object mod, int modGuiId, World world, BlockPos pos)
	{
		if (player instanceof FakePlayer)
		{
			return;
		}
		ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
		if (player instanceof EntityPlayerMP)
		{
			EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
			Container remoteGuiContainer = NetworkRegistry.INSTANCE.getRemoteGuiContainer(mc, entityPlayerMP, modGuiId, world, pos.getX(), pos.getY(), pos.getZ());
			assert remoteGuiContainer instanceof Container00Base;
			if (remoteGuiContainer != null)
			{
				entityPlayerMP.getNextWindowId();
				entityPlayerMP.closeContainer();
				int windowId = entityPlayerMP.currentWindowId;
				Nebula.network.sendToPlayer(new PacketGuiOpen(world, pos, mc.getModId(), modGuiId, windowId, ((Container00Base) remoteGuiContainer).applyAllData()), entityPlayerMP);
				entityPlayerMP.openContainer = remoteGuiContainer;
				entityPlayerMP.openContainer.windowId = windowId;
				entityPlayerMP.openContainer.addListener(entityPlayerMP);
				MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
			}
		}
		else if (G.isClient())
		{
			Object guiContainer = NetworkRegistry.INSTANCE.getLocalGuiContainer(mc, player, modGuiId, world, pos.getX(), pos.getY(), pos.getZ());
			FMLCommonHandler.instance().showGuiScreen(guiContainer);
		}
		else
		{
			FMLLog.fine("Invalid attempt to open a local GUI on a dedicated server. This is likely a bug. GUI ID: %s,%d", mc.getModId(), modGuiId);
		}
	}
	
	public static Entity moveEntityToAnotherDim(Entity entity, int dim, double x, double y, double z)
	{
		WorldServer targetWorld = DimensionManager.getWorld(dim);
		if (targetWorld != null) return moveEntityToAnotherDim(entity, dim, x, y, z, targetWorld.getDefaultTeleporter());
		return null;
	}
	
	public static Entity moveEntityToAnotherDim(Entity entity, int dim, double x, double y, double z, Teleporter teleporter)
	{
		WorldServer targetWorld = DimensionManager.getWorld(dim);
		WorldServer originalWorld = DimensionManager.getWorld(entity.world.provider.getDimension());
		if (targetWorld != null && originalWorld != null && targetWorld != originalWorld)
		{
			if (entity.isRiding())
			{
				entity.dismountRidingEntity();
			}
			if (entity.isBeingRidden())
			{
				entity.removePassengers();
			}
			if (entity instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP) entity;
				player.mcServer.getPlayerList().transferPlayerToDimension(player, dim, teleporter);
				player.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN, 0, false));
				player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
				return player;
			}
			else
			{
				Entity newEntity = entity.changeDimension(dim);
				if (newEntity != null)
				{
					entity.setPosition(x + 0.5D, y + 0.5D, z + 0.5D);
				}
				return newEntity;
			}
		}
		return null;
	}
	
	public static void giveOrDrop(EntityPlayer player, ItemStack stack)
	{
		if (player.world.isRemote || stack == null) return;
		if (!player.inventory.addItemStackToInventory(stack))
		{
			player.dropItem(stack, false);
		}
	}
	
	public static boolean isOp(EntityPlayer player)
	{
		return player.canUseCommand(3, "");
	}
}
