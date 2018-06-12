/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.network.packet;

import java.io.IOException;

import nebula.NebulaProxy;
import nebula.common.gui.Container00Base;
import nebula.common.network.IPacket;
import nebula.common.network.Network;
import nebula.common.network.PacketBufferExt;
import nebula.common.util.W;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ueyudiud
 */
public class PacketGuiOpen extends PacketGui<Container00Base>
{
	private int dimId;
	private BlockPos pos;
	private String modId;
	private int guiId;
	
	private byte[] initializeData;
	
	public PacketGuiOpen()
	{
	}
	public PacketGuiOpen(World world, BlockPos pos, String modId, int guiId, int windowId, byte[] initializeData)
	{
		super.guiid = windowId;
		this.dimId = world.provider.getDimension();
		this.pos = pos;
		this.modId = modId;
		this.guiId = guiId;
		this.initializeData = initializeData;
	}
	
	@Override
	protected void encode(PacketBufferExt output) throws IOException
	{
		output.writeVarInt(this.dimId);
		output.writeBlockPos(this.pos);
		output.writeString(this.modId);
		output.writeVarInt(this.guiId);
		super.encode(output);
		output.writeByteArray(this.initializeData);
	}
	
	@Override
	protected void decode(PacketBufferExt input) throws IOException
	{
		this.dimId = input.readVarInt();
		this.pos = input.readBlockPos();
		this.modId = input.readString(64);
		this.guiId = input.readVarInt();
		super.decode(input);
		this.initializeData = input.readByteArray();
	}
	
	@Override
	public IPacket process(Network network) throws IOException
	{
		EntityPlayer player = getPlayer();
		ModContainer mc = FMLCommonHandler.instance().findContainerFor(this.modId);
		Object guiContainer = NetworkRegistry.INSTANCE.getLocalGuiContainer(mc, player, this.guiId, W.world(this.dimId), this.pos.getX(), this.pos.getY(), this.pos.getZ());
		Runnable task = NebulaProxy.proxy.createOpenGuiTask(guiContainer, this.guiid, this.initializeData);
		
		IThreadListener thread = FMLCommonHandler.instance().getWorldThread(network.getChannel(Side.CLIENT).attr(NetworkRegistry.NET_HANDLER).get());
		if (thread.isCallingFromMinecraftThread())
		{
			task.run();
		}
		else
		{
			thread.addScheduledTask(task);
		}
		return null;
	}
}
