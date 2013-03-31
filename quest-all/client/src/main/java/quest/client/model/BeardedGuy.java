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

    public void tick(RandomInputSource source)
    {

    }

    public void move(Direction direction)
    {
        int oldX = this.position.getX();
        int oldY = this.position.getY();

        switch(direction)
        {
            case DOWN:
                move(-1 ,0);
                break;
            case UP:
                move(1, 0);
                break;
            case LEFT:
                move(0, -1);
                break;
            case RIGHT:
                move(0, 1);
                break;
        }

        logger.info("\"{}\" move {} from [{},{}] to [{},{}]",
            new Object[]{ this.name, direction.name() , oldX, oldY, position.getX(), position.getY() });
    }

    private void move(int x, int y)
    {
        this.position = new Point(position.getX() + x, position.getY() + y);
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
}
