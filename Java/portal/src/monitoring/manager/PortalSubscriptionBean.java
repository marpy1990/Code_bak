package monitoring.manager;

public class PortalSubscriptionBean {
	private int id;
	private String key, eventTypeName, filter;

	public PortalSubscriptionBean() {
		super();
	}

	public PortalSubscriptionBean(String eventTypeName, String filter) {
		super();
		this.eventTypeName = eventTypeName;
		this.filter = filter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEventTypeName() {
		return eventTypeName;
	}

	public void setEventTypeName(String eventTypeName) {
		this.eventTypeName = eventTypeName;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		return "PortalSubscriptionBean [id=" + id + ", eventTypeName="
				+ eventTypeName + ", filter=" + filter + "]";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
