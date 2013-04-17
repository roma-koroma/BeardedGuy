package quest.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roman K.
 */
public class GameControllerException extends Throwable
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(GameControllerException.class);

	public GameControllerException(String s) {
		super(s);
	}
}
