package quest.client.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * @author Roman K.
 */
public class CycleInputSource extends InputSource
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(CycleInputSource.class);

	public int nextEventToInsert;

	public CycleInputSource()
	{
		Random random = new Random();
		nextEventToInsert = random.nextInt() & 3;
	}


	@Override
	public void tick()
	{
		insertEvent(numberToEvent(nextEventToInsert));
		nextEventToInsert = (++nextEventToInsert) & 3;
	}

}
