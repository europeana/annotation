package eu.europeana.annotation.definitions.model.view.impl;

import java.util.Date;

import eu.europeana.annotation.definitions.model.view.AnnotationView;

public class AnnotationViewResourceListItem implements AnnotationView {
	
	private String id;
	
	private Date timestampUpdated;
	
	/**
	 * Getter for timestampUpdated 
	 * @param id the id to set
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Getter for timestampUpdated 
	 * @param id the id to set
	 */
	@Override
	public Date getTimestampUpdated() {
		return this.timestampUpdated;
	}

	/**
	 * Setter for id 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Setter for timestampUpdated 
	 * @param timestampUpdated the timestampUpdated to set
	 */
	public void setTimestampUpdated(Date timestampUpdated) {
		this.timestampUpdated = timestampUpdated;
	}

}
