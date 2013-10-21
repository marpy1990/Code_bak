package monitoring.sensor;

import java.util.HashSet;
import java.util.Set;

public class RuleSubscriptions {
	private Set<String> localTypes = new HashSet<String>();
	private Set<String> remoteKeys = new HashSet<String>();
	public Set<String> getLocalTypes() {
		return localTypes;
	}
	public Set<String> getRemoteKeys() {
		return remoteKeys;
	}
}
