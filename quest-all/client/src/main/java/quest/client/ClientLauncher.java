package quest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.client.model.Point;
import quest.client.model.RandomInputSource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Roman Koretskiy
 */
public class ClientLauncher
{
    /**
     * Логгер
     */
    private static final Logger logger = LoggerFactory.getLogger(ClientLauncher.class);

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8080;

    public static final int BUFFER_SIZE = 1024;

    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    public ClientLauncher() throws IOException, InterruptedException
    {
        SocketChannel channel = connect();

        Selector selector = Selector.open();
//        SelectionKey k = channel.register(selector, SelectionKey.OP_READ| SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
        SelectionKey k = channel.register(selector, SelectionKey.OP_CONNECT);

        BeardedGuy guy = new BeardedGuy("Roman", new Point(0, 0));

        RandomInputSource source = new RandomInputSource();


        while (true)
        {

            while (k.selector().select() > 0)
            {
                source.tick();
                List<RandomInputSource.Event> events = source.getEvents();

                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = readyKeys.iterator();

                SelectionKey key;
                while (iter.hasNext())
                {
                    key = iter.next();
                    iter.remove();

                    if(key.isConnectable())
                        finishConnect(key);
                    else if(key.isReadable())
                        read(key);
                    else if(key.isWritable())
                        write(key, events.get(0));
                }
                Thread.sleep(500);
            }

        }

        /**

         GameFlow flow  = new GameFlow();
         flow.addGuy(guy);
         while(true)
         {
         flow.tick();
         }
         */
    }

    private void write(SelectionKey key , RandomInputSource.Event direction) throws IOException
    {
        SocketChannel ch = (SocketChannel) key.channel();
        ch.write(ByteBuffer.allocate(BUFFER_SIZE).putInt(0, direction.getCode()));
        logger.info("{}", direction.name());
//        key.interestOps(SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException
    {
        SocketChannel ch = (SocketChannel) key.channel();
        readBuffer.clear();
        ch.read(readBuffer);
        logger.info("read [ {} ]", new String(readBuffer.array()));
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void finishConnect(SelectionKey key) throws IOException
    {
        SocketChannel ch = (SocketChannel) (key.channel());
        if(ch.finishConnect())
        {
            logger.info("finally connect to server ({}:{})", HOST, PORT);
            key.interestOps(SelectionKey.OP_WRITE);
        }
        else
            logger.error("can't connect to server ({}:{})", HOST, PORT);
    }

    private SocketChannel connect() throws IOException
    {
        String host = "127.0.0.1";
        InetSocketAddress socketAddress = new InetSocketAddress(host, 8080);

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(socketAddress);
        return channel;

    }


    public static void main(String...args) throws IOException, InterruptedException
    {
        new ClientLauncher();
    }

}
