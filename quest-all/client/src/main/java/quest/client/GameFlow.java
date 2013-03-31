package quest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Koretskiy
 */
public class GameFlow
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(GameFlow.class);

    private List<BeardedGuy> guys;


    public GameFlow()
    {
        this.guys = new ArrayList<BeardedGuy>();
    }

    public void addGuy(BeardedGuy guy)
    {
        this.guys.add(guy);
    }

    public void tick()
    {
        for(BeardedGuy guy : guys)
        {
//            guy.tick();
        }
    }
}
