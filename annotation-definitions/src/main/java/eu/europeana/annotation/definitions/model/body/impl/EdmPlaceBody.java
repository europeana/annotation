package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.body.PlaceBody;
import eu.europeana.annotation.definitions.model.entity.Place;
import eu.europeana.annotation.definitions.model.entity.impl.EdmPlace;

public class EdmPlaceBody extends BaseBody implements PlaceBody{

	Place place = new EdmPlace();

	@Override
	public Place getPlace() {
		return place;
	}

	@Override
	public void setPlace(Place place) {
		this.place = place;
	}
	
	@Override
	public boolean equalsContent(Object other) {
		// TODO Auto-generated method stub
		return super.equalsContent(other);
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(!super.equals(other))
			return false;
		
		if (!(other instanceof EdmPlaceBody))
		        return false;
		
		EdmPlaceBody that = (EdmPlaceBody) other;
		
		if(this.getPlace() == null)
			return that.getPlace() == null; 
		else 
			return this.getPlace().equals(that.getPlace()); 
		
	}
	
}
