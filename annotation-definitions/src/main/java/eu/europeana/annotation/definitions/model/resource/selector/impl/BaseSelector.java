package eu.europeana.annotation.definitions.model.resource.selector.impl;

import java.util.HashMap;
import java.util.Map;

import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.vocabulary.SelectorTypes;

public abstract class BaseSelector implements Selector{

	private String selectorType;
	private Map<String, Integer> dimensionMap;
	
	@Override
	public String getSelectorType() {
		return selectorType;
	}

	@Override
	public void setSelectorType(String selectorType) {
		this.selectorType = selectorType;
	}
	
	@Override
	public void setSelectorTypeEnum(SelectorTypes selectorType) {
		this.selectorType = selectorType.name();
	}

	
	@Override
	public Map<String, Integer> getDimensionMap() {
		return dimensionMap;
	}

	@Override
	public void setDimensionMap(Map<String, Integer> dimensionMap) {
		this.dimensionMap = dimensionMap;
	}

	protected BaseSelector(){
		dimensionMap = new HashMap<String, Integer>(5);
	}

	@Override
	public String toString() {
		String res = "\t### Selector ###\n";
		
		if (getSelectorType() != null) 
			res = res + "\t\t" + "selectorType:" + getSelectorType().toString() + "\n";
		if (getDimensionMap() != null) 
			res = res + "\t\t" + "dimensionMap:" + getDimensionMap().toString() + "\n";
//		res = res + "\t\t" + "dimensionMap:" + TypeUtils.mapToStringExt(getDimensionMap()) + "\n";
		return res;
	}	
	
	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Selector)) {
	        return false;
	    }

	    Selector that = (Selector) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getSelectorType() != null) && (that.getSelectorType() != null) &&
	    		(!this.getSelectorType().equals(that.getSelectorType()))) {
	    	System.out.println("Selector objects have different 'selectorType' fields.");
	    	res = false;
	    }
	    
	    if ((this.getDimensionMap() != null) && (that.getDimensionMap() != null) &&
	    		(!this.getDimensionMap().equals(that.getDimensionMap()))) {
	    	System.out.println("Selector objects have different 'dimensionMap' fields.");
	    	res = false;
	    }
	    
	    return res;
	}			
	
}
