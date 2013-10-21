package pubsub.msg;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class ConnectRequest implements Serializable {
	private InetSocketAddress clientAddr;
	private long time;

	public ConnectRequest(InetSocketAddress clientAddr,
			long time) {
		super();
		this.clientAddr = clientAddr;
		this.time = time;
	}
	public InetSocketAddress getAddr() {
		return clientAddr;
	}
	public long getTimestamp() {
		return time;
	}
	private static final long serialVersionUID = 5493668584696390032L;
}
