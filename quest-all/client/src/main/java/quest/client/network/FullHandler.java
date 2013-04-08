package quest.client.network;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.controller.GameController;

/**
 * @author Roman K.
 */
public class FullHandler implements Handler
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(FullHandler.class);
	private GameController gameController;

	public void setGameController(GameController gameController)
	{
		this.gameController = gameController;
	}

	@Override
	public void handle(ByteString message)
	{

	}
}
