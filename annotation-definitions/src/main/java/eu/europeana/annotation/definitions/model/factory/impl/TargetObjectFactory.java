package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.target.impl.AnnotationTarget;
import eu.europeana.annotation.definitions.model.target.impl.ImageTarget;
import eu.europeana.annotation.definitions.model.target.impl.TextTarget;
import eu.europeana.annotation.definitions.model.target.impl.WebPageTarget;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class TargetObjectFactory extends AbstractModelObjectFactory<Target, TargetTypes>{

	private static TargetObjectFactory singleton;

	//force singleton usage
	private TargetObjectFactory(){};
	
	public static TargetObjectFactory getInstance() {

		if (singleton == null) {
			synchronized (TargetObjectFactory.class) {
				singleton = new TargetObjectFactory();
			}
		}

		return singleton;

	}

	@Override
	public Target createObjectInstance(Enum<TargetTypes> modelObjectType) {
		Target res = super.createObjectInstance(modelObjectType);
		res.setInternalType(modelObjectType.name());
		return res;
	}

	@Override
	public Class<? extends Target> getClassForType(Enum<TargetTypes> modelType) {
				Class<? extends Target> returnType = null;
				TargetTypes targetType = TargetTypes.valueOf(modelType.name());
				switch (targetType){
				case ANNOTATION:
					returnType = AnnotationTarget.class;
					break;
				case IMAGE:
					returnType = ImageTarget.class;
					break;
				case TEXT:
					returnType = TextTarget.class;
					break;
				case WEB_PAGE:
					returnType = WebPageTarget.class;
					break;
				default:
					throw new AnnotationAttributeInstantiationException("Not Supported target type: " + modelType);
				
				} 
				
				return returnType;
	}

	@Override
	public Class<TargetTypes> getEnumClass() {
		return TargetTypes.class;
	}

	
}
