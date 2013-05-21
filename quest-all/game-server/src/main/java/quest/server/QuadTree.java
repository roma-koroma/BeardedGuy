package quest.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.common.model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Quad tree with limited nest.
 *
 * @author Roman K.
 */
public class QuadTree
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(QuadTree.class);

	public static final int DEFAULT_CAPACITY = 4;

	/**
	 * Вместимость определяется одна на все узлы
	 */
	private final int capacity;

	private final Node root;

	public QuadTree(AABB boundary, int maxDepth ,int capacity)
	{
		root = new Node(boundary, maxDepth);
		this.capacity = capacity;
	}

	public void updatePoint(long id, Point oldPosition, Point newPosition)
	{
		//TODO обновление точки
		//find node
		//if new position in the same node — just update position
		//else delete and check node
		//insert new
	}

	public Node findNode(Item item)
	{
		return null;
	}

	public boolean insert(Item item)
	{
		return root.insert(item);
	}

	public boolean insert(long id, Point point)
	{
		return insert(new Item(id, point));
	}

	public List<Long> retrieve(AABB range)
	{
		return root.retrieve(range);
	}


	public enum Direction
	{
		LEFT_TOP(0),
		RIGHT_TOP(1),
		LEFT_BOTTOM(2),
		RIGHT_BOTTOM(3),
		;
		private final int index;


		Direction(int i)
		{
			this.index = i;
		}
	}

	public static class AABB
	{
		/**
		 * Координаты центра
		 */
		private Point center;

		/**
		 * Радиусы по каждой из осей
		 */
		private Point halfDimension;

		public AABB(Point center, Point halfDimension)
		{
			this.center = center;
			this.halfDimension = halfDimension;
		}

		public Point getCenter()
		{
			return center;
		}

		public Point getHalfDimension()
		{
			return halfDimension;
		}

		/**
		 *
		 * @param point
		 * @return
		 */
		public boolean containsPoint(Point point)
		{
			return
				(
					(Math.abs(point.getX() - center.getX()) < halfDimension.getX())
						&&
					(Math.abs(point.getY() - center.getY()) < halfDimension.getY())
				);
		}

		/**
		 *
		 * @param other
		 * @return
		 */
		public boolean isIntersect(AABB other)
		{
			//если расстояние между центрами больше чем сумма радиусов, то пересечения нет.
			if (Math.abs(this.center.getX() - other.center.getX()) > (this.halfDimension.getX() + other.halfDimension.getX()))
				return false;

			if (Math.abs(this.center.getY() - other.center.getY()) > (this.halfDimension.getY() + other.halfDimension.getY()))
				return false;

			return true;
		}

	}

	public class Item
	{
		private long id;

		private Point point;

		private Item(long id, Point point)
		{
			this.id = id;
			this.point = point;
		}

		private long getId()
		{
			return id;
		}

		private Point getPoint()
		{
			return point;
		}
	}

	private class Node
	{
		private final AABB boundary;

		/**
		 * Максимальная вложенность этого узла
		 */
		private final int maxDepth;

		private List<Item> items;

		private Node[] nodes;

		private Node(AABB boundary, int maxDepth)
		{
			this.boundary = boundary;
			this.maxDepth = maxDepth;
			//по умолчанию, все узлы — это листья и хранят только массив точек
			this.items = new ArrayList<Item>(capacity);

		}

		public boolean insert(Item item)
		{

			if (!boundary.containsPoint(item.point))
				return false;

			if (this.items == null)
			{
				return insertInNode(item);
			}

			if (items.size() < capacity)
			{
				items.add(item);
				return true;
			}

			if (nodes == null && maxDepth > 0)
			{
				subdivide();

				//находим для старых точек новые узлы
				for (Item oldItem : items)
				{
					insertInNode(oldItem);
				}

				items = null;

				return insertInNode(item);
			}

			return false;
		}

		private boolean insertInNode(Item item)
		{
			for (Node node : nodes)
			{
				if (node.insert(item))
					return true;
			}

			return false;
		}

		public void subdivide()
		{
			nodes = new Node[4];

			Point newDimension = new Point(
				boundary.getHalfDimension().getX() / 2,
				boundary.getHalfDimension().getY() / 2
			);

			nodes[Direction.LEFT_TOP.index] =
				new Node(
					new AABB(
						new Point(
							boundary.getCenter().getX() - newDimension.getX(),
							boundary.getCenter().getY() - newDimension.getY()
						),
						newDimension)
					,
					maxDepth - 1

				);
			nodes[Direction.RIGHT_TOP.index] =
				new Node(
					new AABB(
						new Point(
							boundary.getCenter().getX() + newDimension.getX(),
							boundary.getCenter().getY() - newDimension.getY()
						),
						newDimension),
					maxDepth - 1
				);
			nodes[Direction.LEFT_BOTTOM.index] =
				new Node(
					new AABB(
						new Point(
							boundary.getCenter().getX() - newDimension.getX(),
							boundary.getCenter().getY() + newDimension.getY()
						),
						newDimension),
					maxDepth - 1
				);
			nodes[Direction.RIGHT_BOTTOM.index] =
				new Node(
					new AABB(
						new Point(
							boundary.getCenter().getX() + newDimension.getX(),
							boundary.getCenter().getY() + newDimension.getY()
						),
						newDimension),
					maxDepth - 1
				);

		}

		//TODO возвращать как минимум пары — (идентификатор, точка)
		public List<Long> retrieve(AABB range)
		{

			List<Long> ret = new ArrayList<Long>();

			//TODO check intersection
			if (!range.isIntersect(boundary))
			{
				return ret;
			}

			if (nodes != null)
			{
				for (Node node : nodes)
				{
					ret.addAll(node.retrieve(range));
				}
				//todo return node's items
			}

			if (items != null)
			{
				for (Item item : items)
				{
					if (range.containsPoint(item.getPoint()))
						ret.add(item.getId());
				}
			}

			return ret;
		}
	}


}
