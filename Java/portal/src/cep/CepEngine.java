package cep;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * In one CepEngine, all events are matched in sequence, while the states are
 * processed in parallel.
 * @author Administrator
 *
 */
public class CepEngine {
	private ArrayList<CepRule> stateList, newStateList;
	private ExecutorService executor = Executors.newCachedThreadPool();
	private CountDownLatch latch;
	private CepEngineListener listener;
	private CepRuleClassLoader classLoader;
	private CepRuleConfig crc;
	private boolean isRunning = false;
	
	public CepEngine() {
		stateList = new ArrayList<CepRule>();
		newStateList = new ArrayList<CepRule>();
		try {
			classLoader = new CepRuleClassLoader();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void addRuleInstance(CepRule rule) {
		stateList.add(rule);
	}
	
	/**
	 * Starts the engine.
	 */
	public synchronized void start() {
		if (crc == null) return;
		try {
			isRunning = true;
			// find the specified rule class and initialize an instance
			String ruleClassName = crc.getClassName();
			Class klass = classLoader.loadClass(ruleClassName);
			CepRule rule = (CepRule) klass.newInstance();
			
			// set parameters
			Map<String, Object> params = crc.getParams();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				rule.setParameter(param.getKey(), param.getValue());
			}
			// add to engine
			addRuleInstance(rule);
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
	}
	
	/**
	 * Stops the engine.
	 * @param id
	 */
	public synchronized void stopRule() {
		isRunning = false;
		stateList.clear();
	}
	
	public void setRule(CepRuleConfig crc) {
		this.crc = crc;
		if (isRunning) {
			stopRule();
			start();
		}
	}
	
	public synchronized void match(Event e) {
		if (!isRunning) return;
//		latch = new CountDownLatch(stateList.size());
		for (CepRule state : stateList) {
//			executor.submit(new CepStateMatcher(state, e));
			state.match(e);
			List<CepRule> newStates = state.getNewRuleInstances();
			if (newStates != null && !newStates.isEmpty()) 
				newStateList.addAll(newStates);
			List<Event> complexEvents = state.getComplexEvents();
			if (complexEvents != null && !complexEvents.isEmpty()) 
				listener.updateBatch(complexEvents);
		}
//		executor.shutdown();
//		System.out.println("Waiting for " + latch.getCount());
//		try {
//			latch.await();
//		} catch (InterruptedException e1) {
//			// TODO don't know what to do
//			e1.printStackTrace();
//		}
		stateList = newStateList;
		newStateList = new ArrayList<CepRule>();
	}
	private class CepStateMatcher implements Runnable {
		private CepRule state;
		private Event e;
		public CepStateMatcher(CepRule state, Event e) {
			this.state = state;
			this.e = e;
		}

		@Override
		public void run() {
			state.match(e);
			List<CepRule> newStates = state.getNewRuleInstances();
//			System.out.println(newStates);
			if (newStates != null && !newStates.isEmpty()) 
				newStateList.addAll(newStates);
			List<Event> complexEvents = state.getComplexEvents();
			if (complexEvents != null && !complexEvents.isEmpty()) 
				listener.updateBatch(complexEvents);
			latch.countDown();
		}
	}
	public void setListener(CepEngineListener listener) {
		this.listener = listener;
	}
}
