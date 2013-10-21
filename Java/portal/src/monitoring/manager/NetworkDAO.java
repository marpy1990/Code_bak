package monitoring.manager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import monitoring.sensor.ProbeRuntimeConfig;
import cep.CepRule;
import cep.CepRuleClassLoader;
import cep.CepRuleConfig;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

public class NetworkDAO {
	private Network network;
	private String configFileName;
	
	public NetworkDAO() {
		this("network.json");
	}
	
	public NetworkDAO(String configFileName) {
		this.configFileName = configFileName;
		load();
	}

	public Network getNetwork() {
		return network;
	}
	
	public void addNode(SensorInfo si) {
		Node node = new Node();
		node.setInfo(si);
		node.setOnline(true);
		network.addNode(node);
		save();
	}
	
	public void login(String nodeId) {
		network.getNode(nodeId).setOnline(true);
	}
	
	public void putCepRuleConfig(String nodeId, CepRuleConfigWithSource crc) {
		network.getNode(nodeId).putCepRuleConfig(crc);
		save();
	}
	
	public void removeCepRuleConfig(String nodeId, String crcId) {
		network.getNode(nodeId).removeCepRuleConfig(crcId);
		save();
	}
	
	public void putProbeRuntimeConfig(String nodeId, ProbeRuntimeConfig prc) {
		network.getNode(nodeId).putProbeRuntimeConfig(prc);
		save();
	}
	
	public void removeProbeRuntimeConfig(String nodeId, String prcId) {
		network.getNode(nodeId).removeProbeRuntimeConfig(prcId);
		save();
	}

	public synchronized void save() {
		try {
			FileWriter writer = new FileWriter(configFileName);
			new Gson().toJson(network, writer);
			writer.close();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void load() {
		try {
			network = new Gson().fromJson(new FileReader(configFileName),
					new TypeToken<Network>(){}.getType());
			if (network == null) {
				network = new Network();
				save();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			network = new Network();
			save();
		}
	}
	
	private String obtainCEType(Map<String, Object> params, String className) {
		try {
			CepRuleClassLoader classloader = new CepRuleClassLoader();
			CepRule rule = (CepRule) classloader.loadClass(className).newInstance();
			for (Map.Entry<String, Object> en : params.entrySet()) {
				rule.setParameter(en.getKey(), en.getValue());
			}
			String cetype = rule.getComplexEventType();
			System.out.println(cetype);
			return cetype;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		Manager.setSimulationMode(false);
		NetworkDAO dao = new NetworkDAO();
		SensorInfo si;
		
		ProbeRuntimeConfig cpuSpikePrc = new ProbeRuntimeConfig();
		cpuSpikePrc.setInterval(1);
		cpuSpikePrc.setTimeunit(TimeUnit.SECONDS);
		cpuSpikePrc.setProbeId("cpu-spike");
		cpuSpikePrc.setRunning(true);
		cpuSpikePrc.setEventType("Cpu");
		cpuSpikePrc.setPrcId("0");
		
		ProbeRuntimeConfig highCpuPrc = new ProbeRuntimeConfig();
		highCpuPrc.setInterval(1);
		highCpuPrc.setTimeunit(TimeUnit.SECONDS);
		highCpuPrc.setProbeId("high-cpu");
		highCpuPrc.setRunning(true);
		highCpuPrc.setPrcId("1");
		highCpuPrc.setEventType("Cpu");
		
		for (int i=20001; i<=20020; i++) {
			si = new SensorInfo();
			String id = "127.0.0.1:" + i;
			si.setId(id);
			si.setIp("127.0.0.1");
			String loc = "Lab0" + ((i-20001)/5+1);
			si.setLocation(loc);
			si.setMachineName("MAC_"+i);
			si.setOs("Windows Server 2008 R2");
			si.setPort(i);
			dao.addNode(si);
			
			if (loc.equals("Lab03")) {
				dao.putProbeRuntimeConfig(id, highCpuPrc);
			} else {
				if ((i-20001)%5==1) {
					dao.putProbeRuntimeConfig(id, highCpuPrc);
				} else {
					dao.putProbeRuntimeConfig(id, cpuSpikePrc);
				}
			}
		}
		System.exit(0);
	}
}
