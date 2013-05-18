package quest.client.network;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.controller.GameController;
import quest.client.controller.GameControllerException;
import quest.protocol.Common;
import quest.protocol.GameServer;

/**
 * @author Roman Koretskiy
 */
public class DeltaHandler implements Handler<GameServer.DeltaState>
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(DeltaHandler.class);
	private GameController gameController;

	@Override
	public void handle(GameServer.DeltaState message)
	{
		for(Common.Action action : message.getActionList())
		{
			switch (action.getType())
			{
				case MOVE:
					GameServer.DeltaState.Move moveOp = parseMoveMessage(action.getMessage());
					gameController.move(moveOp.getEntityId(), moveOp.getNewPosition());
					break;

				case ATTACK:
					break;
				case CAST:
					break;
				case ENTER:
					GameServer.DeltaState.Enter enterOp = parseEnterMessage(action.getMessage());
					for (Common.Character c : enterOp.getEntityList())
					{
						try
						{
							logger.info("Add char {}:{} in point [{},{}]",
								new Object[]{c.getId(), c.getName(), c.getPosition().getX(), c.getPosition().getY()});

							gameController.addEntity(c, false);
						}
						catch (GameControllerException e)
						{
							logger.error("",e);
							System.exit(1);
						}
					}

					break;
				case EXIT:
					GameServer.DeltaState.Exit exitOp = parseExitMessage(action.getMessage());
					for (Integer character : exitOp.getEntityIdList())
					{
						gameController.removeEntity(character);
					}
					break;
			}
		}
	}

	private GameServer.DeltaState.Exit parseExitMessage(ByteString message)
	{
		try
		{
			return GameServer.DeltaState.Exit.parseFrom(message);
		}
		catch (InvalidProtocolBufferException e)
		{
			throw new RuntimeException();
		}	}

	private GameServer.DeltaState.Enter parseEnterMessage(ByteString message)
	{
		try
		{
			return GameServer.DeltaState.Enter.parseFrom(message);
		}
		catch (InvalidProtocolBufferException e)
		{
			throw new RuntimeException();
		}	}

	private GameServer.DeltaState.Move parseMoveMessage(ByteString message)
	{
		try
		{
			return GameServer.DeltaState.Move.parseFrom(message);
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
