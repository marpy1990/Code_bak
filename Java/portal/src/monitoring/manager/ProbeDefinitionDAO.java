package monitoring.manager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import monitoring.sensor.ProbeInfo;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

public class ProbeDefinitionDAO {
	private Map<String, ProbeInfo> probeInfoList;
	private String configFileName;
	
	public ProbeDefinitionDAO(String configFileName) {
		this.configFileName = configFileName;
		load();
	}
	public void saveProbeDefinition(ProbeInfo def) {
		probeInfoList.put(def.getId(), def);
		save();
	}
	
	public ProbeInfo findById(String id) {
		return probeInfoList.get(id);
	}
	
	public Collection<ProbeInfo> findAll() {
		return probeInfoList.values();
	}
	
	public void insert(ProbeInfo def) {
		probeInfoList.put(def.getId(), def);
		save();
	}
	
	public void update(ProbeInfo def) {
		probeInfoList.put(def.getId(), def);
		save();
	}
	
	public void delete(String id) {
		probeInfoList.remove(id);
		save();
	}
	
	public void deleteAll() {
		probeInfoList.clear();
		save();
	}

	private void load() {
		try {
			probeInfoList = new Gson().fromJson(new FileReader(configFileName),
					new TypeToken<Map<String, ProbeInfo>>(){}.getType());
			if (probeInfoList == null) {
				probeInfoList = new HashMap<String, ProbeInfo>();
				save();
			}
		} catch (Exception e) {
			e.printStackTrace();
			probeInfoList = new HashMap<String, ProbeInfo>();
			save();
		}
	}
	
	private synchronized void save() {
		try {
			FileWriter writer = new FileWriter(configFileName);
			new Gson().toJson(probeInfoList, writer);
			writer.close();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ProbeDefinitionDAO dao = new ProbeDefinitionDAO("probe-info.json");
		dao.deleteAll();
		ProbeInfo def = new ProbeInfo();
		def.setAuthor("Yu Cheng");
		def.setClassName("cn.edu.sjtu.monitoring.probe.CpuUsageProbe");
		def.setDescription("Cpu Usage Probe");
		def.setId("cpu");
		def.setName("Cpu Probe");
		dao.insert(def);
		def = new ProbeInfo();
		def.setAuthor("Yu Cheng");
		def.setClassName("cn.edu.sjtu.monitoring.probe.MemoryUsageProbe");
		def.setDescription("Memory Usage Probe");
		def.setId("memory");
		def.setName("Memory Probe");
		dao.insert(def);
		def = new ProbeInfo();
		def.setAuthor("Yu Cheng");
		def.setClassName("cn.edu.sjtu.monitoring.probe.CpuSpikeGenerator");
		def.setDescription("Cpu Spike Generator");
		def.setId("cpu-spike");
		def.setName("CPU Spike");
		dao.insert(def);
		def = new ProbeInfo();
		def.setAuthor("Yu Cheng");
		def.setClassName("cn.edu.sjtu.monitoring.probe.HighCpuGenerator");
		def.setDescription("High Cpu Usage Data Generator");
		def.setId("high-cpu");
		def.setName("High CPU");
		dao.insert(def);
	}
	public Map<String, ProbeInfo> getProbeInfoList() {
		return probeInfoList;
	}
}
