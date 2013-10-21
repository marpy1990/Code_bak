package cep;

import java.util.List;

public class CepRuleDefinition {
	
	public CepRuleDefinition(){}
	public CepRuleDefinition(String name, String description,
			String author, List<CepRuleParamDefinition> paramDefinitions) {
		super();
		this.name = name;
		this.description = description;
		this.author = author;
		this.paramDefinitions = paramDefinitions;
	}
	private String name, description, author, className;
	private List<CepRuleParamDefinition> paramDefinitions;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public boolean addParamDefinition(CepRuleParamDefinition paramSpec) {
		return paramDefinitions.add(paramSpec);
	}
	public CepRuleParamDefinition getParamDefinition(int index) {
		return paramDefinitions.get(index);
	}
	public CepRuleParamDefinition removeParamDefinition(int index) {
		return paramDefinitions.remove(index);
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CepRuleSpec [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", author=");
		builder.append(author);
		builder.append(", paramSpecs=");
		builder.append(paramDefinitions);
		builder.append("]");
		return builder.toString();
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<CepRuleParamDefinition> getParamDefinitions() {
		return paramDefinitions;
	}
	public void setParamDefinitions(List<CepRuleParamDefinition> paramDefinitions) {
		this.paramDefinitions = paramDefinitions;
	}
}
