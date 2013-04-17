package quest.client.network;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

/**
 * @author Roman Koretskiy
 */
public interface Handler<T extends Message>
{
	void handle(T message);
}
