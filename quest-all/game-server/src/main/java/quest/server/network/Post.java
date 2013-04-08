package quest.server.network;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Roman K.
 */
public class Post
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(Post.class);

	Map<Integer, Deque<Message>> messages;

	public Post()
	{
		this.messages = new HashMap<Integer, Deque<Message>>();
	}

	public void addClient(int id)
	{
		this.messages.put(id, new ArrayDeque<Message>());
	}

	public void broadcast(Message message)
	{
		//TODO сообщения одного типа должны группироваться
		for(Deque<Message> deque : messages.values())
		{
			deque.addFirst(message);
		}
	}

	public Message getMessage(Integer id)
	{
		Deque<Message> queue = messages.get(id);
		if(queue != null && !queue.isEmpty())
		{
			return queue.removeLast();
		}
		return null;
	}

	public boolean hasMessages(Integer id)
	{
		Deque<Message> deq = messages.get(id);
		if(deq != null && !deq.isEmpty())
			return  true;
		return false;
	}

	/**
	 * Удаляет все сообщения клиента и самого клиента из ящика.
	 * @param id
	 */
	public void close(int id)
	{
		Deque<Message> deq = messages.get(id);
		if(deq != null)
			messages.get(id).clear();
		messages.remove(id);
	}

	public void sendTo(int id, Message message)
	{
		Deque<Message> deq = messages.get(id);
		if(deq != null)
			deq.addFirst(message);
	}
}
