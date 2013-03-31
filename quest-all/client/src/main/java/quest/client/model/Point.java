package quest.client.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roman Koretskiy
 */
public class Point
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(Point.class);

    private int x, y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }
}
