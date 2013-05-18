package quest.client.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Источник данных от манипуляторов.
 * @author Roman Koretskiy
 */
public class RandomInputSource extends InputSource
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(RandomInputSource.class);


	public RandomInputSource()
    {

    }

    public void tick()
    {
        Random random = new Random();
        int result = random.nextInt() & 3;
		insertEvent(numberToEvent(result));
    }


}
