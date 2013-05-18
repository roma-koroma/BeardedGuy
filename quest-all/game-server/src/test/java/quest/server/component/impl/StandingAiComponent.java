package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.ComponentEvent;

import java.util.List;

/**
 * @author Roman K.
 */
public class StandingAiComponent extends AIComponent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(StandingAiComponent.class);

	@Override
	public String getComponentId()
	{
		return "standingAi";
	}

	@Override
	public void update(double dt)
	{
		MoveComponent moveC = (MoveComponent) getOwner().getComponent("move");
		if(moveC != null)
		{
			moveC.stop();
		}
	}

	@Override
	public void onEvent(String eventType, ComponentEvent event){}

	@Override
	public List<String> getListenedEvents()
	{
		return null;
	}
}
