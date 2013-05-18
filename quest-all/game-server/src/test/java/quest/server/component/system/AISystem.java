package quest.server.component.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.EntityManager;
import quest.server.component.EntityComponent;

import java.util.List;

/**
 * @author Roman K.
 */
public class AISystem extends GameSystem
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(AISystem.class);

	public AISystem(EntityManager entityManager)
	{
		super(entityManager);
	}

	@Override
	public void update(double dt)
	{
		updateAll("ai", dt);
	}

	private void updateAll(String id, double dt)
	{
		List<EntityComponent> entities = getEntityManager().getEntitiesByComponentId(id);

		if (entities != null)
		{
			for (EntityComponent entity : entities)
			{
				entity.update(dt);
			}
		}
	}

}
