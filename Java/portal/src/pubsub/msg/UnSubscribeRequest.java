package pubsub.msg;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;

public class UnSubscribeRequest implements Serializable {
	private String subId;
	private InetSocketAddress lastHop;
	private LinkedHashSet<String> path = new LinkedHashSet<String>();

	public UnSubscribeRequest(String id, InetSocketAddress lasthop) {
		subId = id;
		this.lastHop = lasthop;
	}
	
	public boolean hasBeenHandledBy(String brokerID) {
		return path.contains(brokerID);
	}
	public void recordPath(String brokerID) {
		path.add(brokerID);
	}
	public Set<String> getPath() {
		return path;
	}
	public InetSocketAddress getLastHop() {
		return lastHop;
	}
	public void setLastHop(InetSocketAddress lastHop) {
		this.lastHop = lastHop;
	}
	public String getSubID() {
		return subId;
	}
}
