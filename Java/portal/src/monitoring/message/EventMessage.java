package monitoring.message;

import pubsub.msg.AbstractMessage;
import cep.Event;

public class EventMessage extends AbstractMessage {

	public EventMessage(Event e) {
		super(e.getType());
		put("event", e);
	}

}
