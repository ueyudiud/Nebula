/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.network.packet;

import nebula.common.network.IPacket;
import nebula.common.network.Network;
import net.minecraft.inventory.Container;
import net.minecraft.util.ITickable;

/**
 * The ClientToServer packet.
 * <p>
 * Sent when GUI ticking each tick in client.
 * 
 * @author ueyudiud
 */
public class PacketGuiTickUpdate<C extends Container & ITickable> extends PacketGui<C>
{
	private C container;
	
	public PacketGuiTickUpdate()
	{
	}
	
	public PacketGuiTickUpdate(C container)
	{
		super(container);
		this.container = container;
	}
	
	@Override
	public boolean needToSend()
	{
		return this.container instanceof ITickable;
	}
	
	@Override
	public IPacket process(Network network)
	{
		container().update();
		return null;
	}
}
