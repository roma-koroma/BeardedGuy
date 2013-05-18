package quest.server.component.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.EntityManager;

/**
 * @author Roman K.
 */
public class InputSystem extends GameSystem
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(InputSystem.class);

	public InputSystem(EntityManager gameObjectManager)
	{
		super(gameObjectManager);
	}

	@Override
	public void update(double dt)
	{

	}
}
