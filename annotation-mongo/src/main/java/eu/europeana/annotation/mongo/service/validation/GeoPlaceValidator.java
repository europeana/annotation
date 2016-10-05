package eu.europeana.annotation.mongo.service.validation;

import eu.europeana.annotation.definitions.model.entity.Place;

public interface GeoPlaceValidator extends Validator{

	//TODO: add a checked exception hierarchy for validation exceptions
	/**
	 * Validate the given place object.
	 * @param place
	 * @throws RuntimeException if the input object doesn't match the validation rules for geo locations
	 */
	public void validate(Place place) throws RuntimeException;
}
