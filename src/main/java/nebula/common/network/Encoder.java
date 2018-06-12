/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.network;

import java.io.IOException;

/**
 * The encoder, use to serialize data from Client to Server.
 * @author ueyudiud
 * @see nebula.common.network.packet.PacketGuiSyncData
 */
public interface Encoder
{
	void encode(PacketBufferExt output) throws IOException;
}
