package quest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.CommonMessages;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Roman Koretskiy
 * Date: 01.04.13
 * Time: 23:54
 */
public class GameController
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(GameController.class);

	Map<Long, BeardedGuy> guys;

	public GameController()
	{
		this.guys = new HashMap<Long, BeardedGuy>();
	}

	/**
	 * Обновить положение чувака по сообщению PB. На данный момент мужик либо удаляется, либо у него обновляются координаты.
	 * @param user
	 */
	public void updateGuyByPB(CommonMessages.User user)
	{
		if (!user.getIsOnline())
		{
			BeardedGuy guy = guys.remove(user.getId());
			logger.info("Remove guy {}", guy.getName());
			return;
		}

		BeardedGuy guy = this.guys.get(user.getId());
		if(guy != null)
		{
			logger.info("Move {} from [{},{}] to [{},{}]",
				new Object[]{
					guy.getName(),
					guy.getPosition().getX(),
					guy.getPosition().getY(),
					user.getPosition().getX(),
					user.getPosition().getY()
				});
			CommonMessages.Point newPosition = user.getPosition();
			guy.setPosition(new Point(user.getPosition().getX(), user.getPosition().getY()));
		}
	}
}
