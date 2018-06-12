/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.network.packet;

import java.io.IOException;

import nebula.common.gui.Container00Base;
import nebula.common.network.IPacket;
import nebula.common.network.Network;
import nebula.common.network.PacketBufferExt;

/**
 * @author ueyudiud
 */
public final class PacketContainerDataUpdate extends PacketGui<Container00Base>
{
	private byte[] data;
	
	public PacketContainerDataUpdate()
	{
		
	}
	public PacketContainerDataUpdate(Container00Base container, byte[] data)
	{
		super(container);
		this.data = data;
	}
	
	@Override
	protected void encode(PacketBufferExt output) throws IOException
	{
		super.encode(output);
		output.writeByteArray(this.data);
	}
	
	@Override
	protected void decode(PacketBufferExt input) throws IOException
	{
		super.decode(input);
		this.data = input.readByteArray();
	}
	
	@Override
	public IPacket process(Network network) throws IOException
	{
		Container00Base container = container();
		if (container != null)
		{
			container.updateChangesData(this.data);
		}
		return null;
	}
}
