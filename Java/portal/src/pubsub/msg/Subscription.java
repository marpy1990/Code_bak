package pubsub.msg;

import java.io.Serializable;

import pubsub.filter.Filter;

public class Subscription implements Serializable{
	private String topic, id;
	private Filter filter;
	private static final long serialVersionUID = -873133137295006708L;
	public Subscription(String topic) {
		this.filter = null;
		this.topic = topic;
	}
	public Subscription(String topic, Filter filter) {
		this.filter = filter;
		this.topic = topic;
	}
	public String getTopic() {
		return topic;
	}

	public String getID() {
		return id;
	}
	
	public Filter getFilter() {
		return filter;
	}
	public void setID(String nextSubID) {
		this.id = nextSubID;
	}
	public boolean covers(AbstractMessage msg) {
		// both ok with topic and filter
		if (topic.equals(msg.getTopic()) && 
				(filter == null || filter.eval(msg) == true)) {
			return true;
		} else return false;
	}
	
	public String toString() {
		return "#"+id+":"+topic + "@"+filter;
	}
	public int hashCode() {
		return id.hashCode();
	}
	public boolean equals(Object o) {
		// same id <=> same subscription
		if (o instanceof Subscription && this.id.equals(((Subscription) o).id)){
			return true;
		} else return false;
	}
}
