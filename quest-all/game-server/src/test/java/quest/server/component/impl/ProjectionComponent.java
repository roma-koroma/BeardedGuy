package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.common.model.Point;
import quest.server.component.EntityComponent;

/**
 * @author Roman K.
 */
public class ProjectionComponent extends EntityComponent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProjectionComponent.class);

	private Point position;
	private double radius;


	@Override
	public String getFamilyId()
	{
		return "position";
	}

	@Override
	public String getComponentId()
	{
		return "position";
	}

	@Override
	public void update(double dt)
	{

	}

	public void setPosition(Point position)
	{
		this.position = position;
	}

	public Point getPosition()
	{
		return position;
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	public double getRadius()
	{
		return radius;
	}
}
