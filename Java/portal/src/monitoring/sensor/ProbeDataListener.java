package monitoring.sensor;

import java.util.Map;

public interface ProbeDataListener {

	public void update(String eventType, Map<String, Object> map);
}
