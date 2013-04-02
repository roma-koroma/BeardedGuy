package quest.server.network;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.protocol.ClientMessage;
import quest.server.GameController;

import static quest.server.util.SerializationUtil.serializeGuy;

/**
 * @author Roman K.
 */
public class InputHandler implements Handler
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(InputHandler.class);

	private GameController gameController;

	@Override
	public void handle(int clientId, ByteString bodyMessage, Post post)
	{
		ClientMessage.InputOperation op = parseMessage(bodyMessage);

		if(op.getInputCount() > 0)
		{
			//Сейчас обрабатывается только первая кнопка
			BeardedGuy guy = gameController.moveById(clientId, op.getInput(0));

			if( guy != null)
				post.broadcast(serializeGuy(guy));
		}

	}

	private ClientMessage.InputOperation parseMessage(ByteString message)
	{
		try
		{
			return ClientMessage.InputOperation.parseFrom(message);
		}
		catch (InvalidProtocolBufferException e)
		{
			throw new RuntimeException();
		}
	}

	public void setGameController(GameController gameController)
	{
		this.gameController = gameController;
	}
}
