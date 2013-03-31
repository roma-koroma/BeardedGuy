package quest.client;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.client.model.RandomInputSource;
import quest.protocol.ClientMessage;
import quest.protocol.GameServerMessage;
import quest.protocol.LoginServerMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * @author Roman Koretskiy
 */
public class ClientLauncher
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(ClientLauncher.class);

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8080;

    public static final int BUFFER_SIZE = 256;

    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);

	public static final String LOGIN = "roman";
	public static final String PASS = "roman";

	private BeardedGuy guy;

	/**
	 * Очередь, в которую пишутся все события ввода.
	 */
	private Deque<RandomInputSource.Event> events;

	public ClientLauncher() throws IOException, InterruptedException
    {
		events = new ArrayDeque<RandomInputSource.Event>();

        SocketChannel channel = connect();

        Selector selector = Selector.open();
//        SelectionKey k = channel.register(selector, SelectionKey.OP_READ| SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
        SelectionKey k = channel.register(selector, SelectionKey.OP_CONNECT);

        RandomInputSource source = new RandomInputSource();

        while (true)
        {

            while (k.selector().select() > 0)
            {
                source.tick();
				for( RandomInputSource.Event event : source.getEvents())
				{
					events.addFirst(event);
				}

                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = readyKeys.iterator();

                SelectionKey key;
                while (iter.hasNext())
                {
                    key = iter.next();
                    iter.remove();

                    if(key.isConnectable())
                        finishConnect(key);
                    else if(key.isReadable())
                        read(key);
                    else if(key.isWritable())
                        write(key);
                }
                Thread.sleep(500);
            }

        }
    }

    private void write(SelectionKey key) throws IOException
    {
        SocketChannel ch = (SocketChannel) key.channel();

		/**
		 * Либо авторизуем мужика, либо пишем события ввода.
		 */
		writeBuffer.clear();

		if(guy == null)
		{
			byte[] auth = LoginServerMessage.AuthOperation.newBuilder().setLogin(LOGIN).setPassword(PASS).build()
				.toByteArray();
			writeBuffer.putInt(0, auth.length);
			writeBuffer.position(4);
			writeBuffer.put(auth);
			writeBuffer.position(0);
			ch.write(writeBuffer);
		}
		else
		{
			byte[] input = ClientMessage
				.ClientOperation.newBuilder()
				.setClientId(guy.getId())
				.setOperation(ClientMessage
				.ClientOperation.Type.INPUT)
				.setBodyMessage(getInput())
				.build().toByteArray();
			ch.write(writeBuffer.putInt(0, input.length).put(input));
		}

		key.interestOps(SelectionKey.OP_READ);

	}

	/**
	 * Сериализация событий ввода
	 * @return
	 */
	private ByteString getInput()
	{

		ClientMessage.InputOperation.Builder builder = ClientMessage.InputOperation.newBuilder();

		while(events.size() > 0)
		{
			RandomInputSource.Event event = events.removeLast();
			ClientMessage.InputOperation.Input input;
			switch (event)
			{
				case UP_KEY:
					input = ClientMessage.InputOperation.Input.UP_KEY;
					break;
				case DOWN_KEY:
					input = ClientMessage.InputOperation.Input.DOWN_KEY;
					break;
				case LEFT_KEY:
					input = ClientMessage.InputOperation.Input.LEFT_KEY;
					break;
				case RIGHT_KEY:
					input = ClientMessage.InputOperation.Input.RIGHT_KEY;
					break;
				default:
					input = ClientMessage.InputOperation.Input.NONE;
			}
			builder.addInput(input);
		}

		return builder.build().toByteString();
	}

	private void read(SelectionKey key) throws IOException
    {
		/**

		 GameFlow flow  = new GameFlow();
		 flow.addGuy(guy);
		 while(true)
		 {
		 flow.tick();
		 }
		 */
		SocketChannel ch = (SocketChannel) key.channel();
		readBuffer.clear();
		ch.read(readBuffer);

		if(guy == null)
		{
			int size = readBuffer.getInt(0);

			LoginServerMessage.AuthOperationResult result =
				LoginServerMessage
					.AuthOperationResult
					.parseFrom(
					ByteString.copyFrom(readBuffer.array(), 4, size));
			if(result.getIsSuccess())
			{
				guy = new BeardedGuy(
					result.getUser().getName(),
					new Point(
						result.getUser().getPosition().getX(),
						result.getUser().getPosition().getY()
					)
				);
				guy.setId(result.getUser().getId());
			}
		}

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void finishConnect(SelectionKey key) throws IOException
    {
        SocketChannel ch = (SocketChannel) (key.channel());
        if(ch.finishConnect())
        {
            logger.info("finally connect to server ({}:{})", HOST, PORT);
            key.interestOps(SelectionKey.OP_WRITE);
        }
        else
            logger.error("can't connect to server ({}:{})", HOST, PORT);
    }

    private SocketChannel connect() throws IOException
    {
        String host = "127.0.0.1";
        InetSocketAddress socketAddress = new InetSocketAddress(host, 8080);

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(socketAddress);
        return channel;

    }


    public static void main(String...args) throws IOException, InterruptedException
    {
        new ClientLauncher();
    }

}
