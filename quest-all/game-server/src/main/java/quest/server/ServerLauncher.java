package quest.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Roman Koretskiy
 */
public class ServerLauncher
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

    public static final int BUFFER_SIZE = 1024;

    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    UserController userController;

    public ServerLauncher() throws IOException, InterruptedException
    {
        ServerSocketChannel ssc = configure();

        Selector selector = Selector.open();
        SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

        SelectionKey key;

        logger.info("Waiting for connections...");
        while (true)
        {
            while (acceptKey.selector().select() > 0)
            {
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = readyKeys.iterator();
                while (iter.hasNext())
                {
                    key = iter.next();
                    iter.remove();

                    if (key.isAcceptable())
                        accept(key, selector);
                    else if (key.isReadable())
                        read(key);
                    else if (key.isWritable())
                        write(key);
                    Thread.sleep(500);
                }

            }
        }

    }

    private ServerSocketChannel configure() throws IOException
    {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(8080));
        return ssc;
    }

    private void accept(SelectionKey key, Selector selector) throws IOException
    {
        logger.info("new client accepted");
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException
    {
        buffer.clear();
        SocketChannel ch = (SocketChannel) key.channel();
        ch.read(buffer);
        logger.info("read {}", buffer.getInt(0));
//        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void write(SelectionKey key) throws IOException
    {
//        SocketChannel ch = (SocketChannel) key.channel();
//        buffer.clear();
//        ch.write(Charset.forName("US-ASCII").newEncoder().encode(CharBuffer.wrap("Hello!")));
        logger.info("i don't wont write");
        key.interestOps(SelectionKey.OP_READ);

    }

    public static void main(String...args) throws IOException, InterruptedException
    {
        new ServerLauncher();
    }

    private class UserController
    {
    }
}
