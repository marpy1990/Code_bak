package monitoring.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PortalSubscriptionList {
	private AtomicInteger nextId;
	private Map<Integer, PortalSubscriptionBean> portalSubscriptionBeanList;

	public PortalSubscriptionList() {
		nextId = new AtomicInteger();
		portalSubscriptionBeanList = new HashMap<Integer, PortalSubscriptionBean>();
	}

	public AtomicInteger getNextId() {
		return nextId;
	}

	public void setNextId(AtomicInteger nextId) {
		this.nextId = nextId;
	}

	public Map<Integer, PortalSubscriptionBean> getPortalSubscriptionBeanList() {
		return portalSubscriptionBeanList;
	}

	public void setPortalSubscriptionBeanList(
			Map<Integer, PortalSubscriptionBean> portalSubscriptionBeanList) {
		this.portalSubscriptionBeanList = portalSubscriptionBeanList;
	}
}