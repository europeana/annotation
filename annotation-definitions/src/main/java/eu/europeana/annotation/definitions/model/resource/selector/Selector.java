package eu.europeana.annotation.definitions.model.resource.selector;

import java.util.Map;

import eu.europeana.annotation.definitions.model.vocabulary.SelectorTypes;

public interface Selector{

	public abstract void setDimensionMap(Map<String, Integer> dimensionMap);

	public abstract Map<String, Integer> getDimensionMap();
	
	public abstract void setSelectorTypeEnum(SelectorTypes selectorType);

	public abstract void setSelectorType(String selectorType);

	public abstract String getSelectorType();

	

}
