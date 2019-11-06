package eu.europeana.annotation.utils.serialize;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdProperty;
import org.apache.stanbol.commons.jsonld.JsonLdPropertyValue;
import org.apache.stanbol.commons.jsonld.JsonLdResource;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.GraphBody;
import eu.europeana.annotation.definitions.model.body.PlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.entity.Place;
import eu.europeana.annotation.definitions.model.graph.Graph;
import eu.europeana.annotation.definitions.model.resource.ResourceDescription;
import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.ContextTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.corelib.definitions.edm.entity.Address;

public class AnnotationLdSerializer extends JsonLd {

	/**
	 * @param annotation
	 */
	public AnnotationLdSerializer(Annotation annotation) {
		super();
		setPropOrderComparator(new AnnotationsJsonComparator());
		setAnnotation(annotation);
	}

	public AnnotationLdSerializer() {
		super();
		setPropOrderComparator(new AnnotationsJsonComparator());
	}

	/**
	 * Adds the given annotation to this JsonLd object using the resource's
	 * subject as key. If the key is NULL and there does not exist a resource
	 * with an empty String as key the resource will be added using an empty
	 * String ("") as key.
	 * 
	 * @param annotation
	 */
	public JsonLdResource setAnnotation(Annotation annotation) {

		setUseTypeCoercion(false);
		setUseCuries(true);
		setUsedNamespaces(namespacePrefixMap);

		JsonLdResource jsonLdResource = new JsonLdResource();
		jsonLdResource.setSubject("");
		jsonLdResource.putProperty(WebAnnotationFields.AT_CONTEXT, ContextTypes.ANNO.getJsonValue());

		if (!StringUtils.isBlank(annotation.getType()))
			jsonLdResource.putProperty(WebAnnotationFields.TYPE, annotation.getType());
		else
			jsonLdResource.putProperty(WebAnnotationFields.TYPE, WebAnnotationFields.ANNOTATION_TYPE);

		if (annotation.getAnnotationId() != null)
			jsonLdResource.putProperty(WebAnnotationFields.ID, annotation.getAnnotationId().toHttpUrl());

		if (annotation.getCreated() != null)
			jsonLdResource.putProperty(WebAnnotationFields.CREATED,
					TypeUtils.convertDateToStr(annotation.getCreated()));

		if (annotation.getGenerated() != null)
			jsonLdResource.putProperty(WebAnnotationFields.GENERATED,
					TypeUtils.convertDateToStr(annotation.getGenerated()));

		if (!StringUtils.isBlank(annotation.getMotivation()))
			jsonLdResource.putProperty(WebAnnotationFields.MOTIVATION, annotation.getMotivation());

		putBody(annotation, jsonLdResource);
		putTarget(annotation, jsonLdResource);

		putGenerator(annotation, jsonLdResource);
		putCreator(annotation, jsonLdResource);
		putStyledBy(annotation, jsonLdResource);

		putExtensions(annotation, jsonLdResource);
		
		putStringProperty(WebAnnotationFields.CANONICAL, annotation.getCanonical(), jsonLdResource);
		putStringArrayProperty(WebAnnotationFields.VIA, annotation.getVia(), jsonLdResource, true);
		
		put(jsonLdResource);
		
		return jsonLdResource;
	}
		
	/**
	 * the sameAs and equivalentTo are not in the context these properties need
	 * to be removed.
	 * 
	 * @param annotation
	 * @param jsonLdResource
	 */
	@Deprecated
	protected void putExtensions(Annotation annotation, JsonLdResource jsonLdResource) {
		// TODO: remove this method
		if (annotation.getSameAs() != null)
			jsonLdResource.putProperty(WebAnnotationFields.SAME_AS, annotation.getSameAs());
		if (annotation.getEquivalentTo() != null)
			jsonLdResource.putProperty(WebAnnotationFields.EQUIVALENT_TO, annotation.getEquivalentTo());
	}

	protected void putTarget(Annotation annotation, JsonLdResource jsonLdResource) {
		if (isJsonObjectInput(annotation.getTarget().getInputString())) {
			JsonLdProperty targetProperty = addTargetProperty(annotation);
			if (targetProperty != null)
				jsonLdResource.putProperty(targetProperty);
		} else {
			if (annotation.getInternalType().equals(AnnotationTypes.OBJECT_LINKING.name())) {
				if (annotation.getTarget().getValue() != null){
					//1 target
					putStringProperty(WebAnnotationFields.TARGET, annotation.getTarget().getValue(), jsonLdResource);
//					jsonLdResource.putProperty(WebAnnotationFields.TARGET, annotation.getTarget().getValue());
				}else if (annotation.getTarget().getValues() != null && !annotation.getTarget().getValues().isEmpty()){
					//array as target
					putListProperty(WebAnnotationFields.TARGET, annotation.getTarget().getValues(), jsonLdResource, true);
//					JsonLdProperty targetProperty = buildListProperty(WebAnnotationFields.TARGET,
//							annotation.getTarget().getValues(), true);
//					if (targetProperty != null)
//						jsonLdResource.putProperty(targetProperty);
				}
			} else {
				if(annotation.getTarget().getInputString() != null)
					jsonLdResource.putProperty(WebAnnotationFields.TARGET, annotation.getTarget().getInputString());
				else
					jsonLdResource.putProperty(WebAnnotationFields.TARGET, annotation.getTarget().getHttpUri());
			}
		}
	}

	protected void putBody(Annotation annotation, JsonLdResource jsonLdResource) {
		//if (!annotation.getInternalType().equals(AnnotationTypes.OBJECT_LINKING.name())) {
		if (annotation.getBody()!= null) {
			// tag or comment, not linking
			if (isJsonObjectInput(annotation.getBody().getInputString())) {
				// annotation.getBody().setInputString(null);
				JsonLdProperty bodyProperty = addBodyProperty(annotation);
				if (bodyProperty != null)
					jsonLdResource.putProperty(bodyProperty);
			} else {
				if (annotation.getBody().getValues() != null && annotation.getBody().getValues().size() > 0) {
					putListProperty(WebAnnotationFields.BODY, annotation.getBody().getValues(), jsonLdResource, true);
//					JsonLdProperty bodyProperty = buildListProperty(WebAnnotationFields.BODY,
//							annotation.getBody().getValues(), true);
//					if (bodyProperty != null)
//						jsonLdResource.putProperty(bodyProperty);
				} else{
					if(annotation.getTarget().getInputString() != null)
						putStringProperty(WebAnnotationFields.BODY, annotation.getBody().getInputString(), jsonLdResource);
//						jsonLdResource.putProperty(WebAnnotationFields.BODY, annotation.getBody().getInputString());
					else
						putStringProperty(WebAnnotationFields.BODY, annotation.getBody().getHttpUri(), jsonLdResource);
//						jsonLdResource.putProperty(WebAnnotationFields.BODY, annotation.getBody().getHttpUri());
				}
			}
		}
	}

	protected void putStyledBy(Annotation annotation, JsonLdResource jsonLdResource) {
		JsonLdProperty styledByProperty = addStyledByProperty(annotation);
		if (styledByProperty != null)
			jsonLdResource.putProperty(styledByProperty);
	}

	protected void putCreator(Annotation annotation, JsonLdResource jsonLdResource) {
		if (annotation.getCreator().getInputString() == null
				|| isJsonObjectInput(annotation.getCreator().getInputString())) {
			JsonLdProperty annotatedByProperty = addCreator(annotation);
			if (annotatedByProperty != null)
				jsonLdResource.putProperty(annotatedByProperty);
		} else {
			putStringProperty(WebAnnotationFields.CREATOR, annotation.getCreator().getInputString(), jsonLdResource);
		}
	}

	protected void putGenerator(Annotation annotation, JsonLdResource jsonLdResource) {
		if (annotation.getGenerator().getInputString() == null
				|| isJsonObjectInput(annotation.getGenerator().getInputString())) {
			JsonLdProperty serializedByProperty = addGeneratorProperty(annotation);
			if (serializedByProperty != null)
				jsonLdResource.putProperty(serializedByProperty);
		} else {
			// input string is a single value
			putStringProperty(WebAnnotationFields.GENERATOR, annotation.getGenerator().getInputString(), jsonLdResource);
		}
	}

//	TODO: review the implementation of this method against last standard specification 
	private JsonLdProperty addTargetProperty(Annotation annotation) {
		JsonLdProperty targetProperty = new JsonLdProperty(WebAnnotationFields.TARGET);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();

		if (annotation != null && annotation.getTarget() != null) {
//			if (!StringUtils.isBlank(annotation.getTarget().getInputString()))
//				propertyValue.getValues().put(WebAnnotationFields.INPUT_STRING,
//						annotation.getTarget().getInputString());
			
			List<String> types = annotation.getTarget().getType();
			if (types != null && !types.isEmpty())
				putTypeProperty(propertyValue, types);		
			
			putSpecificResourceProps(annotation.getTarget(), propertyValue);

			if (annotation.getTarget().getSelector() != null) {
				addSelectorProperty(annotation, propertyValue);
			}

			if (propertyValue.getValues().size() == 0)
				return null;
			targetProperty.addValue(propertyValue);
		} else {
			return null;
		}
		return targetProperty;
	}

	//TODO: review the implementation of this method against latest specifications 
	protected void addSelectorProperty(Annotation annotation, JsonLdPropertyValue targetPropertyValue) {
		JsonLdProperty selectorProperty = new JsonLdProperty(WebAnnotationFields.SELECTOR);
		JsonLdPropertyValue selectorPropertyValue = new JsonLdPropertyValue();

		if (!StringUtils.isBlank(annotation.getTarget().getSelector().getSelectorType()))
			selectorPropertyValue.getValues().put(WebAnnotationFields.TYPE,
					annotation.getTarget().getSelector().getSelectorType());

		if (annotation.getTarget().getSelector() != null
				&& !StringUtils.isBlank(annotation.getTarget().getSelector().getDimensionMap().toString()))
			selectorPropertyValue.getValues().put(WebAnnotationFields.DIMENSION_MAP,
					JsonUtils.mapToStringExt(annotation.getTarget().getSelector().getDimensionMap()));

		if (selectorPropertyValue.getValues().size() != 0) {
			selectorProperty.addValue(selectorPropertyValue);
			targetPropertyValue.putProperty(selectorProperty);
		}
	}

	//TODO: review the implementation of this method against latest specifications 
	protected void addSourceProperty(Annotation annotation, JsonLdPropertyValue targetPropertyValue) {
		JsonLdProperty sourceProperty = new JsonLdProperty(WebAnnotationFields.SOURCE);
		JsonLdPropertyValue sourcePropertyValue = new JsonLdPropertyValue();

		
		if (!StringUtils.isBlank(annotation.getTarget().getSourceResource().getContentType()))
			sourcePropertyValue.getValues().put(WebAnnotationFields.FORMAT,
					annotation.getTarget().getSourceResource().getContentType());
		
		if (!StringUtils.isBlank(annotation.getTarget().getSourceResource().getHttpUri()))
			sourcePropertyValue.getValues().put(WebAnnotationFields.ID,
					annotation.getTarget().getSourceResource().getHttpUri());
		
		if (sourcePropertyValue.getValues().size() != 0) {
			sourceProperty.addValue(sourcePropertyValue);
		}

		targetPropertyValue.putProperty(sourceProperty);
	}

//	private JsonLdProperty addConceptProperty(Concept concept) {
//		JsonLdProperty conceptProperty = new JsonLdProperty(WebAnnotationFields.CONCEPT);
//		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
//
//		if (concept != null) {
//			addListToPropertyValue(concept.getNotation(), propertyValue, WebAnnotationFields.NOTATION);
//			addListToPropertyValue(concept.getNarrower(), propertyValue, WebAnnotationFields.NARROWER);
//			addListToPropertyValue(concept.getBroader(), propertyValue, WebAnnotationFields.BROADER);
//			addListToPropertyValue(concept.getRelated(), propertyValue, WebAnnotationFields.RELATED);
//
//			addMapToPropertyValue(concept.getPrefLabel(), propertyValue, WebAnnotationFields.PREF_LABEL);
//			addMapToPropertyValue(concept.getHiddenLabel(), propertyValue, WebAnnotationFields.HIDDEN_LABEL);
//			addMapToPropertyValue(concept.getAltLabel(), propertyValue, WebAnnotationFields.ALT_LABEL);
//			if (propertyValue.getValues().size() != 0) {
//				conceptProperty.addValue(propertyValue);
//			}
//		}
//		return conceptProperty;
//	}

	/**
	 * @param annotation
	 * @return
	 */
	private JsonLdProperty addBodyProperty(Annotation annotation) {
		JsonLdProperty bodyProperty = new JsonLdProperty(WebAnnotationFields.BODY);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();

		//avoid empty body
		if (annotation.getBody() == null)
			return null;

		List<String> types = annotation.getBody().getType();
		if (types != null && !types.isEmpty()){
			putTypeProperty(propertyValue, types);		
		}

		putSpecificResourceProps(annotation.getBody(), propertyValue);

		if(annotation.getBody() instanceof PlaceBody)
			putPlaceProperties(annotation, propertyValue);
		
		if(annotation.getBody() instanceof GraphBody)
			putGraphProperties(annotation, propertyValue);
		
		if(annotation.getBody() instanceof EdmAgentBody)
			putAgentProperties(annotation, propertyValue);
		
		if(annotation.getBody() instanceof VcardAddressBody)
			putVcardAddressProperties(annotation, propertyValue);
		
		bodyProperty.addValue(propertyValue);
		return bodyProperty;
	}

	protected void putSpecificResourceProps(SpecificResource specificResource, JsonLdPropertyValue propertyValue) {
		putResourceDescriptionProps(specificResource, propertyValue);
		
		if (!StringUtils.isBlank(specificResource.getSource()))
			propertyValue.getValues().put(WebAnnotationFields.SOURCE, specificResource.getSource());
		if (specificResource.getSourceResource() != null)
			;// TODO add serialization of resource
		if (!StringUtils.isBlank(specificResource.getPurpose()))
			propertyValue.getValues().put(WebAnnotationFields.PURPOSE, specificResource.getPurpose());
		if (!StringUtils.isBlank(specificResource.getScope()))
			propertyValue.getValues().put(WebAnnotationFields.SCOPE, specificResource.getScope());
	}

	protected void putResourceDescriptionProps(ResourceDescription resourceDescription, JsonLdPropertyValue propertyValue) {
		
		if (StringUtils.isNotBlank(resourceDescription.getHttpUri()))
			propertyValue.getValues().put(WebAnnotationFields.ID, resourceDescription.getHttpUri());
		if (StringUtils.isNotBlank(resourceDescription.getContext()))
			propertyValue.getValues().put(WebAnnotationFields.AT_CONTEXT, resourceDescription.getContext());
		if (StringUtils.isNotBlank(resourceDescription.getValue()))
			propertyValue.getValues().put(WebAnnotationFields.VALUE, resourceDescription.getValue());
		if (StringUtils.isNotBlank(resourceDescription.getLanguage()))
			propertyValue.getValues().put(WebAnnotationFields.LANGUAGE, resourceDescription.getLanguage());
		if (StringUtils.isNotBlank(resourceDescription.getContentType()))
			propertyValue.getValues().put(WebAnnotationFields.FORMAT, resourceDescription.getContentType());
		if (StringUtils.isNotBlank(resourceDescription.getTitle()))
			propertyValue.getValues().put(WebAnnotationFields.TITLE, resourceDescription.getTitle());
	}

	protected void putPlaceProperties(Annotation annotation, JsonLdPropertyValue propertyValue) {
		Place place = ((PlaceBody) annotation.getBody()).getPlace();
		if(place != null){
			if(!StringUtils.isBlank(place.getLatitude()))
					propertyValue.getValues().put(WebAnnotationFields.LATITUDE, place.getLatitude());
			
			if(!StringUtils.isBlank(place.getLatitude()))
				propertyValue.getValues().put(WebAnnotationFields.LONGITUDE, place.getLongitude());
		}
	}
	
	/**
	 * Synchronizing for semantic tag Vcard address
	 * @param annotation
	 * @param propertyValue
	 */
	protected void putVcardAddressProperties(Annotation annotation, JsonLdPropertyValue propertyValue) {
		Address address = ((VcardAddressBody) annotation.getBody()).getAddress();
		if(address != null){
			if(!StringUtils.isBlank(address.getVcardStreetAddress()))
				propertyValue.getValues().put(WebAnnotationFields.STREET_ADDRESS, address.getVcardStreetAddress());

			if(!StringUtils.isBlank(address.getVcardPostalCode()))
				propertyValue.getValues().put(WebAnnotationFields.POSTAL_CODE, address.getVcardPostalCode());

			if(!StringUtils.isBlank(address.getVcardPostOfficeBox()))
				propertyValue.getValues().put(WebAnnotationFields.POST_OFFICE_BOX, address.getVcardPostOfficeBox());

			if(!StringUtils.isBlank(address.getVcardLocality()))
				propertyValue.getValues().put(WebAnnotationFields.LOCALITY, address.getVcardLocality());

			if(!StringUtils.isBlank(address.getVcardHasGeo()))
				propertyValue.getValues().put(WebAnnotationFields.REGION, address.getVcardHasGeo());

			if(!StringUtils.isBlank(address.getVcardCountryName()))
				propertyValue.getValues().put(WebAnnotationFields.COUNTRY_NAME, address.getVcardCountryName());
		}
	}
	
	protected void putAgentProperties(Annotation annotation, JsonLdPropertyValue propertyValue) {
		
		EdmAgent agentBody = (EdmAgent) ((EdmAgentBody) annotation.getBody()).getAgent();
		if(agentBody != null) {
			if (agentBody.getPrefLabel() != null
					&& !StringUtils.isBlank(agentBody.getPrefLabel().toString()))
				addMapToPropertyValue(agentBody.getPrefLabel(), propertyValue, WebAnnotationFields.PREF_LABEL);
			if (agentBody.getPlaceOfBirth() != null
					&& !StringUtils.isBlank(agentBody.getPlaceOfBirth().toString()))
				addMapToPropertyValue(agentBody.getPlaceOfBirth(), propertyValue, WebAnnotationFields.PLACE_OF_BIRTH);
			if (agentBody.getPlaceOfDeath() != null
					&& !StringUtils.isBlank(agentBody.getPlaceOfDeath().toString()))
				addMapToPropertyValue(agentBody.getPlaceOfDeath(), propertyValue, WebAnnotationFields.PLACE_OF_DEATH);
			if (agentBody.getDateOfBirth() != null
					&& !StringUtils.isBlank(agentBody.getDateOfBirth().toString()))
				addListToPropertyValue(agentBody.getDateOfBirth(), propertyValue, WebAnnotationFields.DATE_OF_BIRTH);
			if (agentBody.getDateOfDeath() != null
					&& !StringUtils.isBlank(agentBody.getDateOfDeath().toString()))
				addListToPropertyValue(agentBody.getDateOfDeath(), propertyValue, WebAnnotationFields.DATE_OF_DEATH);
		}
	}
		
	private void putGraphProperties(Annotation annotation, JsonLdPropertyValue propertyValue) {
		Graph graph = ((GraphBody) annotation.getBody()).getGraph();
		if(graph != null){
			JsonLdProperty graphProperty = new JsonLdProperty(WebAnnotationFields.GRAPH);
			JsonLdPropertyValue graphValue = new JsonLdPropertyValue();
			graphValue.getValues().put(WebAnnotationFields.ID, graph.getResourceUri());
			if(StringUtils.isNotBlank(graph.getContext()))
				graphValue.getValues().put(WebAnnotationFields.AT_CONTEXT, graph.getContext());
			
			if(graph.getNodeUri() != null)//the node has only the id
				graphValue.getValues().put(graph.getRelationName(), graph.getNodeUri());
			else{ // the node is a resource{
				JsonLdProperty nodeProperty = new JsonLdProperty(graph.getRelationName());
				JsonLdPropertyValue nodeValue = new JsonLdPropertyValue();
				//nodeValue.getValues().put(WebAnnotationFields.ID, graph.getNode());
				putResourceDescriptionProps(graph.getNode(), nodeValue);
				nodeProperty.addValue(nodeValue);
				graphValue.putProperty(nodeProperty);
			}
			
			graphProperty.addValue(graphValue);
			propertyValue.putProperty(graphProperty);
		}
		
	}

	protected void putTypeProperty(JsonLdPropertyValue propertyValue, List<String> types) {
			propertyValue.putProperty(
					buildListProperty(WebAnnotationFields.TYPE, types, true));
	}

	private JsonLdProperty addGeneratorProperty(Annotation annotation) {
		JsonLdProperty generatorProperty = new JsonLdProperty(WebAnnotationFields.GENERATOR);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
		Agent agent = annotation.getGenerator();
		addAgentByProperty(propertyValue, agent);
		if (propertyValue.getValues().size() == 0)
			return null;
		generatorProperty.addValue(propertyValue);
		return generatorProperty;
	}

	private void addAgentByProperty(JsonLdPropertyValue propertyValue, Agent agent) {

		if (!StringUtils.isBlank(agent.getHttpUrl()))
			propertyValue.getValues().put(WebAnnotationFields.ID, agent.getHttpUrl());

		if (!StringUtils.isBlank(agent.getType())) // convert internal type to json value
			propertyValue.getValues().put(WebAnnotationFields.TYPE,
					AgentTypes.valueOf(agent.getInternalType()).getJsonValue());
		if (!StringUtils.isBlank(agent.getName()))
			propertyValue.getValues().put(WebAnnotationFields.NAME, agent.getName());
		if (!StringUtils.isBlank(agent.getHomepage()))
			propertyValue.getValues().put(WebAnnotationFields.HOMEPAGE, agent.getHomepage());
		if (!StringUtils.isBlank(agent.getNickname()))
			propertyValue.getValues().put(WebAnnotationFields.NICKNAME, agent.getNickname());
		if (!StringUtils.isBlank(agent.getEmail()))
			propertyValue.getValues().put(WebAnnotationFields.EMAIL, agent.getEmail());
		if (!StringUtils.isBlank(agent.getEmail_Sha1()))
			propertyValue.getValues().put(WebAnnotationFields.EMAIL_SHA1, agent.getEmail_Sha1());
	}

	private JsonLdProperty addCreator(Annotation annotation) {
		JsonLdProperty creator = new JsonLdProperty(WebAnnotationFields.CREATOR);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
		Agent agent = annotation.getCreator();
		addAgentByProperty(propertyValue, agent);
		if (propertyValue.getValues().size() == 0)
			return null;
		creator.addValue(propertyValue);
		return creator;
	}

	private boolean isJsonObjectInput(String inputString) {
		return inputString != null && (inputString.contains("{") 
				|| (inputString.indexOf(WebAnnotationFields.BODY_VALUE) == 1));
	}

	private JsonLdProperty addStyledByProperty(Annotation annotation) {
		JsonLdProperty styledByProperty = new JsonLdProperty(WebAnnotationFields.STYLED_BY);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
		Style style = annotation.getStyledBy();
		if (style != null && !StringUtils.isBlank(style.getHttpUri()))
			propertyValue.setType(style.getHttpUri());
		if (style != null && !StringUtils.isBlank(style.getContentType()))
			propertyValue.getValues().put(WebAnnotationFields.STYLE_CLASS, style.getContentType());
		if (style != null && !StringUtils.isBlank(style.getValue()))
			propertyValue.getValues().put(WebAnnotationFields.SOURCE, style.getValue());
		if (propertyValue.getValues().size() == 0)
			return null;
		styledByProperty.addValue(propertyValue);
		return styledByProperty;
	}

	
}
