package quest.client.model;

/**
 * @author Roman Koretskiy
 */
public enum Direction
{
    DOWN(0),
    LEFT(1),
    RIGHT(2),
    UP(3);

    private int code;

    Direction(int code)
    {
        this.code = code;
    }
}
