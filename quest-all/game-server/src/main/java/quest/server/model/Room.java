package quest.server.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.common.model.Point;
import quest.protocol.Common;

import java.util.*;

/**
 * Хранит информацию обо всех существах.
 *
 * Комната хранит в себе: информацию о свое геометрии, информацию о положении существ.
 * @author Roman K.
 */
public class Room
{

	public static final double cellSize = 30;

	//количество ячеек в высоту
	private final int height;

	//количество ячеек в ширину
	private final int width;
	private final Point initialPoint;
	private String name;

	private int roomId;

	private Map<Integer, BeardedGuy> idToGuy;

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(Room.class);
	private int id;

	private Map entities;

	public Room(String name, int width, int height, Point initialPoint)
	{
		this.name = name;
		this.width = width;
		this.height = height;
		this.initialPoint = initialPoint;

		this.idToGuy = new HashMap<Integer, BeardedGuy>();
	}


	public Point getInitPoint()
	{
		return initialPoint;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void removeEntity(BeardedGuy guy)
	{
		this.idToGuy.remove(guy.getId());
	}

	public void placeGuy(BeardedGuy guy)
	{
		this.idToGuy.put(guy.getId(), guy);
	}

	private Point calcStartPoint()
	{
		if (!isOccupied(pointToCell(initialPoint)))
		{
			return initialPoint;
		}

		logger.info("Point [{},{}] is occupied. Search next.", initialPoint.getX(), initialPoint.getY());
		Deque<Point> next = new ArrayDeque<Point>();

		Point point = initialPoint;

		Set<Point> checked = new HashSet<Point>();
		checked.add(point);

		putNeighbors(next, point, checked);

		point = next.removeLast();
		while (isOccupied(point))
		{
			checked.add(point);
			putNeighbors(next, point, checked);
			point = next.removeLast();
		}

		logger.info("Find point [{},{}]", point.getX(), point.getY());

		return point;
	}

	private Cell pointToCell(Point initialPoint)
	{

		return null;
	}

	public boolean isOccupied(int x, int y)
	{
		return this.isOccupied(new Point(x, y));
	}

	public boolean isOccupied(Cell cell)
	{
		for (BeardedGuy guy : entities.values())
		{
			Point p1 = guy.getPosition();
			if (p1.equals(point))
			{
				return true;
			}
		}

		return false;
	}

	private class Cell
	{
	}
}
