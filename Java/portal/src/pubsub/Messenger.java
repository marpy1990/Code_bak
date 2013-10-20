package pubsub;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;

public interface Messenger extends Runnable {
	public void send(Serializable msg, InetSocketAddress target) throws IOException;
	public void connect(InetSocketAddress local, InetSocketAddress neighbor) throws IOException;
}
