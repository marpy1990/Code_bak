package monitoring.manager;

import java.util.HashMap;
import java.util.Map;

public class EventTypeDefinition {
	private String id, description;
	private Map<String, EventAttributeType> attributes
		= new HashMap<String, EventAttributeType>();
	public EventTypeDefinition() {
		
	}
	public EventTypeDefinition(String id, String description) {
		super();
		this.id = id;
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, EventAttributeType> getAttributes() {
		return attributes;
	}
	public EventAttributeType getAttribute(String key) {
		return attributes.get(key);
	}
	public EventAttributeType putAttribute(String key, EventAttributeType value) {
		return attributes.put(key, value);
	}
}
