package pubsub;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import pubsub.ext.Reporter;
import pubsub.msg.AbstractMessage;
import pubsub.msg.P2PMessage;
import pubsub.msg.SubscribeRequest;
import pubsub.msg.Subscription;
import pubsub.msg.UnSubscribeRequest;

public class Broker implements MessengerListener, Serializable {
	private static final long serialVersionUID = -1660537356262518545L;

	private ConcurrentHashMap<InetSocketAddress, BrokerRef> nbSet = 
			new ConcurrentHashMap<InetSocketAddress, BrokerRef>();
	
	private Logger log = Logger.getLogger("Broker");
	private Messenger messenger;
	private RouteTable routeTable;
	private String ip;
	private int port;
	private Reporter reporter;
	public static boolean reporterEnabled = true;
	
	public RouteTable getRouteTable() {
		return routeTable;
	}
	private MessageHandler p2pMsgHandler;
	private InetSocketAddress localAddr;
	private List<NbTableChangeListener> listeners = new ArrayList<NbTableChangeListener>();
	public InetSocketAddress getLocalAddr() {
		return localAddr;
	}
	public void setP2PMessageHandler(MessageHandler handler) {
		this.p2pMsgHandler = handler;
	}
	public MessageHandler getP2PMessageHandler() {
		return p2pMsgHandler;
	}
	public Broker(int port) throws Exception {
		this("127.0.0.1", port);
	}
	public Broker(String ip, int port) throws Exception {
		messenger = new BioMessenger(port, this);
		routeTable = new RouteTable(this);
		this.ip = ip;
		this.port = port;
		localAddr = new InetSocketAddress(ip, port);
		log.setLevel(Level.WARNING);
		Thread messengerThread = new Thread(messenger);
		
		reporter = new Reporter();
		
		messengerThread.start();
	}
	public void addNb(InetSocketAddress addr, long latency) {
		nbSet.put(addr, new BrokerRef(latency, addr));
		log.info(getBrokerID() + " addNb(" + addr + "," + latency+")");
		if (reporter != null) {
			List<String> nbs = new ArrayList<String>();
			for (InetSocketAddress nbAddr : nbSet.keySet()) {
				nbs.add(nbAddr.toString());
			}
			reporter.notifyNbChanged(getBrokerID(),nbs);//notify server 
		}
		for (Subscription sub : routeTable.getLocalSubTable().keySet()) {
			SubscribeRequest msg = new SubscribeRequest(sub, localAddr);
			try {
				messenger.send(msg, addr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (Subscription sub : routeTable.getSubscriptions()) {
			SubscribeRequest msg = new SubscribeRequest(sub, localAddr);
			try {
				messenger.send(msg, addr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (NbTableChangeListener l : listeners) {
			l.onNbTableUpdated();
		}
	}
	
	private void handleMessage(AbstractMessage msg) {
		msg.recordPath(getBrokerID());
		//notify server. return in case of failure
//		if (reporterEnabled)
			
		if (msg instanceof P2PMessage) {
			p2pMsgHandler.handleMessage(msg);
		} else {
			Set<InetSocketAddress> targets = routeTable.routeMessage(msg);
			for (InetSocketAddress addr : targets) {
				try {
					messenger.send(msg, addr);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void handleSubscribeRequest(SubscribeRequest subReq) {
		subReq.recordPath(getBrokerID());
		InetSocketAddress lasthop = subReq.getLastHop();
		// add to route table and forward to neighbors if not from local
		if (lasthop != null) {
			routeTable.addSubscription(subReq.getSubscription(), subReq.getLastHop());
		}
		// forward to neighbors
		subReq.setLastHop(localAddr);
		broadcast(subReq, lasthop);
	}
	
	public void connect(InetSocketAddress addr) throws Exception {
		if (localAddr.equals(addr)) {
			log.warning("Cannot connect to self: " + addr);
			throw new Exception();
		}
		
		try {
			messenger.connect(localAddr, addr);
		} catch (IOException e) {
			log.warning("Failed to connect to " + addr);
			throw new Exception();
		}
	}
	
	public String subscribe(Subscription sub, MessageHandler h) {
		if (h != null) {
			String id = getBrokerID()+"s"+getNextSubId();
			sub.setID(id);
			routeTable.addSubscriptionFromLocal(sub, h);
			SubscribeRequest subReq = new SubscribeRequest(sub, null);
			handleSubscribeRequest(subReq);
			return id;
		} else return null;
	}
	
	public void unsubscribe(String id) {
		boolean success = routeTable.removeSubscriptionFromLocal(id);
		if (success) {
			UnSubscribeRequest unsubReq = new UnSubscribeRequest(id, null);
			handleUnsubscribeRequest(unsubReq);
		}
	}
	
	private void handleUnsubscribeRequest(UnSubscribeRequest unsubReq) {
		unsubReq.recordPath(getBrokerID());
		InetSocketAddress lasthop = unsubReq.getLastHop();
		// add to route table and forward to neighbors if not from local
		if (lasthop != null) {
			routeTable.removeSubscription(unsubReq.getSubID());
		}
		// forward to neighbors
		unsubReq.setLastHop(localAddr);
		broadcast(unsubReq, lasthop);
	}
	public void publish(AbstractMessage msg) {
		msg.setID(getBrokerID()+"m"+getNextEventId());
		handleMessage(msg);
	}
	
	public void sendTo(AbstractMessage msg, InetSocketAddress target) {
		try {
			messenger.send(msg, target);
		} catch (IOException e) {
			log.warning("Failed to send " + msg +" from " + getBrokerID() 
					+ " to " + target + " due to " + e.getMessage());
		}
	}
	
	private void broadcast(Serializable msg, InetSocketAddress lasthop) {
		for (InetSocketAddress addr : nbSet.keySet()) {
			try {
				if (!addr.equals(lasthop))
					messenger.send(msg, addr);
			} catch (IOException e) {
				log.warning("Failed to send " + msg +" from " + getBrokerID() 
						+ " to " + addr + " due to " + e.getMessage());
			}
		}
	}
	@Override
	public void handleMessage(Object msg) {
		if (msg instanceof SubscribeRequest) {
			SubscribeRequest sr = (SubscribeRequest) msg;
			if (!sr.hasBeenHandledBy(getBrokerID())) {
				handleSubscribeRequest(sr);
			}
		} else if (msg instanceof UnSubscribeRequest) {
			UnSubscribeRequest usr = (UnSubscribeRequest) msg;
			if (!usr.hasBeenHandledBy(getBrokerID())) {
				handleUnsubscribeRequest(usr);
			}
		} else if (msg instanceof AbstractMessage) {
			AbstractMessage message = (AbstractMessage) msg;
			if (!message.hasBeenHandledBy(getBrokerID())) {
				handleMessage(message);
			}
		}
	}
	static int subId=0, eId=0;
	public static synchronized int getNextSubId() {
		return subId++;
	}
	public static synchronized int getNextEventId() {
		return eId++;
	}
	public void addNbTableChangeListener(NbTableChangeListener nbTableModel) {
		listeners.add(nbTableModel);
	}
	public String getBrokerID() {
		return getLocalAddr().toString();
	}
	public static boolean isReporterEnabled() {
		return reporterEnabled;
	}
	public void setReporterEnabled(boolean reporterEnabled) {
		Broker.reporterEnabled = reporterEnabled;
	}
	public void subscribeLocal(String eventType, MessageHandler h) {
		Subscription sub = new Subscription(eventType);
		sub.setID(String.valueOf(getNextSubId()));
		routeTable.addSubscriptionFromLocal(sub, h);
	}
	public Reporter getReporter() {
		return reporter;
	}
	public Object[] getNbSet() {
		return nbSet.values().toArray();
	}
}
