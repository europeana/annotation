package eu.europeana.annotation.utils.serialize;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdProperty;
import org.apache.stanbol.commons.jsonld.JsonLdPropertyValue;
import org.apache.stanbol.commons.jsonld.JsonLdResource;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.utils.JsonUtils;

public class AnnotationLdSerializer extends JsonLd {

  
	/**
     * @param annotation
     */
    public AnnotationLdSerializer(Annotation annotation) {
    	setAnnotation(annotation);
    }
    
	/**
     * Adds the given annotation to this JsonLd object using the resource's subject as key. If the key is NULL
     * and there does not exist a resource with an empty String as key the resource will be added using
     * an empty String ("") as key. 
     * 
     * @param annotation
     */
    public void setAnnotation(Annotation annotation) {
                
    	setUseTypeCoercion(false);
        setUseCuries(true);
//        addNamespacePrefix(WebAnnotationFields.OA_CONTEXT, WebAnnotationFields.OA);
        //TODO: verify if the following check is needed
        //if(isApplyNamespaces())
        	setUsedNamespaces(namespacePrefixMap);

        JsonLdResource jsonLdResource = new JsonLdResource();
        jsonLdResource.setSubject("");
    	jsonLdResource.putProperty(WebAnnotationFields.AT_CONTEXT, WebAnnotationFields.WA_CONTEXT);   
        if (!StringUtils.isBlank(annotation.getType())) {
        	jsonLdResource.addType(annotation.getType());
        } else {
        	jsonLdResource.addType(WebAnnotationFields.DEFAULT_ANNOTATION_TYPE);
        }
        
        if (annotation.getAnnotationId() != null){ 
        	jsonLdResource.putProperty(WebAnnotationFields.AT_ID, annotation.getAnnotationId().toHttpUrl()); 
        }
//        if (!StringUtils.isBlank(annotation.getType())) 
//        	jsonLdResource.putProperty(WebAnnotationFields.TYPE, annotation.getType());   
        
        if(annotation.getGenerator().getInputString() == null || isJsonObjectInput(annotation.getGenerator().getInputString())){
        	//user input string provided
        	//TODO: why reset?
        	annotation.getGenerator().setInputString(null);
            JsonLdProperty serializedByProperty = addGeneratorProperty(annotation);
            if (serializedByProperty != null)
            	jsonLdResource.putProperty(serializedByProperty);                
        } else {
        	//input string is a single value
        	jsonLdResource.putProperty(WebAnnotationFields.GENERATOR, annotation.getGenerator().getInputString());
        }
        
        
        
        if (annotation.getCreated() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.CREATED, TypeUtils.convertDateToStr(annotation.getCreated()));

        if (annotation.getCreator().getInputString() == null  || isJsonObjectInput(annotation.getCreator().getInputString())){
        	//TODO: why reset?
        	annotation.getCreator().setInputString(null);
            JsonLdProperty annotatedByProperty = addCreator(annotation);
            if (annotatedByProperty != null)
            	jsonLdResource.putProperty(annotatedByProperty);                
        } else {
        	jsonLdResource.putProperty(WebAnnotationFields.CREATOR, annotation.getCreator().getInputString());
        }
        	
        if (annotation.getGenerated() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.GENERATED, TypeUtils.convertDateToStr(annotation.getGenerated()));
        if (!StringUtils.isBlank(annotation.getMotivation())) 
        	jsonLdResource.putProperty(WebAnnotationFields.MOTIVATION, annotation.getMotivation());
        JsonLdProperty styledByProperty = addStyledByProperty(annotation);
        if (styledByProperty != null)
        	jsonLdResource.putProperty(styledByProperty);                

        // TODO improve the following code 
        
        if (!annotation.getInternalType().equals(AnnotationTypes.OBJECT_LINKING.name())) {
	        //tag or comment, not linking
        	if (isJsonObjectInput(annotation.getBody().getInputString())){
	        	annotation.getBody().setInputString(null);
		        JsonLdProperty bodyProperty = addBodyProperty(annotation);
		        if (bodyProperty != null)
		        	jsonLdResource.putProperty(bodyProperty);
	        } else {
	        	if (annotation.getBody().getValues() != null && annotation.getBody().getValues().size() > 0) {
			        JsonLdProperty bodyProperty = addArrayProperty(WebAnnotationFields.BODY, annotation.getBody().getValues());
			        if (bodyProperty != null)
			        	jsonLdResource.putProperty(bodyProperty);
	        	}
	        	else
	        		jsonLdResource.putProperty(WebAnnotationFields.BODY, annotation.getBody().getInputString());
	        }
        }
        
        if (isJsonObjectInput(annotation.getTarget().getInputString())){
        	annotation.getTarget().setInputString(null);
	        JsonLdProperty targetProperty = addTargetProperty(annotation);
	        if (targetProperty != null)
	        	jsonLdResource.putProperty(targetProperty);
        } else {
            if (annotation.getInternalType().equals(AnnotationTypes.OBJECT_LINKING.name())) {
	            if (!StringUtils.isBlank(TypeUtils.getTypeListAsStr(annotation.getTarget().getValues())))  {
			        JsonLdProperty targetProperty = addArrayProperty(WebAnnotationFields.TARGET, annotation.getTarget().getValues());
			        if (targetProperty != null)
			        	jsonLdResource.putProperty(targetProperty);
	            }
            } else {
            	jsonLdResource.putProperty(WebAnnotationFields.TARGET, annotation.getTarget().getInputString());
            }
        }

	    if (annotation.getSameAs() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.SAME_AS, annotation.getSameAs());
        if (annotation.getEquivalentTo() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.EQUIVALENT_TO, annotation.getEquivalentTo());
        
        put(jsonLdResource);
    }

   

	

	
	
	
    
	
	
	
    
	
    
    /**
     * This method takes passed serialised AnnotationLd GSON string and
     * passes it to the parser. 
     * @param serializedAnnotationLd
     * @return Annotation object
     */
//    public static Annotation deserialise(String serialisedAnnotationLd) {
//    	Annotation res = null;
//        Gson gson = new Gson();
//        AnnotationLd annotationLdDeserialisedObject = gson.fromJson(serialisedAnnotationLd, AnnotationLd.class);
//        String annotationLdDeserialisedString = annotationLdDeserialisedObject.toString();
//        AnnotationLd.toConsole("deserialise: ", annotationLdDeserialisedString);
//        try {
//			JsonLd deserialisedJsonLd = JsonLdParser.parseExt(annotationLdDeserialisedString);
//			res = AnnotationLd.getAnnotationFromJsonLd(deserialisedJsonLd);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//        return res;
//    }
    
//    /**
//     * This method converts deserialised JsonLd to Annotation object.
//     * @param deserialisedJsonLd
//     * @return Annotation object
//     */
//    public static Annotation getAnnotationFromJsonLd(JsonLd deserialisedJsonLd) {
//    	Annotation res = null;
//    	AnnotationLd annotationLd = new AnnotationLd(deserialisedJsonLd);
//    	res = annotationLd.getAnnotation();
//    	return res;
//    }
    
	private JsonLdProperty addTargetProperty(Annotation annotation) {
		JsonLdProperty targetProperty = new JsonLdProperty(WebAnnotationFields.TARGET);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        
        if (annotation != null && annotation.getTarget() != null) {
            if (!StringUtils.isBlank(annotation.getTarget().getInputString())) 
            	propertyValue.getValues().put(WebAnnotationFields.INPUT_STRING, annotation.getTarget().getInputString());
        	if (!StringUtils.isBlank(annotation.getTarget().getType())) 
    			propertyValue.addType(annotation.getTarget().getType().replace("[", "").replace("]", ""));

            if (!StringUtils.isBlank(annotation.getTarget().getType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.TYPE, annotation.getTarget().getType());
            if (!StringUtils.isBlank(annotation.getTarget().getContentType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.CONTENT_TYPE, annotation.getTarget().getContentType());
            if (!StringUtils.isBlank(annotation.getTarget().getHttpUri()))         	
            	propertyValue.getValues().put(WebAnnotationFields.HTTP_URI, annotation.getTarget().getHttpUri());
        	
	        JsonLdProperty sourceProperty = new JsonLdProperty(WebAnnotationFields.SOURCE);
	        JsonLdPropertyValue propertyValue2 = new JsonLdPropertyValue();
	        
        	if (annotation.getTarget().getSourceResource() != null) { 
            	if (!StringUtils.isBlank(annotation.getTarget().getSourceResource().getContentType())) 
            		propertyValue2.getValues().put(WebAnnotationFields.CONTENT_TYPE, annotation.getTarget().getSourceResource().getContentType());
            	if (!StringUtils.isBlank(annotation.getTarget().getSourceResource().getHttpUri())) 
            		propertyValue2.getValues().put(WebAnnotationFields.AT_ID, annotation.getTarget().getSourceResource().getHttpUri());
            	if (!StringUtils.isBlank(annotation.getTarget().getSourceResource().getMediaType())) 
            		propertyValue2.getValues().put(WebAnnotationFields.FORMAT, annotation.getTarget().getSourceResource().getMediaType());
                if (propertyValue2.getValues().size() != 0) {
	            	sourceProperty.addValue(propertyValue2);        
			        propertyValue.putProperty(sourceProperty);
                }
        	}
	        
	        JsonLdProperty selectorProperty = new JsonLdProperty(WebAnnotationFields.SELECTOR);
	        JsonLdPropertyValue propertyValue3 = new JsonLdPropertyValue();

            if (annotation.getTarget().getSelector() != null 
            		&& !StringUtils.isBlank(annotation.getTarget().getSelector().getSelectorType()))         	
            	propertyValue3.getValues().put(WebAnnotationFields.TYPE
            			, annotation.getTarget().getSelector().getSelectorType());
            else
            	propertyValue3.setType(""); // if property is empty - set empty type

            if (annotation.getTarget().getSelector() != null 
            		&& !StringUtils.isBlank(annotation.getTarget().getSelector().getDimensionMap().toString()))         	
            	propertyValue3.getValues().put(
            			WebAnnotationFields.DIMENSION_MAP
            			, JsonUtils.mapToStringExt(annotation.getTarget().getSelector().getDimensionMap()));

            if (propertyValue3.getValues().size() != 0) {
		        selectorProperty.addValue(propertyValue3);        
		        propertyValue.putProperty(selectorProperty);
            }
	        
	        if (propertyValue.getValues().size() == 0)
	        	return null;
	        targetProperty.addValue(propertyValue);
        } else {
        	return null;
        }
		return targetProperty;
	}
    
	private JsonLdProperty addConceptProperty(Concept concept) {
        JsonLdProperty conceptProperty = new JsonLdProperty(WebAnnotationFields.CONCEPT);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();                
        
    	if (concept != null) { 
            addListToProperty(concept.getNotation(), propertyValue, WebAnnotationFields.NOTATION);
            addListToProperty(concept.getNarrower(), propertyValue, WebAnnotationFields.NARROWER);
            addListToProperty(concept.getBroader(), propertyValue, WebAnnotationFields.BROADER);
            addListToProperty(concept.getRelated(), propertyValue, WebAnnotationFields.RELATED);
            
            addMapToProperty(concept.getPrefLabel(), propertyValue, WebAnnotationFields.PREF_LABEL);
            addMapToProperty(concept.getHiddenLabel(), propertyValue, WebAnnotationFields.HIDDEN_LABEL);
            addMapToProperty(concept.getAltLabel(), propertyValue, WebAnnotationFields.ALT_LABEL);
            if (propertyValue.getValues().size() != 0) {
            	conceptProperty.addValue(propertyValue);        
            }
    	}	
    	return conceptProperty;
	}

	/**
	 * @param map
	 * @param propertyValue
	 * @param field
	 */
	private void addMapToProperty(Map<String, String> map, JsonLdPropertyValue propertyValue, String field) {
        JsonLdProperty fieldProperty = new JsonLdProperty(field);
        JsonLdPropertyValue fieldPropertyValue = new JsonLdPropertyValue();
        
	    Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
	        String curValue = pairs.getValue();
        	if (!StringUtils.isBlank(curValue)) 
        		fieldPropertyValue.getValues().put(pairs.getKey(), pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
        if (fieldPropertyValue.getValues().size() != 0) {
         	fieldProperty.addValue(fieldPropertyValue);        
         	propertyValue.putProperty(fieldProperty);
    	}
	}
	
	/**
	 * @param list
	 * @param propertyValue
	 * @param field
	 */
	private void addListToProperty(List<String> list, JsonLdPropertyValue propertyValue, String field) {
		String listString = TypeUtils.getTypeListAsStr(list);
		if (!StringUtils.isBlank(listString)) 
			propertyValue.getValues().put(field, listString);
	}
	
	/**
	 * @param annotation
	 * @return
	 */
	private JsonLdProperty addArrayProperty(String propertyName, List<String> valueList) {
		JsonLdProperty arrProperty = new JsonLdProperty(propertyName);
        if (valueList != null) {
        	Iterator<String> itr = valueList.iterator();
        	while (itr.hasNext()) {
        		String value = itr.next();
        		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        		propertyValue.setValue(value);
    	        arrProperty.addValue(propertyValue);        
        	}
        } else {
        	return null;
        }
		return arrProperty;
	}

	/**
	 * @param annotation
	 * @return
	 */
	private JsonLdProperty addBodyProperty(Annotation annotation) {
		JsonLdProperty bodyProperty = new JsonLdProperty(WebAnnotationFields.BODY);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        
        if (annotation != null && annotation.getBody() != null) {	
            if (!StringUtils.isBlank(annotation.getBody().getInputString())) 
            	propertyValue.getValues().put(WebAnnotationFields.INPUT_STRING, annotation.getBody().getInputString());
            if (!StringUtils.isBlank(TypeUtils.getTypeListAsStr(annotation.getBody().getType()))) 
        	    propertyValue.getValues().put(WebAnnotationFields.TYPE, TypeUtils.getTypeListAsStr(annotation.getBody().getType()));            
            if (!StringUtils.isBlank(annotation.getBody().getValue()))         	
            	propertyValue.getValues().put(WebAnnotationFields.CHARS, annotation.getBody().getValue());
            if (!StringUtils.isBlank(annotation.getBody().getLanguage()))         	
            	propertyValue.getValues().put(WebAnnotationFields.DC_LANGUAGE, annotation.getBody().getLanguage());
            if (!StringUtils.isBlank(annotation.getBody().getContentType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.FORMAT, annotation.getBody().getContentType());	
            if (!StringUtils.isBlank(annotation.getBody().getHttpUri()))         	
            	propertyValue.getValues().put(WebAnnotationFields.ID, annotation.getBody().getHttpUri());
            if (annotation.getBody().getMultilingual() != null && !annotation.getBody().getMultilingual().isEmpty())         	
            	propertyValue.getValues().put(WebAnnotationFields.MULTILINGUAL, JsonUtils.mapToString(annotation.getBody().getMultilingual()));
            if (annotation.getBody().getConcept() != null)         	
            	propertyValue.putProperty(addConceptProperty(annotation.getBody().getConcept()));
//            if (!StringUtils.isBlank(annotation.getBody().getInternalId()))         	
//            	propertyValue.getValues().put(WebAnnotationFields.AT_ID, annotation.getBody().getInternalId());
            if (!StringUtils.isBlank(annotation.getBody().getSource()))         	
            	propertyValue.getValues().put(WebAnnotationFields.SOURCE, annotation.getBody().getSource());
            if (annotation.getBody().getSourceResource() != null)         	
            	;//TODO add serialization of resource
            if (!StringUtils.isBlank(annotation.getBody().getPurpose()))         	
            	propertyValue.getValues().put(WebAnnotationFields.PURPOSE, annotation.getBody().getPurpose());
            if (propertyValue.getValues().size() == 0)
            	return null;
	        bodyProperty.addValue(propertyValue);        
        } else {
        	return null;
        }
		return bodyProperty;
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

	private void addAgentByProperty(JsonLdPropertyValue propertyValue,
			Agent agent) {
       	
		boolean showOpenId = false; 
		if(agent.getOpenId() != null && showOpenId)
       		propertyValue.getValues().put(WebAnnotationFields.ID, agent.getOpenId());
        
       	if (!StringUtils.isBlank(agent.getType())) //convert internal type to json value
        	propertyValue.getValues().put(WebAnnotationFields.TYPE, AgentTypes.valueOf(agent.getInternalType()).getJsonValue());
        if (!StringUtils.isBlank(agent.getName())) 
        	propertyValue.getValues().put(WebAnnotationFields.NAME, agent.getName());
        if (!StringUtils.isBlank(agent.getHomepage())) 
        	propertyValue.getValues().put(WebAnnotationFields.HOMEPAGE, agent.getHomepage());
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
		return inputString!= null && inputString.contains("{");
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
