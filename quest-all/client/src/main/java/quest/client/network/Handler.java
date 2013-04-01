package quest.client.network;

import com.google.protobuf.ByteString;

/**
 * User: Roman Koretskiy
 * Date: 01.04.13
 * Time: 23:35
 */
public interface Handler
{
	void handle(ByteString message);
}
