package quest.server.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roman K.
 */
public class Action
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(Action.class);

	private Type type;

	public enum Type
	{
		MOVE,
		ATTACK
	}

	public Action(Type type)
	{
		this.type = type;
	}

	public Type getType()
	{
		return type;
	}

}
