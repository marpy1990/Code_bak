package monitoring.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import monitoring.sensor.ProbeRuntimeConfig;
import cep.CepRuleConfig;

import com.google.gson.Gson;


public class Node implements Serializable {
	private Map<String, ProbeRuntimeConfig> probeRuntimeConfigList;
	private Map<String, CepRuleConfigWithSource> cepRuleConfigList;
	private SensorInfo info;
	private transient boolean isOnline;
	
	public Node() {
		probeRuntimeConfigList = new HashMap<String, ProbeRuntimeConfig>();
		cepRuleConfigList = new HashMap<String, CepRuleConfigWithSource>();
	}
	public SensorInfo getInfo() {
		return info;
	}
	public void setInfo(SensorInfo info) {
		this.info = info;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public String toString() {
		return info.getId();
	}
	public Collection<ProbeRuntimeConfig> getProbeRuntimeConfigList() {
		return probeRuntimeConfigList.values();
	}
	
	public void putProbeRuntimeConfig(ProbeRuntimeConfig prc) {
		this.probeRuntimeConfigList.put(prc.getPrcId(), prc);
	}
	public void removeProbeRuntimeConfig(String id) {
		this.probeRuntimeConfigList.remove(id);
	}
	
	public Collection<CepRuleConfigWithSource> getCepRuleConfigList() {
		return cepRuleConfigList.values();
	}
	
	public String putCepRuleConfig(CepRuleConfigWithSource r) {
		String id = r.getId();
		if (id == null) {
			id = generateCepRuleConfigId();
			r.setId(id);
		}
		this.cepRuleConfigList.put(id, r);
		return id;
	}
	
	public CepRuleConfigWithSource findCepRuleConfigById(String id) {
		return cepRuleConfigList.get(id);
	}
	
	public void removeCepRuleConfig(String id) {
		this.cepRuleConfigList.remove(id);
	}
	
	private String generateCepRuleConfigId() {
		return String.valueOf(System.currentTimeMillis());
	}
}
