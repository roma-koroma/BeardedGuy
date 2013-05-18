package quest.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import quest.server.component.ComponentManager;

/**
 * @author Roman K.
 */
public class ComponentManagerTest
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(ComponentManagerTest.class);

	@Test
	public void componentManager()
	{
		ComponentManager compManager = new ComponentManager();

	}
}
