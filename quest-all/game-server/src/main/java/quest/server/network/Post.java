package quest.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.client.model.BeardedGuy;
import quest.protocol.Common;

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

	Map<Integer, Deque<Common.Action>> messages;

	public Post()
	{
		this.messages = new HashMap<Integer, Deque<Common.Action>>();
	}

	public void addClient(int id)
	{
		this.messages.put(id, new ArrayDeque<Common.Action>());
	}

	public void broadcast(Common.Action message)
	{
		//TODO сообщения одного типа должны группироваться
		for(Deque<Common.Action> deque : messages.values())
		{
			deque.addFirst(message);
		}
	}

	public void broadcastWithExcluding(Common.Action message, int id)
	{
		for (Map.Entry<Integer, Deque<Common.Action>> entry: messages.entrySet())
		{
			if(entry.getKey() == id)
			{
				continue;
			}
			entry.getValue().addFirst(message);
		}
	}

	public Common.Action getMessage(Integer id)
	{
		Deque<Common.Action> queue = messages.get(id);
		if(queue != null && !queue.isEmpty())
		{
			return queue.removeLast();
		}
		return null;
	}

	public List<Common.Action> getMessages(Integer id)
	{
		Deque<Common.Action> queue = messages.get(id);
		List<Common.Action> ret = new ArrayList<Common.Action>(queue);
		queue.clear();

		return ret;
	}


	public boolean hasMessages(Integer id)
	{
		Deque<Common.Action> deq = messages.get(id);
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
		Deque<Common.Action> deq = messages.get(id);
		if(deq != null)
			messages.get(id).clear();
		messages.remove(id);
	}

	public void sendTo(int id, Common.Action message)
	{
		Deque<Common.Action> deq = messages.get(id);
		if(deq != null)
			deq.addFirst(message);
	}


}
