package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.EntityComponent;

/**
 * @author Roman K.
 */
public class HealthComponent extends EntityComponent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(HealthComponent.class);
	private double oldHealth;
	private double health;

	@Override
	public String getFamilyId()
	{
		return "Health";
	}

	@Override
	public String getComponentId()
	{
		return "CommonHealth";
	}

	@Override
	public void update(double dt)
	{

	}

	public void setHealth(double health)
	{
		this.health = health;
	}

	public double getHealth()
	{
		return health;
	}
}
