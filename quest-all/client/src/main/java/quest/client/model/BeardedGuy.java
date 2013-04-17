package quest.client.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Player
 * @author Roman Koretskiy
 */
public class BeardedGuy
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(BeardedGuy.class);
    private Point position;

    private String name;

    private static final String DEFAULT_NAME = "Unknown guy";
	private int id;
	private boolean online;
	private boolean isOnline;
	private int health;

	public BeardedGuy(String name, Point position)
    {
        this.name = name;
        this.position = position;
    }

    public BeardedGuy()
    {
        this(DEFAULT_NAME, new Point(0,0));
    }

    public BeardedGuy(String name)
    {
        this(name, new Point(0,0));
    }

    public void setPosition(Point position)
    {
        this.position = position;
    }

    public Point getPosition()
    {
        return position;
    }

    public void setName(String name)
    {
        this.name = name;
    }

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public boolean isOnline()
	{
		return isOnline;
	}

	public void setIsOnline(boolean online)
	{
		isOnline = online;
	}

	public void setHealth(int health)
	{
		this.health = health;
	}

	public int getHealth()
	{
		return health;
	}
}
