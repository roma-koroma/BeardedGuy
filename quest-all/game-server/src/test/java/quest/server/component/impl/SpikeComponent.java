package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.ComponentEvent;
import quest.server.component.Entity;
import quest.server.component.EntityComponent;

import java.util.Arrays;
import java.util.List;

/**
 * Шипы. Если какой-то
 * @author Roman K.
 */
public class SpikeComponent extends EntityComponent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(SpikeComponent.class);

	private double damage = 0;

	@Override
	public String getFamilyId()
	{
		return null;
	}

	@Override
	public String getComponentId()
	{
		return null;
	}

	@Override
	public void update(double dt)
	{

	}

	@Override
	public void onEvent(String eventType, ComponentEvent event)
	{
		super.onEvent(eventType, event);

		if(eventType.equals("collision"))
		{
			CollisionEvent cEvent = (CollisionEvent) event;

			Entity reason = cEvent.getTarget();

			HealthComponent health = (HealthComponent) reason.getComponent("health");
			health.setHealth(health.getHealth() - getDamage());
		}
	}

	@Override
	public List<String> getListenedEvents()
	{
		return Arrays.asList("collision");
	}

	public double getDamage()
	{
		return damage;
	}

	public void setDamage(double damage)
	{
		this.damage = damage;
	}
}
