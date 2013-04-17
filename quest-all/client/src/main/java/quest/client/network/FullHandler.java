package quest.client.network;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.controller.GameController;
import quest.client.controller.GameControllerException;
import quest.protocol.Common;
import quest.protocol.GameServer;

/**
 * @author Roman K.
 */
public class FullHandler implements Handler<GameServer.FullState>
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(FullHandler.class);
	private GameController gameController;

	public void setGameController(GameController gameController)
	{
		this.gameController = gameController;
	}

	@Override
	public void handle(GameServer.FullState message)
	{
		for (Common.Character c : message.getUserList())
		{
			try
			{
				gameController.addEntity(c);
			}
			catch (GameControllerException e)
			{
				logger.error("",e);
				System.exit(1);
			}
		}
	}
}
