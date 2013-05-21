package quest.server.component.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.common.model.Point;
import quest.server.QuadTree;
import quest.server.component.Entity;
import quest.server.component.EntityComponent;
import quest.server.component.EntityManager;
import quest.server.component.impl.CollisionComponent;
import quest.server.component.impl.ProjectionComponent;

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

	private QuadTree gameMap;

	public SimulationSystem(EntityManager entityManager, QuadTree gameMap)
	{
		super(entityManager);
		this.gameMap = gameMap;

	}

	@Override
	public void update(double dt)
	{
		//обновляем движения
		moveAll(dt);

		//атаки
		updateAll("attack", dt);

		//каст
		updateAll("cast", dt);

		//проверяем и решаем коллизии.
		resolveCollisions(dt);

	}

	private void moveAll(double dt)
	{
		List<EntityComponent> entities = getEntityManager().getEntitiesByComponentId("move");

		if (entities != null)
		{
			for (EntityComponent entity : entities)
			{
				//вычисляем новые позиции
				entity.update(dt);

				//обновляем их на карте
				ProjectionComponent projection = (ProjectionComponent) entity.getOwner().getComponent("projection");
				gameMap.updatePoint(
					projection.getOwner().getId(),
					projection.getPreviousPosition(),
					projection.getNewPosition()
					);
			}
		}
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

				//получить все ближайшие сущности.
				List<Long> items = gameMap.retrieve(
					new QuadTree.AABB(
						proj.getNewPosition(), new Point(proj.getRadius(), proj.getRadius()))
				);

				for (Long item : items)
				{
					Entity collided = getEntityManager().getEntityById(item);
					collision.collide(collided);
					((CollisionComponent)collided.getComponent("collision")).collide(entity.getOwner());
				}
				entity.update(dt);
			}
		}
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
