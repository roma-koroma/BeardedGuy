package quest.server;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import quest.common.model.Point;
import quest.protocol.Client;
import quest.server.component.*;
import quest.server.component.impl.*;
import quest.server.component.system.AISystem;
import quest.server.component.system.SimulationSystem;
import quest.server.dao.PlayerStorage;
import quest.server.dao.GameController;
import quest.server.model.*;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;


/**
 * @author Roman K.
 */
public class MainFlowTest
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainFlowTest.class);

	GameController controller;

	@Mock
	PlayerStorage playerStorage;

	@BeforeMethod
	public void setUp()
	{
		controller = new GameController();
		MockitoAnnotations.initMocks(this);
		controller.setPlayerStorage(playerStorage);
	}

	@Test
	public void shouldReturnNewGuyIfHeFirstTimeInGame()
	{
		//given
		String login = "test";

		Room room = new Room("Поля новичков", 100, 100, new Point(0, 50));
		controller.setRoom(room);

		when(playerStorage.fingGuyByLogin(login)).thenReturn(null);

		//when
		controller.getGuyByLogin(login);

		//then
		ArgumentCaptor<BeardedGuy> argument = ArgumentCaptor.forClass(BeardedGuy.class);
		verify(playerStorage).saveGuy(argument.capture());

		assertEquals(argument.getAllValues().size(), 1);
		BeardedGuy guy = argument.getValue();

		assertEquals("Test", guy.getName());
		assertEquals(guy.getPosition(), room.getInitPoint());
		assertTrue(guy.getId() >= 0);

	}

	@Test
	public void shouldReturnExistedGuyIfSuchLoginFinded()
	{
		//given
		String login = "test";

		Room room = new Room("Поля новичков", 100, 100, new Point(0, 50));
		controller.setRoom(room);

		BeardedGuy guy = new BeardedGuy();
		guy.setName("Test");
		guy.setId(158);
		guy.setRoom(room);
		guy.setPosition(new Point(10, 10));
		guy.setIsOnline(false);

		when(playerStorage.fingGuyByLogin(login)).thenReturn(guy);

		//when
		controller.getGuyByLogin("test");

		//then
		verify(playerStorage, never()).saveGuy(any(BeardedGuy.class));
	}

	@Test
	public void shouldMoveGuy()
	{
		//given
		Room room = new Room("Поля новичков", 100, 100, new Point(0, 50));
		controller.setRoom(room);

		Point lastPosition = room.getInitPoint();

		BeardedGuy guy = new BeardedGuy();
		guy.setName("Test");
		guy.setId(1);
		guy.setRoom(room);
		guy.setPosition(lastPosition);
		guy.setIsOnline(false);
		guy.setCellsPerMinute(62);

		//от пользователя приходит, что он решил перейти из клетки вверх.

		//сперва он ничего не делает
		assertEquals(guy.getState(), BeardedGuy.State.IDLE);

		//передвигаем его вверх.
		controller.move(1, Client.Operation.Move.Direction.UP);

		//при обновлении комнаты на 500мс игрок сдвинется на полклетки
		List<Action> actions = controller.updateRoom(500);

		assertEquals(actions.size(), 1);
		assertEquals(actions.get(0).getType(), Action.Type.MOVE);
		assertEquals(guy.getState(), BeardedGuy.State.MOVE);

		MoveAction action = (MoveAction) actions.get(0);
		assertEquals(action.getEntityId(), 1);
		assertNotEquals(action.getNewPosition(), lastPosition);

		// вторая половина движения
		actions = controller.updateRoom(500);

		assertEquals(actions.size(), 1);

		//за эти 500мс персонаж успел пройти остаток пути.
		//Его статус сменился на IDLE. Получается, что нужно всем разослать информацию об окончании пути.
		assertEquals(actions.get(0).getType(), Action.Type.MOVE);
		assertEquals(guy.getState(), BeardedGuy.State.IDLE);

		action = (MoveAction) actions.get(0);
		assertFalse(action.isContiune());
	}

	@Test
	public void guyShouldPatrolFromOnePointToAnother()
	{
		Room room = new Room("Поля новичков", 100, 100, new Point(0, 50));
		controller.setRoom(room);

		Point lastPosition = room.getInitPoint();

		BeardedGuy guy = new BeardedGuy();
		guy.setName("Test");
		guy.setId(1);
		guy.setRoom(room);
		guy.setPosition(lastPosition);
		guy.setIsOnline(false);
		guy.setCellsPerMinute(60);


	}

	@Test
	public void gameObjectAsComponentContainer()
	{

		ComponentTemplateFactory cTmplManager = initComponentTemplateManager();
		EntityTemplateFactory entityTemplateFactory = initEntityTemplateFactory(cTmplManager);

		EntityManager entityManager = new EntityManager();

		Entity wizard = initWizard(entityManager);
		Entity goblin = initGoblin(entityManager);

		ComponentManager сompManager = new ComponentManager();

		//по тику таймеры мы собираем пакет сообщений из очереди
		//событий игроков.

		//input переводится в ai компонент игрока
		//ai компонент рассылает сообщения компонентам движения/атаки/смены оружия/каста

		double dt = 0.1;

//		InputSystem input = new InputSystem(entityManager, inputQueue);
//		input.update(dt);

		AISystem ai = new AISystem(entityManager);
		ai.update(dt);

		SimulationSystem simulation = new SimulationSystem(entityManager);
		simulation.update(dt);

	}

	/**
	 * 	создаем все сущности
	 */
	private EntityTemplateFactory initEntityTemplateFactory(ComponentTemplateFactory cTmplManager)
	{
		return null;
	}

	/**
	 * Разрешаем все зависимости и добавляем компоненты в менеджер шаблонов.
	 * Позже, когда будет введен DDXml можно будет брать шаблоны и выставлять
	 * те параметры, что описаны в файле с сущностью
	 * @return
	 */
	private ComponentTemplateFactory initComponentTemplateManager()
//	private ComponentTemplateFactory initComponentTemplateManager(GameEngine engone)
	{
		ComponentTemplateFactory ret = new ComponentTemplateFactory();


		return ret;
	}

	private Entity initGoblin(EntityManager entityManager)
	{
		Entity goblin = new Entity(EntityManager.nextId());

		ProjectionComponent projection = new ProjectionComponent();
		projection.setRadius(10d);
		projection.setPosition(new Point(4, 5));

		HealthComponent health = new HealthComponent();
		health.setHealth(100);

		MoveComponent velocity = new MoveComponent();
		velocity.setMaxVelocity(3);

		CollisionComponent collision = new CollisionComponent();
		SpikeComponent spike = new SpikeComponent();

		StandingAiComponent ai = new StandingAiComponent();

		entityManager.register(goblin, ai);
		entityManager.register(goblin, projection);
		entityManager.register(goblin, health);
		entityManager.register(goblin, velocity);
		entityManager.register(goblin, collision);
		entityManager.register(goblin, spike);

		return goblin;
	}

	private Entity initWizard(EntityManager entityManager)
	{
		Entity wizard = new Entity(EntityManager.nextId());

		ProjectionComponent projection = new ProjectionComponent();
		projection.setPosition(new Point(5, 5));
		projection.setRadius(10d);

		HealthComponent health = new HealthComponent();
		health.setHealth(100);

		MoveComponent velocity = new MoveComponent();
		velocity.setMaxVelocity(3);

		CollisionComponent collision = new CollisionComponent();

		RamAIComponent ai = new RamAIComponent();

		entityManager.register(wizard, ai);
		entityManager.register(wizard, projection);
		entityManager.register(wizard, health);
		entityManager.register(wizard, velocity);
		entityManager.register(wizard, collision);

		return wizard;
	}

	@Test
	public void newArchitectureShouldSplitNetworkingAndLogic()
	{
		//so, we have main module — processing, also network, user, world, ai and groups.
		//из этих модулей у меня не вырисовывались только ai, а группы были только в ближайших планах.
		GameProcess process = new GameProcess();

		//есть модуль — сеть. С ним я работал еще в самом начале, но сейчас в него завязана логика. Давай избавимся от нее

		Network network = new Network();

		//когда кто-нибудь приходит в игру через сеть, то мы авторизуем его
		//как это сделать без привязки логики к сети?

		//объединяем все модули кольцевыми буфферами. Какими-нибудь LinkedBlockingQueue
		//или через publish/subscribe

		/*
		нижний уровень:
		ожидание подключений.
		отправка/прием сообщений.

		события!
		 */

	}

	private class GameProcess
	{

	}

	private class Network
	{
	}


}


class BeardedGuyBuilder
{
	public static BeardedGuy wizard(String name)
	{
		return null;
	}

	public static BeardedGuy goblin(String name)
	{
		return null;
	}
}
