package experiment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;

/**
 * @author Roman Koretskiy
 */
public class ByteBufferTest
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(ByteBufferTest.class);

    @Test
    public void shouldAssertBehaviour()
    {
        ByteBuffer bbuf = ByteBuffer.allocate(8);

        int capacity = bbuf.capacity(); // 10

        bbuf.putInt(0, Integer.MAX_VALUE);

        bbuf.clear();

        bbuf.putInt(1, 2);
		int rem = bbuf.remaining();
        bbuf.get(0); // 0
        bbuf.get(1); // 123

        logger.info("end");

    }
}
