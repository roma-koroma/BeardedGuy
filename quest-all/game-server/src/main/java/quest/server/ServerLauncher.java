package quest.server;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.ClientMessage;
import quest.protocol.CommonMessages;
import quest.protocol.GameServerMessage;
import quest.protocol.LoginServerMessage;
import quest.server.network.Handler;
import quest.server.network.InputHandler;
import quest.server.network.Post;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import static quest.server.util.SerializationUtil.serializeGuy;

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

	private boolean isCreated = false;

	Map<SelectionKey, Integer> keyToId;

	Map<ClientMessage.ClientOperation.Type, Handler> operationRegistry;

	/**
	 * Ящики сообщений клиентов.
	 */
	private Post post;
	private GameController gameController;

	public ServerLauncher() throws IOException, InterruptedException
    {
		//INIT
		this.post = new Post();
		this.keyToId = new HashMap<SelectionKey, Integer>();
		gameController = new GameController();
		initRegistry();
        ServerSocketChannel ssc = configure();



        Selector selector = Selector.open();
        SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

        SelectionKey key;

        logger.info("Waiting for connections...");
        while (true)
        {
            while (selector.select() > 0)
            {
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = readyKeys.iterator();
                while (iter.hasNext())
                {
                    key = iter.next();
                    iter.remove();

                    if (key.isAcceptable())
                        accept(key, selector);
                    if (key.isReadable())
                        read(key);
                    if (key.isWritable())
                        write(key);

					if (post.hasMessages(keyToId.get(key)))
					{
						key.interestOps(SelectionKey.OP_WRITE);
					}
                }

            }
        }

    }

	private void initRegistry()
	{
		this.operationRegistry =
			new EnumMap<ClientMessage.ClientOperation.Type, Handler>(ClientMessage.ClientOperation.Type.class);
		operationRegistry.put(ClientMessage.ClientOperation.Type.INPUT, inputHandler());
	}

	private Handler inputHandler()
	{
		InputHandler ret =new InputHandler();
		ret.setGameController(gameController);
		return ret;

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

		try
		{
			int byteCount = ch.read(buffer);
			if(byteCount > 0)
			{
				int size = buffer.getInt(0);

				Integer id = keyToId.get(key);

				if (id == null)
				{
					LoginServerMessage.AuthOperation auth =
						LoginServerMessage.AuthOperation
							.parseFrom(ByteString.copyFrom(buffer.array(), 4, size));
					logger.info("login:{}, password:{}", auth.getLogin(), auth.getPassword());

					BeardedGuy guy = gameController.getGuyByLogin(auth.getLogin().toUpperCase());

					//save new user locally
					keyToId.put(key, guy.getId());
					post.addClient(guy.getId());
					//sent everyone

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

					post.broadcast(serializeGuy(guy));

					key.interestOps(SelectionKey.OP_READ);
				} else
				{
					ClientMessage.ClientOperation op =
						ClientMessage.ClientOperation
							.parseFrom(ByteString.copyFrom(buffer.array(), 4, size));
					Handler handler = operationRegistry.get((op.getOperation()));
					if (handler != null)
					{
						handler.handle(op.getClientId(), op.getBodyMessage(), post);
					}
				}
			}
			else
			{
				closeConnection(key);
			}

		}
		catch(IOException err)
		{
			logger.info("Client {} unexpectedly closed connection", keyToId.get(key));
			closeConnection(key);
		}
	}

	/**
	 * Бережно закрываем соединение
	 * @param key
	 */
	private void closeConnection(SelectionKey key)
	{
		try
		{
			int id = keyToId.get(key);

			BeardedGuy guy = gameController.close(id);
			post.close(id);
			post.broadcast(serializeGuy(guy));
			key.cancel();
			key.channel().close();
		}
		catch (IOException err)
		{
			logger.error("Error while close channel", err);
		}
	}

	private void write(SelectionKey key) throws IOException
    {
		Integer id = keyToId.get(key);
		if(id != null)
		{
			//TODO отправить все сообщения сразу.

			Message message = post.getMessage(id);
			if(message == null)
				return;

			byte[] msg = wrap(message);

			SocketChannel ch = (SocketChannel) key.channel();
			buffer.clear();
			buffer.putInt(0, msg.length);
			buffer.position(4);
			buffer.put(msg);
			buffer.position(0);
			ch.write(buffer);

			//TODO здесь может быть проблема при большом потоке других пишущих клиентов.
			if(!post.hasMessages(id))
				key.interestOps(SelectionKey.OP_READ);
		}

	}

	//TODO убрать захардкоженный тип
	private byte[] wrap(Message message)
	{
		return GameServerMessage.GameServerOperation
			.newBuilder()
			.setOperation(GameServerMessage.GameServerOperation.Type.DELTA)
			.setBodyMessage(message.toByteString())
			.build().toByteArray();
	}

	public static void main(String...args) throws IOException, InterruptedException
    {
        new ServerLauncher();
    }

    private class UserController
    {
    }
}
