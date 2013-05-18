package quest.server.dao;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import quest.common.model.Point;
import quest.server.model.BeardedGuy;
import quest.server.model.GameMap;
import quest.server.model.Room;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Roman K.
 */
public class GameControllerTest
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(GameControllerTest.class);

}