package pubsub.msg;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class ConnectResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7506225385593836220L;
	private InetSocketAddress serverAddr;
	private long timestamp;
	public ConnectResponse(InetSocketAddress serverAddr, long timestamp) {
		this.serverAddr = serverAddr;
		this.timestamp = timestamp;
	}
	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}
	public long getTimestamp() {
		return timestamp;
	}
}
