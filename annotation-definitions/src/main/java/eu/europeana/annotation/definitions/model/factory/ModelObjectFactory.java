package eu.europeana.annotation.definitions.model.factory;

import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.SelectorObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.StyleObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.TargetObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationPartTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class ModelObjectFactory {

//	private String capitalize(String line)
//	{
//	  return Character.toUpperCase(line.charAt(0)) + line.substring(1);
//	}
	
	/**
	 * This method extracts euType from the input string with multiple types.
	 * The syntax of the euType is as follows: "euType:<Annotation Part>#<Object Type>"
	 * e.g. BODY#SEMANTIC_TAG
	 * @param typesString
	 * @return
	 */
//	public static String extractEuType(String typesString) {
//		String res = "";
//		Pattern pattern = Pattern.compile(WebAnnotationFields.EU_TYPE + ":(.*?)]");
//		Matcher matcher = pattern.matcher(typesString);
//		if (matcher.find())
//		{
//		    res = matcher.group(1);
//		}		
//		return res;
//	}
	
	public Object createModelObjectInstance(String euType) {
		String[] types = euType.split(WebAnnotationFields.SPLITTER, 2);
		String modelObjectType = types[1];
		
		AnnotationPartTypes partType = AnnotationPartTypes.valueOf(types[0]);
		Object annotationPartInstance = null;
		
//		Class<?> annotationPartClass = Class.forName(capitalize(types[0].toLowerCase()) + "ObjectFactory");
//		Object annotationPartInstance = ((AnnotationPart) annotationPartClass).getInstance().createModelObjectInstance(types[1]);

		switch (partType) {
		case AGENT:
			annotationPartInstance = AgentObjectFactory.getInstance().createModelObjectInstance(modelObjectType);
			break;
		case ANNOTATION:
			annotationPartInstance = AnnotationObjectFactory.getInstance().createModelObjectInstance(modelObjectType);
			break;
		case BODY:
			annotationPartInstance = BodyObjectFactory.getInstance().createModelObjectInstance(modelObjectType);
			break;
//		case MOTIVATION:
//			annotationPartInstance = MotivationObjectFactory.getInstance().createModelObjectInstance(types[1]);
//			break;
		case SELECTOR:
			annotationPartInstance = SelectorObjectFactory.getInstance().createModelObjectInstance(modelObjectType);
			break;
//		case SHAPE:
//			annotationPartInstance = ShapeObjectFactory.getInstance().createModelObjectInstance(types[1]);
//			break;
		case STYLE:
			annotationPartInstance = StyleObjectFactory.getInstance().createModelObjectInstance(modelObjectType);
			break;
//		case TAG:
//			annotationPartInstance = TagObjectFactory.getInstance().createModelObjectInstance(types[1]);
//			break;
		case TARGET:
			annotationPartInstance = TargetObjectFactory.getInstance().createModelObjectInstance(modelObjectType);
			break;
		default:
			break;
		}
		
		return annotationPartInstance;
	}
}
