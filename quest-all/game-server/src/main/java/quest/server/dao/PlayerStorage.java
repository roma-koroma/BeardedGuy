package quest.server.dao;

import quest.server.model.BeardedGuy;

/**
 * Хранилище игроков.
 * @author Roman K.
 */
public interface PlayerStorage
{
	int count();

	void saveGuy(BeardedGuy capture);

	BeardedGuy fingGuyByLogin(String login);

	BeardedGuy getById(Integer id);
}
