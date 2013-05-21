package quest.server.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранит все сущности и предоставляет быстый доступ к множествам сущностей по названию компонента.
 * @author Roman K.
 */
public class EntityManager
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

	private static final long FIRST_ID = 1;

	private static long nextId = FIRST_ID;

//	public Map<Class< ? extends EntityComponent>, List<Entity>> components;
	public Map<String, List<EntityComponent>> components;

	public EntityManager()
	{
//		components = new HashMap<Class<? extends EntityComponent>, List<Entity>>();
		components = new HashMap<String, List<EntityComponent>>();
	}

	public void register(Entity entity, EntityComponent component)
	{
		entity.addComponent(component);
		component.setOwner(entity);

		List<EntityComponent> entities = components.get(component.getClass());
		if(entities == null)
		{
			entities = new ArrayList<EntityComponent>();
			components.put(component.getFamilyId(), entities);
		}
		entities.add(component);
	}

	public void remove(EntityComponent component)
	{

	}

	public static long nextId()
	{
		return nextId++;
	}

	public List<EntityComponent> getEntitiesByComponentId(String familyId)
	{
		return components.get(familyId);
	}

	public Entity getEntityById(Long entityId)
	{
		return null;
	}
}
