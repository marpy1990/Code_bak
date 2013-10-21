package pubsub.msg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public abstract class AbstractMessage implements Serializable {
	private Map<String, Serializable> keys = new HashMap<String, Serializable>();
	private static final long serialVersionUID = 1L;
	private String id = null;
	private String topic;
	private LinkedHashSet<String> path = new LinkedHashSet<String>();
	public AbstractMessage(String topic) {
		this.topic = topic;
	}
	public void setID(String id) {
		this.id = id;
	}
	public String getID(){
		return id;
	}
	public String getTopic() {
		return topic;
	}
	public void put(String key, Serializable value){
		keys.put(key, value);
	}
	public Serializable get(String key){
		return keys.get(key);
	}
	public boolean hasBeenHandledBy(String id) {
		return path.contains(id);
	}
	public void recordPath(String id) {
		path.add(id);
	}
	public LinkedHashSet<String> getPath() {
		return path;
	}
}
