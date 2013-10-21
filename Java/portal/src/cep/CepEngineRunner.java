package cep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class CepEngineRunner implements Runnable {
	private Map<String, CepEngine> engines = new ConcurrentHashMap<String, CepEngine>();
	private LinkedBlockingQueue<Event> outputEventQueue = new LinkedBlockingQueue<Event>();
	private CepEngineListener engineListener = new LinkedBlockingQueueCepEngineListener(outputEventQueue);
	
	private List<ComplexEventListener> listeners = new ArrayList<ComplexEventListener>();
	// the complex events with types specified in this set will be sent into the engine again.

	@Override
	public void run() {
		while (true) {
			try {
				Event e = outputEventQueue.take();
				for (ComplexEventListener l : listeners) {
					l.update(e);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void match(String engineId, Event e) {
		CepEngine engine = engines.get(engineId);
		if (engine!=null) engine.match(e);
	}
	
	public void match(Event e) {
		for (CepEngine engine : engines.values()) {
			engine.match(e);
		}
	}
	
	public void addListener(ComplexEventListener l) {
		listeners.add(l);
	}
	
	public void stopRule(String id) {
		CepEngine engine = engines.get(id);
		if (engine != null) engine.stopRule();
	}
	
	public void startRule(String id) {
		CepEngine engine = engines.get(id);
		if (engine != null) engine.start();
	}
	
	public void changeSingleCepRuleConfig(String id,CepRuleConfig crc) {
		CepEngine engine = engines.get(id);
		if (engine != null) {
			engine.setRule(crc);
		} else {
			engine = new CepEngine();
			engine.setRule(crc);
			engine.setListener(engineListener);
			engines.put(id, engine);
			engine.start();
		}
	}
}
