package cep;

import java.util.List;

public interface CepEngineListener {
	public void update(Event e);
	public void updateBatch(List<Event> e);
}
