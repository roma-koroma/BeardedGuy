package quest.server.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import quest.protocol.Common;
import quest.server.dao.GameController;

import java.util.List;

/**
 * @author Roman K.
 */
public class RoomTest
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(RoomTest.class);

	GameController controller;

	@BeforeMethod
	public void setUp()
	{
		controller = new GameController();
	}

	@Test
	public void shouldAssertBehaviour()
	{

		Room room = new Room();

		controller.registerRoom(room);

		BeardedGuy guy = controller.getGuyByLogin("Roman");

		controller.putInRoom(guy, room);

		long deltaTime = 1000;

		List<Common.Action> actions = controller.updateRoom(room, deltaTime);

	}

	@Test
	public void shouldUpdateRoom()
	{
		// regster room

		//controller.updateRoom();

		Room room = new Room();
		for(BeardedGuy guy : room.getGuys())
		{
			//получить все перед
		}

	}

	@Test
	public void shouldStartMoving()
	{
		BeardedGuy guy = new BeardedGuy();
		guy.setCellsPerSecond();

	}

	@Test
	public void shouldFindPlaceForGuy()
	{
		//given


		//when


		//then
	}
}
