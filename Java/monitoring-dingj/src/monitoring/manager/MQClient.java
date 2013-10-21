package monitoring.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

public class MQClient {
	private static MQClient INSTANCE = new MQClient();
	DBHelper db = DBHelper.INSTANCE;
	public Map<String, ArrayList<double[]>> queryGraph(
			String machineName, String type) {
		return db.queryGraph(machineName, type);
	}
	private Set<String> machineList;
	
	
	private ConnectionFactory factory;
	private Connection conn;
	private Session session;
	private MessageConsumer consumer1, consumer2;
	private MessageProducer producer1;
	
	public static MQClient getInstance() {
		return INSTANCE;
	}
	
	public void saveEvent(String type, String machineName, String instanceName,
			String timestamp, String value) {
		db.saveEvent(type, machineName, instanceName, timestamp, value);
	}
	public ArrayList<Probe> queryProbeByMachineName(String machineName) {
		return db.queryProbeByMachineName(machineName);
	}
	public void insertProbe(String type, String machineName, int period) throws JMSException {
		db.insertProbe(type, machineName, period);
		MapMessage map = session.createMapMessage();
		map.setStringProperty("machineName", machineName);
		map.setInt(type, period);
		producer1.send(map);
	}
	public Set<String> getMachineList() {
		return machineList;
	}
	
	public String[] getTypeList() {
		return new String[] {
				"%CPU Usage",
				"%Interrupt Time",
				"%Processor Time",
				"%DPC Time",
				"Logical Disc Free Space",
				"Avg. Log. Disk sec/Transfer",
				"Avg. Log. Disk sec/Read",
				"Avg. Log. Disk sec/Write",
				"Avg. Phs. Disk sec/Transfer",
				"Avg. Phs. Disk sec/Read",
				"Avg. Phs. Disk sec/Write",
				"Memory Available MBytes",
				"Auto Close Flag",
				"Auto Create Statistics Flag",
				"Auto Shrink Flag",
				"DB Chaining Flag",
				"Auto Update Flag",
				"DB Space Free%"
		};
	}

	public MQClient() {
		try {
		int port = 60000;
		factory = new ActiveMQConnectionFactory(
				"tcp://localhost:" + port);
		conn = factory.createConnection();
		conn.setClientID("server");
		conn.start();
		
		session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		consumer1 = session.createDurableSubscriber(new ActiveMQTopic("client-start"), "cs");
		producer1 = session.createProducer(new ActiveMQTopic("change-period"));
		consumer2 = session.createDurableSubscriber(new ActiveMQTopic("event"), "ce");
		
		
		consumer1.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message m) {
				// TODO Auto-generated method stub
				TextMessage t = (TextMessage)m;
				try {
					System.out.println(t.getText() + " logged on");
					machineList.add(t.getText());
					MapMessage map = session.createMapMessage();
					List<Probe> probes = db.queryProbeByMachineName(t.getText());
					map.setStringProperty("machineName", t.getText());
					for (Probe p : probes) {
						map.setInt(p.getType(), p.getPeriod());
					}
					producer1.send(map);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		
		consumer2.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message arg0) {
				MapMessage map = (MapMessage) arg0;
				try {
					db.saveEvent(map.getStringProperty("categoryName"),
							map.getStringProperty("machineName"),
							map.getStringProperty("instanceName"),
							map.getStringProperty("timestamp"),
							map.getString("value"));
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		machineList = db.getMachineList();
	}
}
