package quest.client.model;

/**
 * @author Roman Koretskiy
 */
public enum Direction
{
    DOWN(0, 270d),
    LEFT(1, 180d),
    RIGHT(2, 0d),
    UP(3, 90d);

	private final double angle;

	private final int code;

	Direction(int code, double angle)
    {
        this.code = code;
		this.angle = angle;
    }

	public double getAngle()
	{
		return angle;
	}

	public int getCode()
	{
		return code;
	}

}
