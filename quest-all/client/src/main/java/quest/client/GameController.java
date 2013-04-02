package quest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.CommonMessages;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Koretskiy
 */
public class GameController
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(GameController.class);

	Map<Integer, BeardedGuy> guys;

	public GameController()
	{
		this.guys = new HashMap<Integer, BeardedGuy>();
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

	public void newGuy(BeardedGuy guy)
	{
		this.guys.put(guy.getId(), guy);
	}
}
