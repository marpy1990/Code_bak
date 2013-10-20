package monitoring.sensor;

import java.util.Map;

public interface Probe {
	public Map<String, Object> getData();
}
