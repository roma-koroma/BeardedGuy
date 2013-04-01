package quest.server;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.CommonMessages;
import quest.protocol.LoginServerMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Roman Koretskiy
 */
public class ServerLauncher
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

    public static final int BUFFER_SIZE = 256;

    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    UserController userController;
	private boolean isCreated = false;

	public ServerLauncher() throws IOException, InterruptedException
    {
        ServerSocketChannel ssc = configure();

        Selector selector = Selector.open();
        SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

        SelectionKey key;

        logger.info("Waiting for connections...");
        while (true)
        {
            while (acceptKey.selector().select() > 0)
            {
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = readyKeys.iterator();
                while (iter.hasNext())
                {
                    key = iter.next();
                    iter.remove();

                    if (key.isAcceptable())
                        accept(key, selector);
                    else if (key.isReadable())
                        read(key);
                    else if (key.isWritable())
                        write(key);
                    Thread.sleep(500);
                }

            }
        }

    }

    private ServerSocketChannel configure() throws IOException
    {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(8080));
        return ssc;
    }

    private void accept(SelectionKey key, Selector selector) throws IOException
    {
        logger.info("new client accepted");
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException
    {
		SocketChannel ch = (SocketChannel) key.channel();
		buffer.clear();
        ch.read(buffer);
		if(!isCreated)
		{
			int size = buffer.getInt(0);
			LoginServerMessage.AuthOperation auth =
				LoginServerMessage.AuthOperation
					.parseFrom(ByteString.copyFrom(buffer.array(), 4, size));
			logger.info("login:{}, password:{}", auth.getLogin(), auth.getPassword());

			BeardedGuy guy = new BeardedGuy(auth.getLogin().toUpperCase(), new Point(0, 0));
			guy.setId(1);

			byte[] result = LoginServerMessage.AuthOperationResult.newBuilder()
				.setIsSuccess(true)
				.setUser(serializeGuy(guy))
				.build().toByteArray();

			buffer.clear();
			buffer.putInt(0, result.length);
			buffer.position(4);
			buffer.put(result);
			buffer.position(0);
			ch.write(buffer);

			isCreated = true;
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);

		}

	}

	private CommonMessages.User serializeGuy(BeardedGuy guy)
	{
		return CommonMessages.User.newBuilder()
			.setId(guy.getId())
			.setIsOnline(true)
			.setName(guy.getName())
			.setPosition(serializePoint(guy.getPosition()))
			.build();
	}

	private CommonMessages.Point serializePoint(Point position)
	{
		return CommonMessages.Point.newBuilder()
			.setX(position.getX())
			.setY(position.getY()).build();
	}

	private void write(SelectionKey key) throws IOException
    {
//        SocketChannel ch = (SocketChannel) key.channel();
//        buffer.clear();
//        ch.write(Charset.forName("US-ASCII").newEncoder().encode(CharBuffer.wrap("Hello!")));
//        List<Events> events = getEventsForKey(key);


//		key.interestOps(SelectionKey.OP_READ);
    }

    public static void main(String...args) throws IOException, InterruptedException
    {
        new ServerLauncher();
    }

    private class UserController
    {
    }
}
