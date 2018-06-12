/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.network;

import java.io.IOException;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
public interface INetwokSynch
{
	void writeNetworkData(int type, PacketBufferExt buf) throws IOException;
	
	@SideOnly(Side.CLIENT)
	void readNetworkData(int type, PacketBufferExt buf) throws IOException;
}
