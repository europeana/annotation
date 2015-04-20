/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package eu.europeana.annotation.jsonld;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.apache.stanbol.commons.jsonld.JsonLdProperty;
import org.apache.stanbol.commons.jsonld.JsonLdPropertyValue;
import org.apache.stanbol.commons.jsonld.JsonLdResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.ConceptObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.SelectorObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.StyleObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.TargetObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.ConceptTypes;
import eu.europeana.annotation.definitions.model.vocabulary.SelectorTypes;
import eu.europeana.annotation.utils.JsonUtils;

/**
 * The AnnotationLd class provides an API to create a JSON-LD object structure for Annotation
 * and to serialize this structure. It is designed for Annotation interface.
 * 
 * <p>
 * This implementation is based on the JSON-LD 1.0 specification editor's draft from
 * January 5, 2013. Available online at <a href="http://json-ld.org/spec">http://json-ld.org/spec</a>.
 */
public class AnnotationLd extends JsonLd {
    
    private static final Logger logger = LoggerFactory.getLogger(AnnotationLd.class);
   
    TypeUtils typeHelper = new TypeUtils();
			
    public TypeUtils getTypeHelper() {
		return typeHelper;
	}

	/**
     * @param annotation
     */
    public AnnotationLd(Annotation annotation) {
    	setAnnotation(annotation);
    }
    
    /**
     * @param jsonLd
     */
    public AnnotationLd(JsonLd jsonLd) {
    	setJsonLd(jsonLd);
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
        addNamespacePrefix(WebAnnotationFields.OA_CONTEXT, WebAnnotationFields.OA);
        //TODO: verify if the following check is needed
        //if(isApplyNamespaces())
        	setUsedNamespaces(namespacePrefixMap);

        JsonLdResource jsonLdResource = new JsonLdResource();
        jsonLdResource.setSubject("");
        if (!StringUtils.isBlank(annotation.getType())) {
        	jsonLdResource.addType(annotation.getType());
        } else {
        	jsonLdResource.addType(WebAnnotationFields.DEFAULT_ANNOTATION_TYPE);
        }
        
        if (annotation.getAnnotationId() != null && !StringUtils.isBlank(annotation.getAnnotationId().toString())) 
        	jsonLdResource.putProperty(WebAnnotationFields.SID, annotation.getAnnotationId().toString());   
        if (!StringUtils.isBlank(annotation.getType())) 
        	jsonLdResource.putProperty(WebAnnotationFields.TYPE, annotation.getType());   
       	JsonLdProperty serializedByProperty = addSerializedByProperty(annotation);
        if (serializedByProperty != null)      	
        	jsonLdResource.putProperty(serializedByProperty);        
        if (annotation.getAnnotatedAt() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.ANNOTATED_AT, TypeUtils.convertDateToStr(annotation.getAnnotatedAt()));
        JsonLdProperty annotatedByProperty = addAnnotatedByProperty(annotation);
        if (annotatedByProperty != null)
        	jsonLdResource.putProperty(annotatedByProperty);                
        if (annotation.getSerializedAt() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.SERIALIZED_AT, TypeUtils.convertDateToStr(annotation.getSerializedAt()));
        if (!StringUtils.isBlank(annotation.getMotivatedBy())) 
        	jsonLdResource.putProperty(WebAnnotationFields.MOTIVATED_BY, annotation.getMotivatedBy());
        JsonLdProperty styledByProperty = addStyledByProperty(annotation);
        if (styledByProperty != null)
        	jsonLdResource.putProperty(styledByProperty);                
        JsonLdProperty bodyProperty = addBodyProperty(annotation);
        if (bodyProperty != null)
        	jsonLdResource.putProperty(bodyProperty);
        JsonLdProperty targetProperty = addTargetProperty(annotation);
        if (targetProperty != null)
        	jsonLdResource.putProperty(targetProperty);
        if (annotation.getSameAs() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.SAME_AS, annotation.getSameAs());
        if (annotation.getEquivalentTo() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.EQUIVALENT_TO, annotation.getEquivalentTo());
        
        put(jsonLdResource);
    }

    /**
     * This method converts AnnotationLd to Annotation object.
     * @return Annotation object
     */
    @SuppressWarnings("rawtypes")
	public Annotation getAnnotation() {
		
		Annotation annotation = AnnotationObjectFactory.getInstance().createModelObjectInstance(
				AnnotationTypes.OBJECT_TAG.name());
    	
		Iterator<String> itrSubjects = this.getResourceSubjects().iterator();
    	JsonLdResource resource = getResource(itrSubjects.next()); //"");
    	Iterator<?> it = resource.getPropertyMap().entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry pairs = (Map.Entry)it.next();
		    String key = pairs.getKey().toString();
		    Object mapValue = pairs.getValue();
		    switch (key) {
		    case WebAnnotationFields.SID:
		    	String annotationIdValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(annotationIdValue)) 
					((BaseObjectTag) annotation).parse(annotationIdValue);
		    	break;
		    case WebAnnotationFields.TYPE:
		    	String typeValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(typeValue)) 
					annotation.setType(typeValue);
		    	break;
		    case WebAnnotationFields.ANNOTATED_AT:
		    	String annotatedAtValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(annotatedAtValue)) 
					annotation.setAnnotatedAt(TypeUtils.convertStrToDate(annotatedAtValue));
		    	break;
		    case WebAnnotationFields.SERIALIZED_AT:
		    	String serializedAtValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(serializedAtValue)) 
					annotation.setSerializedAt(TypeUtils.convertStrToDate(serializedAtValue));
		    	break;
		    case WebAnnotationFields.MOTIVATED_BY:
		    	String motivatedByValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(motivatedByValue)) 
					annotation.setMotivatedBy(motivatedByValue);
		    	break;
		    case WebAnnotationFields.BODY:
		    	Body body = getBody(mapValue);
			    annotation.setBody(body);
		    	break;
		    case WebAnnotationFields.TARGET:	    	
				Target target = getTarget(mapValue);						
			    annotation.setTarget(target);
		    	break;
		    case WebAnnotationFields.SERIALIZED_BY:
				Agent serializedBy = getSerializedBy(mapValue);
				annotation.setSerializedBy(serializedBy);
		    	break;
		    case WebAnnotationFields.ANNOTATED_BY:
				Agent annotatedBy = getAnnotatedBy(mapValue);
				annotation.setAnnotatedBy(annotatedBy);
		    	break;
		    case WebAnnotationFields.STYLED_BY:
		    	Style style = getStyledBy(mapValue);
				annotation.setStyledBy(style);
		    	break;
		    case WebAnnotationFields.SAME_AS:
		    	String sameAsValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(sameAsValue)) 
					annotation.setSameAs(sameAsValue);
		    	break;
		    case WebAnnotationFields.EQUIVALENT_TO:
		    	String equivalentToValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(equivalentToValue)) 
					annotation.setEquivalentTo(equivalentToValue);
		    	break;
		    default:
		    	break;
		    }		    
		}	

		return annotation;
    }

	private String getLiteralPropertyValue(Object mapValue) {
		String propValue = null;
		JsonLdProperty property = (JsonLdProperty) mapValue;
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);					
			if (!StringUtils.isBlank(propertyValue.getLiteralValue())) {
				propValue = propertyValue.getLiteralValue();
			}	
		}
		return propValue;
	}

	/**
	 * This method retrieves Agent object for serializedBy field from AnnotationLd object.
	 * @param mapValue
	 * @return Agent object
	 */
	private Agent getSerializedBy(Object mapValue) {
		JsonLdProperty property = (JsonLdProperty) mapValue;
		Agent agent = getAgentByProperty(property);
		return agent;
	}

	private Agent getAgentByProperty(JsonLdProperty property) {
		
		Agent agent = null;  
		
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			String euType = extractEuType(propertyValue);
			//if not set 
			if (StringUtils.isBlank(euType))
				throw new AnnotationAttributeInstantiationException(euType);
			
			agent = AgentObjectFactory.getInstance().createModelObjectInstance(euType);
			if (hasValue(propertyValue, WebAnnotationFields.AT_TYPE)) 
				agent.setAgentTypeAsString(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));			
			if (hasValue(propertyValue, WebAnnotationFields.SID)) 
				agent.setOpenId(propertyValue.getValues().get(WebAnnotationFields.SID));
			if (hasValue(propertyValue, WebAnnotationFields.NAME)) 
				agent.setName(propertyValue.getValues().get(WebAnnotationFields.NAME));
			if (hasValue(propertyValue, WebAnnotationFields.FOAF_HOMEPAGE)) 
				agent.setHomepage(propertyValue.getValues().get(WebAnnotationFields.FOAF_HOMEPAGE));
		}
		return agent;
	}

	protected boolean hasValue(JsonLdPropertyValue propertyValue, String fieldName) {
		return !StringUtils.isBlank(propertyValue.getValues().get(fieldName));
	}

	private String extractEuType(JsonLdPropertyValue propertyValue) {
		return extractEuType(propertyValue, WebAnnotationFields.AT_TYPE);
	}

	private String extractEuType(JsonLdPropertyValue propertyValue, String fieldName) {
		String typeArray = propertyValue.getValues().get(fieldName);
		String euType = getTypeHelper().getEuTypeFromTypeArray(typeArray);
		return euType;
	}

	/**
	 * This method retrieves Agent object for annotatedBy field from AnnotationLd object.
	 * @param mapValue
	 * @return Agent object
	 */
	private Agent getAnnotatedBy(Object mapValue) {
		JsonLdProperty property = (JsonLdProperty) mapValue;
		return getAgentByProperty(property);
	}

	/**
	 * This method retrieves Style object for styledBy field from AnnotationLd object.
	 * @param mapValue
	 * @return Style object
	 */
	private Style getStyledBy(Object mapValue) {
		JsonLdProperty property = (JsonLdProperty) mapValue;
		Style style = null;
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			String euType = "";
			if (!StringUtils.isBlank(propertyValue.getType())) 
				euType = getTypeHelper().getEuTypeFromTypeArray(propertyValue.getType());
//				euType = propertyValue.getType();
			if (StringUtils.isBlank(euType) && hasValue(propertyValue, WebAnnotationFields.AT_TYPE)) 
				euType = getTypeHelper().getEuTypeFromTypeArray(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));
			//if not set 
			if (StringUtils.isBlank(euType))
				throw new AnnotationAttributeInstantiationException(euType);
			
			style = StyleObjectFactory.getInstance().createModelObjectInstance(euType);
			
			if (!StringUtils.isBlank(propertyValue.getType())) 
				style.setHttpUri(propertyValue.getType());
			if (StringUtils.isBlank(propertyValue.getType()) && hasValue(propertyValue, WebAnnotationFields.AT_TYPE)) 
				style.setHttpUri(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));
			if (hasValue(propertyValue, WebAnnotationFields.STYLE_CLASS)) 
				style.setContentType(propertyValue.getValues().get(WebAnnotationFields.STYLE_CLASS));
			if (hasValue(propertyValue, WebAnnotationFields.SOURCE)) 
				style.setValue(propertyValue.getValues().get(WebAnnotationFields.SOURCE));
		}
		return style;
	}

	/**
	 * This method retrieves Target object from AnnotationLd object.
	 * @param mapValue
	 * @return Target object
	 */
	private Target getTarget(Object mapValue) {
		Target target = null;
		JsonLdProperty property = (JsonLdProperty) mapValue;
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			if (hasValue(propertyValue, WebAnnotationFields.TARGET_TYPE)) {
				String euType = extractEuType(propertyValue, WebAnnotationFields.TARGET_TYPE);			
				//if not set 
				if (StringUtils.isBlank(euType))
					throw new AnnotationAttributeInstantiationException(euType);
				
				target = TargetObjectFactory.getInstance().createModelObjectInstance(euType);
//				target = (Target) objectFactory.createModelObjectInstance(
//						AnnotationPartTypes.TARGET.name() + WebAnnotationFields.SPLITTER + euType);
				target.setTargetType(propertyValue.getValues().get(WebAnnotationFields.TARGET_TYPE));
				if (hasValue(propertyValue, WebAnnotationFields.CONTENT_TYPE)) 
					target.setContentType(propertyValue.getValues().get(WebAnnotationFields.CONTENT_TYPE));
				if (hasValue(propertyValue, WebAnnotationFields.HTTP_URI)) 
					target.setHttpUri(propertyValue.getValues().get(WebAnnotationFields.HTTP_URI));
				
				JsonLdProperty sourceProperty = propertyValue.getProperty(WebAnnotationFields.SOURCE);
				JsonLdPropertyValue propertyValue2 = (JsonLdPropertyValue) sourceProperty.getValues().get(0);
				InternetResource source = new BaseInternetResource();
				if (hasValue(propertyValue2, WebAnnotationFields.CONTENT_TYPE)) 
					source.setContentType(propertyValue2.getValues().get(WebAnnotationFields.CONTENT_TYPE));
				if (hasValue(propertyValue2, WebAnnotationFields.SID)) {
					source.setHttpUri(propertyValue2.getValues().get(WebAnnotationFields.SID));
//					target.setEuropeanaId(propertyValue2.getValues().get(WebAnnotationFields.SID));
				}
				if (hasValue(propertyValue2, WebAnnotationFields.FORMAT)) 
					source.setMediaType(propertyValue2.getValues().get(WebAnnotationFields.FORMAT));
				target.setSource(source);
				
				JsonLdProperty selectorProperty = propertyValue.getProperty(WebAnnotationFields.SELECTOR);
				if (selectorProperty != null) {
					JsonLdPropertyValue propertyValue3 = (JsonLdPropertyValue) selectorProperty.getValues().get(0);
					Selector selector = SelectorObjectFactory.getInstance().createModelObjectInstance(
							SelectorTypes.SVG_RECTANGLE_SELECTOR.name());
					if (hasValue(propertyValue3, WebAnnotationFields.AT_TYPE)) 
						selector.setSelectorType(propertyValue3.getValues().get(WebAnnotationFields.AT_TYPE));
					if (hasValue(propertyValue3, WebAnnotationFields.DIMENSION_MAP)) 
						selector.setDimensionMap(
								JsonUtils.stringToMapExt(propertyValue3.getValues().get(WebAnnotationFields.DIMENSION_MAP)));
					target.setSelector((Selector)selector);
				}
			}
		}
		return target;
	}

	/**
	 * This method retrieves Body object from AnnotationLd object.
	 * @param mapValue
	 * @return Body object
	 */
	private Body getBody(Object mapValue) {
		Body body = null;
		JsonLdProperty property = (JsonLdProperty) mapValue;
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			if (hasValue(propertyValue, WebAnnotationFields.AT_TYPE)) {
				String euType = extractEuType(propertyValue);

				//if not set 
				if (StringUtils.isBlank(euType))
					throw new AnnotationAttributeInstantiationException(euType);
				
				body = BodyObjectFactory.getInstance().createModelObjectInstance(euType);
				
				body.setBodyType(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));

				if (hasValue(propertyValue, WebAnnotationFields.CHARS)) 
					body.setValue(propertyValue.getValues().get(WebAnnotationFields.CHARS));
				if (hasValue(propertyValue, WebAnnotationFields.DC_LANGUAGE)) 
					body.setLanguage(propertyValue.getValues().get(WebAnnotationFields.DC_LANGUAGE));
				if (hasValue(propertyValue, WebAnnotationFields.FORMAT)) 
					body.setContentType(propertyValue.getValues().get(WebAnnotationFields.FORMAT));
				if (hasValue(propertyValue, WebAnnotationFields.FOAF_PAGE)) 
					body.setHttpUri(propertyValue.getValues().get(WebAnnotationFields.FOAF_PAGE));
				if (hasValue(propertyValue, WebAnnotationFields.MULTILINGUAL)) 
					body.setMultilingual(JsonUtils.stringToMap(propertyValue.getValues().get(WebAnnotationFields.MULTILINGUAL)));
			}
			
			Concept concept = getConcept(propertyValue); 
			if (concept != null)
				body.setConcept(concept);
		}
		return body;
	}
    
	/**
	 * This method parses string list given in JSON-LD format.
	 * @param propertyValue
	 * @param fieldName
	 * @return string list
	 */
	private List<String> parseList(JsonLdPropertyValue propertyValue, String fieldName) {
		List<String> res = null;
		if (hasValue(propertyValue, fieldName)) {
			String fieldContent = "[" + propertyValue.getValues().get(fieldName) + "]";
			res = JsonUtils.stringToList(fieldContent);
		}
		return res;
	}
	
	/**
	 * This method parses string map given in JSON-LD format.
	 * @param propertyValue
	 * @param fieldName
	 * @return string map
	 */
	private Map<String, String> parseMap(JsonLdPropertyValue propertyValue, String fieldName) {
		Map<String, String> res = null;
		String fieldNameId = fieldName + ":@" + WebAnnotationFields.ID;
//		String fieldNameContainer = fieldName + ":@" + WebAnnotationFields.CONTAINER;
		String fieldNameContainer = "@" + WebAnnotationFields.CONTAINER;
		if (hasValue(propertyValue, fieldNameId)) {
			res = new HashMap<String, String>();
			res.put("@" + WebAnnotationFields.ID, propertyValue.getValues().get(fieldNameId));
			res.put("@" + WebAnnotationFields.CONTAINER, propertyValue.getValues().get(fieldNameContainer));
//			String fieldContent = "" + propertyValue.getValues().get(fieldNameId) + "]";
//			res = JsonUtils.stringToMap(fieldContent);
		}
		return res;
	}
	
	/**
	 * This method parses a Concept object from JSON-LD string.
	 * @param mapValue
	 * @return Concept object
	 */
	private Concept getConcept(Object mapValue) {
		Concept concept = null;
		JsonLdProperty property = ((JsonLdPropertyValue) mapValue).getProperty(WebAnnotationFields.CONCEPT);
		if (property != null && property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			concept = ConceptObjectFactory.getInstance().createModelObjectInstance(
					ConceptTypes.BASE_CONCEPT.name());
			concept.setNotation(parseList(propertyValue, WebAnnotationFields.NOTATION));
			concept.setRelated(parseList(propertyValue, WebAnnotationFields.RELATED));
			concept.setNarrower(parseList(propertyValue, WebAnnotationFields.NARROWER));
			concept.setBroader(parseList(propertyValue, WebAnnotationFields.BROADER));
			concept.setPrefLabel(parseMap(propertyValue, WebAnnotationFields.PREF_LABEL));
			concept.setHiddenLabel(parseMap(propertyValue, WebAnnotationFields.HIDDEN_LABEL));
			concept.setAltLabel(parseMap(propertyValue, WebAnnotationFields.ALT_LABEL));
		}
		
		return concept;
	}
    
	/**
     * Adds the values from the passed JsonLd object. 
     * 
     * @param jsonLd
     */
	public void setJsonLd(JsonLd jsonLd) {
             
    	setUseTypeCoercion(false);
        setUseCuries(true);
        
        if (jsonLd != null) {
    		if (jsonLd.getNamespacePrefixMap() != null) {
    	    	setNamespacePrefixMap(jsonLd.getNamespacePrefixMap());
    		}
    		if (jsonLd.getUsedNamespaces() != null) {
    			setUsedNamespaces(jsonLd.getUsedNamespaces());
    		}
    		if (jsonLd.getResourceSubjects() != null) {
    			Iterator<String> itr = jsonLd.getResourceSubjects().iterator();
    			while (itr.hasNext()) {
    				String resourceName = itr.next();
    				if (resourceName != null) {
    					put(jsonLd.getResource(resourceName));
    				}
    			}
    		}
   			setUseJointGraphs(jsonLd.isUseJointGraphs());
   			setUseTypeCoercion(jsonLd.isUseTypeCoercion());
   			setUseCuries(jsonLd.isUseCuries());
   			setApplyNamespaces(jsonLd.isApplyNamespaces());
    	}    	
    }
    
    /**
     * This method takes passed serialised AnnotationLd GSON string and
     * passes it to the parser. 
     * @param serializedAnnotationLd
     * @return Annotation object
     */
    public static Annotation deserialise(String serialisedAnnotationLd) {
    	Annotation res = null;
        Gson gson = new Gson();
        AnnotationLd annotationLdDeserialisedObject = gson.fromJson(serialisedAnnotationLd, AnnotationLd.class);
        String annotationLdDeserialisedString = annotationLdDeserialisedObject.toString();
        AnnotationLd.toConsole("deserialise: ", annotationLdDeserialisedString);
        try {
			JsonLd deserialisedJsonLd = JsonLdParser.parseExt(annotationLdDeserialisedString);
			res = AnnotationLd.getAnnotationFromJsonLd(deserialisedJsonLd);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return res;
    }
    
    /**
     * This method converts deserialised JsonLd to Annotation object.
     * @param deserialisedJsonLd
     * @return Annotation object
     */
    public static Annotation getAnnotationFromJsonLd(JsonLd deserialisedJsonLd) {
    	Annotation res = null;
    	AnnotationLd annotationLd = new AnnotationLd(deserialisedJsonLd);
    	res = annotationLd.getAnnotation();
    	return res;
    }
    
	private JsonLdProperty addTargetProperty(Annotation annotation) {
		JsonLdProperty targetProperty = new JsonLdProperty(WebAnnotationFields.TARGET);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        
        if (annotation != null && annotation.getTarget() != null) {
        	if (!StringUtils.isBlank(annotation.getTarget().getTargetType())) 
//        		propertyValue.addType(annotation.getTarget().getTargetType());
    			propertyValue.addType(annotation.getTarget().getTargetType().replace("[", "").replace("]", ""));

            if (!StringUtils.isBlank(annotation.getTarget().getTargetType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.TARGET_TYPE, annotation.getTarget().getTargetType());
            if (!StringUtils.isBlank(annotation.getTarget().getContentType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.CONTENT_TYPE, annotation.getTarget().getContentType());
            if (!StringUtils.isBlank(annotation.getTarget().getHttpUri()))         	
            	propertyValue.getValues().put(WebAnnotationFields.HTTP_URI, annotation.getTarget().getHttpUri());
        	
	        JsonLdProperty sourceProperty = new JsonLdProperty(WebAnnotationFields.SOURCE);
	        JsonLdPropertyValue propertyValue2 = new JsonLdPropertyValue();
	        
        	if (annotation.getTarget().getSource() != null) { 
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getContentType())) 
            		propertyValue2.getValues().put(WebAnnotationFields.CONTENT_TYPE, annotation.getTarget().getSource().getContentType());
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getHttpUri())) 
            		propertyValue2.getValues().put(WebAnnotationFields.SID, annotation.getTarget().getSource().getHttpUri());
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getMediaType())) 
            		propertyValue2.getValues().put(WebAnnotationFields.FORMAT, annotation.getTarget().getSource().getMediaType());
                if (propertyValue2.getValues().size() != 0) {
	            	sourceProperty.addValue(propertyValue2);        
			        propertyValue.putProperty(sourceProperty);
                }
        	}
	        
	        JsonLdProperty selectorProperty = new JsonLdProperty(WebAnnotationFields.SELECTOR);
	        JsonLdPropertyValue propertyValue3 = new JsonLdPropertyValue();

            if (annotation.getTarget().getSelector() != null 
            		&& !StringUtils.isBlank(annotation.getTarget().getSelector().getSelectorType()))         	
            	propertyValue3.getValues().put(WebAnnotationFields.AT_TYPE
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
	private JsonLdProperty addBodyProperty(Annotation annotation) {
		JsonLdProperty bodyProperty = new JsonLdProperty(WebAnnotationFields.BODY);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        
        if (annotation != null && annotation.getBody() != null) {	
            if (!StringUtils.isBlank(annotation.getBody().getBodyType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.AT_TYPE, annotation.getBody().getBodyType());
            if (!StringUtils.isBlank(annotation.getBody().getValue()))         	
            	propertyValue.getValues().put(WebAnnotationFields.CHARS, annotation.getBody().getValue());
            if (!StringUtils.isBlank(annotation.getBody().getLanguage()))         	
            	propertyValue.getValues().put(WebAnnotationFields.DC_LANGUAGE, annotation.getBody().getLanguage());
            if (!StringUtils.isBlank(annotation.getBody().getContentType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.FORMAT, annotation.getBody().getContentType());	
            if (!StringUtils.isBlank(annotation.getBody().getHttpUri()))         	
            	propertyValue.getValues().put(WebAnnotationFields.FOAF_PAGE, annotation.getBody().getHttpUri());
            if (annotation.getBody().getMultilingual() != null)         	
            	propertyValue.getValues().put(WebAnnotationFields.MULTILINGUAL, JsonUtils.mapToString(annotation.getBody().getMultilingual()));
            if (annotation.getBody().getConcept() != null)         	
            	propertyValue.putProperty(addConceptProperty(annotation.getBody().getConcept()));
            if (propertyValue.getValues().size() == 0)
            	return null;
	        bodyProperty.addValue(propertyValue);        
        } else {
        	return null;
        }
		return bodyProperty;
	}

	private JsonLdProperty addSerializedByProperty(Annotation annotation) {
		JsonLdProperty serializedByProperty = new JsonLdProperty(WebAnnotationFields.SERIALIZED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Agent agent = annotation.getSerializedBy();        
        addAgentByProperty(propertyValue, agent);
        if (propertyValue.getValues().size() == 0)
        	return null;
        serializedByProperty.addValue(propertyValue);
		return serializedByProperty;
	}

	private void addAgentByProperty(JsonLdPropertyValue propertyValue,
			Agent agent) {
		if (agent != null && !StringUtils.isBlank(agent.getOpenId())) 
        	propertyValue.getValues().put(WebAnnotationFields.SID, agent.getOpenId());
//        if (agent != null && !StringUtils.isBlank(agent.getAgentType())) 
//        	propertyValue.setType(agent.getAgentType());
        if (agent != null && !StringUtils.isBlank(TypeUtils.getTypeListAsStr(agent.getAgentType()))) 
        	propertyValue.getValues().put(WebAnnotationFields.AT_TYPE, TypeUtils.getTypeListAsStr(agent.getAgentType()));
        if (agent != null && !StringUtils.isBlank(agent.getName())) 
        	propertyValue.getValues().put(WebAnnotationFields.NAME, agent.getName());
        if (agent != null && !StringUtils.isBlank(agent.getHomepage())) 
        	propertyValue.getValues().put(WebAnnotationFields.FOAF_HOMEPAGE, agent.getHomepage());
	}

	private JsonLdProperty addAnnotatedByProperty(Annotation annotation) {
		JsonLdProperty annotatedByProperty = new JsonLdProperty(WebAnnotationFields.ANNOTATED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Agent agent = annotation.getAnnotatedBy();      
        addAgentByProperty(propertyValue, agent);
        if (propertyValue.getValues().size() == 0)
        	return null;
        annotatedByProperty.addValue(propertyValue);
		return annotatedByProperty;
	}

	private JsonLdProperty addStyledByProperty(Annotation annotation) {
		JsonLdProperty styledByProperty = new JsonLdProperty(WebAnnotationFields.STYLED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Style style = annotation.getStyledBy();        
        if (style != null && !StringUtils.isBlank(style.getHttpUri())) 
        	propertyValue.setType(style.getHttpUri());
//        if (style != null && !StringUtils.isBlank(style.getMediaType())) 
//        	propertyValue.setType(style.getMediaType());
        if (style != null && !StringUtils.isBlank(style.getContentType())) 
        	propertyValue.getValues().put(WebAnnotationFields.STYLE_CLASS, style.getContentType());
        if (style != null && !StringUtils.isBlank(style.getValue())) 
        	propertyValue.getValues().put(WebAnnotationFields.SOURCE, style.getValue());
        if (propertyValue.getValues().size() == 0)
        	return null;
        styledByProperty.addValue(propertyValue);
		return styledByProperty;
	}
    
    public static void toConsole(String description, String actual) {
    	logger.info(description);
    	logger.info(actual);
        String s = actual;
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replace("\"", "\\\"");
        s = s.replace("\n", "\\n");
        logger.info(s);
    }
}
