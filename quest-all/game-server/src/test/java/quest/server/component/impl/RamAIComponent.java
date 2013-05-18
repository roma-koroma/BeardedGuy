package quest.server.component.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.model.GameMap;

import java.util.Arrays;
import java.util.List;

/**
 * @author Roman K.
 */
public class RamAIComponent extends AIComponent
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(RamAIComponent.class);

	private State state;

	private GameMap gameMap;

	@Override
	public String getComponentId()
	{
		return "ramAi";
	}

	public List<String> requirements()
	{
		return Arrays.asList("position");
	}


	@Override
	public void update(double dt)
	{
		if(state == State.STAND )
		{
			ProjectionComponent position = (ProjectionComponent) getOwner().getComponent("position");
			MoveComponent moveComponent = (MoveComponent) getOwner().getComponent("move");
			moveComponent.move(90, moveComponent.getMaxVelocity());
			moveComponent.stop();

//			long entityId = gameMap.findNearestEntity(position.getPosition()); // на карте есть только сущности
//			// имеющие позицию.
//
//			ProjectionComponent targetPosition = ((ProjectionComponent) entity.getComponent("position")).getPosition();
//
//			double angle = directionAngle(position.getPosition(), targetPosition.getPosition());
//
//			//меняем состояние на "Движение"
//			attachMessage("move", new MoveMessage(angle));
			state = State.MOVE;

		}

	}

	public static enum State
	{
		STAND, MOVE, ATTACK;
	}

}
