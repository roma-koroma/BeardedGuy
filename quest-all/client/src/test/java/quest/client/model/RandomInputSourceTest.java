package quest.client.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * @author Roman Koretskiy
 */
public class RandomInputSourceTest
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(RandomInputSourceTest.class);

    @Test
    public void shouldReturnRandomEvents()
    {
        RandomInputSource source = new RandomInputSource();
        assertEquals(0, source.getEvents().size());
        source.tick();
        assertNotEquals(0, source.getEvents().size());
    }
}
