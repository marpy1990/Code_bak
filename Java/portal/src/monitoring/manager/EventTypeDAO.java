package monitoring.manager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cep.NumberParamDefinition;
import cep.StringParamDefinition;
import cep.TimeUnitParamDefinition;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

public class EventTypeDAO {
	private Map<String, EventTypeDefinition> eventTypeList;
	private String configFileName;
	
	public EventTypeDAO(String configFileName) {
		this.configFileName = configFileName;
		load();
	}
	
	public void saveProbeDefinition(EventTypeDefinition def) {
		eventTypeList.put(def.getId(), def);
		save();
	}
	
	public EventTypeDefinition findByName(String id) {
		return eventTypeList.get(id);
	}
	
	public Collection<EventTypeDefinition> findAll() {
		return eventTypeList.values();
	}
	
	public void insert(EventTypeDefinition def) {
		eventTypeList.put(def.getId(), def);
		save();
	}
	
	public void update(EventTypeDefinition def) {
		eventTypeList.put(def.getId(), def);
		save();
	}
	
	public void delete(String id) {
		eventTypeList.remove(id);
		save();
	}
	
	public void deleteAll() {
		eventTypeList.clear();
		save();
	}

	private void load() {
		try {
			eventTypeList = new Gson().fromJson(new FileReader(configFileName),
					new TypeToken<Map<String, EventTypeDefinition>>(){}.getType());
			if (eventTypeList == null) {
				eventTypeList = new HashMap<String, EventTypeDefinition>();
				save();
			}
		} catch (Exception e) {
			e.printStackTrace();
			eventTypeList = new HashMap<String, EventTypeDefinition>();
			save();
		}
	}
	
	private synchronized void save() {
		try {
			FileWriter writer = new FileWriter(configFileName);
			new Gson().toJson(eventTypeList, writer);
			writer.close();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		EventTypeDAO dao = new EventTypeDAO("event-type.json");
		dao.deleteAll();
		
		EventTypeDefinition def = new EventTypeDefinition("Cpu", "Describes a detected CPU data");
		def.putAttribute("user", EventAttributeType.NUMBER);
		def.putAttribute("sys", EventAttributeType.NUMBER);
		def.putAttribute("wait", EventAttributeType.NUMBER);
		def.putAttribute("combined", EventAttributeType.NUMBER);
		def.putAttribute("idle", EventAttributeType.NUMBER);
		
		dao.insert(def);
		
		def = new EventTypeDefinition("HighCpu", "Describes a high CPU data");
		def.putAttribute("user", EventAttributeType.NUMBER);
		def.putAttribute("sys", EventAttributeType.NUMBER);
		def.putAttribute("wait", EventAttributeType.NUMBER);
		def.putAttribute("combined", EventAttributeType.NUMBER);
		def.putAttribute("idle", EventAttributeType.NUMBER);
		
		dao.insert(def);
		
		def = new EventTypeDefinition("Memory", "Describes a detected memory usage data");
		def.putAttribute("usedperc", EventAttributeType.NUMBER);
		def.putAttribute("freeperc", EventAttributeType.NUMBER);
		dao.insert(def);
		
	}
}
