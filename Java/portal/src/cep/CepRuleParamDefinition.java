package cep;


public class CepRuleParamDefinition {
	private String paramName, description;
	private boolean isRequired;
	protected String type;
	// for numbers
	protected double upperLimit, lowerLimit;
	// for enums
	protected String[] enumTexts;

	public CepRuleParamDefinition(){}
	public CepRuleParamDefinition(String paramName, String description,
			boolean isRequired) {
		super();
		this.paramName = paramName;
		this.description = description;
		this.isRequired = isRequired;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRequired() {
		return isRequired;
	}
	
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	public String getType() {
		return type;
	}
}
