package quest.server.component;

import java.util.ArrayList;
import java.util.List;

abstract public class EntityComponent
{

	private Entity owner;

	public abstract String getFamilyId();
	public abstract String getComponentId();
	public abstract void update(double dt);

	public Entity getOwner()
	{
		return owner;
	}

	public void setOwner(Entity owner)
	{
		this.owner = owner;
	}

	public void onEvent(String eventType, ComponentEvent event)
	{

	}

	public List<String> getListenedEvents()
	{
		return new ArrayList<String>();
	}
}
