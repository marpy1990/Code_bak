package cep;

public class StringParamDefinition extends CepRuleParamDefinition {

	public StringParamDefinition() {
		super();
	}

	public StringParamDefinition(String paramName, String description,
			boolean isRequired) {
		super(paramName, description, isRequired);
		this.type = "string";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StringParamSpec [getParamName()=");
		builder.append(getParamName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", isRequired()=");
		builder.append(isRequired());
		builder.append("]");
		return builder.toString();
	}
}
