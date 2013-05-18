package quest.client.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author Roman K.
 */
abstract public class InputSource
{

	private Deque<Event> events;

	public InputSource()
	{
		this.events = new ArrayDeque<Event>();
	}

	abstract public void tick();

	public Deque<Event> getEvents()
	{
		return events;
	}


	protected void insertEvent(Event event)
	{
		this.events.addFirst(event);
	}

	public Event removeEvent()
	{
		return this.events.removeLast();
	}

	protected Event numberToEvent(int number)
	{
		switch (number)
		{
			case 0:
				return InputSource.Event.UP_KEY;
			case 1:
				return InputSource.Event.DOWN_KEY;
			case 2:
				return InputSource.Event.LEFT_KEY;
			case 3:
				return InputSource.Event.RIGHT_KEY;
		}
		return null;
	}

	public int eventCount()
	{
		return events.size();
	}


	public enum Event
	{
		UP_KEY(0),
		DOWN_KEY(1),
		LEFT_KEY(2),
		RIGHT_KEY(3);

		private int code;

		Event(int code)
		{
			this.code = code;
		}

		public int getCode()
		{
			return code;
		}
	}
}
