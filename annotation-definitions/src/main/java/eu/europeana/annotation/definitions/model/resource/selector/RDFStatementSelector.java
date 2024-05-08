package eu.europeana.annotation.definitions.model.resource.selector;

public interface RDFStatementSelector extends Selector {
	
	public abstract String getHasPredicate();
	public abstract void setHasPredicate(String hasPredicate);

	public String getHasSubject();
	public void setHasSubject(String hasSubject);
	
	public String getHasObject();
	public void setHasObject(String hasObject);

	public abstract TextQuoteSelector getRefinedBy();
	public abstract void setRefinedBy(TextQuoteSelector refinedBy);
}
