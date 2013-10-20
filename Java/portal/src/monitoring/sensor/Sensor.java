package monitoring.sensor;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import monitoring.manager.SensorInfo;
import cep.CepEngineRunner;

public class Sensor {
	private ProbeClassLoader classLoader;
	private ConcurrentHashMap<String, ProbeRunner> probeRunnerList = new ConcurrentHashMap<String, ProbeRunner>();
	private List<ProbeRuntimeConfig> probeRuntimeConfigList;
	private Map<String, ProbeInfo> probeInfoList;
	private ScheduledExecutorService executor;
	
	private CepEngineRunner cepRunner = new CepEngineRunner();
	private SensorInfo info;
	private ProbeDataListener listener;
	public Sensor() {
		try {
			classLoader = new ProbeClassLoader();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		executor = new ScheduledThreadPoolExecutor(4);
	}
	
	public void setListener(ProbeDataListener listener) {
		this.listener = listener;
	}
	public void start() {
		for (ProbeRuntimeConfig config : probeRuntimeConfigList) {
			if (probeRunnerList.containsKey(config.getPrcId())) continue;
			ProbeInfo pi = probeInfoList.get(config.getProbeId());
			if (pi==null) continue;
			ProbeRunner runner = new ProbeRunner(executor, classLoader,
					pi.getClassName());
			runner.addListener(listener);
			runner.changeConfig(config);
			probeRunnerList.put(config.getProbeId(), runner);
		}
	}
	public void stopAll() {
		executor.shutdown();
	}
	
	
	
	public void changeProbeRuntimeConfig() {
		for (ProbeRuntimeConfig prc : probeRuntimeConfigList) {
			ProbeRunner runner = probeRunnerList.get(prc.getProbeId());
			if (runner == null) {
				ProbeInfo pi = probeInfoList.get(prc.getProbeId());
				if (pi==null) continue;
				runner = new ProbeRunner(executor, classLoader, pi.getClassName());
				probeRunnerList.put(prc.getProbeId(), runner);
				runner.addListener(listener);
			} else 
				runner.changeConfig(prc);
		}
	}
	
	public SensorInfo getInfo() {
		return info;
	}
	public void setInfo(SensorInfo info) {
		this.info = info;
	}

	public ProbeClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ProbeClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ConcurrentHashMap<String, ProbeRunner> getProbeRunnerList() {
		return probeRunnerList;
	}

	public void setProbeRunnerList(
			ConcurrentHashMap<String, ProbeRunner> probeRunnerList) {
		this.probeRunnerList = probeRunnerList;
	}

	public List<ProbeRuntimeConfig> getProbeRuntimeConfigList() {
		return probeRuntimeConfigList;
	}

	public void setProbeRuntimeConfigList(
			List<ProbeRuntimeConfig> probeRuntimeConfigList) {
		this.probeRuntimeConfigList = probeRuntimeConfigList;
	}

	public Map<String, ProbeInfo> getProbeInfoList() {
		return probeInfoList;
	}

	public void setProbeInfoList(Map<String, ProbeInfo> probeInfoList) {
		this.probeInfoList = probeInfoList;
	}

	public ScheduledExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	public CepEngineRunner getCepRunner() {
		return cepRunner;
	}

	public void setCepRunner(CepEngineRunner cepRunner) {
		this.cepRunner = cepRunner;
	}

	public ProbeDataListener getListener() {
		return listener;
	}

}
