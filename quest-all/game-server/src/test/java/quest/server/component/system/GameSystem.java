package quest.server.component.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.EntityManager;

/**
 * @author Roman K.
 */
abstract public class GameSystem
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(GameSystem.class);

	private final EntityManager entityManager;

	abstract public void update(double dt);

	public GameSystem(EntityManager gameObjectManager)
	{
		this.entityManager = gameObjectManager;
	}

	public EntityManager getEntityManager()
	{
		return entityManager;
	}


}
