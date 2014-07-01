package eu.europeana.annotation.definitions.model.factory;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;

public abstract class AbstractModelObjectFactory <O, E extends Enum<E>> {

//	 private Class<E> enumClass;
//
//	 protected AbstractModelObjectFactory<O, E >(Class<E> enumClass1) {
//	        this.enumClass = enumClass1;
//	 }
	
	public O createModelObjectInstance(String modelObjectType) {
		return createObjectInstance(getEnumEntry(modelObjectType));
	}
	
	public O createObjectInstance(Enum<E> modelObjectType) {
		
		try {
			return (O) getClassForType(modelObjectType).newInstance();
			
			//O modelObjectClass = getClassForType(agentType).newInstance();
			//agent.setType(agentType.name());
		} catch (Exception e) {
			throw new AnnotationInstantiationException(
					modelObjectType.toString(), e);
		}
	}
	
	public Class<? extends O> getClassForType(String modelObjectType){
		Enum<E> enumEntry = getEnumEntry(modelObjectType);
		return getClassForType(enumEntry);
	}



	private Enum<E> getEnumEntry(String modelObjectType) {
		return  Enum.valueOf(getEnumClass(), modelObjectType);
	}
	
	public abstract Class<? extends O> getClassForType(Enum<E> modelType);

	public abstract Class<E> getEnumClass();
	
		
}
