package quest.server.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.common.model.Point;

/**
 * @author Roman K.
 */
public class MoveAction extends Action
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(MoveAction.class);
	private int entityId;
	private Point position;

	//флаг продолжения движения
	private boolean isContinue;
	private boolean contiune;

	public MoveAction()
	{
		super(Type.MOVE);
	}

	public int getEntityId()
	{
		return entityId;
	}

	public void setEntityId(int entityId)
	{
		this.entityId = entityId;
	}

	public Point getNewPosition()
	{
		return position;
	}


	public boolean isContiune()
	{
		return contiune;
	}
}
