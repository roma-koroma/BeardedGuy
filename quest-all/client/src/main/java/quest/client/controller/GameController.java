package quest.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.common.model.Point;
import quest.protocol.Common;

import java.util.HashMap;
import java.util.Map;

import static quest.common.util.SerializationUtil.deserializeGuy;

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


	private boolean isMain(BeardedGuy guy)
	{
		return mainGuy != null && (guy.getId() == mainGuy.getId());
	}

	public BeardedGuy getMainGuy()
	{
		return mainGuy;
	}

	public void setMainGuy(BeardedGuy mainGuy)
	{
		this.mainGuy = mainGuy;
	}

	public void move(int entityId, Common.Point newPosition)
	{
		BeardedGuy guy = entityId == mainGuy.getId() ? mainGuy : this.guys.get(entityId);

		logger.info("{} move from [{},{}] to [{},{}]",
			new Object[]{
				isMain(guy) ? "I" : guy.getName(),
				guy.getPosition().getX(),
				guy.getPosition().getY(),
				newPosition.getX(),
				newPosition.getY()
			});
		guy.setPosition(new Point(newPosition.getX(), newPosition.getY()));
	}

	public void addEntity(Common.Character c, boolean isSync) throws GameControllerException
	{
		BeardedGuy newGuy = deserializeGuy(c);

		if(mainGuy == null)
		{
			throw new GameControllerException("Ошибочная синхронизация до аутентификации");
		}
		else if (!isSync && isMain(newGuy))
		{
			throw new GameControllerException("Появилась сущность с таким же идентификатором, как и у нас" + newGuy.getId());
		}

		this.guys.put(newGuy.getId(), newGuy);
	}

	public void removeEntity(Integer id)
	{
		BeardedGuy guy = this.guys.remove(id);
		logger.info("Remove guy {}", guy.getName());

	}
}
