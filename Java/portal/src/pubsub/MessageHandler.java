package pubsub;

import pubsub.msg.AbstractMessage;

public interface MessageHandler {
	public void handleMessage(AbstractMessage msg);
}
