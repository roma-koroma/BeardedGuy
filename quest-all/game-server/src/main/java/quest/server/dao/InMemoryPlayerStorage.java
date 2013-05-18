package quest.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quest.server.model.BeardedGuy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman K.
 */
public class InMemoryPlayerStorage implements PlayerStorage
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(InMemoryPlayerStorage.class);

	private final Map<String, Integer> authToId;

	private final HashMap<Integer, BeardedGuy> idToGuy;


	public InMemoryPlayerStorage()
	{
		authToId = new HashMap<String, Integer>();
		idToGuy = new HashMap<Integer, BeardedGuy>();
	}

	@Override
	public int count()
	{
   		return idToGuy.size();
	}

	@Override
	public void saveGuy(BeardedGuy capture)
	{

	}

	@Override
	public BeardedGuy fingGuyByLogin(String login)
	{
		return null;
	}

	@Override
	public BeardedGuy getById(Integer id)
	{
		return idToGuy.get(id);
	}
}
