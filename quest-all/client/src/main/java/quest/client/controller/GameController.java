package quest.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.CommonMessages;

import java.util.HashMap;
import java.util.Map;

import static quest.client.util.SerializationUtil.deserializeGuy;

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
	private BeardedGuy mainGuy;

	public GameController()
	{
		this.guys = new HashMap<Integer, BeardedGuy>();
	}

	/**
	 * Обновить положение чувака по сообщению PB.
	 * На данный момент мужик:
	 * — удаляется,
	 * — обновляются координаты,
	 * — создается новый.
	 * @param user
	 */
	public void updateGuyByPB(CommonMessages.User user)
	{
		BeardedGuy guy = user.getId() == mainGuy.getId() ? mainGuy : this.guys.get(user.getId());

		if (!user.getIsOnline())
		{
			BeardedGuy removedGuy = guys.remove(user.getId());
			logger.info("Remove guy {}", removedGuy.getName());
			return;
		}

		if(guy != null)
		{
			logger.info("{} move from [{},{}] to [{},{}]",
				new Object[]{
					isMain(guy) ? "I" : guy.getName(),
					guy.getPosition().getX(),
					guy.getPosition().getY(),
					user.getPosition().getX(),
					user.getPosition().getY()
				});
			guy.setPosition(new Point(user.getPosition().getX(), user.getPosition().getY()));
		}
		else
		{
			newGuy(deserializeGuy(user));
		}
	}

	private boolean isMain(BeardedGuy guy)
	{

		return mainGuy != null && (guy.getId() == mainGuy.getId());
	}

	public void newGuy(BeardedGuy guy)
	{
		this.guys.put(guy.getId(), guy);
	}

	public BeardedGuy getMainGuy()
	{
		return mainGuy;
	}

	public void setMainGuy(BeardedGuy mainGuy)
	{
		this.mainGuy = mainGuy;
	}
}
