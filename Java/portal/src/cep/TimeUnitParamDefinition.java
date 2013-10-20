package cep;


public class TimeUnitParamDefinition extends CepRuleParamDefinition {
	
	public TimeUnitParamDefinition() {
		super();
	}

	public TimeUnitParamDefinition(String paramName, String description,
			boolean isRequired) {
		super(paramName, description, isRequired);
		this.type = "timeunit";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeUnitParamSpec getParamName()=");
		builder.append(getParamName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", isRequired()=");
		builder.append(isRequired());
		builder.append("]");
		return builder.toString();
	}
}
