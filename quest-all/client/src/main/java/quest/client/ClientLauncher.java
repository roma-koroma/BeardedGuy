package quest.client;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.controller.GameController;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.client.model.RandomInputSource;
import quest.client.network.DeltaHandler;
import quest.client.network.FullHandler;
import quest.client.network.Handler;
import quest.protocol.Client;
import quest.protocol.Common;
import quest.protocol.GameServer;
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

	public String login;
	public static final String PASS = "roman";

	/**
	 * Очередь, в которую пишутся все события ввода.
	 */
	private Deque<RandomInputSource.Event> inputEvents;

	private GameController gameController;

	/**
	 * Флаг о необходимости выключать систему.
	 */
	private boolean isShutdown;
	private DeltaHandler deltaHandler;
	private FullHandler fullHandler;
	private boolean syncRequired = true;


	public ClientLauncher(String name) throws IOException, InterruptedException
    {

		this.login = name;
		this.gameController = new GameController();

		initRegistry();

		inputEvents = new ArrayDeque<RandomInputSource.Event>();

        SocketChannel channel = connect();

        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);

        RandomInputSource source = new RandomInputSource();

        while (!isShutdown)
        {

            while (selector.select() > 0)
            {
                source.tick();
				for( RandomInputSource.Event event : source.getEvents())
				{
					inputEvents.addFirst(event);
				}

                Set<SelectionKey> readyKeys = selector.selectedKeys();

				Iterator<SelectionKey> iter = readyKeys.iterator();

                SelectionKey key;
                while (iter.hasNext())
                {
                    key = iter.next();
                    iter.remove();
					try
					{
						if (key.isValid())
						{
							if (!inputEvents.isEmpty())
							{
								key.interestOps(SelectionKey.OP_WRITE);
							}

							if (key.isValid() && key.isConnectable())
								finishConnect(key);
							if (key.isValid() && key.isReadable())
								read(key);
							if (key.isValid() && key.isWritable())
								write(key);
						}
					}
					catch(IOException err)
					{
						isShutdown = true;
						logger.error("Some IOException ", err);
						throw err;
					}

                }
                Thread.sleep(500);
            }

        }
    }

	private void initRegistry()
	{
		this.deltaHandler = deltaHandler();
		this.fullHandler = fullHandler();
	}

	private FullHandler fullHandler()
	{
		FullHandler handler = new FullHandler();
		handler.setGameController(gameController);
		return handler;
	}

	private DeltaHandler deltaHandler()
	{
		DeltaHandler handler = new DeltaHandler();
		handler.setGameController(gameController);
		return handler;
	}

	private void write(SelectionKey key) throws IOException
    {
        SocketChannel ch = (SocketChannel) key.channel();

		/**
		 * Либо авторизуем мужика, либо пишем события ввода.
		 */
		writeBuffer.clear();

		if(gameController.getMainGuy() == null)
		{
			byte[] auth = LoginServerMessage.AuthOperation.newBuilder().setLogin(login).setPassword(PASS).build()
				.toByteArray();
			writeBuffer.putInt(0, auth.length);
			writeBuffer.position(4);
			writeBuffer.put(auth);
			writeBuffer.position(0);
			ch.write(writeBuffer);
			//отправили данные аутентификации и теперь нам интересен только ответ.
		}
		else
		{
			byte[] input = getInput().toByteArray();
			writeBuffer.putInt(0, input.length);
			writeBuffer.position(4);
			writeBuffer.put(input);
			writeBuffer.position(0);
			ch.write(writeBuffer);
		}
		key.interestOps(SelectionKey.OP_READ);

	}

	/**
	 * Сериализация событий ввода
	 * @return
	 */
	private ByteString getInput()
	{

		Client.Operation.Builder opBuilder = Client.Operation.newBuilder();

		while(inputEvents.size() > 0)
		{
			Common.Action.Builder action = Common.Action.newBuilder();

			RandomInputSource.Event event = inputEvents.removeLast();

			//Считаем, что единственный ввод — это передвижение
			action.setType(Common.Action.Type.MOVE);

			Client.Operation.Move.Builder move = Client.Operation.Move.newBuilder();
			switch (event)
			{
				case UP_KEY:
					move.setDirection(Client.Operation.Move.Direction.UP);
					break;
				case DOWN_KEY:
					move.setDirection(Client.Operation.Move.Direction.DOWN);
					break;
				case LEFT_KEY:
					move.setDirection(Client.Operation.Move.Direction.LEFT);
					break;
				case RIGHT_KEY:
					move.setDirection(Client.Operation.Move.Direction.RIGHT);
					break;
			}
			opBuilder
				.addAction
					(action.setMessage(move.build().toByteString()));

		}

		return opBuilder.build().toByteString();
	}

	private void read(SelectionKey key) throws IOException
	{
		SocketChannel ch = (SocketChannel) key.channel();
		readBuffer.clear();

		try
		{
			int byteCount = ch.read(readBuffer);

			if(byteCount >= 0)
			{
				if (gameController.getMainGuy() == null)
				{
					int size = readBuffer.getInt(0);

					LoginServerMessage.AuthOperationResult result =
						LoginServerMessage
							.AuthOperationResult
							.parseFrom(
								ByteString.copyFrom(readBuffer.array(), 4, size));

					if (result.getIsSuccess())
					{
						BeardedGuy guy = new BeardedGuy(
							result.getUser().getName(),
							new Point(
								result.getUser().getPosition().getX(),
								result.getUser().getPosition().getY()
							)
						);
						guy.setId(result.getUser().getId());
						gameController.setMainGuy(guy);

					}
				}
				//Синхронизируем описание
				if(syncRequired)
				{
					int size = readBuffer.getInt(0);

					GameServer.FullState fullState = GameServer
						.FullState
						.parseFrom(
							ByteString.copyFrom(readBuffer.array(), 4, size)
						);
					fullHandler().handle(fullState);
					syncRequired = false;
				}
				else
				{
					int size = readBuffer.getInt(0);

					//read cooridantes;
					GameServer.DeltaState op = GameServer.DeltaState
						.parseFrom(ByteString.copyFrom(readBuffer.array(), 4, size));
						deltaHandler.handle(op);
				}
			}
			else
			{
				key.channel().close();
				key.cancel();
				isShutdown = true;
			}
		}
		catch(IOException err)
		{
			logger.info("Server unexpectedly closed connection", err);
			isShutdown = true;
			key.cancel();
			key.channel().close();
			return;
		}
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
		if(args.length > 0)
        	new ClientLauncher(args[0]);
		new ClientLauncher("Unknown");
    }

}
