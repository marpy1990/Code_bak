package monitoring.sensor;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import monitoring.manager.CepRuleConfigWithSource;
import monitoring.manager.CepRuleSubscriptionRequest;
import monitoring.manager.SensorInfo;
import monitoring.message.CepRuleConfigChangeMessage;
import monitoring.message.EventMessage;
import monitoring.message.InitConfigMessage;
import monitoring.message.SensorInfoMessage;
import pubsub.Broker;
import pubsub.MessageHandler;
import pubsub.filter.Filter;
import pubsub.msg.AbstractMessage;
import pubsub.msg.Subscription;
import cep.CepEngineRunner;
import cep.CepRuleConfig;
import cep.ComplexEventListener;
import cep.Event;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This is where CEP, pub/sub and sensor are put together
 * @author Administrator
 *
 */
public class SensorRunner implements MessageHandler {
	private int port;
	private String id, ip;
	private Broker broker;
	private Logger log = Logger.getLogger("SensorRunner");
	private String location;
	private Sensor sensor;
	private CepEngineRunner cepRunner;
	
	private Map<String, List<String>> localEventRouter = new HashMap<String, List<String>>();
	
	private ComplexEventAndProbeDataListener listener = new ComplexEventAndProbeDataListener();
	
	private Map<String, RuleSubscriptions> ruleSubscriptionTable = new HashMap<String, RuleSubscriptions>();
	
	private void routeLocal(Event e) {
		String type = e.getType();
		List<String> engineIds = localEventRouter.get(type);
		if (engineIds == null) return;
		for (String id : engineIds) {
			cepRunner.match(id, e);
		}
	}
	
	private void subscribeLocal(String type, String engineId) {
		List<String> engineIds = localEventRouter.get(type);
		if (engineIds == null) {
			engineIds = new ArrayList<String>();
			localEventRouter.put(type, engineIds);
		}
		engineIds.add(engineId);
	}
	
	private void unsubscribeLocal(String type, String engineId) {
		List<String> engineIds = localEventRouter.get(type);
		if (engineIds == null) {
			return;
		}
		engineIds.remove(engineId);
	}
	
	/**
	 * Publishes complex events and probe data
	 * @author Administrator
	 *
	 */
	class ComplexEventAndProbeDataListener implements ComplexEventListener, ProbeDataListener {

		@Override
		public void update(Event e) {
			e.setSource(sensor.getInfo().getId());
			EventMessage msg = new EventMessage(e);
			msg.put("id", sensor.getInfo().getId());
			msg.put("location", sensor.getInfo().getLocation());
			msg.put("os", sensor.getInfo().getOs());
			routeLocal(e);
			broker.publish(msg);
		}

		@Override
		public void update(String eventType, Map<String, Object> map) {
			// wrap raw data into an Event
			Event e = new Event(eventType);
			e.setTime(System.currentTimeMillis());
			e.setSource(sensor.getInfo().getId());
			for (Map.Entry<String, Object> en : map.entrySet()) {
				if (en.getValue() instanceof Serializable)
					e.setAttribute(en.getKey(), (Serializable) en.getValue());
			}
			routeLocal(e);
			
			EventMessage msg = new EventMessage(e);
			msg.put("id", sensor.getInfo().getId());
			msg.put("location", sensor.getInfo().getLocation());
			msg.put("os", sensor.getInfo().getOs());
			broker.publish(msg);
		}
		
	}
	
	class EventMessageHandler implements MessageHandler {
		
		public EventMessageHandler(String engineId) {
			super();
			this.engineId = engineId;
		}

		private String engineId;

		@Override
		public void handleMessage(AbstractMessage msg) {
			if (msg instanceof EventMessage) {
				cepRunner.match(engineId, (Event)msg.get("event"));
			}
		}
	}
	
	public SensorRunner(String ip, int port, int nbPort, int i) {
		this.port = port;
		this.ip = ip;
		try {
			broker = new Broker(ip, port);
			broker.setP2PMessageHandler(this);
			broker.connect(new InetSocketAddress("127.0.0.1", nbPort));
//			if (port != 20001)
//				broker.connect(new InetSocketAddress("127.0.0.1", port - 5));
		} catch (Exception e) {
			return;
		}
		log.info("Broker started on port " + port + ", neighbor: " + nbPort);
		
		try {
			Thread.sleep(500*i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sensor = new Sensor();
		cepRunner = new CepEngineRunner();
		
		// collect system info and register to manager
		String id = ip + ":" + port;
		
		SensorInfo si = new SensorInfo();
		si.setId(id);
		si.setIp(ip);
		si.setPort(port);
		si.setOs("Windows Server 2008 R2");
		
		// fake location for simulation mode: 
		// 20001~20005 belongs to LAB01, 20006~20010 belongs to LAB02, ...
		// till 20020 (LAB04)
		si.setLocation("Lab0" + ((port-20001)/5+1));
		sensor.setInfo(si);
		
		broker.publish(new SensorInfoMessage(si));
	}
	
	@Override
	public void handleMessage(AbstractMessage msg) {
//		System.out.println(broker.getBrokerID() + " handles " + msg);
		if (msg instanceof InitConfigMessage) {
			handleInitConfigMessage((InitConfigMessage)msg);
		} else if (msg instanceof CepRuleConfigChangeMessage) {
			handleCepRuleConfigChangeMessage((CepRuleConfigChangeMessage) msg);
		}
	}
	
	private void handleCepRuleConfigChangeMessage(CepRuleConfigChangeMessage msg) {
		Gson gson = new Gson();
		
		String type = (String) msg.get("type");
		
		if (type.equals("add")) {
			String configString = (String) msg.get("config");
			CepRuleConfigWithSource r = gson.fromJson(configString,
					new TypeToken<CepRuleConfigWithSource>(){}.getType());
			addCepRule(r);
		} else if (type.equals("stop")) {
			String id = (String) msg.get("id");
//			System.out.println(id);
			stopCepRule(id);
		}
	}
	
	private void addCepRule(CepRuleConfigWithSource r) {
		// configure CEP engine
		CepRuleConfig crc = r.getCrc();
		cepRunner.changeSingleCepRuleConfig(r.getId(), crc);
		EventMessageHandler h = new EventMessageHandler(r.getId());

		// make subscriptions
		RuleSubscriptions rs = new RuleSubscriptions();
		ruleSubscriptionTable.put(r.getId(), rs);
		List<CepRuleSubscriptionRequest> subReq = r.getSubReq();
		for (CepRuleSubscriptionRequest crsr : subReq) {
			if (crsr.getAttribute().equals("local")) {
				subscribeLocal(crsr.getEventType(), r.getId());
				rs.getLocalTypes().add(crsr.getEventType());
			} else {
				String key = broker.subscribe(new Subscription(crsr.getEventType(),
						Filter.parseFilter(crsr.getAttribute())),
						h);
				rs.getRemoteKeys().add(key);
			}
		}
	}
	
	private void stopCepRule(String id) {
		cepRunner.stopRule(id);
		RuleSubscriptions rs = ruleSubscriptionTable.get(id);
		for (String localType : rs.getLocalTypes()) {
			unsubscribeLocal(localType, id);
		}
		for (String remoteKey : rs.getRemoteKeys()) {
			broker.unsubscribe(remoteKey);
		}
	}

	private void handleInitConfigMessage(InitConfigMessage msg) {
		String cepConfig = (String) msg.get("cep");
		String probeConfig = (String) msg.get("probe");
		String probeInfoList = (String) msg.get("probe-info");
		Gson gson = new Gson();
		Map<String, ProbeInfo> piList = gson.fromJson(probeInfoList, 
				new TypeToken<Map<String, ProbeInfo>>(){}.getType());
		List<CepRuleConfigWithSource> rList = gson.fromJson(cepConfig, 
				new TypeToken<List<CepRuleConfigWithSource>>(){}.getType());
		List<ProbeRuntimeConfig> prcList = gson.fromJson(probeConfig, 
				new TypeToken<List<ProbeRuntimeConfig>>(){}.getType());
		
		sensor.setProbeInfoList(piList);
		sensor.setProbeRuntimeConfigList(prcList);
		sensor.setListener(listener);
		
		cepRunner.addListener(listener);
		
		for (CepRuleConfigWithSource r : rList) {
			if (r.getCrc().isRunning())
				addCepRule(r);
		}
		new Thread(cepRunner).start();
		sensor.start();
	}
}
