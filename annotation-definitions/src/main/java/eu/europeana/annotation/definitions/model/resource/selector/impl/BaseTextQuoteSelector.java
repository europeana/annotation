package eu.europeana.annotation.definitions.model.resource.selector.impl;

import java.util.Map;

import eu.europeana.annotation.definitions.model.resource.selector.TextQuoteSelector;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class BaseTextQuoteSelector extends BaseSelector implements TextQuoteSelector{

	public BaseTextQuoteSelector() {
		super();
		this.setSelectorType(WebAnnotationFields.TEXT_QUOTE_SELECTOR);
	}

	private String prefix;
	private String suffix;
	private Map<String,String> exact;
	
	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix=prefix;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix=suffix;
	}

	@Override
	public Map<String, String> getExact() {
		return exact;
	}

	@Override
	public void setExact(Map<String, String> exact) {
		this.exact=exact;
	}
	
}
