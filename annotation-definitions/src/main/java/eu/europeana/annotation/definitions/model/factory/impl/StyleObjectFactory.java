package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.resource.style.impl.CssStyle;
import eu.europeana.annotation.definitions.model.vocabulary.StyleTypes;

public class StyleObjectFactory extends AbstractModelObjectFactory<Style, StyleTypes>{

	private static StyleObjectFactory singleton;

	//force singleton usage
	private StyleObjectFactory(){};
	
	public static StyleObjectFactory getInstance() {

		if (singleton == null) {
			synchronized (StyleObjectFactory.class) {
				singleton = new StyleObjectFactory();
			}
		}

		return singleton;

	}

	@Override
	public Class<? extends Style> getClassForType(Enum<StyleTypes> modelType) {
				Class<? extends Style> returnType = null;
				StyleTypes styleType = StyleTypes.valueOf(modelType.name());
				switch (styleType){
				case CSS:
					returnType = CssStyle.class;
					break;
					
				default:
					throw new AnnotationAttributeInstantiationException("Not Supported target type: " + modelType);
				
				} 
				
				return returnType;
	}

	@Override
	public Class<StyleTypes> getEnumClass() {
		return StyleTypes.class;
	}

	
}
