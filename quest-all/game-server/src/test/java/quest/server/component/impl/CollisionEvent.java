package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.ComponentEvent;
import quest.server.component.Entity;

/**
 * @author Roman K.
 */
public class CollisionEvent extends ComponentEvent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(CollisionEvent.class);

	private final Entity target;

	public CollisionEvent(Entity target)
	{
		this.target = target;
	}

	public Entity getTarget()
	{
		return target;
	}
}
