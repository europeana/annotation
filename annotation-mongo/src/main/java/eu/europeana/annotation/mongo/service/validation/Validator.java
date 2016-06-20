package eu.europeana.annotation.mongo.service.validation;

public interface Validator {

		//TODO: add a checked exception hierarchy for validation exceptions
		/**
		 * Validate the given object.
		 * @param place
		 * @throws RuntimeException if the input object doesn't match the validation rules of this validator
		 */
		public void validate(Object obj) throws RuntimeException;
}
