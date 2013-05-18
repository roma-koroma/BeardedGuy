package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.Entity;
import quest.server.component.EntityComponent;
import quest.server.model.Room;

import java.util.Arrays;
import java.util.List;

/**
 * Компонент проверки на коллизию сущности—владельца и каких-нибудь других сущностей.
 * Проверяет пересечение и отправляет сообщение, в случае успеха. Больше никаких функций не несет.
 * @author Roman K.
 */
public class CollisionComponent extends EntityComponent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(CollisionComponent.class);

	private Room map;
	private Entity target;

	@Override
	public String getFamilyId()
	{
		return "collision";
	}

	@Override
	public String getComponentId()
	{
		return "default_collision";
	}

//	public List<String> systemDependecies()
//	{
//		return Arrays.asList("gameMap");
//	}

	@Override
	public void update(double dt)
	{
		if(target != null)
		{
			getOwner().fireEvent("collision", new CollisionEvent(target));
			target = null;
		}

	}

	public void collide(Entity entity)
	{
		this.target = entity;
	}

}
