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
}
