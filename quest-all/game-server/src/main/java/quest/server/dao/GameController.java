package quest.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.common.model.*;
import quest.protocol.Client;
import quest.server.model.Action;
import quest.server.model.BeardedGuy;
import quest.server.model.Room;

import java.util.*;

/**
 * Реализация контроллера, который все держит в памяти
 * @author Roman K.
 */
public class GameController
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(GameController.class);


//	private Map<BeardedGuy, Room> guyToRoom;

	private PlayerStorage playerStorage;
	private Room room;

	public GameController()
	{
//		this.guyToRoom = new HashMap<BeardedGuy,  Room>();
	}

	/**
	 * На самом деле он не просто возвращает парня, а создает нового
	 * @param name
	 * @return
	 */
	public BeardedGuy getGuyByLogin(String name)
	{
		name = name.toLowerCase();
		BeardedGuy guy = playerStorage.fingGuyByLogin(name);
		if(guy == null)
		{

			String newName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			guy = new BeardedGuy(newName);
			guy.setIsOnline(true);
			//выставляем id
			guy.setId((int) System.currentTimeMillis() % Integer.MAX_VALUE);

			placeInRoom(guy, room);
			playerStorage.saveGuy(guy);
		}
		else
		{
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

	private void placeInRoom(BeardedGuy guy, Room room)
	{
		guy.getRoom().removeEntity(guy);
		guy.setRoom(room);
		room.placeGuy(guy);
		guy.setPosition(room.calcStartPoint());
	}

	private void putNeighbors(Deque<Point> next, Point point, Set<Point> checked)
	{
		putIfNotChecked(new Point(point.getX() + 1, point.getY()), next, checked);
		putIfNotChecked(new Point(point.getX() - 1, point.getY()), next, checked);
		putIfNotChecked(new Point(point.getX(), point.getY() + 1), next, checked);
		putIfNotChecked(new Point(point.getX(), point.getY() - 1), next, checked);
	}

	private void putIfNotChecked(Point point, Deque<Point> next, Set<Point> checked)
	{
		if(!checked.contains(point))
		{
			next.addFirst(point);
		}
	}


	public Point move(Integer id, Client.Operation.Move.Direction direction)
	{
		BeardedGuy guy = playerStorage.getById(id);
		if (guy != null)
		{
			move(guy, direction);
		}

		return guy.getPosition();
	}


	private void move(BeardedGuy guy, Client.Operation.Move.Direction direction)
	{
		float oldX = guy.getPosition().getX();
		float oldY = guy.getPosition().getY();

		switch (direction)
		{
			case DOWN:
				move(guy, 0, -1);
				break;
			case UP:
				move(guy, 0, 1);
				break;
			case LEFT:
				move(guy, -1, 0);
				break;
			case RIGHT:
				move(guy, 1, 0);
				break;
		}

		logger.info("\"{}\" move {} from [{},{}] to [{},{}]",
			new Object[]{
				guy.getName(),
				direction.name(),
				oldX,
				oldY,
				guy.getPosition().getX(),
				guy.getPosition().getY()});

	}

	private void move(BeardedGuy movedGuy, int x, int y)
	{
		Point pos = movedGuy.getPosition();
		Point newPos = new Point(pos.getX() + x, pos.getY() + y);

		if(isOccupied(newPos))
		{
			logger.info("Can't move. Point [{},{}] already taken ",
				new Object[]{newPos.getX(), newPos.getY()});
			return;
		}
		movedGuy.setPosition(newPos);
	}

	public BeardedGuy close(int id)
	{
		BeardedGuy guy = playerStorage.getById(id);
		if (guy != null)
		{
			guy.setIsOnline(false);
		}
		return guy;
	}


	public void setPlayerStorage(PlayerStorage playerStorage)
	{
		this.playerStorage = playerStorage;
	}

	public void setRoom(Room room)
	{
		this.room = room;
	}

	/**
	 * Обновить состояние комнаты на n мс.
	 * @param time время в миллисекундах
	 * @return
	 */
	public List<Action> updateRoom(long time)
	{
		return null;
	}
}
