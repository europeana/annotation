package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.impl.AgentBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmPlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.FullTextResourceBody;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.body.impl.RdfGraphBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticLinkBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.body.impl.SpecificResourceBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public class BodyObjectFactory extends
		AbstractModelObjectFactory<Body, BodyInternalTypes> {

	private static BodyObjectFactory singleton;

	// force singleton usage
	private BodyObjectFactory() {
	};

	public static BodyObjectFactory getInstance() {

		if (singleton == null) {
			synchronized (BodyObjectFactory.class) {
				singleton = new BodyObjectFactory();
			}
		}

		return singleton;

	}

	@Override
	public Body createObjectInstance(Enum<BodyInternalTypes> modelObjectType) {
		Body res = super.createObjectInstance(modelObjectType);
		res.setInternalType(modelObjectType.name());
		return res;
	}
		
	@Override
	public Class<? extends Body> getClassForType(Enum<BodyInternalTypes> modelType) {
		// TEXT, TAG, SEMANTIC_TAG, SEMANTIC_LINK;
		Class<? extends Body> returnType = null;
		BodyInternalTypes bodyType = BodyInternalTypes.valueOf(modelType.name());
		switch (bodyType) {
		case SEMANTIC_LINK:
			returnType = SemanticLinkBody.class;
			break;
		case SEMANTIC_TAG:
			returnType = SemanticTagBody.class;
			break;
		case TAG:
			returnType = PlainTagBody.class;
			break;
		case TEXT:
			returnType = TextBody.class; 
			break;
		case GEO_TAG:
			returnType = EdmPlaceBody.class; 
			break;
		case GRAPH:
			returnType = RdfGraphBody.class; 
			break;
		case SPECIFIC_RESOURCE:
			returnType = SpecificResourceBody.class;
			break;
		case FULL_TEXT_RESOURCE:
			returnType = FullTextResourceBody.class;
			break;
		case ENTITY:
			returnType = AgentBody.class;
			break;
		case VCARD_ADDRESS:
			returnType = VcardAddressBody.class;
			break;
		case LINK:
			throw new AnnotationInstantiationException("Bodies of type LINK must be empty!");
		
		default:
			throw new AnnotationInstantiationException("unsupported (internal) body type: " + modelType);

		}

		return returnType;
	}

	@Override
	public Class<BodyInternalTypes> getEnumClass() {
		return BodyInternalTypes.class;
	}

}
