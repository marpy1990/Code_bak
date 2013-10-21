package cep;

public class NumberParamDefinition extends CepRuleParamDefinition {
	public NumberParamDefinition() {
		super();
	}

	public NumberParamDefinition(String paramName, String description,
			boolean isRequired, double upperLimit, double lowerLimit) {
		super(paramName, description, isRequired);
		this.type = "number";
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
	}

	public double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	
}
