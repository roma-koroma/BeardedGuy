package quest.client.network;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.controller.GameController;
import quest.protocol.CommonMessages;
import quest.protocol.GameServerMessage;

/**
 * User: Roman Koretskiy
 * Date: 01.04.13
 * Time: 23:43
 */
public class DeltaHandler implements Handler
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(DeltaHandler.class);
	private GameController gameController;

	@Override
	public void handle(ByteString message)
	{
		GameServerMessage.GameStateOperation operation = parse(message);

		for(CommonMessages.User user : operation.getUserList())
		{
			gameController.updateGuyByPB(user);
		}
	}

	private GameServerMessage.GameStateOperation parse(ByteString message)
	{
		try
		{
			return GameServerMessage.GameStateOperation.parseFrom(message);
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
