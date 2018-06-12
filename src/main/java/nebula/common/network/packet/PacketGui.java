/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.network.packet;

import java.io.IOException;

import nebula.common.network.PacketAbstract;
import nebula.common.network.PacketBufferExt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public abstract class PacketGui<C extends Container> extends PacketAbstract
{
	int guiid;
	
	public PacketGui()
	{
		
	}
	
	public PacketGui(Container container)
	{
		this.guiid = container.windowId;
	}
	
	@Override
	protected void encode(PacketBufferExt output) throws IOException
	{
		output.writeInt(this.guiid);
	}
	
	@Override
	protected void decode(PacketBufferExt input) throws IOException
	{
		this.guiid = input.readInt();
	}
	
	protected C container()
	{
		EntityPlayer player = getPlayer();
		return player.openContainer.windowId == this.guiid ? (C) player.openContainer : null;
	}
}
