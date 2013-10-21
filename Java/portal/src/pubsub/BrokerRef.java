package pubsub;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class BrokerRef implements Serializable {
	private long latency;
	private InetSocketAddress address;
	public BrokerRef(long latency, InetSocketAddress address) {
		this.latency = latency;
		this.address = address;
	}
	public long getLatency() {
		return latency;
	}
	public InetSocketAddress getAddress() {
		return address;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("@").append(address).append("!").append(latency).append("ns");
		return sb.toString();
	}
}
