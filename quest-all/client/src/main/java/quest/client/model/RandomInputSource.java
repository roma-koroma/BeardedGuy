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
public class RandomInputSource
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(RandomInputSource.class);

    private List<Event> events;

    public static enum Event
    {
        UP_KEY(0),
        DOWN_KEY(1),
        LEFT_KEY(2),
        RIGHT_KEY(3);

        private int code;

        Event(int code)
        {
            this.code = code;
        }

        public int getCode()
        {
            return code;
        }
    }

    public RandomInputSource()
    {

    }

    public void tick()
    {
        this.events = new ArrayList<Event>();
        Random random = new Random();
        int result = random.nextInt() & 3;

        switch (result)
        {
            case 0:
                this.events.add(Event.UP_KEY);
                break;
            case 1:
                this.events.add(Event.DOWN_KEY);
                break;
            case 2:
                this.events.add(Event.LEFT_KEY);
                break;
            case 3:
                this.events.add(Event.RIGHT_KEY);
                break;
        }
    }

    public List<Event> getEvents()
    {
        return events;
    }
}
