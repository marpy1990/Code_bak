package cep;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractCepRule implements CepRule {
	private List<Event> complexEventList;
	private List<CepRule> newRuleList;
	
	protected void createComplexEvent(Event e) {
		if (complexEventList == null) complexEventList = new ArrayList<Event>();
		complexEventList.add(e);
	}
	
	protected void createNewRuleInstance(CepRule instance) {
		if (newRuleList == null) newRuleList = new ArrayList<CepRule>();
		newRuleList.add(instance);
	}
	
	@Override
	public List<Event> getComplexEvents() {
		List<Event> ret = complexEventList;
		complexEventList = new ArrayList<Event>();
		return ret;
	}
	@Override
	public List<CepRule> getNewRuleInstances() {
		List<CepRule> ret = newRuleList;
		newRuleList = new ArrayList<CepRule>();
		return ret;
	}
	
}
