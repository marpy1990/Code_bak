package monitoring.manager;

public class CepRuleSubscriptionRequest {

	public CepRuleSubscriptionRequest(String eventType, String attribute) {
		super();
		this.eventType = eventType;
		this.attribute = attribute;
	}

	private String eventType, attribute;

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public String toString() {
		return "CepRuleSubscriptionRequest [eventType=" + eventType
				+ ", attribute=" + attribute +"]";
	}
}
