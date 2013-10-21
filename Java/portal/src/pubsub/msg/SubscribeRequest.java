package pubsub.msg;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;

public class SubscribeRequest implements Serializable {
	private static final long serialVersionUID = -5121530522860774206L;
	private Subscription sub;
	private InetSocketAddress lastHop;
	private LinkedHashSet<String> path = new LinkedHashSet<String>();
	public SubscribeRequest(Subscription sub, InetSocketAddress lastHop) {
		this.sub = sub;
		this.lastHop = lastHop;
	}
	public InetSocketAddress getLastHop() {
		return lastHop;
	}
	public void setLastHop(InetSocketAddress lastHop) {
		this.lastHop = lastHop;
	}
	public Subscription getSubscription() {
		return sub;
	}
	public String getID() {
		return sub.getID();
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
}
