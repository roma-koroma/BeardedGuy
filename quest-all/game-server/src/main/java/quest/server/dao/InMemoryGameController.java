package quest.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.protocol.ClientMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация контроллера, который все держит в памяти
 * @author Roman K.
 */
public class InMemoryGameController implements GameController
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(InMemoryGameController.class);

	private Map<Integer , BeardedGuy> idToGuy;

	private Map<String, Integer> authToId;

	public InMemoryGameController()
	{
		this.idToGuy = new HashMap<Integer, BeardedGuy>();
		this.authToId = new HashMap<String, Integer>();
	}

	/**
	 * На самом деле он не просто возвращает парня, а создает нового
	 * @param name
	 * @return
	 */
	public BeardedGuy getGuyByLogin(String name)
	{
		BeardedGuy guy = null;
		Integer id = authToId.get(name);
		if(id == null)
		{
			guy = new BeardedGuy(name, new Point(0, 0));

			guy.setIsOnline(true);
			//выставляем id
			guy.setId((int) System.currentTimeMillis() % Integer.MAX_VALUE);
			authToId.put(name, guy.getId());
			idToGuy.put(guy.getId(), guy);
		}
		else
		{
			guy = idToGuy.get(id);
			if(!guy.isOnline())
			{
				guy.setIsOnline(true);
			}
			else
			{
			    guy = null;
				logger.info("guy already login");
			}
		}

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

	public BeardedGuy close(int id)
	{
		BeardedGuy guy = idToGuy.get(id);
		if (guy != null)
		{
			guy.setIsOnline(false);
		}
		return guy;
	}

	public Collection<BeardedGuy> getFullModel()
	{
		return idToGuy.values();
	}
}
