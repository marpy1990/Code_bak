package monitoring.sensor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Controls a probe according to the ProbeRuntimeConfig
 * @author Administrator
 *
 */
public class ProbeRunner implements Runnable {
	private ProbeRuntimeConfig config;
	private ScheduledExecutorService executor;
	private List<ProbeDataListener> listeners = new CopyOnWriteArrayList<ProbeDataListener>();
	private ScheduledFuture<?> future;
	private ProbeClassLoader classLoader;
	private String className;
	private Probe probe;
	public ProbeRunner(ScheduledExecutorService executor, ProbeClassLoader classLoader, String className) {
		this.executor = executor;
		this.classLoader = classLoader;
		this.className = className;
	}
	public void changeConfig(ProbeRuntimeConfig newConfig) {
		if (config == null || config.isChanged(newConfig)) {
			config = newConfig;
			if (future!=null) future.cancel(false);
			if (newConfig.isRunning()) {
				try {
					Class klass = classLoader.loadClass(className);
					probe = (Probe) klass.newInstance();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
//				System.out.println(config.getInterval()+config.getTimeunit().toString());
				future = executor.scheduleAtFixedRate(this, 0, config.getInterval(), config.getTimeunit());
			}
		}
	}
	public void addListener(ProbeDataListener listener) {
		listeners.add(listener);
	}
	@Override
	public void run() {
		if (probe != null) {
			for (ProbeDataListener listener : listeners) {
				listener.update(config.getEventType(), probe.getData()); 
			}
		}
	}
}
