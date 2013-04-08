package quest.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.CommonMessages;

/**
 * @author Roman K.
 */
public class SerializationUtil
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(SerializationUtil.class);

	public static CommonMessages.User serializeGuy(BeardedGuy guy)
	{
		return CommonMessages.User.newBuilder()
			.setId(guy.getId())
			.setIsOnline(guy.isOnline())
			.setName(guy.getName())
			.setPosition(serializePoint(guy.getPosition()))
			.build();
	}

	public static BeardedGuy deserializeGuy(CommonMessages.User user)
	{
		BeardedGuy guy = new BeardedGuy();
		guy.setIsOnline(user.getIsOnline());
		guy.setId(user.getId());
		guy.setName(user.getName());
		guy.setPosition(deserializePoint(user.getPosition()));
		return guy;
	}

	private static Point deserializePoint(CommonMessages.Point position)
	{
		return new Point(position.getX(), position.getY());
	}

	public static CommonMessages.Point serializePoint(Point position)
	{
		return CommonMessages.Point.newBuilder()
			.setX(position.getX())
			.setY(position.getY()).build();
	}
}
