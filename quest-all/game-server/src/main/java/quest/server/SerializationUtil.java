package quest.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.GameServer;

import static quest.client.util.SerializationUtil.serializeGuy;
import static quest.client.util.SerializationUtil.serializePoint;

/**
 * @author Roman K.
 */
public class SerializationUtil
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(SerializationUtil.class);

	public static GameServer.DeltaState.Move serializeMove(Integer id, Point point)
	{
		return GameServer.DeltaState.Move.newBuilder()
			.setEntityId(id)
			.setNewPosition(serializePoint(point))
			.build();
	}

	public static GameServer.DeltaState.Enter serializeEnter(BeardedGuy guy)
	{
		return GameServer.DeltaState.Enter.newBuilder()
			.addEntity(serializeGuy(guy))
			.build();
	}

	public static GameServer.DeltaState.Exit serializeExit(int id)
	{
		return GameServer.DeltaState.Exit.newBuilder()
			.addEntityId(id)
			.build();
	}


}
