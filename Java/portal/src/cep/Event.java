package cep;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;



public class Event implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type, source;
	private long time;
	private Map<String, Serializable> attributes;
	public Event(String type) {
		this.type = type;
		attributes = new HashMap<String, Serializable>();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	public void setAttribute(String key, Serializable value) {
		attributes.put(key, value);
	}
	public String toString() {
		StringBuilder sb = new StringBuilder(type);
		sb.append("@").append(source);
		sb.append(attributes.toString());
		return sb.toString();
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getAttributesAsString() {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<String, Serializable> en : attributes.entrySet()) {
			if (!first) sb.append(",");
			else first = false;
			sb.append(en.getKey()).append(":\"").append(en.getValue().toString()).append("\"");
		}
		sb.append("}");
		return sb.toString();
	}
}
