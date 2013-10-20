package pubsub;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import pubsub.msg.AbstractMessage;
import pubsub.msg.Subscription;

public class RouteTable {
	private List<RouteTableChangeListener> listeners = new ArrayList<RouteTableChangeListener>();
	private List<LocalSubTableChangeListener> lstListeners = new ArrayList<LocalSubTableChangeListener>();
	private ConcurrentMap<Subscription, InetSocketAddress> table = 
			new ConcurrentHashMap<Subscription, InetSocketAddress>();
	private Logger log = Logger.getLogger("RouteTable");
	
	private ConcurrentMap<Subscription, MessageHandler> localSubTable = 
			new ConcurrentHashMap<Subscription, MessageHandler>();
	private Broker broker;
	
	public RouteTable(Broker broker) {
		log.setLevel(Level.WARNING);
		this.broker = broker;
	}
	
	public void addSubscriptionFromLocal(Subscription sub, MessageHandler h) {
		localSubTable.putIfAbsent(sub, h);
		log.info("addSubscriptionFromLocal(" + sub + ")");
		fireLocalSubscriptionTableChanged();
	}
	
	public void addSubscription(Subscription sub, InetSocketAddress node) {
		table.putIfAbsent(sub, node);
		log.info("addSubscription(" + sub + ")");
		fireRouteTableChanged();
	}
	public String toString() {
		StringBuffer sb = new StringBuffer("Route table");
		for (Map.Entry<Subscription, InetSocketAddress> en : table.entrySet()) {
			sb.append(en.getKey()).append(" => ").append(en.getValue()).append('\n');
		}
		return sb.toString();
	}
	
	public ConcurrentMap<Subscription, InetSocketAddress> getTable() {
		return table;
	}
	public Set<InetSocketAddress> routeMessage(AbstractMessage msg) {
		boolean reported = false;
		
		// search local subscription table
		// (whether this broker wants this message)
		for (Map.Entry<Subscription, MessageHandler> en : localSubTable
				.entrySet()) {
			// reporter
			Subscription sub = en.getKey();
			if (sub.covers(msg)) {
				// local message (path length = 1) need not be displayed on network graph
				// only report messages with path length > 1
				if (msg.getPath().size() > 1) {
					if (broker != null && broker.getReporter() != null
							&& !reported) {
						reported = true;
						broker.getReporter().notifyMsgReceived(msg.getTopic(),
								msg.getPath());
					}
				}
				// handle all messages
				en.getValue().handleMessage(msg);
			}
		}
		
		// search route table and set targets to forward the message
		Set<InetSocketAddress> targets = new HashSet<InetSocketAddress>();
		for (Map.Entry<Subscription, InetSocketAddress> en : table.entrySet()) {
			Subscription sub = en.getKey();
			if (sub.covers(msg)) {
				targets.add(en.getValue());
			}
		}
		return targets;
	}
	public void addChangeListener(RouteTableChangeListener listener) {
		listeners.add(listener);
	}
	public Set<Subscription> getSubscriptions() {
		return table.keySet();
	}
	public boolean removeSubscriptionFromLocal(String id) {
		Subscription dummy = new Subscription(null);
		dummy.setID(id);
		MessageHandler h = localSubTable.remove(dummy);
		if (h != null) {
			log.info("Subscription " + id + " was removed from local subscription table.");
			fireLocalSubscriptionTableChanged();
			return true;
		} else {
			log.warning("Subscription " + id + " is not found in local subscription table.");
			return false;
		}
	}

	public void removeSubscription(String id) {
		Subscription dummy = new Subscription(null);
		dummy.setID(id);
		InetSocketAddress h = table.remove(dummy);
		if (h != null) {
			log.info("Subscription " + id + " was removed from route table.");
		} else {
			log.warning("Subscription " + id + " is not found in route table.");
		}
		fireRouteTableChanged();
	}

	public ConcurrentMap<Subscription, MessageHandler> getLocalSubTable() {
		return localSubTable;
	}

	private void fireLocalSubscriptionTableChanged() {
		for (LocalSubTableChangeListener listener:lstListeners) {
			listener.onLocalSubTableUpdated();
		}
	}

	private void fireRouteTableChanged() {
		for (RouteTableChangeListener listener:listeners) {
			listener.onRouteTableUpdated();
		}
	}

	public void addLocalSubTableChangeListener(
			LocalSubTableChangeListener localSubTableModel) {
		lstListeners.add(localSubTableModel);
	}
}
