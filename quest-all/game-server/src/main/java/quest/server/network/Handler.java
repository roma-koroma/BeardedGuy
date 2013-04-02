package quest.server.network;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roman K.
 */
public interface Handler
{

	void handle(int clientId, ByteString bodyMessage, Post post);
}
