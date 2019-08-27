package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.model.entity.Concept;
import eu.europeana.annotation.definitions.model.entity.impl.BaseConcept;
import eu.europeana.annotation.definitions.model.entity.impl.BaseSkosConcept;
import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.ConceptTypes;

@Deprecated
public class ConceptObjectFactory extends AbstractModelObjectFactory<Concept, ConceptTypes>{

	private static ConceptObjectFactory singleton;

	//force singleton usage
	private ConceptObjectFactory(){};
	
	public static ConceptObjectFactory getInstance() {

		if (singleton == null) {
			synchronized (ConceptObjectFactory.class) {
				singleton = new ConceptObjectFactory();
			}
		}

		return singleton;

	}

	@Override
	public Class<? extends Concept> getClassForType(Enum<ConceptTypes> modelType) {
				Class<? extends Concept> returnType = null;
				ConceptTypes conceptType = ConceptTypes.valueOf(modelType.name());
				switch (conceptType){
				case BASE_CONCEPT:
					returnType = BaseConcept.class;
					break;
				case SKOS_CONCEPT:
					returnType = BaseSkosConcept.class;
					break;					
				default:
					throw new AnnotationAttributeInstantiationException("Not Supported concept type: " + modelType);
				
				} 
				
				return returnType;
	}

	@Override
	public Class<ConceptTypes> getEnumClass() {
		return ConceptTypes.class;
	}

	
}
