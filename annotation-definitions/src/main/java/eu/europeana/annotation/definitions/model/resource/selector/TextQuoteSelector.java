package eu.europeana.annotation.definitions.model.resource.selector;

import java.util.Map;

public interface TextQuoteSelector extends Selector {
	
	public abstract String getPrefix();
	public abstract void setPrefix(String prefix);

	public abstract String getSuffix();
	public abstract void setSuffix(String suffix);

	public abstract Map<String,String> getExact();
	public abstract void setExact(Map<String,String> exact);
}
