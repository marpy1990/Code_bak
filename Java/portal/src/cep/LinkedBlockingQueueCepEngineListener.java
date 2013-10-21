package cep;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueCepEngineListener implements CepEngineListener {

	private LinkedBlockingQueue<Event> eventQueue;
	
	public LinkedBlockingQueueCepEngineListener(
			LinkedBlockingQueue<Event> eventQueue) {
		this.eventQueue = eventQueue;
	}

	@Override
	public void update(Event e) {
		try {
			eventQueue.put(e);
		} catch (InterruptedException e1) {
		}
	}

	@Override
	public void updateBatch(List<Event> e) {
		eventQueue.addAll(e);
	}
	
}
