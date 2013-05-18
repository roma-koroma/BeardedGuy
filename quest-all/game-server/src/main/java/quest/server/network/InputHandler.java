package quest.server.network;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.Point;
import quest.protocol.Client;
import quest.protocol.Common;
import quest.server.dao.GameController;

import static quest.common.util.SerializationUtil.serializeAction;
import static quest.server.SerializationUtil.serializeMove;

/**
 * @author Roman K.
 */
public class InputHandler
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(InputHandler.class);

	private GameController gameController;

	public void handle(Integer id, Client.Operation op, Post post)
	{

		for (Common.Action action : op.getActionList())
		{
			switch (action.getType())
			{

				case MOVE:
					Client.Operation.Move moveOp = parseMoveMessage(action.getMessage());
					Point point = gameController.move(id, moveOp.getDirection());
					post.broadcast(serializeAction(Common.Action.Type.MOVE, serializeMove(id, point).toByteString()));

					break;
				case ATTACK:
					break;
				case CAST:
					break;
				case ENTER:
					break;
				case EXIT:
					break;
			}
		}
	}

	private Client.Operation.Move parseMoveMessage(ByteString message)
	{
		try
		{
			return Client.Operation.Move.parseFrom(message);
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
