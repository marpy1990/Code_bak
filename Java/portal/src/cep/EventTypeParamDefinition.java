package cep;


public class EventTypeParamDefinition extends CepRuleParamDefinition {

	public EventTypeParamDefinition(String name, String description, boolean isRequired) {
		super(name, description, isRequired);
		this.type="eventType";
	}

}
