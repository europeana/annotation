package eu.europeana.annotation.mongo.service.validation.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.entity.Place;
import eu.europeana.annotation.mongo.service.validation.GeoPlaceValidator;

public class EdmPlaceValidatorImpl implements GeoPlaceValidator{

	public static final int MAX_LAT = 90;
	public static final int MIN_LAT = -MAX_LAT;
	public static final int MAX_LONG = 180;
	public static final int MIN_LONG = -MAX_LONG;
	NumberFormat geoCoordinateFormat;
	
	@Override
	public void validate(Place place) {
		
		try {
			validateGeoCoordinate(place.getLatitude(), MIN_LAT, MAX_LAT);
			validateGeoCoordinate(place.getLongitude(), MIN_LONG, MAX_LONG);
		} catch (Throwable e) {
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_INVALID_BODY + place , e);
		}
		
	}
	
	private void validateGeoCoordinate(String coordinate, double minRange, double maxRange) throws ParseException {
		double val = getGeoCoordinateFormat().parse(coordinate).doubleValue();
		if(val<minRange || val>maxRange)
			throw new NumberFormatException(coordinate + " value is out of range: " + minRange +" - " + maxRange);
	}
	
	protected NumberFormat getGeoCoordinateFormat() {
		if(geoCoordinateFormat == null){
			geoCoordinateFormat = NumberFormat.getNumberInstance(Locale.US);
			geoCoordinateFormat.setMaximumFractionDigits(6);
			geoCoordinateFormat.setMaximumIntegerDigits(3);
		}
		
		return geoCoordinateFormat;
	}

	@Override
	public void validate(Object obj) throws RuntimeException {
		if(obj instanceof Place)
			validate((Place)obj);
		else
			throw new AnnotationValidationException(this.getClass() + " is not able to validate objects of type: " + obj.getClass());
				
	}
	
}
