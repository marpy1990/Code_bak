package cep;

import java.util.Arrays;

public class EnumParamDefinition extends CepRuleParamDefinition {
	
	public EnumParamDefinition() {
		super();
	}

	public EnumParamDefinition(String paramName, String description,
			boolean isRequired, String[] enumTexts) {
		super(paramName, description, isRequired);
		this.enumTexts = enumTexts;
		this.type = "enum";
	}

	public String[] getEnumTexts() {
		return enumTexts;
	}

	public void setEnumTexts(String[] enumTexts) {
		this.enumTexts = enumTexts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnumParamSpec [enumTexts=");
		builder.append(Arrays.toString(enumTexts));
		builder.append(", getParamName()=");
		builder.append(getParamName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", isRequired()=");
		builder.append(isRequired());
		builder.append("]");
		return builder.toString();
	}
}
