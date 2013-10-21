package pubsub.msg;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class ConnectAck implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5304028766371456392L;
	private InetSocketAddress clientAddr;
	private long latency;
	public ConnectAck(InetSocketAddress clientAddr,
			long latency) {
		super();
		this.clientAddr = clientAddr;
		this.latency = latency;
	}
	public InetSocketAddress getClientAddr() {
		return clientAddr;
	}
	public long getLatency() {
		return latency;
	}
}
