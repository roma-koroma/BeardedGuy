package quest.client.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Roman Koretskiy
 */
public class BeardedGuyTest
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(BeardedGuyTest.class);

    BeardedGuy guy;

    @BeforeMethod
    public void setUp()
    {
        guy = new BeardedGuy();
        guy.setName("Bob");
    }

    @Test
    public void shouldStayAtZeroPoint()
    {
        Point pos = guy.getPosition();
        assertEquals(0, pos.getX());
        assertEquals(0, pos.getY());
    }

    @DataProvider(name = "move")
    public Object[][] moveProvider()
    {
        return new Object[][]
            {
                {Direction.UP, 1, 0},
                {Direction.DOWN, -1, 0},
                {Direction.LEFT, 0, -1},
                {Direction.RIGHT, 0, 1},
            };
    }

    @Test(dataProvider = "move")
    public void shouldMove(Direction dir, int x, int y)
    {
        guy.move(dir);
        assertEquals(guy.getPosition().getX(), x);
        assertEquals(guy.getPosition().getY(), y);
    }
}
