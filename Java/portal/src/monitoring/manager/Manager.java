package monitoring.manager;

import java.net.InetSocketAddress;

import monitoring.message.CepRuleConfigChangeMessage;
import monitoring.message.InitConfigMessage;
import monitoring.sensor.SensorRunner;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import pubsub.Broker;
import pubsub.MessageHandler;
import pubsub.ext.ReporterServer;
import pubsub.filter.Filter;
import pubsub.msg.AbstractMessage;
import pubsub.msg.Subscription;
import cep.CepEngine;
import cep.Event;

public class Manager {
	private static Manager m = new Manager();

	public static Manager getInstance() {
		return m;
	}

	private static boolean simulationMode = false;
	private Broker broker;
	private EventDAO eventDao;
	private ProbeDefinitionDAO probeDefDao;
	private NetworkDAO networkDao;
	private CepRuleDefinitionDAO cepRuleDefDao;
	private EventTypeDAO eventTypeDao;
	private PortalSubscriptionDAO portalSubscriptionDao;
	private CepEngine engine;
	
	private EventMessageHandler eventHandler = new EventMessageHandler();

	private Manager() {
		new Thread(new ReporterServer()).start();
		
		
		// load configuration database (XML currently)
		eventDao = new EventDAO();
		eventDao.init();
		probeDefDao = new ProbeDefinitionDAO("probe-info.json");
		portalSubscriptionDao = new PortalSubscriptionDAO("portal-subscription.json");
		networkDao = new NetworkDAO("network.json");
		cepRuleDefDao = new CepRuleDefinitionDAO("cep-rule.json");
		eventTypeDao = new EventTypeDAO("event-type.json");
		engine = new CepEngine();

		try {
			broker = new Broker(20000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		if (simulationMode) {
//			System.out.println("runSimulationMode()");
			// m.broker.setReporterEnabled(false);
//			if (Broker.isReporterEnabled())
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						ReporterServer.main(null);
//
//					}
//				}).start();
//		System.out.println("starting RunAsServer¡£¡£¡£");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runSimulationMode();
//		}
		
		
		broker.subscribe(new Subscription("sensor"), new MessageHandler() {

			@Override
			public void handleMessage(AbstractMessage msg) {
				SensorInfo sensorInfo = (SensorInfo)msg.get("sensorInfo");
//				 System.out.println("SensorInfo received: " +
//						 sensorInfo.getId());
				String id = sensorInfo.getId();
				Node n = networkDao.getNetwork().getNode(id);
				if (n == null) {
					networkDao.addNode(sensorInfo);
				} else {
					broker.sendTo(
							new InitConfigMessage(n, probeDefDao
									.getProbeInfoList()),
							new InetSocketAddress(sensorInfo.getIp(),
									sensorInfo.getPort()));
				}
			}
		});
		
		for (PortalSubscriptionBean sub : portalSubscriptionDao.findAll()) {
			Subscription s;
			String filter = sub.getFilter();
			if (filter != null && filter.isEmpty()) 
				s = new Subscription(sub.getEventTypeName());
			else {
				Filter f = Filter.parseFilter(filter);
				if (f != null) {
					s = new Subscription(sub.getEventTypeName(), f);
				} else {
					s = new Subscription(sub.getEventTypeName());
				}
			}
			
			String key = broker.subscribe(s, eventHandler);
			sub.setKey(key);
			portalSubscriptionDao.update(sub);
		}
	}

	private void runSimulationMode() {
		
		Network n = networkDao.getNetwork();
		
		for (int i=0; i<4; i++) {
			final int myPort = 20001 + i*5;
			final int ii = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					SensorRunner runner = new SensorRunner("127.0.0.1", myPort,
							20000, ii);
				}
			}).start();
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		for (int i=20002; i<=20020; i++) {
			final int nbPort;
			if (i == 20001 || i == 20006 || i==20011||i==20016)
				continue;
			final int myPort = i;
			final int ii = i - 20000;
			nbPort = 20001 + ((i-20001)/5)*5;
			new Thread(new Runnable() {
				@Override
				public void run() {
					SensorRunner runner = new SensorRunner("127.0.0.1", myPort,
							nbPort, ii);
				}
			}).start();
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	class EventMessageHandler implements MessageHandler {

		@Override
		public void handleMessage(AbstractMessage msg) {
			handleEvent((Event) (msg.get("event")));
		}

		private void handleEvent(Event event) {
			eventDao.saveEvent(event);
		}
	}

	public EventDAO getDAO() {
		return eventDao;
	}

	public static void main(String[] args) {
		org.eclipse.jetty.server.Server webServer = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(45678);
		webServer.addConnector(connector);

		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setDescriptor("./WebContent/WEB-INF/web.xml");
		context.setResourceBase("./WebContent");
		context.setParentLoaderPriority(true);

		webServer.setHandler(context);
		webServer.setStopAtShutdown(true);
		webServer.setSendServerVersion(true);
		try {
			webServer.start();
		} catch (Exception e) {
			// logger.severe("Can't start web server.");
			System.exit(0);
		}
	}

	public NetworkDAO getNetworkDao() {
		return networkDao;
	}

	public String lookupCepRuleClassName(String ruleName) {
		return cepRuleDefDao.findByName(ruleName).getClassName();
	}

	public void sendCepRuleConfigChange(String method,
			CepRuleConfigWithSource r, String machineId) {
		InetSocketAddress addr = lookupAddress(machineId);
		if (addr != null) {
			CepRuleConfigChangeMessage m = new CepRuleConfigChangeMessage(
					method, r);
			broker.sendTo(m, addr);
		}
	}

	private InetSocketAddress lookupAddress(String machineId) {
		Node node = networkDao.getNetwork().getNode(machineId);
		if (node == null)
			return null;
		return new InetSocketAddress(node.getInfo().getIp(), node.getInfo()
				.getPort());
	}

	public CepRuleDefinitionDAO getCepRuleDefinitionDao() {
		return cepRuleDefDao;
	}

	public CepEngine getEngine() {
		return engine;
	}

	public void setEngine(CepEngine engine) {
		this.engine = engine;
	}

	public EventTypeDAO getEventTypeDao() {
		return eventTypeDao;
	}

	public ProbeDefinitionDAO getProbeDefDao() {
		return probeDefDao;
	}

	public PortalSubscriptionDAO getPortalSubscriptionDao() {
		return portalSubscriptionDao;
	}

	public static void setSimulationMode(boolean b) {
		simulationMode = b;
	}

	public void createPortalSubscription(PortalSubscriptionBean sub) {
		String filter = sub.getFilter();
		Subscription s;
		if (filter != null && filter.isEmpty()) 
			s = new Subscription(sub.getEventTypeName());
		else {
			Filter f = Filter.parseFilter(filter);
			if (f != null) {
				s = new Subscription(sub.getEventTypeName(), f);
			} else {
				s = new Subscription(sub.getEventTypeName());
			}
		}
		
		String key = broker.subscribe(s, eventHandler);
		sub.setKey(key);
		portalSubscriptionDao.insert(sub);
	}
	
	public void updatePortalSubscription(PortalSubscriptionBean sub) {
		String filter = sub.getFilter();
		Subscription s;
		if (filter != null && filter.isEmpty()) 
			s = new Subscription(sub.getEventTypeName());
		else {
			Filter f = Filter.parseFilter(filter);
			if (f != null) {
				s = new Subscription(sub.getEventTypeName(), f);
			} else {
				s = new Subscription(sub.getEventTypeName());
			}
		}
		
		PortalSubscriptionBean bean = portalSubscriptionDao.findById(sub.getId());
		String key = bean.getKey();
		broker.unsubscribe(key);
		String newKey = broker.subscribe(s, eventHandler);
		sub.setKey(newKey);
		portalSubscriptionDao.update(sub);
	}
	
	public void deletePortalSubscription(int id) {
		PortalSubscriptionBean bean = portalSubscriptionDao.findById(id);
		String key = bean.getKey();
		broker.unsubscribe(key);
		portalSubscriptionDao.delete(id);
	}

	public void sendCepRuleStopCommand(String ruleId, String machineId) {
		InetSocketAddress addr = lookupAddress(machineId);
		if (addr != null) {
			CepRuleConfigChangeMessage m = new CepRuleConfigChangeMessage(
					"stop", ruleId);
			broker.sendTo(m, addr);
		}
	}
}
