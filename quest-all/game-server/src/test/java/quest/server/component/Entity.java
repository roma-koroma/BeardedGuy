package quest.server.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity
{
	Map<String, EntityComponent> components;
	private long id;

	Map<String, List<EntityComponent>> listeners;

	public Entity(long id)
	{
		this.id = id;
		components = new HashMap<String, EntityComponent>();
		this.listeners = new HashMap<String, List<EntityComponent>>();
	}

	public void addComponent(EntityComponent component)
	{
		components.put(component.getFamilyId(), component);
		List<String> listenedEvents = component.getListenedEvents();

		if( listenedEvents != null && !listenedEvents.isEmpty())
		{
			for(String event : listenedEvents)
			{
				addListener(event, component);
			}
		}
	}

	private void addListener(String event, EntityComponent component)
	{
		List<EntityComponent> objects = this.listeners.get(event);
		if(objects == null)
		{
			objects = new ArrayList<EntityComponent>();
			listeners.put(event, objects);
		}
		objects.add(component);
	}

	public void fireEvent(String eventType, ComponentEvent event)
	{
		List<EntityComponent> handlers = listeners.get(eventType);
		if(handlers != null && !handlers.isEmpty())
		{
			for (EntityComponent handler : handlers)
			{
				handler.onEvent(eventType, event);
			}
		}
	}

	public EntityComponent getComponent(String familyId)
	{
		return components.get(familyId);
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public long getId()
	{
		return id;
	}
}
