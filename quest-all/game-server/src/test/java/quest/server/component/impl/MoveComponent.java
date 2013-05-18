package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.EntityComponent;

/**
 * @author Roman K.
 */
public class MoveComponent extends EntityComponent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(MoveComponent.class);

	private double velocity = 0;

	private double maxVelocity;

	private double direction = 0;

	public void setMaxVelocity(double maxVelocity)
	{
		this.maxVelocity = maxVelocity;
	}

	public double getMaxVelocity()
	{
		return maxVelocity;
	}

	public double getVelocity()
	{
		return velocity;
	}

	public void setVelocity(double velocity)
	{
		this.velocity = velocity;
	}

	public double getDirection()
	{
		return direction;
	}

	public void setDirection(double direction)
	{
		this.direction = direction;
	}

	@Override
	public String getFamilyId()
	{
		return "Velocity";
	}

	@Override
	public String getComponentId()
	{
		return "CommonVelocity";
	}

	@Override
	public void update(double dt)
	{
		//подвигаем
		//проверяем на коллизии.
	}

	public void move(double direction, double velocity)
	{
		this.direction = direction;
		this.velocity = velocity;
	}

	public void stop()
	{
		this.velocity = 0;
	}
}
