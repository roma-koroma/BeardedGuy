package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.component.EntityComponent;

/**
 * @author Roman K.
 */
public abstract class AIComponent extends EntityComponent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(AIComponent.class);

	@Override
	public String getFamilyId()
	{
		return "ai";
	}

	@Override
	abstract public String getComponentId();

}
