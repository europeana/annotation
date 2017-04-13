package eu.europeana.annotation.definitions.model.factory;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;

public abstract class AbstractModelObjectFactory<O, E extends Enum<E>> {

	public O createModelObjectInstance(String modelObjectType) {
		return createObjectInstance(getEnumEntry(modelObjectType));
	}

	public O createObjectInstance(Enum<E> modelObjectType) {

		try {
			return (O) getClassForType(modelObjectType).newInstance();

		} catch (AnnotationInstantiationException e) {
			throw e;
		} catch (Exception e) {
			throw new AnnotationInstantiationException(
					"Cannot instantiate object for type: " + modelObjectType.toString(), e);
		}
	}

	public Class<? extends O> getClassForType(String modelObjectType) {
		Enum<E> enumEntry = getEnumEntry(modelObjectType);
		return getClassForType(enumEntry);
	}

	private Enum<E> getEnumEntry(String modelObjectType) {
		return Enum.valueOf(getEnumClass(), modelObjectType.toUpperCase());
	}

	public abstract Class<? extends O> getClassForType(Enum<E> modelType);

	public abstract Class<E> getEnumClass();

}
