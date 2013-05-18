package quest.server.model;

import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Игровая карта — cеть комнат.
 * @author Roman K.
 */
public class GameMap
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(GameMap.class);
	private Integer initRoomId;
	private Map<Integer, Room> rooms;
	private int nextRoomId;

	public GameMap()
	{
		this.rooms = new HashMap<Integer, Room>();
		this.nextRoomId = 1;
	}


	public int registerRoom(Room room, boolean isInit)
	{
		room.setId(nextRoomId++);
		if(isInit)
			if(initRoomId == null)
				initRoomId = room.getId();
			else
				throw new RuntimeException("Initial room already defined");
		rooms.put(room.getId(), room);
		return room.getId();
	}

	public Room getInitRoom()
	{
		return rooms.get(initRoomId);
	}

	public List<Room> getRooms()
	{
		return new ArrayList(rooms.values());
	}
}
