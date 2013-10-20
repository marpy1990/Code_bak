package monitoring.manager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cep.CepRuleDefinition;
import cep.CepRuleParamDefinition;
import cep.EnumParamDefinition;
import cep.EventTypeParamDefinition;
import cep.NumberParamDefinition;
import cep.StringParamDefinition;
import cep.TimeUnitParamDefinition;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

public class CepRuleDefinitionDAO {
	private Map<String, CepRuleDefinition> cepRuleDefinitionList;
	private String configFileName;
	
	public CepRuleDefinitionDAO(String configFileName) {
		this.configFileName = configFileName;
		load();
	}
	
	public void saveProbeDefinition(CepRuleDefinition def) {
		cepRuleDefinitionList.put(def.getName(), def);
		save();
	}
	
	public CepRuleDefinition findByName(String id) {
		return cepRuleDefinitionList.get(id);
	}
	
	public Collection<CepRuleDefinition> findAll() {
		return cepRuleDefinitionList.values();
	}
	
	public void insert(CepRuleDefinition def) {
		cepRuleDefinitionList.put(def.getName(), def);
		save();
	}
	
	public void update(CepRuleDefinition def) {
		cepRuleDefinitionList.put(def.getName(), def);
		save();
	}
	
	public void delete(String id) {
		cepRuleDefinitionList.remove(id);
		save();
	}
	
	public void deleteAll() {
		cepRuleDefinitionList.clear();
		save();
	}

	private void load() {
		try {
			cepRuleDefinitionList = new Gson().fromJson(new FileReader(configFileName),
					new TypeToken<Map<String, CepRuleDefinition>>(){}.getType());
			if (cepRuleDefinitionList == null) {
				cepRuleDefinitionList = new HashMap<String, CepRuleDefinition>();
				save();
			}
		} catch (Exception e) {
			e.printStackTrace();
			cepRuleDefinitionList = new HashMap<String, CepRuleDefinition>();
			save();
		}
	}
	
	private synchronized void save() {
		try {
			FileWriter writer = new FileWriter(configFileName);
			new Gson().toJson(cepRuleDefinitionList, writer);
			writer.close();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CepRuleDefinitionDAO dao = new CepRuleDefinitionDAO("cep-rule.json");
		dao.deleteAll();
		
		List<CepRuleParamDefinition> paramSpecs = new ArrayList<CepRuleParamDefinition>();
		paramSpecs.add(new EventTypeParamDefinition("incomingEventType", "The name of incoming event type", true));
		paramSpecs.add(new StringParamDefinition("complexEventType", "The name of complex event type", true));
		paramSpecs.add(new StringParamDefinition("attribute", "The name of the attribute in the incoming event type to be filtered", true));
		paramSpecs.add(new NumberParamDefinition("lowerLimit", "The lower limit", true, Double.MAX_VALUE, Double.MIN_VALUE));
		paramSpecs.add(new NumberParamDefinition("upperLimit", "The upper limit", true, Double.MAX_VALUE, Double.MIN_VALUE));
		CepRuleDefinition def = new CepRuleDefinition("Filter", 
				"A basic attribute filter", 
				"Yu Cheng", paramSpecs);
		def.setClassName("cn.edu.sjtu.cep.rule.Filter");
		dao.insert(def);
		
		paramSpecs = new ArrayList<CepRuleParamDefinition>();
		paramSpecs.add(new EventTypeParamDefinition("incomingEventType", "The name of incoming event type", true));
		paramSpecs.add(new StringParamDefinition("complexEventType", "The name of complex event type", true));
		paramSpecs.add(new StringParamDefinition("attribute", "The name of the attribute to be monitored", true));
		paramSpecs.add(new NumberParamDefinition("spikeThreshold", "X is considered a spike if X > threshold * avg(X)", true, Double.MAX_VALUE, 1));
		paramSpecs.add(new TimeUnitParamDefinition("timeWindow", "The time interval", true));
		def = new CepRuleDefinition("SpikeDetector", 
				"A spike detector", 
				"Yu Cheng", paramSpecs);
		def.setClassName("cn.edu.sjtu.cep.rule.SpikeDetector");
		dao.insert(def);
		
		paramSpecs = new ArrayList<CepRuleParamDefinition>();
		paramSpecs.add(new EventTypeParamDefinition("incomingEventType", "The name of incoming event type", true));
		paramSpecs.add(new StringParamDefinition("complexEventType", "The name of complex event type", true));
		paramSpecs.add(new StringParamDefinition("attribute", "The name of the monitored attribute", true));
		paramSpecs.add(new NumberParamDefinition("imbalanceThreshold", "A cluster is considered unbalanced if the standard deviation of the tracked attribute exceeds the threshold.", true, Double.MAX_VALUE, 0));
		paramSpecs.add(new TimeUnitParamDefinition("timeWindow", "The time interval", true));
		def = new CepRuleDefinition("UnbalancedClusterDetector", 
				"An unbalanced cluster detector", 
				"Yu Cheng", paramSpecs);
		def.setClassName("cn.edu.sjtu.cep.rule.UnbalancedClusterDetector");
		dao.insert(def);
		
		paramSpecs = new ArrayList<CepRuleParamDefinition>();
		paramSpecs.add(new EventTypeParamDefinition("incomingEventType", "The name of incoming event type", true));
		paramSpecs.add(new StringParamDefinition("complexEventType", "The name of complex event type", true));
		paramSpecs.add(new StringParamDefinition("attribute", "The name of the monitored attribute", true));
		paramSpecs.add(new NumberParamDefinition("fluctuatingThreshold", "A metric is considered fluctuating if the standard deviation of the tracked attribute exceeds the threshold.", true, Double.MAX_VALUE, 0));
		paramSpecs.add(new TimeUnitParamDefinition("timeWindow", "The time interval", true));
		def = new CepRuleDefinition("FluctuatingMetricDetector", 
				"Fires a warning when a metric is fluctuating", 
				"Yu Cheng", paramSpecs);
		def.setClassName("cn.edu.sjtu.cep.rule.FluctuatingDetector");
		dao.insert(def);
		
		paramSpecs = new ArrayList<CepRuleParamDefinition>();
		paramSpecs.add(new EventTypeParamDefinition("incomingEventType", "The name of incoming event type", true));
		paramSpecs.add(new StringParamDefinition("complexEventType", "The name of complex event type", true));
		paramSpecs.add(new StringParamDefinition("attribute", "The name of the monitored attribute", true));
		paramSpecs.add(new EnumParamDefinition("aggregateType", "The type of the aggregate function", true, 
				new String[]{"average", "sum", "min", "max"}));
		paramSpecs.add(new TimeUnitParamDefinition("timeWindow", "The time interval", true));
		def = new CepRuleDefinition("Aggregator", 
				"Performs aggregate functions on the specified attribute", 
				"Yu Cheng", paramSpecs);
		def.setClassName("cn.edu.sjtu.cep.rule.Aggregator");
		dao.insert(def);
	}
}
