package monitoring.manager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

public class PortalSubscriptionDAO {
	private PortalSubscriptionList list;
	private String configFileName;
	
	public PortalSubscriptionDAO(String configFileName) {
		this.configFileName = configFileName;
		load();
	}
	private int getNextId() {
		return list.getNextId().incrementAndGet();
	}
	
	public PortalSubscriptionBean findById(int id) {
		return list.getPortalSubscriptionBeanList().get(id);
	}
	
	public Collection<PortalSubscriptionBean> findAll() {
		return list.getPortalSubscriptionBeanList().values();
	}
	
	public void insert(PortalSubscriptionBean def) {
		int id = getNextId();
		def.setId(id);
		list.getPortalSubscriptionBeanList().put(id, def);
		save();
	}
	
	public void update(PortalSubscriptionBean def) {
		list.getPortalSubscriptionBeanList().put(def.getId(), def);
		save();
	}
	
	public void delete(int id) {
		list.getPortalSubscriptionBeanList().remove(id);
		save();
	}
	
	public void deleteAll() {
		list.getPortalSubscriptionBeanList().clear();
		save();
	}

	private void load() {
		try {
			list = new Gson().fromJson(new FileReader(configFileName),
					new TypeToken<PortalSubscriptionList>(){}.getType());
			if (list.getPortalSubscriptionBeanList() == null) {
				list = new PortalSubscriptionList();
				save();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			list = new PortalSubscriptionList();
			save();
		} catch (Exception e) {
			e.printStackTrace();
			list = new PortalSubscriptionList();
			save();
		}
	}
	
	private synchronized void save() {
		try {
			FileWriter writer = new FileWriter(configFileName);
			new Gson().toJson(list, writer);
			writer.close();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		PortalSubscriptionDAO dao = new PortalSubscriptionDAO("portal-subscription.json");
		dao.deleteAll();
		dao.insert(new PortalSubscriptionBean("CpuSpike", ""));
//		dao.insert(new PortalSubscriptionBean("HighCpu", ""));
//		dao.insert(new PortalSubscriptionBean("3", "Memory", ""));
		dao.insert(new PortalSubscriptionBean("UnbalancedCluster", ""));
	}
	public Map<Integer, PortalSubscriptionBean> getPortalSubscriptionBeanList() {
		return list.getPortalSubscriptionBeanList();
	}
}
