package quest.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Direction;
import quest.client.model.Point;
import quest.protocol.ClientMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman K.
 */
public class GameController
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(GameController.class);

	private Map<Integer , BeardedGuy> idToGuy;

	public GameController()
	{
		this.idToGuy = new HashMap<Integer, BeardedGuy>();
	}

	/**
	 * На самом деле он не просто возвращает парня, а создает нового
	 * @param name
	 * @return
	 */
	public BeardedGuy getGuyByLogin(String name)
	{
		BeardedGuy guy = new BeardedGuy(name , new Point(0, 0));

		//выставляем id
		guy.setId((int) System.currentTimeMillis() % Integer.MAX_VALUE);
		idToGuy.put(guy.getId(), guy);

		return guy;
	}


	public BeardedGuy moveById(int clientId, ClientMessage.InputOperation.Input input)
	{
		BeardedGuy guy = idToGuy.get(clientId);
		if(guy != null)
		{
			move(guy, input);
		}

		return guy;
	}

	private void move(BeardedGuy guy, ClientMessage.InputOperation.Input input)
	{
		int oldX = guy.getPosition().getX();
		int oldY = guy.getPosition().getY();

		switch (input)
		{
			case DOWN_KEY:
				move(guy, -1, 0);
				break;
			case UP_KEY:
				move(guy, 1, 0);
				break;
			case LEFT_KEY:
				move(guy, 0, -1);
				break;
			case RIGHT_KEY:
				move(guy, 0, 1);
				break;
		}

		logger.info("\"{}\" move {} from [{},{}] to [{},{}]",
			new Object[]{
				guy.getName(),
				input.name(),
				oldX,
				oldY,
				guy.getPosition().getX(),
				guy.getPosition().getY()});

	}

	private void move(BeardedGuy guy, int x, int y)
	{
		Point pos = guy.getPosition();
		guy.setPosition(new Point(pos.getX() + x, pos.getY() + y)); ;
	}
}
