/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.network.packet;

import java.io.IOException;

import nebula.common.gui.IGuiActionListener;
import nebula.common.network.IPacket;
import nebula.common.network.Network;
import nebula.common.network.PacketBufferExt;
import net.minecraft.inventory.Container;

/**
 * @author ueyudiud
 */
public class PacketGuiAction<C extends Container & IGuiActionListener> extends PacketGui<C>
{
	private byte	type;
	private long	code;
	
	public PacketGuiAction()
	{
	}
	
	public PacketGuiAction(byte type, long code, C container)
	{
		super(container);
		this.type = type;
		this.code = code;
	}
	
	@Override
	protected void encode(PacketBufferExt output) throws IOException
	{
		super.encode(output);
		output.writeByte(this.type);
		output.writeLong(this.code);
	}
	
	@Override
	protected void decode(PacketBufferExt input) throws IOException
	{
		super.decode(input);
		this.type = input.readByte();
		this.code = input.readLong();
	}
	
	@Override
	public IPacket process(Network network) throws IOException
	{
		container().onRecieveGUIAction(this.type, this.code);
		return null;
	}
}
