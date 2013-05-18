package quest.server.component.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.EntityComponent;
import quest.server.component.EntityManager;
import quest.server.component.impl.CollisionComponent;
import quest.server.component.impl.ProjectionComponent;
import quest.server.model.GameMap;

import java.util.List;

/**
 * @author Roman K.
 */
public class SimulationSystem extends GameSystem
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(SimulationSystem.class);

	private GameMap gameMap;

	public SimulationSystem(EntityManager entityManager)
	{
		super(entityManager);
	}

	@Override
	public void update(double dt)
	{
		//обновляем движения
		updateAll("move", dt);

		//атаки
		updateAll("attack", dt);

		//каст
		updateAll("cast", dt);

		//проверяем на потенциальные коллизии.
		resolveCollisions(dt);

	}

	private void resolveCollisions(double dt)
	{
		List<EntityComponent> entities = getEntityManager().getEntitiesByComponentId("collision");

		if (entities != null)
		{
			for (EntityComponent entity : entities)
			{
				CollisionComponent collision = (CollisionComponent) entity;
				ProjectionComponent proj = (ProjectionComponent) collision.getOwner().getComponent("projection");

				//получить всех ближайшией сущности.
				gameMap.isCollide();

				//проверить каждую
				entity.update(dt);
			}
		}
	}

	private void updateAll(String id, double dt)
	{
		List<EntityComponent> entities = getEntityManager().getEntitiesByComponentId(id);

		if(entities != null)
		{
			for (EntityComponent entity : entities)
			{
				entity.update(dt);
			}
		}
	}
}
