package quest.server;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.protocol.Client;
import quest.protocol.Common;
import quest.protocol.GameServer;
import quest.protocol.LoginServerMessage;
import quest.server.dao.GameController;
import quest.server.model.BeardedGuy;
import quest.server.network.InputHandler;
import quest.server.network.Post;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

import static quest.common.util.SerializationUtil.serializeAction;
import static quest.common.util.SerializationUtil.serializeGuy;
import static quest.server.SerializationUtil.serializeEnter;
import static quest.server.SerializationUtil.serializeExit;

/**
 * @author Roman Koretskiy
 */
public class ServerLauncher
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

	/**
	 * Ящики сообщений клиентов.
	 */
	private Post post;
	private GameController gameController;
	private InputHandler inputHandler;

	private Network network = new Network();

	public ServerLauncher() throws IOException, InterruptedException
    {
		//INIT
		this.post = new Post();
		this.keyToId = new HashMap<SelectionKey, Integer>();
		gameController = new GameController();
		initRegistry();

		Network network = new Network();

		new Thread(network).start();
    }

	private void initRegistry()
	{
		this.inputHandler = inputHandler();
	}

	private InputHandler inputHandler()
	{
		InputHandler ret =new InputHandler();
		ret.setGameController(gameController);
		return ret;

	}


	private GameServer.FullState fullGameState(Collection<BeardedGuy> fullModel)
	{
		GameServer.FullState.Builder retBuilder = GameServer.FullState.newBuilder();

		for(BeardedGuy guy : fullModel)
		{
			retBuilder.addUser(serializeGuy(guy));
		}

		return retBuilder.build();
	}

	private void writeMessage(SocketChannel ch, byte[] result) throws IOException
	{
		buffer.clear();
		buffer.putInt(0, result.length);
		buffer.position(4);
		buffer.put(result);
		buffer.position(0);
		ch.write(buffer);
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
			post.broadcast(serializeAction(Common.Action.Type.EXIT, serializeExit(guy.getId())));
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

			List<Common.Action> messages = post.getMessages(id);
			if(messages == null || messages.isEmpty())
				return;

			byte[] msg = wrapDelta(messages);

			SocketChannel ch = (SocketChannel) key.channel();
			writeMessage(ch, msg);
			logger.info("write to user {}", id);

			//TODO здесь может быть проблема при большом потоке других пишущих клиентов.
			if(!post.hasMessages(id))
				key.interestOps(SelectionKey.OP_READ);
		}

	}

	private byte[] wrapDelta(List<Common.Action> messages)
	{
		return GameServer.DeltaState.newBuilder().addAllAction(messages).build().toByteArray();
	}

	public static void main(String...args) throws IOException, InterruptedException
    {
        new ServerLauncher();
    }

	/**
	 * В отдельном потоке ожидает
	 */
	private class Network implements Runnable
	{

		public static final int BUFFER_SIZE = 1024;

		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

		Map<SelectionKey, Integer> keyToId;

		Messenger messenger;

		/**
		 * Здесь хранятся ключи всех клиентов с флагами об их состоянии.
		 */

		public Network()
		{
			messenger = new Messenger();
		}

		@Override
		public void run()
		{
			ServerSocketChannel ssc = configure();
			Selector selector = Selector.open();
			ssc.register(selector, SelectionKey.OP_ACCEPT);

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
						try
						{
							if (key.isValid())
							{
								if (key.isValid() && key.isAcceptable())
									accept(key, selector);
								if (key.isValid() && key.isReadable())
									read(key);
								if (key.isValid() && key.isWritable())
									write(key);

//								if (post.hasMessages(keyToId.get(key)))
//								{
//									key.interestOps(SelectionKey.OP_WRITE);
//								}
							}
						}
						catch (CancelledKeyException err)
						{
							logger.error("CancelledKeyException while working with [{}]", keyToId.get(key));
							continue;
						}
						catch (IOException err)
						{
							logger.error("Some IOException but we continue", err);
							continue;
						}


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
			channel.
		}

		private void read(SelectionKey key) throws IOException
		{
			SocketChannel ch = (SocketChannel) key.channel();
			buffer.clear();

			try
			{
				int byteCount = ch.read(buffer);

				if (byteCount >= 0)
				{
					int size = buffer.getInt(0);

					Integer id = keyToId.get(key);

					//Here

					if (id == null)
					{

						//здесь начинается интересное.
						//fireNewClientEvent();
						messenger.handleMessage(key, buffer.array());

						LoginServerMessage.AuthOperation auth =
							LoginServerMessage.AuthOperation
								.parseFrom(ByteString.copyFrom(buffer.array(), 4, size));
						logger.info("login:{}, password:{}", auth.getLogin(), auth.getPassword());

						BeardedGuy guy = gameController.getGuyByLogin(auth.getLogin().toUpperCase());

						//save new user locally
						keyToId.put(key, guy.getId());
						post.addClient(guy.getId());

						byte[] result = LoginServerMessage.AuthOperationResult.newBuilder()
							.setIsSuccess(true)
							.setUser(serializeGuy(guy))
							.build().toByteArray();

						writeMessage(ch, result);

						//отправка полной модели также происходит в спец. режиме
						writeMessage(ch, fullGameState(gameController.getFullModel()).toByteArray());
						for (BeardedGuy sendedGuy : gameController.getFullModel())
						{
							logger.warn("{}:{}", sendedGuy.getId(), sendedGuy.getName());
						}
						post.broadcastWithExcluding(serializeAction(Common.Action.Type.ENTER, serializeEnter(guy)), guy.getId());

						key.interestOps(SelectionKey.OP_READ);
					} else
					{
						Client.Operation op =
							Client.Operation
								.parseFrom(ByteString.copyFrom(buffer.array(), 4, size));
						inputHandler.handle(id, op, post);
					}
				} else
				{
					closeConnection(key);
				}

			}
			catch (IOException err)
			{
				logger.info("Client {} unexpectedly closed connection", keyToId.get(key));
				closeConnection(key);
			}
		}

		private class Client
		{

		}

	}

	/**
	 * Логика преобразования сообщений
	 *
	 * Знает все протоколы. Знает как и в каком порядке должен присылать сообщения клиент.
	 */
	private class Messenger
	{
		/**
		 * Состояние каждого клиента на сетевом уровне. Может меняться и в зависимости от сообщений клиента и от приказа сервера
		 * Каждый клиент представляет собой конечный автомат и может меняться только определенным образом.
		 */
		Map<Integer, State> states;



		public Messenger()
		{

		}

		public void handleMessage(Void SelectionKey key, byte[] array)
		{

		}
	}

}
