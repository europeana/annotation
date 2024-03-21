package eu.europeana.annotation.definitions.model.resource.selector.impl;

import eu.europeana.annotation.definitions.model.resource.selector.RDFStatementSelector;
import eu.europeana.annotation.definitions.model.resource.selector.TextQuoteSelector;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class BaseRDFStatementSelector extends BaseSelector implements RDFStatementSelector{

	public BaseRDFStatementSelector() {
		super();
		this.setSelectorType(WebAnnotationFields.RDF_STATEMENT_SELECTOR);
	}

	private String hasPredicate;
	private String hasSubject;
	private String hasObject;
	private TextQuoteSelector refinedBy;
	
	@Override
	public String getHasPredicate() {
		return hasPredicate;
	}
	@Override
	public void setHasPredicate(String hasPredicate) {
		this.hasPredicate=hasPredicate;
	}
	
	@Override
	public String getHasSubject() {
		return hasSubject;
	}
	@Override
	public void setHasSubject(String hasSubject) {
		this.hasSubject = hasSubject;
	}
	
	@Override
	public String getHasObject() {
		return hasObject;
	}
	@Override
	public void setHasObject(String hasObject) {
		this.hasObject = hasObject;
	}
	
	@Override
	public TextQuoteSelector getRefinedBy() {
		return refinedBy;
	}
	@Override
	public void setRefinedBy(TextQuoteSelector refinedBy) {
		this.refinedBy=refinedBy;
	}
	
}
