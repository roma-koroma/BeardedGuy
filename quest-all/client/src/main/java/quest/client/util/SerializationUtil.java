package quest.client.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.Common;

/**
 * @author Roman K.
 */
public class SerializationUtil
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(SerializationUtil.class);

	public static Common.Character serializeGuy(BeardedGuy guy)
	{
		return Common.Character.newBuilder()
			.setId(guy.getId())
			.setName(guy.getName())
			.setPosition(serializePoint(guy.getPosition()))
			.setHealth(guy.getHealth())

			.build();
	}

	public static BeardedGuy deserializeGuy(Common.Character c)
	{
		BeardedGuy guy = new BeardedGuy();
		guy.setIsOnline(true);
		guy.setId(c.getId());
		guy.setName(c.getName());
		guy.setPosition(deserializePoint(c.getPosition()));
		guy.setHealth(c.getHealth());

		return guy;
	}

	private static Point deserializePoint(Common.Point position)
	{
		return new Point(position.getX(), position.getY());
	}

	public static Common.Point serializePoint(Point position)
	{
		return Common.Point.newBuilder()
			.setX(position.getX())
			.setY(position.getY()).build();
	}

	public static Common.Action serializeAction(Common.Action.Type actionType, ByteString actionString)
	{
		return Common.Action.newBuilder()
			.setType(actionType)
			.setMessage(actionString)
			.build();
	}

	public static Common.Action serializeAction(Common.Action.Type actionType, Message message)
	{
		return serializeAction(actionType, message.toByteString());
	}

}
