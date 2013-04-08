package quest.server.dao;

import quest.client.model.BeardedGuy;
import quest.protocol.ClientMessage;

/**
 * @author Roman K.
 */
public interface GameController
{
	BeardedGuy getGuyByLogin(String name);

	BeardedGuy moveById(int clientId, ClientMessage.InputOperation.Input input);

	BeardedGuy close(int id);

}
