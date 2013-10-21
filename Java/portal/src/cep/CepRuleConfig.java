package cep;

import java.util.Map;

public class CepRuleConfig {
	private String className, ruleName, complexEventType;
	private boolean running;
	private Map<String, Object> params;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CepRuleConfig [className=");
		builder.append(className);
		builder.append(", ruleName=");
		builder.append(ruleName);
		builder.append(", running=");
		builder.append(running);
		builder.append(", params=");
		builder.append(params);
		builder.append("]");
		return builder.toString();
	}
	public String getComplexEventType() {
		return complexEventType;
	}
	public void setComplexEventType(String complexEventType) {
		this.complexEventType = complexEventType;
	}
}
